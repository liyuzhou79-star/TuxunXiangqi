package com.tuxunxiangqi.android.ui

import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.Puzzle
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.engine.PikafishEngine
import com.tuxunxiangqi.android.engine.SoundFx
import com.tuxunxiangqi.android.engine.SoundKind
import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import java.util.Calendar
import kotlinx.coroutines.delay

/** Endgame puzzles: Red to move and win. The user plays Red, the
 *  engine defends Black and also referees — after each user move
 *  the evaluation must stay clearly winning, otherwise the move is
 *  flagged (but kept, with undo offered). Solved state persists. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PuzzleScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
    }
    var solvedIds by remember {
        mutableStateOf(prefs.getStringSet("solved", emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet())
    }
    var playing by remember { mutableStateOf<Puzzle?>(null) }

    if (playing != null) {
        PuzzlePlay(
            puzzle = playing!!,
            onExit = { playing = null },
            onSolved = { id, steps, assisted ->
                val next = solvedIds + id
                solvedIds = next
                val editor = prefs.edit()
                    .putStringSet("solved", next.map { it.toString() }.toSet())
                // Best (fewest) user moves for this puzzle; demo
                // solves pass steps = null and never count.
                if (steps != null && steps > 0) {
                    val old = prefs.getInt("best_$id", 0)
                    if (old == 0 || steps < old) editor.putInt("best_$id", steps)
                }
                // A solve that used the hint button carries a
                // visible mark until the user re-solves the puzzle
                // cleanly, which clears it again.
                if (steps != null) {
                    if (assisted) {
                        editor.putBoolean("assisted_$id", true)
                    } else {
                        editor.remove("assisted_$id")
                    }
                }
                editor.apply()
            }
        )
        return
    }

    val daily = Puzzles.daily(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "残局题",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "全部是红先胜的经典子力残局。你执红，引擎执黑防守，还兼任裁判：哪一步把优势走没了，它会当场提醒。解出 " +
                solvedIds.size + " / " + Puzzles.all.size + " 题。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        val sortedPuzzles = Puzzles.all.sortedBy { it.id }
        for (category in Puzzles.categoryOrder) {
            val group = sortedPuzzles.filter { Puzzles.categoryOf(it.id) == category }
            if (group.isEmpty()) continue
            val groupSolved = group.count { it.id in solvedIds }
            Text(
                text = category + " · " + group.size + " 题" +
                    if (groupSolved > 0) "（已解 $groupSolved）" else "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            for (p in group) {
            val solved = p.id in solvedIds
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (solved) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = (if (p.id == daily.id) "今日一题 · " else "") + p.title +
                            " · 难度 " + "★".repeat(Puzzles.difficultyOf(p.id)) +
                            if (solved) "（已解出）" else "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = p.hint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (solved) {
                        val best = prefs.getInt("best_${p.id}", 0)
                        if (best > 0) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "最佳成绩：$best 步解出",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (prefs.getBoolean("assisted_${p.id}", false)) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "这道题用过提示解出——不用提示再解一次，这个标记就会去掉。",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(onClick = { playing = p }) {
                        Text(if (solved) "再解一次" else "开始解题")
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
        OutlinedButton(onClick = onBack) { Text("返回") }
        Spacer(Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PuzzlePlay(
    puzzle: Puzzle,
    onExit: () -> Unit,
    /** steps = the user's own move count on a genuine solve;
     *  null when the demo solved it (never a personal best).
     *  assisted = the solving attempt used the hint button. */
    onSolved: (Int, Int?, Boolean) -> Unit
) {
    val context = LocalContext.current
    val bestBefore = remember(puzzle.id) {
        context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
            .getInt("best_${puzzle.id}", 0)
    }
    val engine = remember { PikafishEngine(context) }
    DisposableEffect(engine) { onDispose { engine.close() } }
    LaunchedEffect(Unit) { engine.start({}, {}) }

    val gs = remember(puzzle.id) {
        val parsed = Fen.fromFen(puzzle.fen)
        GameState().also {
            if (parsed != null) it.newGame(parsed.first, parsed.second)
        }
    }
    var selected by remember { mutableStateOf<Pos?>(null) }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("红先。请走第一步。") }
    var userMoves by remember { mutableStateOf(0) }
    var solved by remember { mutableStateOf(false) }
    var demoOn by remember { mutableStateOf(false) }
    var lastJudge by remember { mutableStateOf<String?>(null) }
    // Hint button: at most three hints per attempt. The engine's
    // top move is shown as a board arrow plus its notation; using
    // any hint marks the eventual solve as assisted.
    var hintsLeft by remember(puzzle.id) { mutableStateOf(3) }
    var usedHint by remember(puzzle.id) { mutableStateOf(false) }
    var hintBusy by remember { mutableStateOf(false) }
    var hintMove by remember(puzzle.id) { mutableStateOf<Pair<Pos, Pos>?>(null) }

    fun askHint() {
        if (busy || demoOn || hintBusy || hintsLeft <= 0) return
        if (gs.result != null || gs.turn != Side.RED) return
        hintBusy = true
        status = "引擎正在想一步好棋……"
        engine.analyze(Fen.toFen(gs), 800, multiPv = 1) { res, err ->
            hintBusy = false
            val best = res?.candidates?.firstOrNull()
            val mv = best?.let { Fen.parseUciMove(it.moveUci) }
            if (mv != null) {
                hintsLeft--
                usedHint = true
                hintMove = mv
                val text = Notation.moveText(gs.cells, mv.first, mv.second)
                status = "提示：试试走「$text」。" +
                    if (hintsLeft > 0) "这轮还剩 $hintsLeft 次提示。" else "提示次数用完了。"
            } else {
                status = "引擎这次没能给出提示" +
                    (err?.let { "：$it" } ?: "。") + "次数没扣，再试一次。"
            }
        }
    }

    // Move sounds, same rule as the game screen: only when the
    // move count grows (undo stays silent), check outranks capture.
    var lastSoundCount by remember { mutableStateOf(0) }
    LaunchedEffect(gs.lastMove) {
        val mv = gs.lastMove
        if (mv != null && gs.moveCount > lastSoundCount && gs.result == null) {
            SoundFx.play(
                context,
                when {
                    gs.inCheck -> SoundKind.CHECK_VOICE
                    mv.captured != null -> SoundKind.CAPTURE
                    else -> SoundKind.MOVE
                }
            )
        }
        lastSoundCount = gs.moveCount
    }

    fun solvedStatus(): String {
        val bestNote = when {
            bestBefore == 0 -> "这就是你的最佳成绩。"
            userMoves < bestBefore -> "比之前的最佳（$bestBefore 步）还快！"
            userMoves == bestBefore -> "追平了你的最佳成绩。"
            else -> "你的最佳是 $bestBefore 步，下次试着更快解出。"
        }
        return "解出来了！共用 $userMoves 步。黑方" + gs.result!!.reason + "。" + bestNote +
            if (usedHint) "这轮用过提示，题目列表里会如实标出来。" else ""
    }

    fun afterUserMove() {
        userMoves++
        selected = null
        hintMove = null
        if (gs.result != null) {
            if (gs.result!!.winner == Side.RED) {
                solved = true
                onSolved(puzzle.id, userMoves, usedHint)
                SoundFx.play(context, SoundKind.MATE_VOICE)
                status = solvedStatus()
            } else {
                status = "这局没解出来（" + gs.result!!.reason + "）。悔棋或重开再试。"
            }
            return
        }
        busy = true
        status = "引擎防守中……"
        // Judge first (is Red still clearly winning?), then defend.
        engine.analyze(Fen.toFen(gs), 500, multiPv = 1) { res, _ ->
            val best = res?.candidates?.firstOrNull()
            if (best != null) {
                val (redCp, redMate) = EvalUtil.toRedPerspective(
                    best.scoreCp, best.mateIn, gs.turn
                )
                lastJudge = when {
                    redMate != null && redMate > 0 -> "形势：红方有杀棋在路上。"
                    redMate != null -> "裁判提醒：这步之后黑方反而有杀，优势丢了——建议悔棋。"
                    redCp != null && redCp >= 300 -> "形势：红方优势明显，继续保持。"
                    redCp != null && redCp >= 120 -> "形势：红方还占优，但优势在缩水，小心。"
                    else -> "裁判提醒：这步把优势走没了——建议悔棋重走。"
                }
            }
            engine.compute(Fen.toFen(gs), 800) { eres, eerr ->
                busy = false
                if (eres != null) {
                    val mv = Fen.parseUciMove(eres.bestMoveUci)
                    if (mv != null) {
                        val text = Notation.moveText(gs.cells, mv.first, mv.second)
                        gs.tryMove(mv.first, mv.second)
                        status = "黑方走了「$text」。" + (lastJudge ?: "")
                        if (gs.result != null && gs.result!!.winner == Side.RED) {
                            solved = true
                            onSolved(puzzle.id, userMoves, usedHint)
                            SoundFx.play(context, SoundKind.MATE_VOICE)
                            status = solvedStatus()
                        }
                    }
                } else if (eerr != null) {
                    status = "引擎没能走棋：$eerr"
                }
            }
        }
    }

    // Demo mode: engine plays both sides.
    LaunchedEffect(demoOn) {
        while (demoOn && gs.result == null) {
            delay(500)
            if (busy) continue
            busy = true
            engine.compute(Fen.toFen(gs), 700) { eres, _ ->
                busy = false
                if (eres != null) {
                    val mv = Fen.parseUciMove(eres.bestMoveUci)
                    if (mv != null) {
                        if (gs.turn == Side.RED) userMoves++
                        gs.tryMove(mv.first, mv.second)
                        status = "演示中……（引擎双方对弈）"
                        if (gs.result != null) {
                            demoOn = false
                            if (gs.result!!.winner == Side.RED) {
                                solved = true
                                onSolved(puzzle.id, null, false)
                                status = "演示结束：红方胜（" + gs.result!!.reason + "）。轮到你自己解一次。"
                            } else {
                                status = "演示结束：红方没赢——这题比演示的路线更讲究，自己试试。"
                            }
                        }
                    }
                }
            }
            delay(900)
        }
    }

    val targets = remember(selected, gs.moveCount, busy, demoOn) {
        if (busy || demoOn || gs.result != null || gs.turn != Side.RED) {
            emptyList()
        } else {
            selected?.let { Rules.legalTargets(gs.cells, it) } ?: emptyList()
        }
    }

    fun onTap(pos: Pos) {
        if (busy || demoOn || gs.result != null || gs.turn != Side.RED) return
        val piece = gs.cells[pos.row * 9 + pos.col]
        val sel = selected
        when {
            sel != null && pos in targets -> {
                if (gs.tryMove(sel, pos)) afterUserMove()
            }
            piece != null && piece.side == Side.RED -> selected = pos
            else -> selected = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = puzzle.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "红先胜 · " + puzzle.hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        BoardFrame(modifier = Modifier.fillMaxWidth()) {
            XiangqiBoard(
                state = gs,
                selected = selected,
                legalTargets = targets,
                hint = hintMove,
                onTap = { onTap(it) }
            )
        }
        Spacer(Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (solved) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    if (!busy) {
                        // Undo the engine reply and the user move.
                        if (gs.undo()) {
                            if (gs.turn == Side.BLACK) gs.undo()
                            userMoves = (userMoves - 1).coerceAtLeast(0)
                            selected = null
                            hintMove = null
                            status = "已悔棋，重新想一步。"
                        }
                    }
                },
                enabled = !busy && !demoOn
            ) { Text("悔棋") }
            OutlinedButton(
                onClick = {
                    if (!busy) {
                        val parsed = Fen.fromFen(puzzle.fen)
                        if (parsed != null) gs.newGame(parsed.first, parsed.second)
                        userMoves = 0
                        selected = null
                        solved = false
                        hintsLeft = 3
                        usedHint = false
                        hintMove = null
                        status = "已重开。红先，请走第一步。"
                    }
                },
                enabled = !busy && !demoOn
            ) { Text("重开") }
            OutlinedButton(
                onClick = { askHint() },
                enabled = !busy && !demoOn && !hintBusy && hintsLeft > 0 &&
                    gs.result == null
            ) { Text("提示一步（剩 $hintsLeft 次）") }
            Button(
                onClick = { demoOn = !demoOn },
                enabled = !busy || demoOn
            ) { Text(if (demoOn) "停止演示" else "看正解演示") }
            OutlinedButton(onClick = onExit) { Text("回题目列表") }
        }
        Spacer(Modifier.height(24.dp))
    }
}
