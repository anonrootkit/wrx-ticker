package com.fiore.wazirxticker.ui.home.investments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiore.wazirxticker.data.database.entities.Investment
import com.fiore.wazirxticker.databinding.ListItemInvestmentBinding

class InvestmentsAdapter(private val inflater: LayoutInflater) :
    ListAdapter<Investment, InvestmentsAdapter.InvestmentViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Investment>() {
        override fun areItemsTheSame(oldItem: Investment, newItem: Investment): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Investment, newItem: Investment): Boolean =
            oldItem == newItem
    }

    inner class InvestmentViewHolder(
        private val binding: ListItemInvestmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(investment: Investment) {
            binding.investment = investment
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentViewHolder {
        return InvestmentViewHolder(
            ListItemInvestmentBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InvestmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}