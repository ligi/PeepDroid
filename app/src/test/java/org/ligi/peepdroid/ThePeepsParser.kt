package org.ligi.peepdroid

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.ligi.peepdroid.api.parsePeeps

class ThePeepsParser {
    @Test
    fun canParsePeeps() {
        assertThat(parsePeeps(javaClass.getResource("/peeps.json").readText()).size)
                .isEqualTo(25)
    }
}