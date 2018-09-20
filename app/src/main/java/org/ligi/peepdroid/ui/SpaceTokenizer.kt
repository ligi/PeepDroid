package org.ligi.peepdroid.ui

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.MultiAutoCompleteTextView

class SpaceTokenizer : MultiAutoCompleteTextView.Tokenizer {
    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor

        while (i > 0 && text[i - 1] != ' ') {
            i--
        }

        return i
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        var i = cursor
        val len = text.length

        while (i < len) {
            if (text[i] == ' ') {
                return i
            } else {
                i++
            }
        }

        return len
    }

    override fun terminateToken(text: CharSequence) = if (text.isNotEmpty() && text[text.length - 1] == ' ') {
        text
    } else {
        if (text is Spanned) {
            val sp = SpannableString(text.toString() + ", ")
            TextUtils.copySpansFrom(text, 0, text.length,
                    Any::class.java, sp, 0)
            sp
        } else {
            text.toString() + ""
        }
    }
}