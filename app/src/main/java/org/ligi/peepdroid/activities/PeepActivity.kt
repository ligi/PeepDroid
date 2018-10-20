package org.ligi.peepdroid.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_peep.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import org.ligi.kaxtui.alert
import org.ligi.peepdroid.R
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.Settings
import org.ligi.peepdroid.ui.PeepViewHolder
import org.ligi.peepdroid.ui.SpaceTokenizer

class PeepActivity : AppCompatActivity() {

    private val peepAPI: PeepAPI by inject()
    private val settings: Settings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peep)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val peep = if (intent.hasExtra("PEEP")) {
            intent.getParcelableExtra<Peep>("PEEP")
        } else null

        if (peep != null) {
            val peepView = LayoutInflater.from(this).inflate(R.layout.peep, peep_container)
            PeepViewHolder(peepView, settings, peepAPI).bind(peep, peepView, false)
        }

        val isReply = intent.getBooleanExtra("REPLY", false) && peep != null
        val isRepeep = intent.getBooleanExtra("REPEEP", false) && peep != null

        if (isReply) {
            supportActionBar?.subtitle = "Replying to " + peep?.name
        } else if (isRepeep) {
            supportActionBar?.subtitle = "Repeep " + peep?.name
        }

        fab.setOnClickListener {
            launch {
                val response = if (isReply && peep != null) {
                    peepAPI.reply(peep_input.text.toString(), peep)
                } else if (isRepeep && peep != null) {
                    peepAPI.share(peep_input.text.toString(), peep)
                } else {
                    peepAPI.peep(peep_input.text.toString())
                }

                val responseBody = response.body()?.string()

                launch(UI) {
                    if (response.code() != 200) {

                        alert("could not send peep: $responseBody")
                    } else {
                        finish()
                    }
                }
            }
        }

        val usernameArray = arrayOf("@peepeth", "@ligi", "@bevan", "@zen", "@ethberlin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, usernameArray)

        peep_input.setAdapter<ArrayAdapter<String>>(adapter)
        peep_input.setTokenizer(SpaceTokenizer())
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> true.also { finish() }
        else -> super.onOptionsItemSelected(item)
    }
}
