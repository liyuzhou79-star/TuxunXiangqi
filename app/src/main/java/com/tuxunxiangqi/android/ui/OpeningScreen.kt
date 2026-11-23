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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.OpeningBook
import kotlinx.coroutines.launch

/** Browsable opening book (1.15.0). The same book the game
 *  screen's analysis panel quotes from, laid out as a study
 *  page: search by 定式 name or by move notation, tap any line
 *  to see its position on the board with the suggested moves
 *  as arrows. Read-only — studying never starts a game. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OpeningScreen(
    onBack: () -> Unit,
    /** Start a real game from the previewed position (1.16.0):
     *  cells, whether Red is to move, the game mode, and the
     *  book history that led here (1.18.0: so the game screen
     *  can keep quoting book lines from this point on). */
    onStartGame: (List<com.tuxunxiangqi.android.xiangqi.Piece?>, Boolean, BoardMode, List<String>) -> Unit =
        { _, _, _, _ -> }
) {
    var query by remember { mutableStateOf("") }
    val allNodes = remember { OpeningBook.allNodes() }
    val totalSuggestions = remember { allNodes.sumOf { it.suggestions.size } }
    val nodes = remember(query) { OpeningBook.search(query) }

    var selHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var selUci by remember { mutableStateOf<String?>(
        OpeningBook.lookup(emptyList()).firstOrNull()?.first
    ) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val suggestions = remember(selHistory) { OpeningBook.lookup(selHistory) }
    val preview = remember(selHistory) {
        GameState().also { gs ->
            for (uci in selHistory) {
                val mv = Fen.parseUciMove(uci) ?: break
                if (!gs.tryMove(mv.first, mv.second)) break
            }
        }
    }
    val historyText = remember(selHistory) {
        if (selHistory.isEmpty()) {
            "起始局面"
        } else {
            OpeningBook.historyNotation(selHistory).joinToString(" · ")
        }
    }
    val selName = suggestions.firstOrNull { it.first == selUci }?.second

    fun selectNode(node: OpeningBook.BookNode) {
        selHistory = node.historyUci
        selUci = node.suggestions.firstOrNull()?.first
        scope.launch { scrollState.animateScrollTo(0) }
    }

    Scaffold(
        topBar = { SubPageTopBar("开局库", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "内置定式共 " + allNodes.size + " 个节点、" + totalSuggestions +
                " 条建议着法，对局分析时也会自动引用。点任意一条线，" +
                "棋盘会摆出那个局面，建议着法用箭头标出来。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("搜索定式或着法，如：中炮、顺炮、马二进三") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = historyText +
                "（" + (if (OpeningBook.turnAfter(selHistory) ==
                    com.tuxunxiangqi.android.xiangqi.Side.RED
                ) "红" else "黑") + "方走棋）" +
                if (selName != null) "（建议：$selName）" else "",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        BoardFrame(modifier = Modifier.fillMaxWidth()) {
            XiangqiBoard(
                state = preview,
                selected = null,
                legalTargets = emptyList(),
                hint = selUci?.let { Fen.parseUciMove(it) },
                onTap = {}
            )
        }
        if (suggestions.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for ((uci, name) in suggestions) {
                    FilterChip(
                        selected = uci == selUci,
                        onClick = { selUci = uci },
                        label = { Text(name) }
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            androidx.compose.material3.Button(
                onClick = {
                    onStartGame(
                        preview.cells.toList(),
                        OpeningBook.turnAfter(selHistory) ==
                            com.tuxunxiangqi.android.xiangqi.Side.RED,
                        BoardMode.AI,
                        selHistory
                    )
                }
            ) { Text("从这个局面人机对弈") }
            OutlinedButton(
                onClick = {
                    onStartGame(
                        preview.cells.toList(),
                        OpeningBook.turnAfter(selHistory) ==
                            com.tuxunxiangqi.android.xiangqi.Side.RED,
                        BoardMode.PVP,
                        selHistory
                    )
                }
            ) { Text("从这个局面人人对弈") }
        }
        Spacer(Modifier.height(16.dp))
        if (nodes.isEmpty()) {
            Text(
                text = "没找到匹配的定式。换个词试试，比如「中炮」「飞相」「仙人指路」，" +
                    "或者直接搜着法记谱，如「炮二平五」。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        for (node in nodes) {
            val isSel = node.historyUci == selHistory
            Card(
                onClick = { selectNode(node) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSel) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (node.historyUci.isEmpty()) {
                            "起始局面"
                        } else {
                            OpeningBook.historyNotation(node.historyUci)
                                .joinToString(" · ")
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = node.suggestions.joinToString(" · ") { it.second },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(24.dp))
    }
    }
}
