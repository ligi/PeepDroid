package org.ligi.peepdroid.activities

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_peep.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import org.ligi.kaxtui.alert
import org.ligi.peepdroid.R
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.PeepethPicture
import org.ligi.peepdroid.model.REQUEST_CODE_PICK_IMAGE
import org.ligi.peepdroid.model.Settings
import org.ligi.peepdroid.ui.PeepViewHolder
import org.ligi.peepdroid.ui.SpaceTokenizer


class PeepActivity : AppCompatActivity() {

    private val peepAPI: PeepAPI by inject()
    private val settings: Settings by inject()

    private var image: PeepethPicture? = null

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
            if (image != null && (image?.ipfsHash == null || image?.serverID == null)) {
                showPeepingBusyIndicatorDialog()
            } else {

                doPeep(isReply, peep, isRepeep)

            }
        }

        val usernameArray = arrayOf("@peepeth", "@ligi", "@bevan", "@zen", "@ethberlin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, usernameArray)

        peep_input.setAdapter<ArrayAdapter<String>>(adapter)
        peep_input.setTokenizer(SpaceTokenizer())
    }

    private fun doPeep(isReply: Boolean, peep: Peep?, isRepeep: Boolean) = launch {
        val response = if (isReply && peep != null) {
            peepAPI.reply(peep_input.text.toString(), peep, image)
        } else if (isRepeep && peep != null) {
            peepAPI.share(peep_input.text.toString(), peep)
        } else {
            peepAPI.peep(peep_input.text.toString(), picture = image)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.peep, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> true.also { finish() }
        R.id.menu_add_image -> true.also {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PICK_IMAGE)

        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {


        if (resultCode == RESULT_OK && imageReturnedIntent != null) {

            val imageBytes = contentResolver.openInputStream(imageReturnedIntent.data).readBytes()
            image_preview.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size))
            image = PeepethPicture(null, null)
            fabProgressCircle.show()
            launch {
                val res = peepAPI.uploadImage(imageBytes)
                val string = res.body()?.string()

                string?.lines()?.forEach {
                    if (it.startsWith("Server")) {
                        image?.serverID = it.split(":").last().trim()
                    }
                    if (it.startsWith("IPFS")) {
                        image?.ipfsHash = it.split(":").last().trim()
                    }
                }

                launch(UI) {
                    fabProgressCircle.hide()
                }
            }

        }


        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
    }
}

fun Context.showPeepingBusyIndicatorDialog() {

    val llPadding = 30
    val ll = LinearLayout(this)
    ll.orientation = LinearLayout.HORIZONTAL
    ll.setPadding(llPadding, llPadding, llPadding, llPadding)
    ll.gravity = Gravity.CENTER
    var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
    llParam.gravity = Gravity.CENTER
    ll.layoutParams = llParam

    val progressBar = ProgressBar(this)
    progressBar.isIndeterminate = true
    progressBar.setPadding(0, 0, llPadding, 0)
    progressBar.layoutParams = llParam

    llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    llParam.gravity = Gravity.CENTER
    val tvText = TextView(this)
    tvText.text = "Peeping .."
    tvText.setTextColor(Color.parseColor("#000000"))
    tvText.textSize = 20f
    tvText.layoutParams = llParam

    ll.addView(progressBar)
    ll.addView(tvText)

    val builder = AlertDialog.Builder(this)
    builder.setCancelable(true)
    builder.setView(ll)

    val dialog = builder.create()
    dialog.show()
    val window = dialog.window
    if (window != null) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window.attributes)
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.window.attributes = layoutParams
    }
}
