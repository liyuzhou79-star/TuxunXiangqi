package com.tuxunxiangqi.android.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.Puzzles
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.data.WrongBookStore

/** Career page computed from the saved records: results against
 *  the engine per difficulty, other modes, puzzle progress. */
@Composable
fun StatsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val records = remember { RecordStore(context).loadAll() }
    val wrongCount = remember { WrongBookStore(context).loadAll().size }
    val solvedCount = remember {
        context.getSharedPreferences("puzzles", Context.MODE_PRIVATE)
            .getStringSet("solved", emptySet()).orEmpty().size
    }

    val aiGames = records.filter { it.modeLabel.startsWith("人机对弈") }
    fun outcome(r: com.tuxunxiangqi.android.data.GameRecord): String {
        val humanRed = r.modeLabel.contains("执红")
        return when {
            r.resultText.startsWith("和棋") -> "和"
            r.resultText.startsWith("红方胜") -> if (humanRed) "胜" else "负"
            r.resultText.startsWith("黑方胜") -> if (humanRed) "负" else "胜"
            else -> "未完"
        }
    }
    val finished = aiGames.filter { outcome(it) != "未完" }
    val wins = finished.count { outcome(it) == "胜" }
    val losses = finished.count { outcome(it) == "负" }
    val draws = finished.count { outcome(it) == "和" }
    val levels = listOf("高手", "普通", "休闲")
    val bestBeaten = levels.firstOrNull { lv ->
        finished.any { it.modeLabel.contains(lv) && outcome(it) == "胜" }
    }
    val pvpCount = records.count { it.modeLabel.startsWith("人人对弈") }
    val spectacleCount = records.count { it.modeLabel.startsWith("AI 表演赛") }
    val totalMoves = records.sumOf { it.movesUci.size }
    val longest = records.maxByOrNull { it.movesUci.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "战绩",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
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
                    text = "共 " + finished.size + " 局：胜 $wins · 负 $losses · 和 $draws",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                for (lv in levels) {
                    val games = finished.filter { it.modeLabel.contains(lv) }
                    if (games.isNotEmpty()) {
                        val w = games.count { outcome(it) == "胜" }
                        val l = games.count { outcome(it) == "负" }
                        val d = games.count { outcome(it) == "和" }
                        Text(
                            text = "$lv 档：" + games.size + " 局（胜 $w · 负 $l · 和 $d）",
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
                text = "残局题解出 $solvedCount / " + Puzzles.all.size + " 题",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "错题本里还有 $wrongCount 道题等着重练",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedButton(onClick = onBack) { Text("返回") }
        Spacer(Modifier.height(24.dp))
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
