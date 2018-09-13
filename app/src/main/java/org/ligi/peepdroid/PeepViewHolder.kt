package org.ligi.peepdroid

import android.support.v7.widget.RecyclerView
import android.text.TextUtils.isEmpty
import android.text.format.DateUtils
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import kotlinx.android.synthetic.main.peep.view.*
import org.ligi.peepdroid.model.Peep
import java.util.*

class PeepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(peep: Peep) {
        bind(peep, itemView)
    }

    fun bind(peep: Peep, view: View) {
        view.peep_text.text = peep.content

        Linkify.addLinks(view.peep_text, Linkify.ALL)
        view.peeper_text.text = if (peep.realName.isNotBlank()) {
            peep.realName
        } else {
            peep.name
        }

        val asDate = Date(peep.timestamp * 1000)
        view.peep_time_text.text = DateUtils.getRelativeTimeSpanString(asDate.time, Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
        if (!isEmpty(peep.avatarUrl)) {
            val avatarSplit = peep.avatarUrl.split(":")
            UrlImageViewHelper.setUrlDrawable(view.avatar_image, "https://peepeth.s3-us-west-1.amazonaws.com/images/avatars/" + avatarSplit[1] + "/small." + avatarSplit[2])
        }

        if (peep.parent != null) {
            val parent = LayoutInflater.from(view.context).inflate(R.layout.peep, view.parent_container, false)
            bind(peep.parent, parent)
            view.parent_container.addView(parent)
        }
    }
}
