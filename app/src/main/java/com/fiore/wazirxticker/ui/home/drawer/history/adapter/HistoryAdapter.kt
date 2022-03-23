package com.fiore.wazirxticker.ui.home.drawer.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fiore.wazirxticker.data.database.entities.History
import com.fiore.wazirxticker.databinding.ListItemHistoryBinding

data class HistoryAdapter(
    private val inflater: LayoutInflater,
    private val onEditClick: (History) -> Unit
) : ListAdapter<History, HistoryAdapter.ViewHolder>(DiffUtilCallback) {

    object DiffUtilCallback : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(
        private val binding: ListItemHistoryBinding,
        private val onEditClick: (History) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.history = history
            binding.executePendingBindings()
        }

        init {
            binding.editHistory.setOnClickListener {
                binding.history?.let(onEditClick)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemHistoryBinding.inflate(inflater, parent, false), onEditClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}