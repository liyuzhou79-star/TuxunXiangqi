package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side

enum class BoardMode { AI, PVP }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(mode: BoardMode, onBack: () -> Unit) {
    val state = remember { GameState() }
    var selected by remember { mutableStateOf<Pos?>(null) }
    val targets = remember(selected, state.lastMove) {
        selected?.let { Rules.legalTargets(state.cells, it) } ?: emptyList()
    }

    fun onTap(pos: Pos) {
        if (state.result != null) return
        val piece = state.pieceAt(pos)
        val sel = selected
        when {
            sel == null -> {
                if (piece != null && piece.side == state.turn) selected = pos
            }
            pos == sel -> selected = null
            piece != null && piece.side == state.turn -> selected = pos
            else -> {
                if (state.tryMove(sel, pos)) selected = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (mode == BoardMode.AI) "人机对弈" else "人人对弈")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 22.sp)
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
                .padding(horizontal = 16.dp)
        ) {
            // Turn status.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.turn == Side.RED)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onBackground
                        )
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = if (state.turn == Side.RED) "红方走棋" else "黑方走棋",
                    style = MaterialTheme.typography.titleMedium
                )
                if (state.result == null && state.inCheck) {
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "将军！",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "第 ${state.moveCount + 1} 手",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            state.result?.let { res ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (if (res.winner == Side.RED) "红方胜" else "黑方胜") +
                            " · " + res.reason,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            if (mode == BoardMode.AI) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "引擎对弈接入中：Pikafish 已能在 CI 编出 Android 版，" +
                            "正式接入对弈在下一里程碑。当前棋盘先以双人走棋演示。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            BoardFrame(modifier = Modifier.fillMaxWidth()) {
                XiangqiBoard(
                    state = state,
                    selected = selected,
                    legalTargets = targets,
                    onTap = { pos -> onTap(pos) }
                )
            }

            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { if (state.undo()) selected = null },
                    enabled = state.lastMove != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("悔棋")
                }
                Button(
                    onClick = { state.reset(); selected = null },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("重新开始")
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "点选棋子，再点高亮处落子。规则已校验：蹩马腿、塞象眼、" +
                    "炮须隔子吃、九宫与过河限制、将帅照面；不能送将，被将军时必须应将。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
