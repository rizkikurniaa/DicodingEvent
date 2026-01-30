package com.dicoding.event.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.event.data.response.ListEventsItem
import com.dicoding.event.databinding.ItemEventBinding
import com.dicoding.event.databinding.ItemEventHorizontalBinding

class EventAdapter(
    private val viewTypeLayout: Int = VIEW_TYPE_VERTICAL,
    private val onItemClick: (ListEventsItem) -> Unit
) : ListAdapter<ListEventsItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewTypeLayout == VIEW_TYPE_HORIZONTAL) {
            val binding = ItemEventHorizontalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            HorizontalViewHolder(binding)
        } else {
            val binding = ItemEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            VerticalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        if (holder is HorizontalViewHolder) {
            holder.bind(event)
        } else if (holder is VerticalViewHolder) {
            holder.bind(event)
        }
        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    class VerticalViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvItemName.text = event.name
            Glide.with(binding.root.context)
                .load(event.mediaCover)
                .into(binding.imgItemPhoto)
        }
    }

    class HorizontalViewHolder(private val binding: ItemEventHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvItemName.text = event.name
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.imgItemPhoto)
        }
    }

    companion object {
        const val VIEW_TYPE_VERTICAL = 0
        const val VIEW_TYPE_HORIZONTAL = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
