package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxunxiangqi.android.engine.EngineResult
import com.tuxunxiangqi.android.engine.PikafishEngine
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side

enum class BoardMode { AI, PVP }

/** Difficulty levels: thinking time per move, plus a depth cap on
 *  the casual level so it stays genuinely casual. */
private enum class Level(val label: String, val hint: String, val movetimeMs: Int, val depthCap: Int?) {
    CASUAL("休闲", "每步约 0.3 秒，随便玩玩", 300, 8),
    NORMAL("普通", "每步约 1 秒，棋力已经很强", 1000, null),
    EXPERT("高手", "每步约 3 秒，全力思考", 3000, null)
}

/** Turns an engine score (side-to-move point of view) into a plain
 *  sentence from Red's point of view. */
private fun evalText(result: EngineResult, sideToMove: Side): String {
    val sign = if (sideToMove == Side.RED) 1 else -1
    val mate = result.mateIn?.times(sign)
    if (mate != null) {
        return if (mate > 0) {
            "红方有将杀的机会，约 $mate 步内见分晓。"
        } else {
            "黑方有将杀的机会，约 ${-mate} 步内见分晓。"
        }
    }
    val cp = (result.scoreCp ?: 0) * sign
    val pawns = kotlin.math.abs(cp) / 100.0
    val pawnsText = String.format("%.1f", pawns)
    return when {
        cp >= 50 -> "红方占优，约合 $pawnsText 个兵。"
        cp <= -50 -> "黑方占优，约合 $pawnsText 个兵。"
        cp >= 15 -> "局势接近，红方稍好一点。"
        cp <= -15 -> "局势接近，黑方稍好一点。"
        else -> "局势基本均势。"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    mode: BoardMode,
    onBack: () -> Unit,
    startCells: List<com.tuxunxiangqi.android.xiangqi.Piece?>? = null
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

    var selected by remember { mutableStateOf<Pos?>(null) }
    // AI game setup.
    var setupDone by remember { mutableStateOf(mode == BoardMode.PVP) }
    var humanSide by remember { mutableStateOf(Side.RED) }
    var level by remember { mutableStateOf(Level.NORMAL) }
    // Engine state.
    var thinking by remember { mutableStateOf(false) }
    var engineError by remember { mutableStateOf<String?>(null) }
    var hintBusy by remember { mutableStateOf(false) }
    var hintMove by remember { mutableStateOf<Pair<Pos, Pos>?>(null) }
    var hintText by remember { mutableStateOf<String?>(null) }

    val busy = thinking || hintBusy
    val humanTurn = mode == BoardMode.PVP || state.turn == humanSide
    val targets = remember(selected, state.lastMove) {
        selected?.let { Rules.legalTargets(state.cells, it) } ?: emptyList()
    }

    fun clearHint() {
        hintMove = null
        hintText = null
    }

    fun engineMove() {
        if (mode != BoardMode.AI || !setupDone) return
        if (state.result != null || state.turn == humanSide || thinking) return
        thinking = true
        engineError = null
        clearHint()
        val fen = Fen.toFen(state)
        engine.compute(fen, level.movetimeMs, level.depthCap) { res, err ->
            thinking = false
            when {
                err != null -> engineError = err
                res != null -> {
                    val mv = Fen.parseUciMove(res.bestMoveUci)
                    if (mv == null || !state.tryMove(mv.first, mv.second)) {
                        engineError = "引擎给的着法对不上棋盘，请重开一局。"
                    } else {
                        selected = null
                    }
                }
            }
        }
    }

    LaunchedEffect(state.lastMove, setupDone) {
        engineMove()
    }

    fun askHint() {
        if (busy || state.result != null) return
        hintBusy = true
        hintText = "引擎正在算，请稍等…"
        hintMove = null
        engineError = null
        val fen = Fen.toFen(state)
        val sideToMove = state.turn
        val atMove = state.moveCount
        val movetime = if (mode == BoardMode.AI) level.movetimeMs else 1000
        val cap = if (mode == BoardMode.AI) level.depthCap else null
        engine.compute(fen, movetime, cap) { res, err ->
            hintBusy = false
            if (state.moveCount != atMove) {
                // The position moved on while the engine was thinking;
                // a stale hint would point at the wrong move.
                clearHint()
                return@compute
            }
            when {
                err != null -> {
                    hintText = null
                    engineError = err
                }
                res != null -> {
                    val mv = Fen.parseUciMove(res.bestMoveUci)
                    if (mv == null) {
                        hintText = null
                        engineError = "引擎给的着法看不懂，请再试一次。"
                    } else {
                        hintMove = mv
                        hintText = "一选已在棋盘上标出。" + evalText(res, sideToMove)
                    }
                }
            }
        }
    }

    fun undo() {
        if (busy) return
        selected = null
        clearHint()
        engineError = null
        if (mode == BoardMode.AI) {
            // Take back the engine's move together with the human's.
            var guard = 0
            while (guard < 200) {
                if (!state.undo()) break
                guard++
                if (state.turn == humanSide || state.moveCount == 0) break
            }
        } else {
            state.undo()
        }
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
                    clearHint()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (mode == BoardMode.AI) "人机对弈" else "人人对弈")
                },
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // The whole page is one scrollable column: on short screens
        // the board, buttons and texts must all stay reachable.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (mode == BoardMode.AI && !setupDone) {
                SetupSection(
                    humanSide = humanSide,
                    onSideChange = { humanSide = it },
                    level = level,
                    onLevelChange = { level = it },
                    onStart = {
                        setupDone = true
                        engineError = null
                        engine.start(
                            onReady = {},
                            onError = { msg -> engineError = msg }
                        )
                    }
                )
            } else {
                // Turn status.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (state.turn == Side.RED)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onBackground
                            )
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = when {
                            state.result != null ->
                                if (state.result!!.winner == Side.RED) "红方胜" else "黑方胜"
                            thinking -> "引擎思考中…"
                            mode == BoardMode.AI && !humanTurn -> "引擎走棋"
                            state.turn == Side.RED -> "红方走棋"
                            else -> "黑方走棋"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (state.result == null && state.inCheck) {
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "将军！",
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

                if (thinking) {
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
                            text = (if (res.winner == Side.RED) "红方胜" else "黑方胜") +
                                " · " + res.reason,
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

                BoardFrame(modifier = Modifier.fillMaxWidth()) {
                    XiangqiBoard(
                        state = state,
                        selected = selected,
                        legalTargets = targets,
                        hint = hintMove,
                        onTap = { pos -> onTap(pos) }
                    )
                }

                hintText?.let { text ->
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { undo() },
                        enabled = state.lastMove != null && !busy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("悔棋")
                    }
                    OutlinedButton(
                        onClick = { askHint() },
                        enabled = state.result == null && !busy,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("AI 一选")
                    }
                    Button(
                        onClick = {
                            if (!busy) {
                                state.newGame(state.initialCells, state.initialTurn)
                                selected = null
                                clearHint()
                                engineError = null
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("重开")
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "点选棋子，再点高亮处落子。规则已校验：蹩马腿、塞象眼、" +
                        "炮须隔子吃、九宫与过河限制、将帅照面；不能送将，被将军时必须应将。" +
                        "「AI 一选」会请引擎算一步，并把推荐着法标在棋盘上。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupSection(
    humanSide: Side,
    onSideChange: (Side) -> Unit,
    level: Level,
    onLevelChange: (Level) -> Unit,
    onStart: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "你执哪一方？",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = humanSide == Side.RED,
                onClick = { onSideChange(Side.RED) },
                label = { Text("执红先行") }
            )
            FilterChip(
                selected = humanSide == Side.BLACK,
                onClick = { onSideChange(Side.BLACK) },
                label = { Text("执黑后行") }
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "引擎档位",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth()
        ) {
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
