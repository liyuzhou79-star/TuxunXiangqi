package com.tuxunxiangqi.android.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.GuessStatsStore
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.data.WrongBookStore

/** Career page computed from the saved records: results against
 *  the engine per difficulty, other modes, puzzle progress. */
/** Career-page outcome of one AI-game record, read off the
 *  record's own mode/result labels (both written by the board
 *  screen in exactly one place, with stable wording). "胜"/"负"
 *  are from the human's side, "和" a draw, "未完" anything
 *  without a decided result. Extracted from the page in 1.79.0
 *  so the classification is unit-testable. */
fun recordOutcome(r: com.tuxunxiangqi.android.data.GameRecord): String {
    val humanRed = r.modeLabel.contains("执红")
    return when {
        r.resultText.startsWith("和棋") -> "和"
        r.resultText.startsWith("红方胜") -> if (humanRed) "胜" else "负"
        r.resultText.startsWith("黑方胜") -> if (humanRed) "负" else "胜"
        else -> "未完"
    }
}

@Composable
fun StatsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val records = remember { RecordStore(context).loadAll() }
    val wrongCount = remember { WrongBookStore(context).loadAll().size }
    val solvedIds = remember {
        context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
            .getStringSet("solved", emptySet()).orEmpty()
            .mapNotNull { it.toIntOrNull() }.toSet()
    }
    val solvedCount = solvedIds.size
    val assistedCount = remember {
        val p = context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
        solvedIds.count { p.getBoolean("assisted_$it", false) }
    }
    val guessStats = remember { GuessStatsStore(context) }
    val guessTotal = remember { guessStats.total() }
    // Daily-puzzle days (1.29.0): total days solved and the
    // current streak, from the same "daily_days" set the home
    // card reads.
    val dailyDaysInfo = remember {
        val p = context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
        val days = p.getStringSet("daily_days", emptySet()).orEmpty()
            .mapNotNull { it.toLongOrNull() }.toSet()
        days.size to com.tuxunxiangqi.android.data.dailyStreak(
            days, java.time.LocalDate.now().toEpochDay()
        )
    }
    // Recent seven days for the dot row under the daily line
    // (1.33.0): (epochDay, solved) oldest first, today last.
    val recentDaily = remember {
        val p = context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
        val days = p.getStringSet("daily_days", emptySet()).orEmpty()
            .mapNotNull { it.toLongOrNull() }.toSet()
        com.tuxunxiangqi.android.data.recentDailyStatus(
            days, java.time.LocalDate.now().toEpochDay()
        )
    }

    val aiGames = records.filter { it.modeLabel.startsWith("人机对弈") }
    val finished = aiGames.filter { recordOutcome(it) != "未完" }
    val wins = finished.count { recordOutcome(it) == "胜" }
    val losses = finished.count { recordOutcome(it) == "负" }
    val draws = finished.count { recordOutcome(it) == "和" }
    val levels = listOf("高手", "普通", "休闲")
    val bestBeaten = levels.firstOrNull { lv ->
        finished.any { it.modeLabel.contains(lv) && recordOutcome(it) == "胜" }
    }
    val pvpCount = records.count { it.modeLabel.startsWith("人人对弈") }
    val spectacleCount = records.count { it.modeLabel.startsWith("AI 表演赛") }
    val totalMoves = records.sumOf { it.movesUci.size }
    val longest = records.maxByOrNull { it.movesUci.size }

    Scaffold(
        topBar = { SubPageTopBar("战绩", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "都按保存在这台手机上的棋谱统计，删了棋谱这里也会跟着变。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))

        StatsCard(title = "对引擎战绩") {
            if (finished.isEmpty()) {
                Text(
                    text = "还没有下完的人机对局。去和引擎下一盘，结束时会自动记到这里。",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "共 " + finished.size + " 局：胜 $wins · 负 $losses · 和 $draws" +
                        " · 胜率 " + (wins * 100 / finished.size) + "%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                for (lv in levels) {
                    val games = finished.filter { it.modeLabel.contains(lv) }
                    if (games.isNotEmpty()) {
                        val w = games.count { recordOutcome(it) == "胜" }
                        val l = games.count { recordOutcome(it) == "负" }
                        val d = games.count { recordOutcome(it) == "和" }
                        Text(
                            text = "$lv 档：" + games.size + " 局（胜 $w · 负 $l · 和 $d）",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                for ((sideLabel, sideGames) in listOf(
                    "执红" to finished.filter { it.modeLabel.contains("执红") },
                    "执黑" to finished.filter { it.modeLabel.contains("执黑") }
                )) {
                    if (sideGames.isNotEmpty()) {
                        val w = sideGames.count { recordOutcome(it) == "胜" }
                        val l = sideGames.count { recordOutcome(it) == "负" }
                        val d = sideGames.count { recordOutcome(it) == "和" }
                        Text(
                            text = "$sideLabel：" + sideGames.size +
                                " 局（胜 $w · 负 $l · 和 $d）",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Text(
                    text = if (bestBeaten != null) {
                        "赢过的最高难度：$bestBeaten 档"
                    } else {
                        "还没有赢过引擎——先从休闲档找找感觉，让子也可以用起来。"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        StatsCard(title = "其他对局") {
            Text(
                text = "人人对弈 $pvpCount 局 · AI 表演赛 $spectacleCount 局 · 棋谱总计 " +
                    records.size + " 局",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "累计落子 $totalMoves 手" +
                    (longest?.let { "，最长一局 " + it.movesUci.size + " 手（" + it.title + "）" } ?: ""),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        StatsCard(title = "训练进度") {
            Text(
                text = "残局题解出 $solvedCount / " + Puzzles.all.size + " 题" +
                    if (assistedCount > 0) "（其中 $assistedCount 题用过提示）" else "",
                style = MaterialTheme.typography.bodyMedium
            )
            if (dailyDaysInfo.first > 0) {
                Text(
                    text = "每日一题累计完成 " + dailyDaysInfo.first + " 天" +
                        if (dailyDaysInfo.second >= 2) {
                            "，当前连续 " + dailyDaysInfo.second + " 天"
                        } else {
                            ""
                        },
                    style = MaterialTheme.typography.bodyMedium
                )
                // Last seven days as dots (1.33.0): a filled dot
                // is a solved day, today carries a ring so the
                // row reads at a glance next to the numbers.
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    for ((epochDay, done) in recentDaily) {
                        val date = java.time.LocalDate.ofEpochDay(epochDay)
                        val isToday = epochDay ==
                            java.time.LocalDate.now().toEpochDay()
                        Column(
                            horizontalAlignment =
                                androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .then(
                                        if (isToday) {
                                            Modifier.border(
                                                1.5.dp,
                                                MaterialTheme.colorScheme.primary,
                                                CircleShape
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .background(
                                        if (done) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme
                                                .surfaceContainerHighest
                                        },
                                        CircleShape
                                    )
                            )
                            Text(
                                text = if (isToday) {
                                    "今"
                                } else {
                                    when (date.dayOfWeek) {
                                        java.time.DayOfWeek.MONDAY -> "一"
                                        java.time.DayOfWeek.TUESDAY -> "二"
                                        java.time.DayOfWeek.WEDNESDAY -> "三"
                                        java.time.DayOfWeek.THURSDAY -> "四"
                                        java.time.DayOfWeek.FRIDAY -> "五"
                                        java.time.DayOfWeek.SATURDAY -> "六"
                                        else -> "日"
                                    }
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "近 7 天每日一题",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            for (cat in Puzzles.categoryOrder) {
                val inCat = Puzzles.all.filter { Puzzles.categoryOf(it.id) == cat }
                if (inCat.isNotEmpty()) {
                    val done = inCat.count { it.id in solvedIds }
                    Text(
                        text = "$cat 类：已解 $done / " + inCat.size + " 题",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Completion bar per category (1.15.0): the
                    // same numbers as the text line, readable at
                    // a glance.
                    LinearProgressIndicator(
                        progress = { done.toFloat() / inCat.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 4.dp)
                    )
                }
            }
            Text(
                text = "错题本里还有 $wrongCount 道题等着重练",
                style = MaterialTheme.typography.bodyMedium
            )
            if (guessTotal.asked > 0) {
                Text(
                    text = "猜一选累计答题 " + guessTotal.asked + " 题：命中一选 " +
                        guessTotal.bestHits + " 次（一选命中率 " + guessTotal.bestRatePercent +
                        "%）· 进前四 " + guessTotal.top4 + " 次",
                    style = MaterialTheme.typography.bodyMedium
                )
                for (bucket in GuessStatsStore.BUCKETS) {
                    val s = guessStats.stat(bucket)
                    if (s.asked > 0) {
                        Text(
                            text = "$bucket：答 " + s.asked + " 题，命中一选 " + s.bestHits +
                                " 次（" + s.bestRatePercent + "%）",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Text(
                    text = "猜一选还没有答过题，去训练里猜几题，这里会记下你的一选命中率。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            val timedStat = guessStats.timedStat()
            if (timedStat.asked > 0) {
                Text(
                    text = "限时挑战单列：答 " + timedStat.asked + " 题，命中一选 " +
                        timedStat.bestHits + " 次（一选命中率 " + timedStat.bestRatePercent +
                        "%）· 进前四 " + timedStat.top4 + " 次",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
    }
}

@Composable
private fun StatsCard(
    title: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
    Spacer(Modifier.height(12.dp))
}
