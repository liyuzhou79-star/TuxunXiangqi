package com.tuxunxiangqi.android

import com.tuxunxiangqi.android.ui.isNewerVersion
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pins the update check's version comparison (1.110.0): numeric
 *  segment comparison, so three-digit minors order correctly and
 *  an install ahead of the release page is not told to "update"
 *  to an older version. */
class UpdateVersionTest {

    @Test
    fun threeDigitMinorBeatsTwoDigit() {
        assertTrue(isNewerVersion("1.100.0", "1.99.0"))
        assertFalse(isNewerVersion("1.99.0", "1.100.0"))
    }

    @Test
    fun equalIsNotNewer() {
        assertFalse(isNewerVersion("1.110.0", "1.110.0"))
    }

    @Test
    fun majorAndPatchOrder() {
        assertTrue(isNewerVersion("2.0.0", "1.999.0"))
        assertTrue(isNewerVersion("1.110.1", "1.110.0"))
        assertFalse(isNewerVersion("1.110.0", "1.110.1"))
    }

    @Test
    fun unparseableIsNeverNewer() {
        assertFalse(isNewerVersion("", "1.0.0"))
        assertFalse(isNewerVersion("abc", "1.0.0"))
        assertFalse(isNewerVersion("1.0.0", ""))
    }

    @Test
    fun vPrefixTolerated() {
        assertTrue(isNewerVersion("v1.111.0", "1.110.0"))
    }
}
