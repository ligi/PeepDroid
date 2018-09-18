package org.ligi.peepdroid

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.isEmpty
import android.text.format.DateUtils
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import kotlinx.android.synthetic.main.peep.view.*
import org.ligi.kaxt.setVisibility
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.Settings
import java.util.*

class PeepViewHolder(itemView: View,private val settings: Settings) : RecyclerView.ViewHolder(itemView) {

    fun bind(peep: Peep) {
        bind(peep, itemView)
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
            view.peep_time_text.text = DateUtils.getRelativeTimeSpanString(asDate.time, Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            view.peep_time_text.visibility = View.VISIBLE
        } else {
            view.peep_time_text.visibility = View.GONE
        }

        if (settings.isAvatarsWanted()) {
            if (!isEmpty(peep.avatarUrl)) {
                val avatarSplit = peep.avatarUrl.split(":")
                UrlImageViewHelper.setUrlDrawable(view.avatar_image, "https://peepeth.s3-us-west-1.amazonaws.com/images/avatars/" + avatarSplit[1] + "/small." + avatarSplit[2])
            } else {
                view.avatar_image.setImageResource(R.mipmap.ic_launcher)
            }
            view.avatar_image.visibility = View.VISIBLE
        } else {
            view.avatar_image.visibility = View.GONE
        }

        view.controls_container.setVisibility(showControls)

        view.reply_btn.setOnClickListener {
            startPeepActivity(view, peep,"REPLY")
        }

        view.repeep_btn.setOnClickListener {
            startPeepActivity(view, peep, "REPEEP")
        }

        if (peep.parent != null) {
            val parent = LayoutInflater.from(view.context).inflate(R.layout.peep, view.parent_container, false)
            bind(peep.parent, parent)
            view.parent_container.addView(parent)
        } else {
            view.parent_container.visibility = View.GONE
        }
    }

    private fun startPeepActivity(view: View, peep: Peep, mode: String) {
        val intent = Intent(view.context, PeepActivity::class.java)
        intent.putExtra("PEEP", peep)
        intent.putExtra(mode, true)
        view.context.startActivity(intent)
    }
}
