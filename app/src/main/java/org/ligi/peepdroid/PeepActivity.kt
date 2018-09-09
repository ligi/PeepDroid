package org.ligi.peepdroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_peep.*
import org.koin.android.ext.android.inject
import org.ligi.kaxtui.alert
import org.ligi.peepdroid.model.PeepAPI

class PeepActivity : AppCompatActivity() {

    private val peepAPI: PeepAPI by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peep)

        fab.setOnClickListener {
            val response = peepAPI.peep(peep_input.text.toString())
            if (response.code() != 200) {
                alert("could not send peep: " +response.body()?.string())
            } else {
                finish()
            }
        }
    }

}
