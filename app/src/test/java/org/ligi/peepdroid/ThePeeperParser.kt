package org.ligi.peepdroid

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.ligi.peepdroid.api.parsePeeper

class ThePeeperParser {
    @Test
    fun canParsePeepers() {
        val parsed = parsePeeper(javaClass.getResource("/peeper.json").readText())

        assertThat(parsed).isNotNull()
        assertThat(parsed!!.name).isEqualTo("ligi")
        assertThat(parsed.following.size).isEqualTo(110)
    }
}