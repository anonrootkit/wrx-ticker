package com.fiore.wazirxticker.ui.home.coins.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiore.wazirxticker.data.database.entities.Coin
import com.fiore.wazirxticker.databinding.ListItemCoinBinding

class CoinsAdapter(private val inflater: LayoutInflater) :
    ListAdapter<Coin, CoinsAdapter.CoinViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Coin>() {
        override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean =
            oldItem == newItem
    }

    inner class CoinViewHolder(
        private val binding: ListItemCoinBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(coin: Coin) {
            binding.coin = coin
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        return CoinViewHolder(
            ListItemCoinBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}