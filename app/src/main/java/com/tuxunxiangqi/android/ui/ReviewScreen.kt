package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.GameRecord
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
    val engine = remember { PikafishEngine(context) }
    DisposableEffect(engine) { onDispose { engine.close() } }
    LaunchedEffect(Unit) { engine.start({}, {}) }
    val scope = rememberCoroutineScope()

    val total = record.movesUci.size

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
    var compareText by remember { mutableStateOf<String?>(null) }
    var compareBusy by remember { mutableStateOf(false) }

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

    // Recorded evals as (moveCount, redCp) points for the chart.
    val trendPoints = remember(record.id) {
        record.evals.entries.sortedBy { it.key }.map { it.key to it.value }
    }
    val swings = remember(trendPoints) { keySwings(trendPoints) }
    val swingMoves = remember(swings) { swings.map { it.first } }

    fun compareWithEngine() {
        if (compareBusy) return
        val actualUci = record.movesUci.getOrNull(idx) ?: run {
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = record.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = record.modeLabel + " · " + record.resultText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        BoardFrame(modifier = Modifier.fillMaxWidth()) {
            XiangqiBoard(
                state = displayState,
                selected = null,
                legalTargets = emptyList(),
                hint = null,
                onTap = {}
            )
        }

        Spacer(Modifier.height(12.dp))
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
                val evalHere = record.evals[idx]
                if (evalHere != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "这一步的形势：" + EvalUtil.describeRed(evalHere, null),
                        style = MaterialTheme.typography.bodyMedium
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
                        modifier = Modifier.fillMaxWidth().height(72.dp)
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
                val text = buildString {
                    notations.forEachIndexed { i, n ->
                        if (i % 2 == 0) {
                            if (i > 0) append('\n')
                            append(i / 2 + 1).append(". ").append(n)
                        } else {
                            append("  ").append(n)
                        }
                    }
                }
                Text(
                    text = if (text.isBlank()) "（无着法记录）" else text,
                    style = MaterialTheme.typography.bodyMedium
                )
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
