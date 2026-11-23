package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.SettingsStore
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.engine.Candidate
import com.tuxunxiangqi.android.engine.PikafishEngine
import com.tuxunxiangqi.android.engine.SoundFx
import com.tuxunxiangqi.android.engine.SoundKind
import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import kotlinx.coroutines.delay

enum class BoardMode { AI, PVP, SPECTACLE }

private enum class Level(val label: String, val hint: String, val movetimeMs: Int, val depthCap: Int?) {
    CASUAL("休闲", "每步约 0.3 秒，随便玩玩", 300, 8),
    NORMAL("普通", "每步约 1 秒，棋力已经很强", 1000, null),
    EXPERT("高手", "每步约 3 秒，全力思考", 3000, null)
}

private enum class Handicap(val label: String) {
    NONE("不让子"), ONE_HORSE("让一马"), TWO_HORSES("让双马"),
    ONE_CHARIOT("让一车"), BIG("让车马炮")
}

private enum class SpecSpeed(val label: String, val movetimeMs: Int) {
    SLOW("慢速", 2000), MID("中速", 900), FAST("快速", 350)
}

private data class CandidateUi(
    val rank: Int,
    val from: Pos,
    val to: Pos,
    val title: String,
    val scoreText: String,
    val pvText: String,
    val redWinPercent: Double
)

/** Removes the handicap pieces from [side] on a fresh start layout. */
private fun applyHandicap(cells: List<Piece?>, side: Side, handicap: Handicap): List<Piece?> {
    if (handicap == Handicap.NONE) return cells
    val out = cells.toMutableList()
    val backRow = if (side == Side.BLACK) 0 else 9
    val cannonRow = if (side == Side.BLACK) 2 else 7
    fun clear(col: Int, row: Int, type: PieceType) {
        val p = out[row * 9 + col]
        if (p != null && p.side == side && p.type == type) out[row * 9 + col] = null
    }
    when (handicap) {
        Handicap.ONE_HORSE -> clear(1, backRow, PieceType.HORSE)
        Handicap.TWO_HORSES -> {
            clear(1, backRow, PieceType.HORSE)
            clear(7, backRow, PieceType.HORSE)
        }
        Handicap.ONE_CHARIOT -> clear(0, backRow, PieceType.CHARIOT)
        Handicap.BIG -> {
            clear(0, backRow, PieceType.CHARIOT)
            clear(1, backRow, PieceType.HORSE)
            clear(1, cannonRow, PieceType.CANNON)
        }
        Handicap.NONE -> {}
    }
    return out
}

private fun fmtClock(ms: Long): String {
    val totalSec = (ms.coerceAtLeast(0) + 999) / 1000
    return String.format("%d:%02d", totalSec / 60, totalSec % 60)
}

/** One captured piece as a small disc in the captured strip. */
@Composable
private fun MiniPieceDisc(piece: Piece) {
    val ink = if (piece.side == Side.RED) Color(0xFFB33A1F) else Color(0xFF2A241C)
    Box(
        modifier = Modifier
            .size(23.dp)
            .clip(CircleShape)
            .background(Color(0xFFF3E3BC))
            .border(1.dp, ink, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = piece.type.label(piece.side),
            fontFamily = PieceFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = ink
        )
    }
}

