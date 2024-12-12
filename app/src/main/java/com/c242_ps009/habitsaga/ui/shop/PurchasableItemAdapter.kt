package com.c242_ps009.habitsaga.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.c242_ps009.habitsaga.databinding.ItemItemBinding

class PurchasableItemAdapter(private val onClick: (PurchasableItem) -> Unit): ListAdapter<PurchasableItem, PurchasableItemAdapter.ShopItemViewHolder>(ItemDiffCallback()) {
    class ShopItemViewHolder(private val binding: ItemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PurchasableItem, onClick: (PurchasableItem) -> Unit) {
            binding.icon.load(item.img + "thumbnail.png")
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val shopItem = getItem(position)
        holder.bind(shopItem, onClick)
    }

    class ItemDiffCallback: DiffUtil.ItemCallback<PurchasableItem>() {
        override fun areItemsTheSame(oldItem: PurchasableItem, newItem: PurchasableItem): Boolean {
            return oldItem.img == newItem.img
        }

        override fun areContentsTheSame(oldItem: PurchasableItem, newItem: PurchasableItem): Boolean {
            return oldItem == newItem
        }
    }
}