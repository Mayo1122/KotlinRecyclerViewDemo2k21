package com.macrohard.recyclerviewlib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.Nullable

abstract class GenericAdapter<ITEM> constructor(protected var itemList: ArrayList<ITEM>,
                                                 private val layoutResId: Int, private val bindId: Int) : RecyclerView.Adapter<GenericAdapter.GenericViewHolder>() {
    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = parent inflate layoutResId
        val viewHolder = GenericViewHolder(view)
        val itemView = viewHolder.itemView
        itemView.setOnClickListener {
            val adapterPosition = viewHolder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onItemClick(itemView, adapterPosition)
                onItemClick(viewHolder)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: GenericAdapter.GenericViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item!!, bindId)
    }


    @MainThread
    protected open fun update(items: ArrayList<ITEM>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(itemList, items), false)
        this.itemList.clear()
        this.itemList.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    fun add(item: ITEM) {
        itemList.add(item)
        notifyItemInserted(itemList.size)
    }

    fun remove(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }


    protected open fun onViewRecycled(itemView: View) {
    }

    protected open fun onItemClick(itemView: View, position: Int) {
    }

    protected open fun onItemClick(viewHolder:GenericViewHolder) {
    }

    override fun onViewRecycled(holder: GenericViewHolder) {
        super.onViewRecycled(holder)
        this.onViewRecycled(holder.itemView)
    }

    class GenericViewHolder internal constructor(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(any: Any, id: Int) {
            binding.setVariable(id, any)
            binding.executePendingBindings()
        }
    }

}

internal class DiffUtilCallback<ITEM>(private val oldItems: MutableList<ITEM>,
                                      private val newItems: MutableList<ITEM>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}


