package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.data.toPortableText
import com.tuxunxiangqi.android.engine.AnalysisResult
import com.tuxunxiangqi.android.engine.PikafishEngine
import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.Side
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Step-by-step replay of a saved record: jump anywhere, hold to
 *  skim, auto-play, per-move scores where the game recorded them,
 *  key-move patrol and an engine best-move comparison. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewScreen(record: GameRecord, onBack: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val engine = remember { PikafishEngine(context) }
    DisposableEffect(engine) { onDispose { engine.close() } }
    LaunchedEffect(Unit) { engine.start({}, {}) }
    val scope = rememberCoroutineScope()
    val recordStore = remember { RecordStore(context) }

    // The record as currently stored; whole-game analysis writes
    // its results back into this copy so the page (chart, marks,
    // move list) refreshes without leaving and re-entering.
    var liveRecord by remember(record.id) { mutableStateOf(record) }

    val total = liveRecord.movesUci.size

    // Rebuild every position and the Chinese notation of each move.
    val replay = remember(record.id) {
        val parsed = Fen.fromFen(record.initialFen)
        val startCells: List<Piece?> = parsed?.first ?: GameState().cells.toList()
        val startTurn: Side = parsed?.second ?: Side.RED
        val scratch = GameState()
        scratch.newGame(startCells, startTurn)
        val snapshots = ArrayList<Pair<List<Piece?>, Side>>()
        val notations = ArrayList<String>()
        snapshots.add(scratch.cells.toList() to scratch.turn)
        for (uci in record.movesUci) {
            val ft = Fen.parseUciMove(uci)
            if (ft == null) break
            val (from, to) = ft
            notations.add(Notation.moveText(scratch.cells, from, to))
            if (!scratch.tryMove(from, to)) break
            snapshots.add(scratch.cells.toList() to scratch.turn)
        }
        snapshots to notations
    }
    val snapshots = replay.first
    val notations = replay.second
    val maxIdx = snapshots.size - 1

    var idx by remember { mutableStateOf(0) }
    var playing by remember { mutableStateOf(false) }
    // Hoisted so the move list at the bottom of the page can
    // scroll back to the board after a jump (1.17.0).
    val pageScroll = rememberScrollState()
    // Board screenshot plumbing for 分享棋盘图 (1.22.0), the same
    // capture-and-crop path the live game page uses.
    val rootView = LocalView.current
    var boardPos by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var boardSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    var compareText by remember { mutableStateOf<String?>(null) }
    var compareBusy by remember { mutableStateOf(false) }

    // Whole-game batch analysis: the engine scores every position
    // in turn (about 0.3 s each, strictly one at a time on the
    // resident engine so the phone only warms up gently), then
    // score-drops are classified into ? / ?? marks and both are
    // written back into the stored record.
    var batchRunning by remember { mutableStateOf(false) }
    var batchDone by remember { mutableStateOf(0) }
    var batchNote by remember { mutableStateOf<String?>(null) }
    val batchCancel = remember { java.util.concurrent.atomic.AtomicBoolean(false) }

    // Single-step deep analysis: one position gets a long,
    // four-candidate look (2 s MultiPV 4). It and the whole-game
    // batch run exclude each other so the two never fight over
    // the one resident engine. The result lives in memory only —
    // it is never written back into the stored record.
    var deepBusy by remember { mutableStateOf(false) }
    var deepIdx by remember { mutableStateOf(-1) }
    // Results are cached per step: deep-analysing another step
    // no longer throws away the earlier step's card, and stepping
    // back shows it again without re-running the engine.
    val deepCache = remember { mutableStateMapOf<Int, AnalysisResult>() }
    val deepErrors = remember { mutableStateMapOf<Int, String>() }
    // Candidate selection in the deep card, unified with the
    // in-game analysis panel (1.13.0): one candidate is always
    // selected (the top one by default), tapping a card switches
    // the board arrow to that candidate, and each card carries a
    // trial-walk button that replays the candidate's PV on a
    // scratch board — exactly like BoardScreen's preview.
    var deepSelectedRank by remember { mutableStateOf<Int?>(null) }
    var deepTrialRank by remember { mutableStateOf<Int?>(null) }
    var deepTrialStep by remember { mutableStateOf(0) }
    val deepTrialState = remember { GameState() }

    fun exitDeepTrial() {
        deepTrialRank = null
        deepTrialStep = 0
    }

    /** Rebuilds the trial board: the position at the current
     *  step plus the first [step] moves of the candidate's PV.
     *  Rebuilding from scratch keeps stepping correct both ways;
     *  the replayed record itself is never touched. The base
     *  position is passed in by the caller (the display state is
     *  declared further down and cannot be forward-referenced). */
    fun applyDeepTrial(
        cand: com.tuxunxiangqi.android.engine.Candidate,
        step: Int,
        baseCells: List<Piece?>,
        baseTurn: Side
    ) {
        deepTrialState.loadPosition(baseCells, baseTurn)
        val pv = cand.pvUci.take(12)
        val n = step.coerceIn(0, pv.size)
        for (i in 0 until n) {
            val mv = Fen.parseUciMove(pv[i]) ?: break
            if (!deepTrialState.tryMove(mv.first, mv.second)) break
        }
        deepTrialStep = n
    }

    fun startDeepTrial(
        cand: com.tuxunxiangqi.android.engine.Candidate,
        baseCells: List<Piece?>,
        baseTurn: Side
    ) {
        deepSelectedRank = cand.rank
        deepTrialRank = cand.rank
        applyDeepTrial(cand, 1, baseCells, baseTurn)
    }

    fun startBatchAnalysis() {
        if (batchRunning || maxIdx == 0) return
        exitDeepTrial()
        batchCancel.set(false)
        batchRunning = true
        batchDone = 0
        batchNote = null
        val results = HashMap<Int, Int>()
        val startTurn = snapshots[0].second

        fun finish(headline: String) {
            batchRunning = false
            val merged = liveRecord.evals.toMutableMap()
            merged.putAll(results)
            val marks = EvalUtil.classifyMoves(merged, startTurn, total)
            val updated = liveRecord.copy(evals = merged, marks = marks)
            recordStore.save(updated)
            liveRecord = updated
            val q = marks.values.count { it == "?" }
            val b = marks.values.count { it == "??" }
            batchNote = headline +
                "已保存 ${results.size} 个局面的评分，标出疑问手 $q 手、失误手 $b 手。"
        }

        fun step(i: Int) {
            if (batchCancel.get()) {
                finish("已取消分析。")
                return
            }
            if (i > maxIdx) {
                finish("整局分析完成。")
                return
            }
            val snap = snapshots[i]
            val gs = GameState()
            gs.newGame(snap.first, snap.second)
            engine.analyze(Fen.toFen(gs), 300, multiPv = 1) { res, err ->
                if (err != null) {
                    finish("引擎分析中断（$err）。")
                    return@analyze
                }
                val best = res?.candidates?.firstOrNull()
                if (best != null) {
                    val (rcp, rmate) = EvalUtil.toRedPerspective(
                        best.scoreCp, best.mateIn, snap.second
                    )
                    results[i] = rcp ?: rmate?.let { if (it > 0) 1500 else -1500 } ?: 0
                }
                batchDone = i + 1
                step(i + 1)
            }
        }
        step(0)
    }

    LaunchedEffect(playing) {
        while (playing) {
            delay(1100)
            if (idx >= maxIdx) {
                playing = false
            } else {
                idx++
            }
        }
    }

    val displayState = remember(idx, snapshots) {
        val gs = GameState()
        val snap = snapshots[idx]
        gs.newGame(snap.first, snap.second)
        gs
    }

    fun startDeepAnalysis() {
        if (deepBusy || batchRunning) return
        exitDeepTrial()
        deepSelectedRank = null
        deepBusy = true
        deepIdx = idx
        val atIdx = idx
        deepErrors.remove(atIdx)
        engine.analyze(Fen.toFen(displayState), 2000, multiPv = 4) { res, err ->
            deepBusy = false
            // Cached under the analysed step even if the user has
            // stepped elsewhere meanwhile — nothing is thrown away.
            when {
                err != null -> deepErrors[atIdx] = err
                res != null -> deepCache[atIdx] = res
                else -> deepErrors[atIdx] = "引擎暂时没给出候选。"
            }
        }
    }

    LaunchedEffect(idx) {
        deepSelectedRank = null
        exitDeepTrial()
    }

    // Recorded evals as (moveCount, redCp) points for the chart.
    val trendPoints = remember(liveRecord.evals) {
        liveRecord.evals.entries.sortedBy { it.key }.map { it.key to it.value }
    }
    val swings = remember(trendPoints) { keySwings(trendPoints) }
    val swingMoves = remember(swings) { swings.map { it.first } }

    fun compareWithEngine() {
        if (compareBusy) return
        val actualUci = liveRecord.movesUci.getOrNull(idx) ?: run {
            compareText = "已经是最后一手了，没有下一步可对比。"
            return
        }
        compareBusy = true
        compareText = "引擎正在看这一手……"
        engine.analyze(Fen.toFen(displayState), 600, multiPv = 1) { res, err ->
            compareBusy = false
            val best = res?.candidates?.firstOrNull()
            compareText = when {
                err != null -> "引擎没能完成分析：$err"
                best == null -> "引擎暂时没给出候选。"
                else -> {
                    val actualText = notations.getOrNull(idx) ?: actualUci
                    if (best.moveUci == actualUci) {
                        "实战的「$actualText」正是引擎一选，眼光很准。"
                    } else {
                        val scratch = GameState()
                        val snap = snapshots[idx]
                        scratch.newGame(snap.first, snap.second)
                        val ft = Fen.parseUciMove(best.moveUci)
                        val bestText = if (ft != null) {
                            Notation.moveText(scratch.cells, ft.first, ft.second)
                        } else {
                            best.moveUci
                        }
                        val (redCp, redMate) = EvalUtil.toRedPerspective(
                            best.scoreCp, best.mateIn, displayState.turn
                        )
                        "实战走了「$actualText」，引擎一选是「$bestText」：" +
                            EvalUtil.describeRed(redCp, redMate)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(pageScroll)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = liveRecord.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = liveRecord.modeLabel + " · " + liveRecord.resultText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        // Share this game straight from the review page (1.20.0):
        // the portable text carries the whole-game analysis
        // marks too once they have been computed and saved.
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                clipboard.setText(AnnotatedString(liveRecord.toPortableText()))
                Toast.makeText(context, "棋谱文本已复制", Toast.LENGTH_SHORT).show()
            }) { Text("复制这局") }
            OutlinedButton(onClick = {
                shareText(context, liveRecord.toPortableText(), liveRecord.title)
            }) { Text("分享这局") }
            OutlinedButton(onClick = {
                try {
                    val full = rootView.drawToBitmap()
                    val x = boardPos.x.toInt().coerceIn(0, full.width - 1)
                    val y = boardPos.y.toInt().coerceIn(0, full.height - 1)
                    val w = boardSize.width.coerceAtMost(full.width - x)
                    val h = boardSize.height.coerceAtMost(full.height - y)
                    val cropped = android.graphics.Bitmap.createBitmap(full, x, y, w, h)
                    shareBitmap(context, cropped, "将！复盘局面图")
                } catch (_: Exception) {
                    Toast.makeText(context, "局面图生成失败，请再试一次", Toast.LENGTH_SHORT).show()
                }
            }) { Text("分享棋盘图") }
        }
        Spacer(Modifier.height(12.dp))

        val deepNow = deepCache[idx]
        val trialCand = deepNow?.candidates?.firstOrNull { it.rank == deepTrialRank }
        val trialPv = trialCand?.pvUci?.take(12) ?: emptyList()
        val trialNextHint = if (trialCand != null && deepTrialStep < trialPv.size) {
            Fen.parseUciMove(trialPv[deepTrialStep])
        } else {
            null
        }
        val selectedCand = deepNow?.candidates?.firstOrNull {
            it.rank == deepSelectedRank
        } ?: deepNow?.candidates?.firstOrNull()

        Box(
            modifier = Modifier.onGloballyPositioned { coords ->
                boardPos = coords.positionInWindow()
                boardSize = coords.size
            }
        ) {
            BoardFrame(modifier = Modifier.fillMaxWidth()) {
                XiangqiBoard(
                    state = if (trialCand != null) deepTrialState else displayState,
                    selected = null,
                    legalTargets = emptyList(),
                    hint = if (trialCand != null) {
                        trialNextHint
                    } else {
                        selectedCand?.let { Fen.parseUciMove(it.moveUci) }
                    },
                    onTap = {}
                )
            }
        }

        if (trialCand != null) {
            Spacer(Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "试走这条变化：第 $deepTrialStep/${trialPv.size} 步",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = trialNextHint?.let {
                            "下一步：" + Notation.moveText(
                                deepTrialState.cells, it.first, it.second
                            )
                        } ?: "这条变化已经走完。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                applyDeepTrial(
                                    trialCand, deepTrialStep - 1,
                                    displayState.cells.toList(), displayState.turn
                                )
                            },
                            enabled = deepTrialStep > 0
                        ) { Text("上一步") }
                        OutlinedButton(
                            onClick = {
                                applyDeepTrial(
                                    trialCand, deepTrialStep + 1,
                                    displayState.cells.toList(), displayState.turn
                                )
                            },
                            enabled = deepTrialStep < trialPv.size
                        ) { Text("下一步") }
                        TextButton(onClick = { exitDeepTrial() }) { Text("退出试走") }
                    }
                    Text(
                        text = "试走只是把引擎的变化摆出来看，不会改动棋谱；切换步数会自动回到复盘局面。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        if (trialCand != null) {
            CapturedStrip(snapshots[idx].first, deepTrialState.cells.toList())
        } else {
            CapturedStrip(snapshots[0].first, snapshots[idx].first)
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val moveLabel = when {
                    idx == 0 -> "初始局面"
                    notations.getOrNull(idx - 1) != null ->
                        "第 $idx 手：" + notations[idx - 1]
                    else -> "第 $idx 手"
                }
                Text(
                    text = moveLabel + "（共 $total 手）",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val evalHere = liveRecord.evals[idx]
                if (evalHere != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "这一步的形势：" + EvalUtil.describeRed(evalHere, null),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Whole-game analysis verdict for the move that
                // produced this position.
                val markHere = if (idx >= 1) liveRecord.marks[idx] else null
                if (markHere != null) {
                    val loss = EvalUtil.moveLossCp(
                        liveRecord.evals, snapshots[0].second, idx
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "上一手被标为" +
                            (if (markHere == "??") "失误手（??）" else "疑问手（?）") +
                            (loss?.let {
                                "：形势掉了约合 " +
                                    String.format("%.1f", it / 100.0) + " 个兵"
                            } ?: ""),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(Modifier.height(10.dp))
                if (maxIdx > 0) {
                    Text(
                        text = "拖动跳到任意一手",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = idx.toFloat(),
                        onValueChange = { idx = it.toInt() },
                        valueRange = 0f..maxIdx.toFloat(),
                        steps = (maxIdx - 1).coerceAtLeast(0)
                    )
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { idx = 0 }) { Text("开头") }
                    HoldButton("后退", enabled = idx > 0) { idx = (idx - 1).coerceAtLeast(0) }
                    HoldButton("前进", enabled = idx < maxIdx) { idx = (idx + 1).coerceAtMost(maxIdx) }
                    OutlinedButton(onClick = { idx = maxIdx }) { Text("结尾") }
                    OutlinedButton(onClick = { playing = !playing }) {
                        Text(if (playing) "暂停播放" else "自动播放")
                    }
                }
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { compareWithEngine() },
                        enabled = !compareBusy
                    ) { Text("和引擎一选对比") }
                    if (swingMoves.isNotEmpty()) {
                        OutlinedButton(onClick = {
                            val next = swingMoves.firstOrNull { it > idx }
                                ?: swingMoves.first()
                            idx = next.coerceAtMost(maxIdx)
                        }) { Text("下一关键手") }
                    }
                }
                if (compareText != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = compareText!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { startBatchAnalysis() },
                        enabled = !batchRunning && !deepBusy && maxIdx > 0
                    ) { Text("整局分析") }
                    if (batchRunning) {
                        OutlinedButton(onClick = { batchCancel.set(true) }) {
                            Text("取消分析")
                        }
                    }
                    if (liveRecord.marks.isNotEmpty()) {
                        OutlinedButton(onClick = {
                            val marked = liveRecord.marks.keys.sorted()
                            val next = marked.firstOrNull { it > idx }
                                ?: marked.first()
                            idx = next.coerceAtMost(maxIdx)
                        }) { Text("下一标记手") }
                    }
                }
                if (batchRunning) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (maxIdx > 0) batchDone.toFloat() / (maxIdx + 1)
                            else 0f
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "正在分析第 $batchDone / ${maxIdx + 1} 个局面。" +
                            "每步约 0.3 秒，整局越长越久，手机会微微发热，可随时取消，进度不会白费。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (batchNote != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = batchNote!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { startDeepAnalysis() },
                        enabled = !deepBusy && !batchRunning
                    ) { Text("深度分析这一步") }
                }
                if (deepBusy && deepIdx == idx) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "引擎正在深算这一步（约 2 秒，看前四个候选）……",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val deepError = deepErrors[idx]
                if (deepError != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "深析没能完成：$deepError",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                val deep = deepCache[idx]
                if (deep != null) {
                    Spacer(Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (idx == 0) {
                                    "初始局面深析（引擎看了约 2 秒）"
                                } else {
                                    "第 $idx 步局面深析（引擎看了约 2 秒）"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "点任意候选，棋盘箭头会换成那一步；" +
                                    "点候选卡里的「试走这条线」，可以一步步摆出后续变化。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme
                                    .colorScheme.onSurfaceVariant
                            )
                            for (cand in deep.candidates) {
                                val mv = Fen.parseUciMove(cand.moveUci)
                                val title = if (mv != null) {
                                    Notation.moveText(
                                        displayState.cells, mv.first, mv.second
                                    )
                                } else {
                                    cand.moveUci
                                }
                                val (redCp, redMate) = EvalUtil.toRedPerspective(
                                    cand.scoreCp, cand.mateIn, displayState.turn
                                )
                                val pv = Notation.pvText(
                                    displayState.cells, cand.pvUci, 5
                                ).joinToString(" ")
                                Spacer(Modifier.height(6.dp))
                                Card(
                                    onClick = {
                                        if (mv != null) {
                                            deepSelectedRank = cand.rank
                                        }
                                    },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (
                                            cand.rank == (deepSelectedRank ?: 1)
                                        ) {
                                            MaterialTheme.colorScheme
                                                .primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme
                                                .surfaceContainerHighest
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "第 ${cand.rank} 选：$title",
                                            style = MaterialTheme
                                                .typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = EvalUtil.describeRed(
                                                redCp, redMate
                                            ),
                                            style = MaterialTheme
                                                .typography.bodyMedium,
                                            color = MaterialTheme
                                                .colorScheme.onSurfaceVariant
                                        )
                                        if (pv.isNotBlank()) {
                                            Text(
                                                text = "变化：$pv",
                                                style = MaterialTheme
                                                    .typography.bodySmall,
                                                color = MaterialTheme
                                                    .colorScheme.onSurfaceVariant
                                            )
                                        }
                                        FlowRow(
                                            horizontalArrangement =
                                                Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    startDeepTrial(
                                                        cand,
                                                        displayState.cells.toList(),
                                                        displayState.turn
                                                    )
                                                }
                                            ) { Text("试走这条线") }
                                        }
                                    }
                                }
                            }
                            val actualUci = liveRecord.movesUci.getOrNull(idx)
                            if (actualUci != null) {
                                val actualText = notations.getOrNull(idx)
                                    ?: actualUci
                                val rank = EvalUtil.rankOfMove(
                                    deep.candidates.map { it.moveUci }, actualUci
                                )
                                val verdict = when {
                                    rank == 1 ->
                                        "实战的「$actualText」正是深析一选。"
                                    rank != null -> {
                                        val bestCand = deep.candidates.first()
                                        val playedCand = deep.candidates[rank - 1]
                                        val loss = EvalUtil.lossVsBestCp(
                                            bestCand.scoreCp, bestCand.mateIn,
                                            playedCand.scoreCp, playedCand.mateIn
                                        )
                                        "实战的「$actualText」是深析第 $rank 选" +
                                            (loss?.let {
                                                "，比一选差约合 " +
                                                    String.format("%.1f", it / 100.0) +
                                                    " 个兵。"
                                            } ?: "。")
                                    }
                                    else -> {
                                        val storedLoss = EvalUtil.moveLossCp(
                                            liveRecord.evals, snapshots[0].second,
                                            idx + 1
                                        )
                                        "实战的「$actualText」没进深析前四" +
                                            (storedLoss?.let {
                                                "；按整局分析的评分，这手掉了约合 " +
                                                    String.format("%.1f", it / 100.0) +
                                                    " 个兵。"
                                            } ?: "。")
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = verdict,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "深析结果只在这页临时看，不会存进棋谱。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (trendPoints.size > 1) {
            Spacer(Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "全场走势",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    EvalTrendChart(
                        points = trendPoints,
                        modifier = Modifier.fillMaxWidth().height(72.dp),
                        highlightMove = idx
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "图上的圈是你现在看的这一步，拖动上面的滑杆或步进，圈会跟着走。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (swings.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "关键手在第 " + swings.joinToString("、") +
                                " 手（形势摆动最大），点「下一关键手」逐个看。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "全部着法",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                if (notations.isEmpty()) {
                    Text(
                        text = "（无着法记录）",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    // Every move is a tappable chip (1.17.0):
                    // tapping jumps the board straight to the
                    // position after that move and scrolls back
                    // up to the board. The chip for the move
                    // that produced the current position stays
                    // selected; batch-analysis marks ride along
                    // in the label, as the old text table did.
                    Text(
                        text = "点任意一手，直接跳到那一步。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in notations.indices) {
                            val n = notations[i]
                            if (i % 2 == 0) {
                                Text(
                                    text = "${i / 2 + 1}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme
                                        .colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                            FilterChip(
                                selected = idx == i + 1,
                                onClick = {
                                    playing = false
                                    idx = i + 1
                                    scope.launch {
                                        pageScroll.animateScrollTo(0)
                                    }
                                },
                                label = {
                                    Text(n + (liveRecord.marks[i + 1] ?: ""))
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        OutlinedButton(onClick = onBack) { Text("返回棋谱库") }
        Spacer(Modifier.height(24.dp))
    }
}

/** Button that steps once per tap and keeps stepping while held
 *  (the repeat job is cancelled as soon as the press is released). */
@Composable
private fun HoldButton(text: String, enabled: Boolean, onStep: () -> Unit) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    androidx.compose.material3.Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        color = if (enabled) colors.primary else colors.surfaceVariant,
        contentColor = if (enabled) colors.onPrimary else colors.onSurfaceVariant,
        modifier = Modifier.pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onPress = {
                    onStep()
                    val job = scope.launch {
                        delay(420)
                        while (true) {
                            onStep()
                            delay(130)
                        }
                    }
                    tryAwaitRelease()
                    job.cancel()
                }
            )
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}
