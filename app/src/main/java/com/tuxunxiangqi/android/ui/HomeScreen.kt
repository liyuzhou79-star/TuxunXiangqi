package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import com.tuxunxiangqi.android.data.Puzzles
import java.util.Calendar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.R

@Composable
fun HomeScreen(
    onVsAi: () -> Unit,
    onVsFriend: () -> Unit,
    onRecords: () -> Unit,
    onEditor: () -> Unit = {},
    onSpectacle: () -> Unit = {},
    onGuess: () -> Unit = {},
    onPuzzles: () -> Unit = {},
    onOpenings: () -> Unit = {},
    onGuide: () -> Unit = {},
    onSettings: () -> Unit = {},
    onResume: (com.tuxunxiangqi.android.data.ResumeGame) -> Unit = {}
) {
    val homeContext = androidx.compose.ui.platform.LocalContext.current
    val resumeStore = remember { com.tuxunxiangqi.android.data.ResumeStore(homeContext) }
    var savedGame by remember {
        mutableStateOf(resumeStore.load()?.takeIf { it.movesUci.isNotEmpty() })
    }
    // One scrollable container for the whole page (scroll audit:
    // every page must stay reachable on short screens).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // In-app artwork display uses the drawable-nodpi bitmap copy,
        // never the mipmap adaptive icon (which crashes painterResource
        // on Android 8+).
        Image(
            painter = painterResource(R.drawable.ic_launcher_art),
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "将！",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "免费开源象棋 · 本地引擎对弈与分析",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))
        // Continue-an-unfinished-game card, only when a snapshot
        // exists (games snapshot themselves after every move).
        savedGame?.let { game ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "继续对局",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = game.summary() + "，上次下到一半的棋还留着。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(10.dp))
                    Row {
                        Button(onClick = { onResume(game) }) { Text("接着下") }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(onClick = {
                            resumeStore.clear()
                            savedGame = null
                        }) { Text("放弃这局") }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
        }
        // Primary entry: filled vermilion card so the main action
        // reads as the main action.
        EntryCard(
            title = "人机对弈",
            subtitle = "挑战手机里的 Pikafish 引擎，三档棋力可选",
            icon = Icons.Filled.Face,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            iconTint = MaterialTheme.colorScheme.onPrimary,
            onClick = onVsAi
        )
        Spacer(Modifier.height(14.dp))
        // Daily puzzle card.
        val daily = Puzzles.daily(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "今日一题 · " + daily.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = daily.hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onPuzzles) { Text("去解今日一题") }
            }
        }
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "人人对弈",
            subtitle = "和朋友在同一块棋盘上对弈，还能请引擎给一选",
            icon = Icons.Filled.Person,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onVsFriend
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "棋谱",
            subtitle = "保存、导入与复盘你的对局",
            icon = Icons.Filled.List,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onRecords
        )

        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "AI 表演赛",
            subtitle = "两台引擎自弈对打，你在旁边看，还能暂停调速",
            icon = Icons.Filled.PlayArrow,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onSpectacle
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "猜一选",
            subtitle = "看局面猜引擎的第一选择，猜错进错题本",
            icon = Icons.Filled.Star,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onGuess
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "残局题",
            subtitle = "共 " + Puzzles.all.size + " 道红先胜残局，引擎当裁判，还有每日一题",
            icon = Icons.Filled.Favorite,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onPuzzles
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "开局库",
            subtitle = "内置定式可以搜、可以看，点开就在棋盘上摆出局面和建议着法",
            icon = Icons.Filled.List,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onOpenings
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "新手指南",
            subtitle = "每种棋子怎么走，小棋盘图一步看懂",
            icon = Icons.Filled.Create,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onGuide
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "设置",
            subtitle = "主题、默认难度、数据备份与恢复、检查更新、战绩",
            icon = Icons.Filled.Settings,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onSettings
        )
        Spacer(Modifier.height(14.dp))
        EntryCard(
            title = "摆棋编辑",
            subtitle = "自己摆一个局面，从那里开始对弈或分析",
            icon = Icons.Filled.Edit,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconTint = MaterialTheme.colorScheme.primary,
            onClick = onEditor
        )

        Spacer(Modifier.height(14.dp))
        EngineInfoCard()
    }
}

@Composable
private fun EntryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.78f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun EngineInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "引擎信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            InfoLine("引擎", "Pikafish 2026-09-06 · 官方 NNUE 网络")
            InfoLine("运行方式", "完全离线，全部计算在手机本地完成")
            InfoLine("许可", "GPL-3.0 开源 · 无广告 · 不联网")
            // Read the real version from the package instead of a
            // hardcoded string (a hardcoded one went stale before).
            val ctx = androidx.compose.ui.platform.LocalContext.current
            val versionName = androidx.compose.runtime.remember {
                try {
                    ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "?"
                } catch (_: Exception) {
                    "?"
                }
            }
            InfoLine("版本", versionName)
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
