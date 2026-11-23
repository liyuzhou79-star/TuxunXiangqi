package com.tuxunxiangqi.android.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.RecordStore
import com.tuxunxiangqi.android.data.filterRecords
import com.tuxunxiangqi.android.data.sortRecords
import com.tuxunxiangqi.android.data.parsePortableRecords
import com.tuxunxiangqi.android.data.sourceLabel
import com.tuxunxiangqi.android.data.toPortableText
import com.tuxunxiangqi.android.data.toPortableTextBundle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Game library: finished games land here automatically; records
 *  can be searched, filtered, renamed, deleted, exported as text
 *  or file, and imported back the same ways. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordsScreen(
    onBack: () -> Unit,
    onOpenReview: (GameRecord) -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val store = remember { RecordStore(context) }
    var records by remember { mutableStateOf(store.loadAll()) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("全部") }
    var resultFilter by remember { mutableStateOf("全部结果") }
    var sortOrder by remember { mutableStateOf("最新在前") }
    var renameTarget by remember { mutableStateOf<GameRecord?>(null) }
    var renameText by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<GameRecord?>(null) }
    var importOpen by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }
    // Text waiting to be written by the create-document launcher:
    // one record's portable text, or a whole selection's bundle.
    var exportText by remember { mutableStateOf<String?>(null) }
    // Multi-select mode (1.19.0): batch delete and batch export
    // for cleaning up or backing up the library in one go.
    var selectMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var deleteSelectedOpen by remember { mutableStateOf(false) }

    fun refresh() {
        records = store.loadAll()
        val alive = records.map { it.id }.toSet()
        selectedIds = selectedIds.intersect(alive)
    }

    fun toggleSelected(id: Long) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    val importFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val text = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()?.readText().orEmpty()
                val parsed = parsePortableRecords(text, System.currentTimeMillis())
                if (parsed.isNotEmpty()) {
                    parsed.forEach { store.save(it) }
                    refresh()
                    toast("导入成功，共 " + parsed.size + " 局")
                } else {
                    toast("这个文件不是将！的棋谱文本")
                }
            } catch (_: Exception) {
                toast("读取文件失败")
            }
        }
    }
    val exportFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        val text = exportText
        exportText = null
        if (uri != null && text != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()
                    ?.use { it.write(text) }
                toast("已导出到文件")
            } catch (_: Exception) {
                toast("导出失败")
            }
        }
    }

    LaunchedEffect(exportText) {
        if (exportText != null) {
            exportFileLauncher.launch("jiang-record.txt")
        }
    }

    val shown = remember(records, filter, query, resultFilter, sortOrder) {
        sortRecords(filterRecords(records, filter, query, resultFilter), sortOrder)
    }
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA) }

    // LazyColumn (1.13.0): with a few hundred records the old
    // eager Column composed every card at once and the page
    // opened slowly; now only the visible cards are composed.
    Scaffold(
        topBar = { SubPageTopBar("棋谱库", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column {
                Text(
                    text = "对局结束会自动存到这里；点开任意一局就能复盘。棋谱可以导出成文本或文件，换手机也不怕丢。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("搜索棋谱") },
                    leadingIcon = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                // Single-choice rows are M3 segmented buttons
                // (1.25.0), not stacks of filter chips; the
                // values behind the short labels are unchanged.
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val options = listOf(
                        "全部" to "全部", "人机对弈" to "人机",
                        "人人对弈" to "人人", "AI 表演赛" to "表演赛"
                    )
                    options.forEachIndexed { index, (value, label) ->
                        SegmentedButton(
                            selected = filter == value,
                            onClick = { filter = value },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size)
                        ) { Text(label) }
                    }
                }
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val options = listOf(
                        "全部结果" to "全部", "红方胜" to "红胜",
                        "黑方胜" to "黑胜", "和棋" to "和棋",
                        "未分胜负" to "未分"
                    )
                    options.forEachIndexed { index, (value, label) ->
                        SegmentedButton(
                            selected = resultFilter == value,
                            onClick = { resultFilter = value },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size)
                        ) { Text(label) }
                    }
                }
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    val options = listOf(
                        "最新在前" to "最新", "最早在前" to "最早",
                        "手数多到少" to "手数多", "手数少到多" to "手数少",
                        "标题" to "标题"
                    )
                    options.forEachIndexed { index, (value, label) ->
                        SegmentedButton(
                            selected = sortOrder == value,
                            onClick = { sortOrder = value },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size)
                        ) { Text(label) }
                    }
                }
                Spacer(Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { importOpen = true }) { Text("粘贴导入") }
                    OutlinedButton(onClick = {
                        importFileLauncher.launch(arrayOf("text/plain", "text/*"))
                    }) { Text("从文件导入") }
                    OutlinedButton(onClick = {
                        selectMode = !selectMode
                        if (!selectMode) selectedIds = emptySet()
                    }) { Text(if (selectMode) "退出多选" else "多选管理") }
                }
                if (selectMode) {
                    Spacer(Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "已选 " + selectedIds.size + " 局 · 当前列表 " +
                                    shown.size + " 局（全部 " + records.size + " 局）",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    selectedIds = selectedIds + shown.map { it.id }
                                }) { Text("全选当前列表") }
                                OutlinedButton(
                                    onClick = { selectedIds = emptySet() },
                                    enabled = selectedIds.isNotEmpty()
                                ) { Text("清空选择") }
                                OutlinedButton(
                                    onClick = {
                                        val sel = records.filter { it.id in selectedIds }
                                        clipboard.setText(
                                            AnnotatedString(sel.toPortableTextBundle())
                                        )
                                        toast("已复制 " + sel.size + " 局棋谱文本")
                                    },
                                    enabled = selectedIds.isNotEmpty()
                                ) { Text("复制所选") }
                                OutlinedButton(
                                    onClick = {
                                        exportText = records
                                            .filter { it.id in selectedIds }
                                            .toPortableTextBundle()
                                    },
                                    enabled = selectedIds.isNotEmpty()
                                ) { Text("导出所选到文件") }
                                TextButton(
                                    onClick = { deleteSelectedOpen = true },
                                    enabled = selectedIds.isNotEmpty()
                                ) { Text("删除所选") }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "点下面的棋谱卡就能勾选。导出的多局文本可以整段粘贴导入，局数不变。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        if (shown.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (records.isEmpty()) {
                            "还没有棋谱。去下一局，结束时会自动存进来；下到一半也可以在对局页点「存谱」。"
                        } else {
                            "没有符合条件的棋谱，换个筛选或搜索词试试。"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        items(shown, key = { it.id }) { r ->
            if (selectMode) {
                Card(
                    onClick = { toggleSelected(r.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (r.id in selectedIds) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
                        Checkbox(
                            checked = r.id in selectedIds,
                            onCheckedChange = { toggleSelected(r.id) }
                        )
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = r.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = dateFmt.format(Date(r.dateMs)) + " · 共 " +
                                    r.movesUci.size + " 手 · " + r.resultText + " · " + r.sourceLabel(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = r.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = dateFmt.format(Date(r.dateMs)) + " · 共 " +
                                r.movesUci.size + " 手 · " + r.resultText + " · " + r.sourceLabel(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { onOpenReview(r) }) {
                                Text("复盘")
                            }
                            OutlinedButton(onClick = {
                                renameTarget = r
                                renameText = r.title
                            }) { Text("改名") }
                            OutlinedButton(onClick = {
                                clipboard.setText(AnnotatedString(r.toPortableText()))
                                toast("棋谱文本已复制，去任意地方粘贴就能分享")
                            }) { Text("复制文本") }
                            OutlinedButton(onClick = {
                                shareText(context, r.toPortableText(), r.title)
                            }) { Text("分享") }
                            OutlinedButton(onClick = { exportText = r.toPortableText() }) {
                                Text("导出文件")
                            }
                            TextButton(onClick = { deleteTarget = r }) {
                                Text("删除")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
    }

    if (renameTarget != null) {
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("给棋谱改个名字") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val t = renameTarget
                    if (t != null && renameText.isNotBlank()) {
                        store.rename(t.id, renameText.trim())
                        refresh()
                    }
                    renameTarget = null
                }) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) { Text("取消") }
            }
        )
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除这局棋谱？") },
            text = { Text("删了就找不回来了。重要的棋谱建议先导出一份。") },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget?.let { store.delete(it.id) }
                    deleteTarget = null
                    refresh()
                }) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("取消") }
            }
        )
    }

    if (deleteSelectedOpen) {
        AlertDialog(
            onDismissRequest = { deleteSelectedOpen = false },
            title = { Text("删除选中的 " + selectedIds.size + " 局棋谱？") },
            text = { Text("删了就找不回来了。重要的棋谱建议先用「导出所选到文件」存一份。") },
            confirmButton = {
                TextButton(onClick = {
                    store.deleteMany(selectedIds)
                    deleteSelectedOpen = false
                    selectedIds = emptySet()
                    selectMode = false
                    refresh()
                    toast("已删除")
                }) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { deleteSelectedOpen = false }) { Text("取消") }
            }
        )
    }

    if (importOpen) {
        AlertDialog(
            onDismissRequest = { importOpen = false },
            title = { Text("粘贴棋谱文本导入") },
            text = {
                Column {
                    Text(
                        text = "把从将！导出的棋谱文本整段粘贴进来（包含 [FEN] 那一行）。多选导出的多局文本也能整段粘贴，局数不变。",
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
                    val parsed = parsePortableRecords(importText, System.currentTimeMillis())
                    if (parsed.isNotEmpty()) {
                        parsed.forEach { store.save(it) }
                        refresh()
                        toast("导入成功，共 " + parsed.size + " 局")
                        importOpen = false
                        importText = ""
                    } else {
                        toast("没认出这段文本，确认是从将！导出的棋谱")
                    }
                }) { Text("导入") }
            },
            dismissButton = {
                TextButton(onClick = { importOpen = false }) { Text("取消") }
            }
        )
    }
}
