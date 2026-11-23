package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.data.DeepStepCandidate
import com.tuxunxiangqi.android.data.GameRecord
import com.tuxunxiangqi.android.data.encodeDeepStep
import com.tuxunxiangqi.android.data.parseDeepStep
import com.tuxunxiangqi.android.data.parsePortableRecord
import com.tuxunxiangqi.android.data.toPortableText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Deep-analysis persistence (1.33.0): a step's candidates are
 *  encoded into one line, stored with the record and carried
 *  in the portable export; import must read them back exactly. */
class DeepCodecTest {

    private val sampleCandidates = listOf(
        DeepStepCandidate(
            "h2e2", 350, null, listOf("h2e2", "h9g7", "h0g2", "a9a8")
        ),
        DeepStepCandidate("b2e2", null, -3, listOf("b2e2", "b9c7")),
        DeepStepCandidate("c3c4", -25, null, emptyList()),
        DeepStepCandidate("g0e2", null, null, listOf("g0e2"))
    )

    @Test
    fun stepCodecRoundTrips() {
        val encoded = encodeDeepStep(sampleCandidates)
        assertEquals(sampleCandidates, parseDeepStep(encoded))
    }

    @Test
    fun stepCodecCapsThePvAtTwelve() {
        val longPv = (1..20).map { "a0a$it" }
        val encoded = encodeDeepStep(
            listOf(DeepStepCandidate("h2e2", 10, null, longPv))
        )
        assertEquals(12, parseDeepStep(encoded).single().pvUci.size)
    }

    @Test
    fun stepCodecSkipsMalformedEntries() {
        val parsed = parseDeepStep(";;,c10,;h2e2,c5,;bad")
        assertEquals(1, parsed.size)
        assertEquals("h2e2", parsed[0].moveUci)
        assertEquals(5, parsed[0].scoreCp)
    }

    @Test
    fun portableRoundTripCarriesDeepResults() {
        val deep = mapOf(
            0 to encodeDeepStep(sampleCandidates),
            7 to encodeDeepStep(
                listOf(DeepStepCandidate("a0a1", 88, null, listOf("a0a1")))
            )
        )
        val rec = GameRecord(
            id = 5L,
            title = "深析测试局",
            dateMs = 1234L,
            modeLabel = "人机对弈 · 执红",
            resultText = "红方胜",
            initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
            movesUci = listOf("h2e2", "h9g7"),
            evals = mapOf(0 to 10),
            marks = emptyMap(),
            deep = deep
        )
        val text = rec.toPortableText()
        assertTrue(text.contains("[深析]"))
        val back = parsePortableRecord(text, 99L)
        assertEquals(deep, back?.deep)
        // And the parsed-back steps decode to the same candidates.
        assertEquals(
            sampleCandidates,
            parseDeepStep(back!!.deep.getValue(0))
        )
    }

    @Test
    fun recordWithoutDeepExportsNoDeepLine() {
        val rec = GameRecord(
            id = 6L,
            title = "普通局",
            dateMs = 1L,
            modeLabel = "人人对弈",
            resultText = "和棋",
            initialFen = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1",
            movesUci = emptyList(),
            evals = emptyMap()
        )
        assertTrue(!rec.toPortableText().contains("[深析]"))
        assertEquals(
            emptyMap<Int, String>(),
            parsePortableRecord(rec.toPortableText(), 7L)?.deep
        )
    }
}
