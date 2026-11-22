package com.tuxunxiangqi.android.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.data.SettingsStore
import com.tuxunxiangqi.android.data.applyBackupJson
import com.tuxunxiangqi.android.data.buildBackupJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Settings: theme, game defaults, data backup/restore and the
 *  manual update check — the app's only networking, and only when
 *  the user taps the button. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenStats: () -> Unit,
    onThemeChanged: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember { SettingsStore(context) }
    val scope = rememberCoroutineScope()
    var themeMode by remember { mutableStateOf(store.themeMode) }
    var level by remember { mutableStateOf(store.defaultLevelLabel) }
    var clock by remember { mutableStateOf(store.defaultClockMinutes) }
    var analysisMs by remember { mutableStateOf(store.defaultAnalysisMs.toFloat()) }
    var soundOn by remember { mutableStateOf(store.soundOn) }
    var updateText by remember { mutableStateOf<String?>(null) }
    var checking by remember { mutableStateOf(false) }
    var importOpen by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }
    var clearOpen by remember { mutableStateOf(false) }
    var exportBackup by remember { mutableStateOf(false) }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
        } catch (_: Exception) {
            "?"
        }
    }

    val importFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val text = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()?.readText().orEmpty()
                val result = applyBackupJson(context, text)
                if (result != null) {
                    themeMode = store.themeMode
                    onThemeChanged(themeMode)
                    toast(result)
                } else {
                    toast("这个文件不是将！的备份")
                }
            } catch (_: Exception) {
                toast("读取文件失败")
            }
        }
    }
    val exportFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()
                    ?.use { it.write(buildBackupJson(context)) }
                toast("备份已导出")
            } catch (_: Exception) {
                toast("导出失败")
            }
        }
    }
    LaunchedEffect(exportBackup) {
        if (exportBackup) {
            exportBackup = false
            exportFileLauncher.launch("jiang-backup.json")
        }
    }

    fun checkUpdate() {
        if (checking) return
        checking = true
        updateText = "正在检查……"
        scope.launch {
            val outcome = withContext(Dispatchers.IO) {
                try {
                    val conn = URL(
                        "https://api.github.com/repos/liyuzhou79-star/TuxunXiangqi/releases/latest"
                    ).openConnection() as HttpURLConnection
                    conn.connectTimeout = 8000
                    conn.readTimeout = 8000
                    conn.setRequestProperty("Accept", "application/vnd.github+json")
                    try {
                        when (conn.responseCode) {
                            200 -> {
                                val body = conn.inputStream.bufferedReader().readText()
                                val tag = JSONObject(body).optString("tag_name", "")
                                "FOUND:" + tag.removePrefix("v")
                            }
                            404 -> "NORELEASE"
                            else -> "HTTP" + conn.responseCode
                        }
                    } finally {
                        conn.disconnect()
                    }
                } catch (_: Exception) {
                    "NETERR"
                }
            }
            checking = false
            updateText = when {
                outcome == "NORELEASE" ->
                    "项目还没有正式发布页，目前新版本通过测试包发放；这里以后会自动对上。"
                outcome == "NETERR" ->
                    "网络没通，没查成。等有网了再点一次。"
                outcome.startsWith("FOUND:") -> {
                    val latest = outcome.removePrefix("FOUND:")
                    if (latest.isNotBlank() && latest != versionName) {
                        "发现新版本 $latest（你现在是 $versionName），去发布页下载最新安装包。"
                    } else {
                        "你用的已经是最新版本（$versionName）。"
                    }
                }
                else -> "检查没成功（$outcome），稍后再试。"
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
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(14.dp))

        SettingsCard(title = "外观") {
            Text("主题", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((value, label) in listOf("system" to "跟随系统", "light" to "浅色", "dark" to "深色")) {
                    FilterChip(
                        selected = themeMode == value,
                        onClick = {
                            themeMode = value
                            store.themeMode = value
                            onThemeChanged(value)
                        },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("音效", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "落子、吃子、将军、胜负都有短音效，全部由程序现场合成，不占素材文件。关掉就全 App 静音。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = soundOn,
                    onCheckedChange = {
                        soundOn = it
                        store.soundOn = it
                    }
                )
            }
        }

        SettingsCard(title = "对弈默认") {
            Text("默认难度", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (l in listOf("休闲", "普通", "高手")) {
                    FilterChip(
                        selected = level == l,
                        onClick = {
                            level = l
                            store.defaultLevelLabel = l
                        },
                        label = { Text(l) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("默认棋钟", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((m, label) in listOf(0 to "不限时", 10 to "10 分钟", 20 to "20 分钟", 30 to "30 分钟")) {
                    FilterChip(
                        selected = clock == m,
                        onClick = {
                            clock = m
                            store.defaultClockMinutes = m
                        },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = "默认分析时长：" + (analysisMs / 1000f) + " 秒（对局页里还能临时调）",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = analysisMs,
                onValueChange = { analysisMs = it },
                onValueChangeFinished = { store.defaultAnalysisMs = analysisMs.toInt() },
                valueRange = 500f..4000f,
                steps = 6
            )
        }

        SettingsCard(title = "我的数据") {
            Text(
                text = "战绩、棋谱总数、解过的残局，都在战绩页里。",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenStats) { Text("看战绩") }
            }
            Spacer(Modifier.height(14.dp))
            Text("备份与恢复", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "备份包含全部棋谱、错题本和设置（不含引擎文件）。恢复会覆盖当前数据，先导出一份更稳妥。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { exportBackup = true }) { Text("导出备份文件") }
                OutlinedButton(onClick = {
                    shareText(context, buildBackupJson(context), "将！数据备份")
                }) { Text("分享备份文本") }
                OutlinedButton(onClick = { importOpen = true }) { Text("粘贴恢复") }
                OutlinedButton(onClick = {
                    importFileLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                }) { Text("从文件恢复") }
            }
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = { clearOpen = true }) { Text("清空全部棋谱") }
        }

        SettingsCard(title = "检查更新") {
            Text(
                text = "只有点下面按钮时才会联网查一次，平时 App 不联网。",
                style = MaterialTheme.typography.bodyMedium
            )
            if (updateText != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = updateText!!,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { checkUpdate() }, enabled = !checking) {
                    Text(if (checking) "检查中…" else "检查更新")
                }
                OutlinedButton(onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/liyuzhou79-star/TuxunXiangqi/releases")
                        )
                    )
                }) { Text("打开发布页") }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "当前版本 $versionName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedButton(onClick = onBack) { Text("返回") }
        Spacer(Modifier.height(24.dp))
    }

    if (importOpen) {
        AlertDialog(
            onDismissRequest = { importOpen = false },
            title = { Text("粘贴备份文本恢复") },
            text = {
                Column {
                    Text(
                        text = "把备份文本整段粘贴进来。恢复会覆盖现在的棋谱、错题和设置。",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val result = applyBackupJson(context, importText)
                    if (result != null) {
                        themeMode = store.themeMode
                        onThemeChanged(themeMode)
                        toast(result)
                        importOpen = false
                        importText = ""
                    } else {
                        toast("没认出这段备份，确认粘贴的是完整备份文本")
                    }
                }) { Text("恢复") }
            },
            dismissButton = {
                TextButton(onClick = { importOpen = false }) { Text("取消") }
            }
        )
    }

    if (clearOpen) {
        AlertDialog(
            onDismissRequest = { clearOpen = false },
            title = { Text("清空全部棋谱？") },
            text = { Text("棋谱库里的对局会被全部删掉，找不回来。错题本和设置不动。") },
            confirmButton = {
                TextButton(onClick = {
                    RecordStore(context).replaceAll(emptyList())
                    clearOpen = false
                    toast("棋谱已清空")
                }) { Text("清空") }
            },
            dismissButton = {
                TextButton(onClick = { clearOpen = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun SettingsCard(
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
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
    Spacer(Modifier.height(12.dp))
}
