package org.ligi.peepdroid.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.ligi.peepdroid.R
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.Settings

class PeepAdapter(private val list: List<Peep>,
                  private val settings: Settings,
                  private val peepAPI: PeepAPI) : RecyclerView.Adapter<PeepViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeepViewHolder {
        val li = LayoutInflater.from(parent.context)
        return PeepViewHolder(li.inflate(R.layout.peep, parent, false), settings, peepAPI)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: PeepViewHolder, position: Int) {
        holder.bind(list[position])
    }

}