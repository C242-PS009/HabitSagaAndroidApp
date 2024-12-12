package com.c242_ps009.habitsaga.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.c242_ps009.habitsaga.databinding.ItemItemBinding

class InventoryAdapter(private val onClick: (Inventory) -> Unit): ListAdapter<Inventory, InventoryAdapter.InventoryViewHolder>(ItemDiffCallback()) {
    class InventoryViewHolder(private val binding: ItemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Inventory, onClick: (Inventory) -> Unit) {
            binding.icon.load(item.img + "thumbnail.png")
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventoryItem = getItem(position)
        holder.bind(inventoryItem, onClick)
    }

    class ItemDiffCallback: DiffUtil.ItemCallback<Inventory>() {
        override fun areItemsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
            return oldItem.img == newItem.img
        }

        override fun areContentsTheSame(oldItem: Inventory, newItem: Inventory): Boolean {
            return oldItem == newItem
        }
    }
}