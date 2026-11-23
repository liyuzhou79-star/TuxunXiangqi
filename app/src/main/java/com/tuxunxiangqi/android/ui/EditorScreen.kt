package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side

/** Position validation for the editor: returns a plain-language
 *  problem, or null when the position can start a game. */
fun validatePosition(cells: List<Piece?>): String? {
    val redG = Rules.generalPos(cells, Side.RED)
    val blackG = Rules.generalPos(cells, Side.BLACK)
    if (redG == null) return "红方还没有放帅"
    if (blackG == null) return "黑方还没有放将"
    fun inPalace(side: Side, p: Pos): Boolean {
        if (p.col !in 3..5) return false
        return if (side == Side.RED) p.row in 7..9 else p.row in 0..2
    }
    if (!inPalace(Side.RED, redG)) return "红帅必须放在红方九宫里"
    if (!inPalace(Side.BLACK, blackG)) return "黑将必须放在黑方九宫里"
    if (redG.col == blackG.col) {
        var blocked = false
        val lo = minOf(redG.row, blackG.row)
        val hi = maxOf(redG.row, blackG.row)
        for (r in (lo + 1) until hi) {
            if (cells[r * 9 + redG.col] != null) blocked = true
        }
        if (!blocked) return "将帅不能照面，中间至少要隔一个棋子"
    }
    // Advisors must be in the palace, elephants on their own half.
    for (r in 0..9) {
        for (c in 0..8) {
            val p = cells[r * 9 + c] ?: continue
            val pos = Pos(c, r)
            when (p.type) {
                PieceType.ADVISOR ->
                    if (!inPalace(p.side, pos)) return "仕/士只能放在九宫里"
                PieceType.ELEPHANT -> {
                    val ok = if (p.side == Side.RED) r in 5..9 else r in 0..4
                    if (!ok) return "相/象不能过河"
                }
                else -> {}
            }
        }
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
    onBack: () -> Unit,
    onStart: (List<Piece?>, BoardMode) -> Unit,
    initialFen: String? = null
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    // An initial FEN can be handed in (1.27.0: the records page
    // import box recognises a bare FEN and routes it here); the
    // editor still normalises to Red to move, as its FEN card
    // explains. An unparseable hand-in simply starts standard.
    val state = remember {
        val gs = GameState()
        if (initialFen != null) {
            Fen.fromFen(initialFen)?.let { (cells, _) ->
                gs.loadPosition(cells, Side.RED)
            }
        }
        gs
    }
    // null tool = eraser.
    var tool by remember { mutableStateOf<Piece?>(Piece(PieceType.CHARIOT, Side.RED)) }
    var erase by remember { mutableStateOf(false) }
    var version by remember { mutableStateOf(0) }
    val problem = remember(version) { validatePosition(state.cells) }
    // FEN of the position being edited (the editor always starts
    // Red to move), plus the paste-a-FEN import dialog state.
    val fenText = remember(version) { Fen.toFen(state.cells, Side.RED) }
    var fenImportOpen by remember { mutableStateOf(false) }
    var fenImportText by remember { mutableStateOf("") }

    fun toast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun syncFromCells(cells: List<Piece?>) {
        state.loadPosition(cells, Side.RED)
        version++
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("摆棋编辑") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "先选棋子，再点棋盘放下。点已放的格子可以换子，选橡皮擦再点就是拿走。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            BoardFrame(modifier = Modifier.fillMaxWidth()) {
                XiangqiBoard(
                    state = state,
                    selected = null,
                    legalTargets = emptyList(),
                    onTap = { pos ->
                        val cells = state.cells.toMutableList()
                        cells[pos.row * 9 + pos.col] = if (erase) null else tool
                        syncFromCells(cells)
                    }
                )
            }
            Spacer(Modifier.height(12.dp))
            for (side in listOf(Side.RED, Side.BLACK)) {
                Text(
                    text = if (side == Side.RED) "红方" else "黑方",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow {
                    for (type in PieceType.entries) {
                        val piece = Piece(type, side)
                        FilterChip(
                            selected = !erase && tool == piece,
                            onClick = {
                                erase = false
                                tool = piece
                            },
                            label = { Text(type.label(side)) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
            FlowRow {
                FilterChip(
                    selected = erase,
                    onClick = { erase = true },
                    label = { Text("橡皮擦") },
                    modifier = Modifier.padding(end = 6.dp)
                )
                // Clear / reset are actions, not selectable options:
                // real buttons, not chips (M3 semantics audit).
                OutlinedButton(
                    onClick = {
                        val cells = MutableList<Piece?>(90) { null }
                        syncFromCells(cells)
                    },
                    modifier = Modifier.padding(end = 6.dp)
                ) { Text("清空") }
                OutlinedButton(
                    onClick = {
                        state.reset()
                        version++
                    }
                ) { Text("恢复初始局面") }
            }
            Spacer(Modifier.height(12.dp))
            if (problem != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = problem,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "这个局面的 FEN",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = fenText,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow {
                        OutlinedButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(fenText))
                                toast("FEN 已复制")
                            },
                            modifier = Modifier.padding(end = 6.dp)
                        ) { Text("复制 FEN") }
                        OutlinedButton(
                            onClick = { shareText(context, fenText, "将！摆棋局面 FEN") },
                            modifier = Modifier.padding(end = 6.dp)
                        ) { Text("分享 FEN") }
                        OutlinedButton(
                            onClick = {
                                fenImportText = ""
                                fenImportOpen = true
                            }
                        ) { Text("粘贴 FEN 摆棋") }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "把 FEN 发给朋友，对方粘贴进来就能摆出同一个局面；从别处抄来的 FEN 也能粘贴进来试下。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { onStart(state.cells.toList(), BoardMode.PVP) },
                    enabled = problem == null,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("人人对弈")
                }
                Button(
                    onClick = { onStart(state.cells.toList(), BoardMode.AI) },
                    enabled = problem == null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("人机对弈")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "人机对弈时你执红、引擎执黑，从这个局面开始。红方先行。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
        }

        if (fenImportOpen) {
            AlertDialog(
                onDismissRequest = { fenImportOpen = false },
                title = { Text("粘贴 FEN 摆棋") },
                text = {
                    Column {
                        Text(
                            text = "把一段 FEN 粘贴进来，棋盘会换成那个局面。编辑器固定红方先行，FEN 里写的轮次不影响开局方。",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fenImportText,
                            onValueChange = { fenImportText = it },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val parsed = Fen.fromFen(fenImportText)
                        if (parsed != null) {
                            syncFromCells(parsed.first)
                            fenImportOpen = false
                            toast("已按 FEN 摆好局面")
                        } else {
                            toast("没认出这段 FEN，检查一下是不是少行或多了字符")
                        }
                    }) { Text("摆棋") }
                },
                dismissButton = {
                    TextButton(onClick = { fenImportOpen = false }) { Text("取消") }
                }
            )
        }
    }
}
