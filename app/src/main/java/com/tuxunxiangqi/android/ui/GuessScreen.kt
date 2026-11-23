package com.tuxunxiangqi.android.ui

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.tuxunxiangqi.android.data.GuessOutcome
import com.tuxunxiangqi.android.data.GuessPositions
import com.tuxunxiangqi.android.data.GuessStatsStore
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.data.WrongBookStore
import com.tuxunxiangqi.android.data.WrongEntry
import com.tuxunxiangqi.android.engine.Candidate
import com.tuxunxiangqi.android.engine.PikafishEngine
import com.tuxunxiangqi.android.engine.SoundFx
import com.tuxunxiangqi.android.engine.SoundKind
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side
import kotlin.random.Random

/** Guess-the-best-move trainer. Positions come from curated sets
 *  in three difficulties, from the user's own saved games, or from
 *  the wrong book. The engine's MultiPV top-4 is the answer key. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GuessScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val engine = remember { PikafishEngine(context) }
    DisposableEffect(engine) { onDispose { engine.close() } }
    LaunchedEffect(Unit) { engine.start({}, {}) }
    val wrongStore = remember { WrongBookStore(context) }
    val recordStore = remember { RecordStore(context) }
    val statsStore = remember { GuessStatsStore(context) }

    var source by remember { mutableStateOf("普通") }
    val gs = remember { GameState() }
    var title by remember { mutableStateOf("") }
    var currentFen by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Pos?>(null) }
    var revealed by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var verdict by remember { mutableStateOf<String?>(null) }
    var candidates by remember { mutableStateOf<List<Candidate>>(emptyList()) }
    var hintMove by remember { mutableStateOf<Pair<Pos, Pos>?>(null) }
    // After judging, the candidates show as the same tappable
    // cards the game and review screens use (unified in 1.14.0):
    // tapping one points the board arrow at it.
    var selectedCandRank by remember { mutableStateOf<Int?>(null) }
    var lastGuessUci by remember { mutableStateOf<String?>(null) }
    var asked by remember { mutableStateOf(0) }
    var correct by remember { mutableStateOf(0) }
    var notice by remember { mutableStateOf<String?>(null) }
    var wrongList by remember { mutableStateOf(wrongStore.loadAll()) }
    // How many saved games are long enough to quiz from; shown
    // on the 我的棋谱 chip so an empty-looking source explains
    // itself before the user taps it (1.19.0).
    var usableRecords by remember {
        mutableStateOf(recordStore.loadAll().count { it.movesUci.size >= 8 })
    }
    var rotation by remember { mutableStateOf(0) }
    var wrongFen by remember { mutableStateOf<String?>(null) }
    // Timed mode: 30 seconds per question; running out reveals
    // the answer and counts as a miss.
    var timed by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(30) }
    var questionId by remember { mutableStateOf(0) }

    // The round score belongs to one source + mode combination
    // (1.13.0 alignment): switching the source or the timed
    // toggle starts a fresh round instead of mixing two kinds
    // of answers into one line. Lifetime stats are unaffected.
    LaunchedEffect(source, timed) {
        asked = 0
        correct = 0
    }

    fun loadPosition(fen: String, label: String, wrong: Boolean = false) {
        wrongFen = if (wrong) fen else null
        val parsed = Fen.fromFen(fen)
        if (parsed == null) {
            notice = "这个局面读不出来，换一题。"
            return
        }
        gs.newGame(parsed.first, parsed.second)
        currentFen = fen
        title = label
        selected = null
        revealed = false
        verdict = null
        candidates = emptyList()
        hintMove = null
        selectedCandRank = null
        lastGuessUci = null
        notice = null
        questionId++
        secondsLeft = 30
    }

    fun newQuestion() {
        when (source) {
            "简单", "普通", "困难" -> {
                val list = when (source) {
                    "简单" -> GuessPositions.easy
                    "困难" -> GuessPositions.hard
                    else -> GuessPositions.normal
                }
                rotation++
                val pick = list[(rotation + Random.nextInt(list.size)) % list.size]
                loadPosition(pick.second, pick.first + " · " + source)
            }
            "我的棋谱" -> {
                val usable = recordStore.loadAll().filter { it.movesUci.size >= 8 }
                if (usable.isEmpty()) {
                    notice = "还没有够长的棋谱。先下几局（8 手以上），这里就能从你的对局里出题了。"
                    return
                }
                val rec = usable[Random.nextInt(usable.size)]
                val idx = Random.nextInt(2, rec.movesUci.size - 2)
                val parsed = Fen.fromFen(rec.initialFen)
                if (parsed == null) {
                    notice = "这局棋谱的初始局面读不出来，换一题试试。"
                    return
                }
                val scratch = GameState()
                scratch.newGame(parsed.first, parsed.second)
                var ok = true
                for (i in 0 until idx) {
                    val ft = Fen.parseUciMove(rec.movesUci[i])
                    if (ft == null || !scratch.tryMove(ft.first, ft.second)) {
                        ok = false
                        break
                    }
                }
                if (!ok) {
                    notice = "这局棋谱中途对不上，换一题试试。"
                    return
                }
                loadPosition(
                    Fen.toFen(scratch.cells.toList(), scratch.turn),
                    "来自棋谱《" + rec.title + "》第 " + (idx + 1) + " 手"
                )
            }
            "错题本" -> {
                val list = wrongStore.loadAll()
                if (list.isEmpty()) {
                    notice = "错题本是空的——猜错的题会自动收进来。"
                    return
                }
                val entry = list.first()
                loadPosition(entry.fen, "错题重练 · " + entry.title, wrong = true)
            }
        }
    }

    LaunchedEffect(source) { newQuestion() }

    /** Shared judging for a submitted guess and for a timed-mode
     *  timeout (guess = null). The engine's MultiPV top-4 is the
     *  answer key either way. */
    fun judge(guess: Pair<Pos, Pos>?) {
        if (busy || revealed) return
        busy = true
        val bucketNow = source
        // Timed answers are accounted in their own bucket, not
        // mixed into the source's untimed record (since 1.8.0).
        val timedNow = timed
        val guessUci = guess?.let { Fen.toUci(it.first, it.second) }
        val fenNow = Fen.toFen(gs)
        val cellsNow = gs.cells.toList()
        val turnNow = gs.turn
        engine.analyze(fenNow, 900, multiPv = 4) { res, err ->
            busy = false
            if (err != null || res == null || res.candidates.isEmpty()) {
                verdict = "引擎没能给出答案（$err），这题不算，换一题吧。"
                revealed = true
                return@analyze
            }
            val cands = res.candidates.sortedBy { it.rank }
            candidates = cands
            val best = cands.first()
            hintMove = Fen.parseUciMove(best.moveUci)
            selectedCandRank = 1
            lastGuessUci = guessUci
            asked++
            revealed = true
            val guessRank = cands.firstOrNull { it.moveUci == guessUci }?.rank
            statsStore.record(
                if (timedNow) GuessStatsStore.TIMED_BUCKET else bucketNow,
                when {
                    guessUci == best.moveUci -> GuessOutcome.BEST
                    guessRank != null -> GuessOutcome.TOP4
                    else -> GuessOutcome.MISS
                }
            )
            val guessText = guess?.let { Notation.moveText(cellsNow, it.first, it.second) }
            val bestText = Fen.parseUciMove(best.moveUci)?.let {
                Notation.moveText(cellsNow, it.first, it.second)
            } ?: best.moveUci
            verdict = when {
                guessUci == null -> {
                    SoundFx.play(context, SoundKind.BAD)
                    if (wrongFen == null) {
                        wrongStore.add(
                            WrongEntry(fenNow, best.moveUci, title, System.currentTimeMillis())
                        )
                        wrongList = wrongStore.loadAll()
                    }
                    "时间到，没来得及出手。一选是「$bestText」。" +
                        if (wrongFen == currentFen) "这题继续留在错题本里。" else "这题已收进错题本。"
                }
                guessUci == best.moveUci -> {
                    SoundFx.play(context, SoundKind.GOOD)
                    correct++
                    val wasRetry = wrongFen == currentFen
                    if (wasRetry) {
                        wrongStore.remove(currentFen)
                        wrongList = wrongStore.loadAll()
                        wrongFen = null
                    }
                    "猜中了！「$guessText」正是引擎一选。" +
                        if (wasRetry) "这题已移出错题本。" else ""
                }
                guessRank != null -> {
                    SoundFx.play(context, SoundKind.BAD)
                    if (wrongFen == null) {
                        wrongStore.add(
                            WrongEntry(fenNow, best.moveUci, title, System.currentTimeMillis())
                        )
                        wrongList = wrongStore.loadAll()
                    }
                    "「$guessText」进了引擎前四（第 $guessRank 位），但一选是「$bestText」。" +
                        if (wrongFen == currentFen) "这题继续留在错题本里。" else "这题已收进错题本。"
                }
                else -> {
                    SoundFx.play(context, SoundKind.BAD)
                    if (wrongFen == null) {
                        wrongStore.add(
                            WrongEntry(fenNow, best.moveUci, title, System.currentTimeMillis())
                        )
                        wrongList = wrongStore.loadAll()
                    }
                    "「$guessText」没进引擎前四。一选是「$bestText」。" +
                        if (wrongFen == currentFen) "这题继续留在错题本里。" else "这题已收进错题本。"
                }
            }
        }
    }

    // Timed mode countdown: ticks only while a question is open;
    // at zero the shared judge reveals the answer as a miss.
    // (Declared after judge — Kotlin local functions cannot be
    // referenced before their declaration.)
    LaunchedEffect(questionId, timed, revealed) {
        if (!timed || revealed) return@LaunchedEffect
        while (secondsLeft > 0 && !revealed) {
            kotlinx.coroutines.delay(1000)
            secondsLeft--
        }
        if (secondsLeft == 0 && !revealed && !busy && currentFen.isNotBlank()) {
            judge(null)
        }
    }

    val targets = remember(selected, gs.moveCount, revealed) {
        if (revealed) emptyList()
        else selected?.let { Rules.legalTargets(gs.cells, it) } ?: emptyList()
    }

    fun onTap(pos: Pos) {
        if (revealed || busy) return
        val piece = gs.cells[pos.row * 9 + pos.col]
        val sel = selected
        when {
            sel != null && pos in targets -> {
                selected = null
                judge(sel to pos)
            }
            piece != null && piece.side == gs.turn -> selected = pos
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
            text = "猜一选",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "看局面，点你的子、再点落点，猜引擎的第一选择。猜错的题会进错题本，可以反复重练。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (s in listOf("简单", "普通", "困难", "我的棋谱", "错题本")) {
                val countLabel = when (s) {
                    "简单" -> "（" + GuessPositions.easy.size + " 题）"
                    "普通" -> "（" + GuessPositions.normal.size + " 题）"
                    "困难" -> "（" + GuessPositions.hard.size + " 题）"
                    "我的棋谱" -> "（" + usableRecords + " 局）"
                    else -> "（" + wrongList.size + " 题）"
                }
                FilterChip(
                    selected = source == s,
                    onClick = { source = s },
                    label = { Text(s + countLabel) }
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = when (source) {
                "简单", "普通", "困难" -> "「" + source + "」题库共 " + when (source) {
                    "简单" -> GuessPositions.easy.size
                    "困难" -> GuessPositions.hard.size
                    else -> GuessPositions.normal.size
                } + " 题，随机抽题，答过的也可能再遇到。"
                "我的棋谱" -> "能出题的棋谱共 " + usableRecords +
                    " 局（8 手以上），从对局中段随机出题。"
                else -> "错题本共 " + wrongList.size + " 题，猜中一选就移出。"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = timed,
                onClick = { timed = !timed },
                label = { Text("限时挑战（30 秒）") }
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "本轮成绩" + (if (timed) "（限时）" else "") +
                "：猜对 $correct / 共 $asked 题" +
                if (!revealed && currentFen.isNotBlank()) {
                    " · 正在答第 " + (asked + 1) + " 题"
                } else {
                    ""
                },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        val lifetime = remember(asked) { statsStore.total() }
        if (lifetime.asked > 0) {
            Text(
                text = "累计成绩：共答 " + lifetime.asked + " 题 · 命中一选 " + lifetime.bestHits +
                    " 次（" + lifetime.bestRatePercent + "%）· 进前四 " + lifetime.top4 + " 次",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        val timedLifetime = remember(asked) { statsStore.timedStat() }
        if (timedLifetime.asked > 0) {
            Text(
                text = "限时挑战累计：共答 " + timedLifetime.asked + " 题 · 命中一选 " +
                    timedLifetime.bestHits + " 次（" + timedLifetime.bestRatePercent +
                    "%）· 进前四 " + timedLifetime.top4 + " 次",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (timed && !revealed && currentFen.isNotBlank()) {
            Text(
                text = "还剩 $secondsLeft 秒",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (secondsLeft <= 5) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        if (notice != null) {
            Spacer(Modifier.height(6.dp))
            // Informational notices (empty wrong book, short
            // records) are not errors — show them as a neutral
            // card, not red text.
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = notice!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = title + if (currentFen.isNotBlank()) {
                "（" + (if (gs.turn == Side.RED) "红" else "黑") + "方走棋）"
            } else {
                ""
            },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
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

        if (verdict != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = verdict!!,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (candidates.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "点任意候选，棋盘箭头会换成那一步。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        val cards = buildCandidateCards(
                            gs.cells.toList(), gs.turn, candidates
                        )
                        CandidateCardList(
                            cards = cards,
                            selectedRank = selectedCandRank,
                            onSelect = { card ->
                                selectedCandRank = card.rank
                                hintMove = card.from to card.to
                            },
                            tagFor = { card ->
                                if (card.moveUci == lastGuessUci) "你猜的这步" else null
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { newQuestion() }, enabled = !busy) {
                Text("下一题")
            }
            OutlinedButton(onClick = onBack) { Text("返回") }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "错题本（" + wrongList.size + " 题）",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        if (wrongList.isEmpty()) {
            Text(
                text = "还没有错题。猜错一题，这里就会多一题。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        for (entry in wrongList) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            loadPosition(entry.fen, "错题重练 · " + entry.title, wrong = true)
                        }) { Text("重练这题") }
                        TextButton(onClick = {
                            wrongStore.remove(entry.fen)
                            wrongList = wrongStore.loadAll()
                        }) { Text("移出错题本") }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(24.dp))
    }
}
