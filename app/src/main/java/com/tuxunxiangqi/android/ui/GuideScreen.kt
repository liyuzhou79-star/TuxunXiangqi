package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.xiangqi.GameState
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.PieceType
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Rules
import com.tuxunxiangqi.android.xiangqi.Side

/** Beginner guide: every piece gets a small live board diagram —
 *  the piece is selected and its real legal targets are dotted,
 *  computed by the same rules the game uses. */
@Composable
fun GuideScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "新手指南",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "每种棋子一节：小棋盘上被选中的就是它，圆点是它现在能走的格子——都是按真实规则算出来的。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(14.dp))

        PieceSection(
            title = "车——横冲直撞的大将",
            body = "沿横线、竖线任意走几格都行，路上不能有子挡着。车是全场最强的子，一车十子寒，说的就是它。",
            pieces = listOf(
                Triple(PieceType.CHARIOT, Side.RED, Pos(4, 5))
            ),
            focus = Pos(4, 5)
        )
        PieceSection(
            title = "马——走日字，会被蹩腿",
            body = "马走「日」字：先横或竖走一格，再斜走一格。紧挨着它的出发方向若有子挡路（蹩马腿），那个方向就跳不过去。图中马的右边有卒挡着，所以右侧两个落点没有圆点。",
            pieces = listOf(
                Triple(PieceType.HORSE, Side.RED, Pos(4, 5)),
                Triple(PieceType.SOLDIER, Side.BLACK, Pos(5, 5))
            ),
            focus = Pos(4, 5)
        )
        PieceSection(
            title = "炮——隔子打子",
            body = "炮平时和车一样走直线；但吃子时必须隔着正好一个子（炮架）跳过去打。图中炮沿中线：隔着自己的兵，正好能打到最远处的黑卒，那个格子也在圆点里。",
            pieces = listOf(
                Triple(PieceType.CANNON, Side.RED, Pos(4, 5)),
                Triple(PieceType.SOLDIER, Side.RED, Pos(4, 3)),
                Triple(PieceType.SOLDIER, Side.BLACK, Pos(4, 1))
            ),
            focus = Pos(4, 5)
        )
        PieceSection(
            title = "象——走田字，不过河",
            body = "象走「田」字：斜着走两格。中间那个格子被堵住（塞象眼）就走不了。象只能在自己半场活动，永远不过河。图中左下方向被黑卒塞了象眼。",
            pieces = listOf(
                Triple(PieceType.ELEPHANT, Side.RED, Pos(4, 7)),
                Triple(PieceType.SOLDIER, Side.BLACK, Pos(3, 6))
            ),
            focus = Pos(4, 7)
        )
        PieceSection(
            title = "士——九宫里的斜线保镖",
            body = "士只能在九宫里斜走一格，四个方向来回。它的工作就是贴身保护将帅。",
            pieces = listOf(
                Triple(PieceType.ADVISOR, Side.RED, Pos(4, 8))
            ),
            focus = Pos(4, 8)
        )
        PieceSection(
            title = "将／帅——九宫之主",
            body = "将帅在九宫里横走或竖走一格。还有一条特殊规则：双方将帅若在同一条竖线上、中间没有子，就算「照面」，走棋的一方可以直接飞过去吃掉对方——所以对局中绝不能让两将照面。",
            pieces = listOf(
                Triple(PieceType.GENERAL, Side.RED, Pos(4, 9))
            ),
            focus = Pos(4, 9)
        )
        PieceSection(
            title = "兵／卒——过河就横着走",
            body = "兵没过河时只能向前走一格；过河之后多了横走一格的本事，但永远不能后退。图中的兵已经过河，所以前面、左面、右面都有圆点。",
            pieces = listOf(
                Triple(PieceType.SOLDIER, Side.RED, Pos(4, 4))
            ),
            focus = Pos(4, 4)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "将军、将死与困毙",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "攻击到对方将帅叫「将军」。被将军的一方必须立刻解围；如果怎么走都解不开，就是「将死」，对局结束。" +
                        "还有一种输法：如果轮到你走棋、你没有被将军、却一步合法棋都走不出来，那叫「困毙」，在象棋里困毙也算输（和国际象棋不同）。",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "怎么记棋谱",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "中文记谱按「子 + 路数 + 进／退／平 + 数字」来写：比如「炮二平五」是把二路的炮平移到五路；" +
                        "「马八进七」是八路的马向前跳到七路。红方路数用中文数字、从右往左数；黑方用阿拉伯数字、从左往右数。" +
                        "在本 App 的复盘和分析里看到的都是这种记谱，多看几局就熟了。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        OutlinedButton(onClick = onBack) { Text("返回") }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PieceSection(
    title: String,
    body: String,
    pieces: List<Triple<PieceType, Side, Pos>>,
    focus: Pos
) {
    val state = remember(title) {
        val cells = MutableList<Piece?>(90) { null }
        // Generals tucked into safe corners so every diagram is a
        // legal-looking position (never facing each other).
        if (pieces.none { it.first == PieceType.GENERAL && it.second == Side.BLACK }) {
            cells[0 * 9 + 3] = Piece(PieceType.GENERAL, Side.BLACK)
        }
        if (pieces.none { it.first == PieceType.GENERAL && it.second == Side.RED }) {
            cells[9 * 9 + 5] = Piece(PieceType.GENERAL, Side.RED)
        }
        for ((type, side, pos) in pieces) {
            cells[pos.row * 9 + pos.col] = Piece(type, side)
        }
        GameState().also { it.newGame(cells, Side.RED) }
    }
    val targets = remember(title) { Rules.legalTargets(state.cells, focus) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .width(230.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                XiangqiBoard(
                    state = state,
                    selected = focus,
                    legalTargets = targets,
                    hint = null,
                    onTap = {}
                )
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}