/** Captured-pieces strip under the board, the way xiangqi apps
 *  lay out the taken pieces: one row per side showing what that
 *  side has captured (strongest first), with the material edge
 *  marked on the side that is ahead. Renders nothing until the
 *  first capture. Derived from GameState, so undo and restart
 *  keep it truthful on their own. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CapturedStrip(state: GameState) {
    val redCaps = state.capturedBy(Side.RED)
    val blackCaps = state.capturedBy(Side.BLACK)
    if (redCaps.isEmpty() && blackCaps.isEmpty()) return
    val diff = state.materialScore(Side.RED) - state.materialScore(Side.BLACK)
    fun edgeText(points: Int): String {
        val pawns = points / 60.0
        val num = if (pawns == pawns.toInt().toDouble()) {
            pawns.toInt().toString()
        } else {
            String.format("%.1f", pawns)
        }
        return "子力占优 ≈ $num 兵"
    }
    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CapturedRow("红方所吃", redCaps, if (diff > 0) edgeText(diff) else null)
                CapturedRow("黑方所吃", blackCaps, if (diff < 0) edgeText(-diff) else null)
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CapturedRow(label: String, caps: List<Piece>, edge: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )
        if (caps.isEmpty()) {
            Text(
                text = "—",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                for (p in caps) MiniPieceDisc(p)
            }
        }
        if (edge != null) {
            Text(
                text = edge,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BoardScreen(
    mode: BoardMode,
    onBack: () -> Unit,
    startCells: List<Piece?>? = null,
    onReviewRecord: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val state = remember {
        GameState().apply {
            if (startCells != null) newGame(startCells, Side.RED)
        }
    }
    val engine = remember { PikafishEngine(context) }
    DisposableEffect(engine) {
        onDispose { engine.close() }
    }
    val recordStore = remember { RecordStore(context) }
    var savedRecordId by remember { mutableStateOf<Long?>(null) }
    var finalSaved by remember { mutableStateOf(false) }
    val rootView = LocalView.current
    var boardPos by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var boardSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    var selected by remember { mutableStateOf<Pos?>(null) }
    // Setup.
    var setupDone by remember { mutableStateOf(mode == BoardMode.PVP) }
    var humanSide by remember { mutableStateOf(Side.RED) }
    val settingsStore = remember { SettingsStore(context) }
    var level by remember {
        mutableStateOf(
            when (settingsStore.defaultLevelLabel) {
                "休闲" -> Level.CASUAL
                "高手" -> Level.EXPERT
                else -> Level.NORMAL
            }
        )
    }
    var handicap by remember { mutableStateOf(Handicap.NONE) }
    var specSpeed by remember { mutableStateOf(SpecSpeed.MID) }
    var spectaclePaused by remember { mutableStateOf(false) }
    // Clock.
    var clockMinutes by remember { mutableStateOf(settingsStore.defaultClockMinutes) }
    var redMs by remember { mutableStateOf(0L) }
    var blackMs by remember { mutableStateOf(0L) }
    var clockPaused by remember { mutableStateOf(false) }
    // View.
    var flipped by remember { mutableStateOf(false) }
    var showCoords by remember { mutableStateOf(false) }
    var focusMode by remember { mutableStateOf(false) }
    // Engine / analysis.
    var thinking by remember { mutableStateOf(false) }
    var engineError by remember { mutableStateOf<String?>(null) }
    var analysisBusy by remember { mutableStateOf(false) }
    var candidates by remember { mutableStateOf<List<CandidateUi>>(emptyList()) }
    var selectedRank by remember { mutableStateOf(1) }
    var hintMove by remember { mutableStateOf<Pair<Pos, Pos>?>(null) }
    var autoAnalyze by remember { mutableStateOf(false) }
    var analysisMs by remember { mutableStateOf(settingsStore.defaultAnalysisMs.toFloat()) }
    var evalPoints by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var lastRedCp by remember { mutableStateOf<Int?>(null) }
    var showTrendDialog by remember { mutableStateOf(false) }
    // Dialogs / messages.
    var showResignDialog by remember { mutableStateOf(false) }
    var showDrawDialog by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<String?>(null) }

    val busy = thinking || analysisBusy
    val humanTurn = when (mode) {
        BoardMode.PVP -> true
        BoardMode.AI -> state.turn == humanSide
        BoardMode.SPECTACLE -> false
    }
    val targets = remember(selected, state.lastMove, state.moveCount) {
        selected?.let { Rules.legalTargets(state.cells, it) } ?: emptyList()
    }
    // Safety judgement for the selected piece's targets (green =
    // survives, red = 送死， green ring = safe capture, no marker
    // = recaptured capture). Pure rules-layer arithmetic over a
    // 90-cell board — computed synchronously in this remember so
    // the markers are on screen in the same frame as the tap;
    // it must never go through the engine (a UCI round trip
    // would be felt). Cost is pinned by TargetSafetyTest.
    val safetyHintsOn = remember { settingsStore.safetyHintsOn }
    val targetSafety = remember(selected, state.lastMove, state.moveCount, safetyHintsOn) {
        if (safetyHintsOn && selected != null) {
            Rules.targetSafeties(state.cells, selected!!)
        } else {
            emptyMap()
        }
    }
    val extraHints = candidates
        .filter { it.rank != selectedRank }
        .map { it.from to it.to }

    fun addEvalPoint(moveCount: Int, redCp: Int) {
        val cleaned = evalPoints.filter { it.first != moveCount }
        evalPoints = (cleaned + (moveCount to redCp)).sortedBy { it.first }
        lastRedCp = redCp
    }

    fun clearAnalysis() {
        candidates = emptyList()
        hintMove = null
        selectedRank = 1
    }

    fun currentRecord(final: Boolean): GameRecord {
        val res = state.result
        val resultText = when {
            res != null -> (when (res.winner) {
                Side.RED -> "红方胜"
                Side.BLACK -> "黑方胜"
                null -> "和棋"
            }) + " · " + res.reason
            final -> "对局结束"
            else -> "对局进行中"
        }
        val modeLabel = when (mode) {
            BoardMode.AI -> "人机对弈 · " + level.label +
                " · " + (if (humanSide == Side.RED) "执红" else "执黑") +
                (if (handicap != Handicap.NONE) " · " + handicap.label else "")
            BoardMode.PVP -> "人人对弈"
            BoardMode.SPECTACLE -> "AI 表演赛"
        }
        val fmt = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.CHINA)
        return GameRecord(
            id = savedRecordId ?: System.currentTimeMillis(),
            title = fmt.format(java.util.Date()) + " " + modeLabel,
            dateMs = System.currentTimeMillis(),
            modeLabel = modeLabel,
            resultText = resultText,
            initialFen = Fen.toFen(state.initialCells, state.initialTurn),
            movesUci = state.historyMoves().map { Fen.toUci(it.from, it.to) },
            evals = evalPoints.toMap()
        )
    }

    fun saveRecord(final: Boolean) {
        if (state.moveCount == 0) {
            notice = "还没有走子，先下一会儿再存吧。"
            return
        }
        val rec = currentRecord(final)
        savedRecordId = rec.id
        recordStore.save(rec)
        notice = "棋谱已保存，去首页「棋谱」里就能找到。"
    }

    // Finished games save themselves.
    LaunchedEffect(state.result) {
        if (state.result != null && state.moveCount > 0 && !finalSaved) {
            finalSaved = true
            saveRecord(final = true)
        }
    }

    // Sound effects: only when the move count actually grows, so
    // undo and restart stay silent. Check outranks capture.
    var lastSoundCount by remember { mutableStateOf(0) }
    LaunchedEffect(state.lastMove) {
        val mv = state.lastMove
        if (mv != null && state.moveCount > lastSoundCount && state.result == null) {
            SoundFx.play(
                context,
                when {
                    state.inCheck -> SoundKind.CHECK
                    mv.captured != null -> SoundKind.CAPTURE
                    else -> SoundKind.MOVE
                }
            )
        }
        lastSoundCount = state.moveCount
    }
    LaunchedEffect(state.result) {
        val res = state.result ?: return@LaunchedEffect
        when {
            res.winner == null -> SoundFx.play(context, SoundKind.DRAW)
            mode == BoardMode.AI ->
                SoundFx.play(
                    context,
                    if (res.winner == humanSide) SoundKind.WIN else SoundKind.LOSE
                )
            else -> SoundFx.play(context, SoundKind.WIN)
        }
    }

    fun engineMove() {
        if (!setupDone || state.result != null || thinking) return
        val isEngineTurn = when (mode) {
            BoardMode.AI -> state.turn != humanSide
            BoardMode.SPECTACLE -> !spectaclePaused
            BoardMode.PVP -> false
        }
        if (!isEngineTurn) return
        thinking = true
        engineError = null
        val fen = Fen.toFen(state)
        val engineSide = state.turn
        val movetime = when (mode) {
            BoardMode.SPECTACLE -> specSpeed.movetimeMs
            else -> level.movetimeMs
        }
        val cap = if (mode == BoardMode.AI) level.depthCap else null
        engine.compute(fen, movetime, cap) { res, err ->
            thinking = false
            when {
                err != null -> engineError = err
                res != null -> {
                    val mv = Fen.parseUciMove(res.bestMoveUci)
                    if (mv == null || !state.tryMove(mv.first, mv.second)) {
                        engineError = "引擎给的着法对不上棋盘，请重开一局。"
                    } else {
                        selected = null
                        clearAnalysis()
                        val (redCp, redMate) = EvalUtil.toRedPerspective(
                            res.scoreCp, res.mateIn, engineSide
                        )
                        addEvalPoint(
                            state.moveCount,
                            redMate?.let { if (it > 0) 1500 else -1500 }
                                ?: (redCp ?: 0)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(state.lastMove, setupDone, spectaclePaused) {
        engineMove()
    }

    fun runAnalysis() {
        if (busy || state.result != null || !setupDone) return
        analysisBusy = true
        engineError = null
        notice = null
        val fen = Fen.toFen(state)
        val sideToMove = state.turn
        val cellsSnapshot = state.cells.toList()
        val atMove = state.moveCount
        engine.analyze(fen, analysisMs.toInt(), multiPv = 4) { res, err ->
            analysisBusy = false
            if (state.moveCount != atMove) {
                // Position moved on; stale candidates would mislead.
                return@analyze
            }
            when {
                err != null -> engineError = err
                res != null -> {
                    val built = ArrayList<CandidateUi>()
                    for (cand: Candidate in res.candidates) {
                        val mv = Fen.parseUciMove(cand.moveUci) ?: continue
                        val (redCp, redMate) = EvalUtil.toRedPerspective(
                            cand.scoreCp, cand.mateIn, sideToMove
                        )
                        built.add(
                            CandidateUi(
                                rank = cand.rank,
                                from = mv.first,
                                to = mv.second,
                                title = Notation.moveText(cellsSnapshot, mv.first, mv.second),
                                scoreText = EvalUtil.describeRed(redCp, redMate),
                                pvText = Notation.pvText(cellsSnapshot, cand.pvUci, 5)
                                    .joinToString(" "),
                                redWinPercent = EvalUtil.winRatePercent(redCp, redMate)
                            )
                        )
                    }
                    candidates = built
                    selectedRank = 1
                    hintMove = built.firstOrNull()?.let { it.from to it.to }
                    val best = res.candidates.firstOrNull()
                    if (best != null) {
                        val (redCp, redMate) = EvalUtil.toRedPerspective(
                            best.scoreCp, best.mateIn, sideToMove
                        )
                        addEvalPoint(
                            atMove,
                            redMate?.let { if (it > 0) 1500 else -1500 }
                                ?: (redCp ?: 0)
                        )
                    }
                }
            }
        }
    }

    // Continuous analysis: follow every move automatically, but
    // never fight the engine for its own turn.
    LaunchedEffect(state.lastMove, autoAnalyze) {
        if (autoAnalyze && setupDone && state.result == null && !thinking &&
            (mode == BoardMode.PVP || (mode == BoardMode.AI && state.turn == humanSide))
        ) {
            runAnalysis()
        }
    }

    // Chess clock: counts down the side to move; timeout loses.
    LaunchedEffect(clockMinutes, state.turn, clockPaused, state.result, setupDone) {
        if (clockMinutes <= 0 || !setupDone || state.result != null || clockPaused) {
            return@LaunchedEffect
        }
        var last = System.currentTimeMillis()
        while (true) {
            delay(200)
            val now = System.currentTimeMillis()
            val elapsed = now - last
            last = now
            if (state.result != null) break
            if (state.turn == Side.RED) {
                redMs -= elapsed
                if (redMs <= 0) {
                    redMs = 0
                    state.forceResult(Side.BLACK, "红方超时")
                    break
                }
            } else {
                blackMs -= elapsed
                if (blackMs <= 0) {
                    blackMs = 0
                    state.forceResult(Side.RED, "黑方超时")
                    break
                }
            }
        }
    }

    fun resetClocks() {
        redMs = clockMinutes * 60_000L
        blackMs = clockMinutes * 60_000L
    }

    fun restart() {
        if (busy) return
        savedRecordId = null
        finalSaved = false
        state.newGame(state.initialCells, state.initialTurn)
        selected = null
        clearAnalysis()
        evalPoints = emptyList()
        engineError = null
        notice = null
        resetClocks()
    }

    fun undo() {
        if (busy) return
        selected = null
        clearAnalysis()
        engineError = null
        if (mode == BoardMode.AI) {
            var guard = 0
            while (guard < 200) {
                if (!state.undo()) break
                guard++
                if (state.turn == humanSide || state.moveCount == 0) break
            }
        } else {
            state.undo()
            if (mode == BoardMode.SPECTACLE) state.undo()
        }
        evalPoints = evalPoints.filter { it.first <= state.moveCount }
    }

    fun onTap(pos: Pos) {
        if (state.result != null || busy || !humanTurn) return
        val piece = state.pieceAt(pos)
        val sel = selected
        when {
            sel == null -> {
                if (piece != null && piece.side == state.turn) selected = pos
            }
            pos == sel -> selected = null
            piece != null && piece.side == state.turn -> selected = pos
            else -> {
                if (state.tryMove(sel, pos)) {
                    selected = null
                    clearAnalysis()
                }
            }
        }
    }

    val title = when (mode) {
        BoardMode.AI -> "人机对弈"
        BoardMode.PVP -> "人人对弈"
        BoardMode.SPECTACLE -> "AI 表演赛"
    }

    Scaffold(
        topBar = {
            if (!focusMode) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Text("←", fontSize = 22.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (!setupDone) {
                when (mode) {
                    BoardMode.AI -> SetupAiSection(
                        humanSide = humanSide,
                        onSideChange = {
                            humanSide = it
                            flipped = it == Side.BLACK
                        },
                        level = level,
                        onLevelChange = { level = it },
                        handicap = handicap,
                        onHandicapChange = { handicap = it },
                        clockMinutes = clockMinutes,
                        onClockChange = { clockMinutes = it },
                        customStart = startCells != null,
                        onStart = {
                            if (startCells == null && handicap != Handicap.NONE) {
                                val base = GameState()
                                val engineSide = humanSide.other()
                                val modified = applyHandicap(base.cells, engineSide, handicap)
                                state.newGame(modified, Side.RED)
                            }
                            resetClocks()
                            setupDone = true
                            engine.start(onReady = {}, onError = { msg -> engineError = msg })
                        }
                    )
                    BoardMode.SPECTACLE -> SetupSpectacleSection(
                        specSpeed = specSpeed,
                        onSpeedChange = { specSpeed = it },
                        onStart = {
                            setupDone = true
                            engine.start(onReady = {}, onError = { msg -> engineError = msg })
                        }
                    )
                    BoardMode.PVP -> {}
                }
            } else if (focusMode) {
                Spacer(Modifier.height(8.dp))
                // Focus mode hides the normal status/banner area, so
                // show the result here too — otherwise a finished
                // game just looks like a frozen board.
                state.result?.let { res ->
                    Text(
                        text = (when (res.winner) {
                            Side.RED -> "红方胜"
                            Side.BLACK -> "黑方胜"
                            null -> "和棋"
                        }) + " · " + res.reason,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { focusMode = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("退出专注")
                    }
                    OutlinedButton(
                        onClick = { undo() },
                        enabled = state.lastMove != null && !busy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("悔棋")
                    }
                }
                Spacer(Modifier.height(12.dp))
                BoardFrame(modifier = Modifier.fillMaxWidth()) {
                    XiangqiBoard(
                        state = state,
                        selected = selected,
                        legalTargets = targets,
                        hint = hintMove,
                        extraHints = extraHints,
                        flipped = flipped,
                        showCoords = showCoords,
                        targetSafety = targetSafety,
                        onTap = { pos -> onTap(pos) }
                    )
                }
                Spacer(Modifier.height(16.dp))
            } else {
                // Status row.
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = when {
                            state.result != null -> when (state.result!!.winner) {
                                Side.RED -> "红方胜"
                                Side.BLACK -> "黑方胜"
                                null -> "和棋"
                            }
                            thinking -> "引擎思考中…"
                            mode == BoardMode.SPECTACLE && spectaclePaused -> "表演赛已暂停"
                            state.turn == Side.RED -> "红方走棋"
                            else -> "黑方走棋"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (state.result == null && state.inCheck) {
                        Text(
                            text = " · 将军！",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "第 ${state.moveCount + 1} 手",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (thinking || analysisBusy) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                }

                state.result?.let { res ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = (when (res.winner) {
                                Side.RED -> "红方胜"
                                Side.BLACK -> "黑方胜"
                                null -> "和棋"
                            }) + " · " + res.reason,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                engineError?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "引擎出了点问题：$msg",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                notice?.let { msg ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                if (clockMinutes > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ClockCard(
                            label = "红方",
                            ms = redMs,
                            active = state.turn == Side.RED && state.result == null,
                            modifier = Modifier.weight(1f)
                        )
                        ClockCard(
                            label = "黑方",
                            ms = blackMs,
                            active = state.turn == Side.BLACK && state.result == null,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = { clockPaused = !clockPaused }) {
                            Text(if (clockPaused) "继续" else "暂停")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Box(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        boardPos = coords.positionInWindow()
                        boardSize = coords.size
                    }
                ) {
                    BoardFrame(modifier = Modifier.fillMaxWidth()) {
                        XiangqiBoard(
                            state = state,
                            selected = selected,
                            legalTargets = targets,
                            hint = hintMove,
                            extraHints = extraHints,
                            flipped = flipped,
                            showCoords = showCoords,
                            targetSafety = targetSafety,
                            onTap = { pos -> onTap(pos) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                CapturedStrip(state)
                // View toggles.
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = flipped,
                        onClick = { flipped = !flipped },
                        label = { Text("翻转棋盘") }
                    )
                    FilterChip(
                        selected = showCoords,
                        onClick = { showCoords = !showCoords },
                        label = { Text("坐标") }
                    )
                    FilterChip(
                        selected = focusMode,
                        onClick = { focusMode = true },
                        label = { Text("专注模式") }
                    )
                    FilterChip(
                        selected = autoAnalyze,
                        onClick = { autoAnalyze = !autoAnalyze },
                        label = { Text("自动分析") }
                    )
                    if (mode == BoardMode.SPECTACLE) {
                        FilterChip(
                            selected = spectaclePaused,
                            onClick = { spectaclePaused = !spectaclePaused },
                            label = { Text(if (spectaclePaused) "已暂停" else "暂停表演") }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { undo() },
                        enabled = state.lastMove != null && !busy
                    ) {
                        Text("悔棋")
                    }
                    Button(
                        onClick = { runAnalysis() },
                        enabled = state.result == null && !busy
                    ) {
                        Text("分析")
                    }
                    OutlinedButton(onClick = { restart() }) {
                        Text("重开")
                    }
                    OutlinedButton(
                        onClick = { saveRecord(final = state.result != null) },
                        enabled = !busy
                    ) {
                        Text("存谱")
                    }
                    if (state.result != null && savedRecordId != null) {
                        Button(onClick = { onReviewRecord(savedRecordId!!) }) {
                            Text("复盘这局")
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            try {
                                val full = rootView.drawToBitmap()
                                val x = boardPos.x.toInt().coerceIn(0, full.width - 1)
                                val y = boardPos.y.toInt().coerceIn(0, full.height - 1)
                                val w = boardSize.width.coerceAtMost(full.width - x)
                                val h = boardSize.height.coerceAtMost(full.height - y)
                                val cropped = android.graphics.Bitmap.createBitmap(full, x, y, w, h)
                                shareBitmap(context, cropped, "将！局面图")
                            } catch (_: Exception) {
                                notice = "局面图生成失败，请再试一次。"
                            }
                        }
                    ) {
                        Text("分享局面")
                    }
                    if (mode != BoardMode.SPECTACLE) {
                        OutlinedButton(
                            onClick = { showResignDialog = true },
                            enabled = state.result == null && !busy
                        ) {
                            Text("认输")
                        }
                        OutlinedButton(
                            onClick = { showDrawDialog = true },
                            enabled = state.result == null && !busy
                        ) {
                            Text("求和")
                        }
                    }
                }

                if (mode == BoardMode.PVP && state.moveCount == 0) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "棋钟（开局前选好）",
                        style = MaterialTheme.typography.titleSmall
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (m in listOf(0, 10, 20, 30)) {
                            FilterChip(
                                selected = clockMinutes == m,
                                onClick = {
                                    clockMinutes = m
                                    redMs = m * 60_000L
                                    blackMs = m * 60_000L
                                },
                                label = { Text(if (m == 0) "不限时" else "$m 分钟") }
                            )
                        }
                    }
                }

                // Analysis panel.
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "形势与候选",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        // Opening book: only for games from the
                        // standard start; suggestions set the arrow.
                        val bookMoves = remember(state.moveCount, state.initialCells) {
                            val standard = Fen.toFen(GameState())
                            if (Fen.toFen(state.initialCells, state.initialTurn) == standard) {
                                OpeningBook.lookup(
                                    state.historyMoves().map { Fen.toUci(it.from, it.to) }
                                )
                            } else {
                                emptyList()
                            }
                        }
                        if (bookMoves.isNotEmpty()) {
                            Text(
                                text = "开局库（定式走法，点一下在棋盘上标出来）：",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            for ((uci, name) in bookMoves) {
                                TextButton(
                                    onClick = {
                                        hintMove = Fen.parseUciMove(uci)
                                    }
                                ) {
                                    Text(name)
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                        }
                        val best = candidates.firstOrNull()
                        if (best == null) {
                            Text(
                                text = "点「分析」，引擎会给出前四个候选着法、各自的形势判断和后续变化。" +
                                    "打开「自动分析」后，每走一步都会自动跟算。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "红方胜率约 ${best.redWinPercent.toInt()}% · ${best.scoreText}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (best.redWinPercent / 100.0).toFloat() },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(10.dp))
                            for (cand in candidates) {
                                CandidateRow(
                                    cand = cand,
                                    chosen = cand.rank == selectedRank,
                                    onClick = {
                                        selectedRank = cand.rank
                                        hintMove = cand.from to cand.to
                                    }
                                )
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "分析思考时长：${(analysisMs / 1000f).let { String.format("%.1f", it) }} 秒",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = analysisMs,
                            onValueChange = { analysisMs = it },
                            valueRange = 200f..4000f,
                            steps = 18
                        )
                    }
                }

                // Trend chart.
                Spacer(Modifier.height(12.dp))
                Card(
                    onClick = { if (evalPoints.size >= 2) showTrendDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "局内走势（红方分数）",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        if (evalPoints.size < 2) {
                            Text(
                                text = "对局中引擎每算一次就会记一个点，连成线就能看出形势在哪几手翻盘。点这里看大图。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            EvalTrendChart(points = evalPoints)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "点选棋子，再点高亮处落子。规则已校验：蹩马腿、塞象眼、炮须隔子吃、" +
                        "九宫与过河限制、将帅照面；不能送将，被将军时必须应将。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showResignDialog) {
        AlertDialog(
            onDismissRequest = { showResignDialog = false },
            title = { Text("认输？") },
            text = { Text("认输后这一局就结束了，确定吗？") },
            confirmButton = {
                TextButton(onClick = {
                    showResignDialog = false
                    val loser = if (mode == BoardMode.AI) humanSide else state.turn
                    state.forceResult(loser.other(), "认输")
                }) {
                    Text("认输")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResignDialog = false }) {
                    Text("再想想")
                }
            }
        )
    }

    if (showDrawDialog) {
        if (mode == BoardMode.PVP) {
            AlertDialog(
                onDismissRequest = { showDrawDialog = false },
                title = { Text("提议和棋") },
                text = { Text("请把手机递给对方：同意和棋吗？") },
                confirmButton = {
                    TextButton(onClick = {
                        showDrawDialog = false
                        state.forceResult(null, "双方同意和棋")
                    }) {
                        Text("同意和棋")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDrawDialog = false }) {
                        Text("不同意，继续下")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showDrawDialog = false },
                title = { Text("向引擎求和？") },
                text = {
                    Text("引擎只在局势接近、或者它并不占优的时候才会同意和棋。")
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDrawDialog = false
                        val humanCp = lastRedCp?.let {
                            if (humanSide == Side.RED) it else -it
                        }
                        if (humanCp != null && humanCp >= -80) {
                            state.forceResult(null, "引擎同意和棋")
                        } else {
                            notice = "引擎拒绝了和棋：现在的局势对它更有利，或者还没有算清。"
                        }
                    }) {
                        Text("求和")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDrawDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }

    if (showTrendDialog) {
        AlertDialog(
            onDismissRequest = { showTrendDialog = false },
            title = { Text("局内走势大图") },
            text = {
                // Scrollable: chart + key-move list can exceed the
                // dialog height on small screens.
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    EvalTrendChart(points = evalPoints, big = true)
                    Spacer(Modifier.height(10.dp))
                    val swings = keySwings(evalPoints)
                    if (swings.isEmpty()) {
                        Text("还没有出现大起大落的手，形势变化比较平稳。")
                    } else {
                        Text("关键手（分数大起大落处）：", fontWeight = FontWeight.SemiBold)
                        for ((moveIdx, _, delta) in swings.take(5)) {
                            val pawns = String.format("%.1f", kotlin.math.abs(delta) / 100.0)
                            Text(
                                text = "第 $moveIdx 手后，形势变化约 $pawns 个兵" +
                                    (if (delta > 0) "（转向红方）" else "（转向黑方）")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrendDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }
}

@Composable
private fun ClockCard(label: String, ms: Long, active: Boolean, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (active) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = fmtClock(ms),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidateRow(cand: CandidateUi, chosen: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (chosen) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row {
                Text(
                    text = when (cand.rank) {
                        1 -> "一选"
                        2 -> "二选"
                        3 -> "三选"
                        else -> "四选"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = cand.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = cand.scoreText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (cand.pvText.isNotBlank()) {
                Text(
                    text = "变化：${cand.pvText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SetupAiSection(
    humanSide: Side,
    onSideChange: (Side) -> Unit,
    level: Level,
    onLevelChange: (Level) -> Unit,
    handicap: Handicap,
    onHandicapChange: (Handicap) -> Unit,
    clockMinutes: Int,
    onClockChange: (Int) -> Unit,
    customStart: Boolean,
    onStart: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "你执哪一方？", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = humanSide == Side.RED,
                onClick = { onSideChange(Side.RED) },
                label = { Text("执红先行") }
            )
            FilterChip(
                selected = humanSide == Side.BLACK,
                onClick = { onSideChange(Side.BLACK) },
                label = { Text("执黑后行（让先）") }
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(text = "引擎档位", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (lv in Level.entries) {
                FilterChip(
                    selected = level == lv,
                    onClick = { onLevelChange(lv) },
                    label = { Text(lv.label) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = level.hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!customStart) {
            Spacer(Modifier.height(20.dp))
            Text(text = "让子（引擎让）", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (h in Handicap.entries) {
                    FilterChip(
                        selected = handicap == h,
                        onClick = { onHandicapChange(h) },
                        label = { Text(h.label) }
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(text = "棋钟", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (m in listOf(0, 10, 20, 30)) {
                FilterChip(
                    selected = clockMinutes == m,
                    onClick = { onClockChange(m) },
                    label = { Text(if (m == 0) "不限时" else "$m 分钟") }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("开始对弈")
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "引擎是内置的 Pikafish，全部在手机本地计算，不联网。" +
                "第一次启动要先把棋力文件准备好，可能多等几秒。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SetupSpectacleSection(
    specSpeed: SpecSpeed,
    onSpeedChange: (SpecSpeed) -> Unit,
    onStart: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "两台引擎自己对弈，你在旁边看。可以随时暂停、调速、悔棋看前面。",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(20.dp))
        Text(text = "落子速度", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (sp in SpecSpeed.entries) {
                FilterChip(
                    selected = specSpeed == sp,
                    onClick = { onSpeedChange(sp) },
                    label = { Text(sp.label) }
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text("开始表演赛")
        }
    }
}
