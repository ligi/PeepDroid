package org.ligi.peepdroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_info.*
import org.koin.android.ext.android.inject
import org.ligi.compat.HtmlCompat
import org.ligi.peepdroid.model.PeepAPI

class InfoActivity : AppCompatActivity() {

    private val peepAPI: PeepAPI by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.subtitle = "Info"
        info_text.text = HtmlCompat.fromHtml(getString(R.string.info_text))
        info_text.movementMethod = LinkMovementMethod()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        android.R.id.home -> true.also { finish() }
        else -> super.onOptionsItemSelected(item)
    }
}
