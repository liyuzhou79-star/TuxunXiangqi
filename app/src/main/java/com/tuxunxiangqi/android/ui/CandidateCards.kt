package com.tuxunxiangqi.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuxunxiangqi.android.engine.Candidate
import com.tuxunxiangqi.android.xiangqi.EvalUtil
import com.tuxunxiangqi.android.xiangqi.Fen
import com.tuxunxiangqi.android.xiangqi.Notation
import com.tuxunxiangqi.android.xiangqi.Piece
import com.tuxunxiangqi.android.xiangqi.Pos
import com.tuxunxiangqi.android.xiangqi.Side

/** One engine candidate prepared for display: rank, Chinese
 *  notation, the plain-language situation from Red's perspective
 *  and the first moves of the principal variation. The same card
 *  shape is used by the game screen's analysis panel, the review
 *  screen's deep analysis, the guess trainer and the puzzle
 *  hint — one vocabulary everywhere (unified in 1.14.0). */
data class CandidateCardData(
    val rank: Int,
    val moveUci: String,
    val from: Pos,
    val to: Pos,
    val title: String,
    val scoreText: String,
    val pvText: String
)

fun candidateRankLabel(rank: Int): String = when (rank) {
    1 -> "一选"
    2 -> "二选"
    3 -> "三选"
    4 -> "四选"
    else -> "第 $rank 选"
}

/** Builds display cards from raw engine candidates on the given
 *  position. Scores are converted to Red's perspective using
 *  [turn] (the side to move when the analysis ran). Candidates
 *  whose move cannot be parsed are dropped. */
fun buildCandidateCards(
    cells: List<Piece?>,
    turn: Side,
    candidates: List<Candidate>
): List<CandidateCardData> {
    val out = ArrayList<CandidateCardData>()
    for (cand in candidates.sortedBy { it.rank }) {
        val mv = Fen.parseUciMove(cand.moveUci) ?: continue
        val (redCp, redMate) = EvalUtil.toRedPerspective(
            cand.scoreCp, cand.mateIn, turn
        )
        out.add(
            CandidateCardData(
                rank = cand.rank,
                moveUci = cand.moveUci,
                from = mv.first,
                to = mv.second,
                title = Notation.moveText(cells, mv.first, mv.second),
                scoreText = EvalUtil.describeRed(redCp, redMate),
                pvText = Notation.pvText(cells, cand.pvUci, 5)
                    .joinToString(" ")
            )
        )
    }
    return out
}

/** The shared candidate card list: whole-card tap selects the
 *  candidate (the caller points the board arrow at it), the
 *  selected card wears the primary container colour, the rest
 *  the highest surface container — the same language as the
 *  game and review screens. [tagFor] may attach a short label
 *  (e.g. "你猜的这步") to a card. */
@Composable
fun CandidateCardList(
    cards: List<CandidateCardData>,
    selectedRank: Int?,
    onSelect: (CandidateCardData) -> Unit,
    tagFor: (CandidateCardData) -> String? = { null }
) {
    for ((i, card) in cards.withIndex()) {
        if (i > 0) Spacer(Modifier.height(6.dp))
        Card(
            onClick = { onSelect(card) },
            colors = CardDefaults.cardColors(
                containerColor = if (card.rank == (selectedRank ?: 1)) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row {
                    Text(
                        text = candidateRankLabel(card.rank),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val tag = tagFor(card)
                    if (tag != null) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = card.scoreText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (card.pvText.isNotBlank()) {
                    Text(
                        text = "变化：${card.pvText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
