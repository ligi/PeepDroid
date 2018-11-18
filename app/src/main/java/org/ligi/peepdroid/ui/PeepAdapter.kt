package org.ligi.peepdroid.ui

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import org.ligi.peepdroid.R
import org.ligi.peepdroid.api.PeepAPI
import org.ligi.peepdroid.model.Peep
import org.ligi.peepdroid.model.PeepDao
import org.ligi.peepdroid.model.Settings


class PeepDiffCallback : DiffUtil.ItemCallback<Peep>() {

    override fun areItemsTheSame(oldItem: Peep, newItem: Peep): Boolean {
        return oldItem.ipfs == newItem.ipfs
    }

    override fun areContentsTheSame(oldItem: Peep, newItem: Peep): Boolean {
        return oldItem == newItem
    }
}

class PeepAdapter(private val settings: Settings,
                  private val peepAPI: PeepAPI,
                  private val peepDao: PeepDao) : PagedListAdapter<Peep, PeepViewHolder>(PeepDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeepViewHolder {
        val li = LayoutInflater.from(parent.context)
        return PeepViewHolder(li.inflate(R.layout.peep, parent, false), settings, peepAPI, peepDao)
    }

    override fun onBindViewHolder(holder: PeepViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}