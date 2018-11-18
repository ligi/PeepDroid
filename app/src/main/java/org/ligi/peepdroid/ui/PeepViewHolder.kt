package org.ligi.peepdroid.ui

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.isEmpty
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.getRelativeTimeSpanString
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import kotlinx.android.synthetic.main.peep.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ligi.kaxt.setVisibility
import org.ligi.kaxtui.alert
import org.ligi.peepdroid.R
import org.ligi.peepdroid.activities.PeepActivity
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.PeepDao
import org.ligi.peepdroid.model.Settings
import java.util.*

class PeepViewHolder(itemView: View,
                     private val settings: Settings,
                     private val peepAPI: PeepAPI,
                     private val peepDao: PeepDao
) : RecyclerView.ViewHolder(itemView) {

    fun bind(peep: Peep?) {
        peep?.run {
            bind(peep, itemView)
        }

    }

    fun bind(peep: Peep, view: View, showControls: Boolean = true) {
        view.peep_text.text = peep.content

        Linkify.addLinks(view.peep_text, Linkify.ALL)
        view.peeper_text.text = if (peep.realName.isNotBlank()) {
            peep.realName
        } else {
            peep.name
        }

        if (settings.isTimeWanted()) {
            val asDate = Date(peep.timestamp * 1000)
            val timeInMillis = Calendar.getInstance().timeInMillis
            val timeString = getRelativeTimeSpanString(asDate.time, timeInMillis, MINUTE_IN_MILLIS)

            view.peep_time_text.text = if (timeString.startsWith("0 ")) "just now" else timeString
            view.peep_time_text.visibility = View.VISIBLE
        } else {
            view.peep_time_text.visibility = View.GONE
        }


        if (peep.image_url != null) {
            UrlImageViewHelper.setUrlDrawable(view.peep_image, peep.image_url)
            view.peep_image.visibility = View.VISIBLE
        } else {
            view.peep_image.visibility = View.GONE
        }

        if (settings.isAvatarsWanted()) {
            if (!isEmpty(peep.avatarUrl)) {
                UrlImageViewHelper.setUrlDrawable(view.avatar_image, peep.avatarUrl.asPeepethImageURL("avatars"))
            } else {
                view.avatar_image.setImageResource(R.mipmap.ic_launcher)
            }
            view.avatar_image.visibility = View.VISIBLE
        } else {
            view.avatar_image.visibility = View.GONE
        }

        view.controls_container.setVisibility(showControls)

        view.reply_btn.setOnClickListener {
            startPeepActivity(view, peep, "REPLY")
        }

        view.repeep_btn.setOnClickListener {
            startPeepActivity(view, peep, "REPEEP")
        }

        val parentPeepId = peep.parentId ?: peep.shareId
        if (parentPeepId != null) {
            val parent = LayoutInflater.from(view.context).inflate(R.layout.peep, view.parent_container, false)
            val parentPeep = peepDao.getPeepByID(parentPeepId)
            bind(parentPeep, parent)
            view.parent_container.removeAllViews()
            view.parent_container.addView(parent)
        } else {
            view.parent_container.visibility = View.GONE
        }

        view.esno_btn.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val result = withContext(Dispatchers.Default) { peepAPI.love(peep.ipfs) }

                when (result.code()) {
                    200 -> Snackbar.make(view.rootView, "Ensō offered successfully", Snackbar.LENGTH_LONG).show()
                    422 -> view.context.alert("Please login")
                    406 -> view.context.alert("Cannot offer esno yet - please wait")
                    else -> view.context.alert("Could not offer esno " + withContext(Dispatchers.Default) { result.body()?.string() })
                }

            }

        }
    }

    private fun startPeepActivity(view: View, peep: Peep, mode: String) {
        val intent = Intent(view.context, PeepActivity::class.java)
        intent.putExtra("PEEP", peep)
        intent.putExtra(mode, true)
        view.context.startActivity(intent)
    }
}

fun String.asPeepethImageURL(path: String, size: String = "small") = split(":").let {
    "https://peepeth.s3-us-west-1.amazonaws.com/images/$path/" + it[1] + "/$size." + it[2]
}
