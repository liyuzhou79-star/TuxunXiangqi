package com.tuxunxiangqi.android.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.Puzzle
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.data.SettingsStore
import com.tuxunxiangqi.android.data.dailyStreak
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    // Daily catch-up (1.32.0): the epoch day the currently
    // played puzzle was launched as, when it came from the
    // 近 7 天 daily strip; solving stamps THAT day, so a missed
    // daily can still be completed for its own date. Null for
    // ordinary list/random launches (today's daily still stamps
    // today via the id check in onSolved).
    var playingDailyDay by remember { mutableStateOf<Long?>(null) }
    var dailyDays by remember {
        mutableStateOf(
            prefs.getStringSet("daily_days", emptySet()).orEmpty()
                .mapNotNull { it.toLongOrNull() }.toSet()
        )
    }
    // List filters (1.19.0): one difficulty tier at a time, plus
    // an unsolved-only toggle for working through what's left.
    var diffFilter by remember { mutableStateOf<Int?>(null) }
    var unsolvedOnly by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SubPageTopBar(
                title = playing?.title ?: "残局题",
                onBack = {
                    if (playing != null) {
                        playing = null
                        playingDailyDay = null
                    } else {
                        onBack()
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
    if (playing != null) {
        // key() on the puzzle id (1.38.0): jumping straight to
        // the next puzzle recreates the play screen, so none of
        // its per-attempt state (solved flag, move count, hint
        // cards) can leak into the new puzzle.
        key(playing!!.id) {
        PuzzlePlay(
            puzzle = playing!!,
            modifier = Modifier.padding(padding),
            nextPuzzle = Puzzles.nextUnsolved(solvedIds, playing!!.id),
            onNextPuzzle = { next ->
                playingDailyDay = null
                playing = next
            },
            onExit = {
                playing = null
                playingDailyDay = null
            },
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
                // Solving a daily puzzle stamps its day for
                // the streak (1.28.0): the day it was launched
                // as from the daily strip (1.32.0 catch-up), or
                // today when the solved puzzle is today's daily
                // however it was launched.
                val stampDay = playingDailyDay ?: run {
                    val todayDailyId = Puzzles.daily(
                        Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    ).id
                    if (id == todayDailyId) {
                        java.time.LocalDate.now().toEpochDay()
                    } else {
                        null
                    }
                }
                if (stampDay != null) {
                    val days = dailyDays + stampDay
                    dailyDays = days
                    editor.putStringSet(
                        "daily_days", days.map { it.toString() }.toSet()
                    )
                }
                editor.apply()
            }
        )
        }
    } else {

    val daily = Puzzles.daily(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "全部是红先胜的经典子力残局。你执红，引擎执黑防守，还兼任裁判：哪一步把优势走没了，它会当场提醒。解出 " +
                solvedIds.size + " / " + Puzzles.all.size + " 题。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))
        // Daily catch-up strip (1.32.0): the last seven dailies
        // with their completion state. Solving one stamps its
        // own date, so a missed day can still join the streak —
        // but only its own date: skipping yesterday and solving
        // today's does not backfill yesterday.
        val todayEpoch = java.time.LocalDate.now().toEpochDay()
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "每日一题 · 近 7 天",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Text(
                    text = "错过的可以补做：补做哪天的题，就按哪天记，连续天数能接上。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                val streakNow = dailyStreak(dailyDays, todayEpoch)
                if (streakNow > 0) {
                    Text(
                        text = "当前连续完成 $streakNow 天",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                for (epochDay in Puzzles.recentDailyDays(todayEpoch)) {
                    val date = java.time.LocalDate.ofEpochDay(epochDay)
                    val p = Puzzles.dailyForEpochDay(epochDay)
                    val done = epochDay in dailyDays
                    val dateLabel = when (epochDay) {
                        todayEpoch -> "今天"
                        todayEpoch - 1 -> "昨天"
                        else -> "" + date.monthValue + "月" + date.dayOfMonth + "日"
                    }
                    ListItem(
                        headlineContent = {
                            Text(
                                text = dateLabel + " · " + p.title,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        supportingContent = {
                            Text(text = if (done) "已完成" else "还没解")
                        },
                        leadingContent = if (done) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "已完成",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            null
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = androidx.compose.material3.ListItemDefaults.colors(
                            containerColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        modifier = Modifier.clickable {
                            playingDailyDay = epochDay
                            playing = p
                        }
                    )
                    if (epochDay != todayEpoch - 6) {
                        HorizontalDivider()
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "按难度筛选",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val options = listOf<Int?>(null, 1, 2, 3)
            val labels = listOf("全部", "★", "★★", "★★★")
            options.forEachIndexed { index, tier ->
                SegmentedButton(
                    selected = diffFilter == tier,
                    onClick = { diffFilter = tier },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size)
                ) { Text(labels[index]) }
            }
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = unsolvedOnly,
                onClick = { unsolvedOnly = !unsolvedOnly },
                label = { Text("只看未解") }
            )
        }
        Spacer(Modifier.height(10.dp))
        val sortedPuzzles = Puzzles.all.sortedBy { it.id }
        val shownPuzzles = Puzzles.filterPuzzles(
            sortedPuzzles, diffFilter, unsolvedOnly, solvedIds
        )
        // One-tap practice (1.20.0): a random puzzle from what
        // the filters currently show, unsolved ones first.
        OutlinedButton(
            onClick = {
                val unsolved = shownPuzzles.filter { it.id !in solvedIds }
                val pool = if (unsolved.isNotEmpty()) unsolved else shownPuzzles
                if (pool.isNotEmpty()) {
                    playingDailyDay = null
                    playing = pool.random()
                }
            },
            enabled = shownPuzzles.isNotEmpty()
        ) { Text("随手来一题（优先未解）") }
        Spacer(Modifier.height(14.dp))
        if (shownPuzzles.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "没有符合筛选的题目，换个难度或关掉「只看未解」试试。",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
        }
        for (category in Puzzles.categoryOrder) {
            val group = Puzzles.filterPuzzles(
                sortedPuzzles.filter { Puzzles.categoryOf(it.id) == category },
                diffFilter, unsolvedOnly, solvedIds
            )
            if (group.isEmpty()) continue
            val groupSolved = group.count { it.id in solvedIds }
            Text(
                text = category + " · " + group.size + " 题" +
                    if (groupSolved > 0) "（已解 $groupSolved）" else "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Category progress bar (1.37.0): the header count
            // as a bar, same numbers — how far through this
            // group the player is reads at a glance.
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = {
                    if (group.isEmpty()) 0f
                    else groupSolved.toFloat() / group.size
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            for (p in group) {
            val solved = p.id in solvedIds
            ListItem(
                headlineContent = {
                    Text(
                        text = (if (p.id == daily.id) "今日一题 · " else "") + p.title +
                            " · 难度 " + "★".repeat(Puzzles.difficultyOf(p.id)) +
                            if (solved) "（已解出）" else "",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                supportingContent = {
                    Column {
                        Text(text = p.hint)
                        if (solved) {
                            val best = prefs.getInt("best_${p.id}", 0)
                            if (best > 0) {
                                Text(
                                    text = "最佳成绩：$best 步解出",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (prefs.getBoolean("assisted_${p.id}", false)) {
                                Text(
                                    text = "这道题用过提示解出——不用提示再解一次，这个标记就会去掉。"
                                )
                            }
                        }
                    }
                },
                leadingContent = if (solved) {
                    {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "已解出",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    null
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.clickable {
                    playingDailyDay = null
                    playing = p
                }
            )
            HorizontalDivider()
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(24.dp))
    }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PuzzlePlay(
    puzzle: Puzzle,
    modifier: Modifier = Modifier,
    /** The next unsolved puzzle to jump to (1.38.0), computed
     *  by the list side from the live solved set; null hides
     *  the jump button (everything else is solved). */
    nextPuzzle: Puzzle? = null,
    onNextPuzzle: ((Puzzle) -> Unit)? = null,
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
    // Since 1.14.0 the hint shows the engine's top four as the
    // same tappable candidate cards the game and review screens
    // use: tapping one points the board arrow at it. One ask
    // still costs exactly one of the three hints.
    var hintCards by remember(puzzle.id) {
        mutableStateOf<List<CandidateCardData>>(emptyList())
    }
    var hintSelRank by remember(puzzle.id) { mutableStateOf<Int?>(null) }

    fun clearHintCards() {
        hintCards = emptyList()
        hintSelRank = null
    }

    fun askHint() {
        if (busy || demoOn || hintBusy || hintsLeft <= 0) return
        if (gs.result != null || gs.turn != Side.RED) return
        hintBusy = true
        status = "引擎正在想一步好棋……"
        engine.analyze(Fen.toFen(gs), 800, multiPv = 4) { res, err ->
            hintBusy = false
            val best = res?.candidates?.firstOrNull()
            val mv = best?.let { Fen.parseUciMove(it.moveUci) }
            if (mv != null && res != null) {
                hintsLeft--
                usedHint = true
                hintMove = mv
                hintCards = buildCandidateCards(
                    gs.cells.toList(), gs.turn, res.candidates
                )
                hintSelRank = 1
                val text = Notation.moveText(gs.cells, mv.first, mv.second)
                status = "提示：一选是「$text」，下面四张候选卡点哪张，" +
                    "棋盘箭头就指哪一步。" +
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
        clearHintCards()
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

    // Safety judgements for the selected piece, wired exactly
    // like the game screen (1.38.1): this screen once shipped
    // without them, so every target fell back to the board's
    // plain marker — which then borrowed the red ink colour
    // and read as "every move is suicidal", and captures never
    // showed their safe green ring. Same settings toggle, same
    // shared Rules.safetyMap entry point.
    val settingsStore = remember { SettingsStore(context) }
    val safetyHintsOn = remember { settingsStore.safetyHintsOn }
    val targetSafety = remember(selected, gs.lastMove, gs.moveCount, safetyHintsOn) {
        Rules.safetyMap(safetyHintsOn, gs.cells, selected)
    }

    fun onTap(pos: Pos) {
        if (busy || demoOn || gs.result != null || gs.turn != Side.RED) return
        val piece = gs.cells[pos.row * 9 + pos.col]
        val sel = selected
        when {
            // Legality is judged live at tap time, not against
            // the precomputed display list: the list is for the
            // dots, the move itself must never depend on a
            // value captured by an earlier composition (the
            // 1.24.0 frozen-gate bug).
            sel != null && Rules.isLegal(gs.cells, sel, pos) -> {
                if (gs.tryMove(sel, pos)) afterUserMove()
            }
            piece != null && piece.side == Side.RED -> selected = pos
            else -> selected = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
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
                targetSafety = targetSafety,
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
        if (hintCards.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "引擎候选（点一张看箭头）",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            CandidateCardList(
                cards = hintCards,
                selectedRank = hintSelRank,
                onSelect = { card ->
                    hintSelRank = card.rank
                    hintMove = card.from to card.to
                }
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
                            clearHintCards()
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
                        clearHintCards()
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
                onClick = {
                    if (!demoOn) {
                        hintMove = null
                        clearHintCards()
                    }
                    demoOn = !demoOn
                },
                enabled = !busy || demoOn
            ) { Text(if (demoOn) "停止演示" else "看正解演示") }
            if (nextPuzzle != null && onNextPuzzle != null) {
                Button(
                    onClick = { onNextPuzzle(nextPuzzle) },
                    enabled = !busy && !demoOn
                ) { Text("下一道未解的题") }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
