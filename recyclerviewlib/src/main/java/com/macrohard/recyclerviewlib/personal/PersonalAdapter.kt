package com.macrohard.recyclerviewlib.personal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.macrohard.recyclerviewlib.DiffUtilCallback

class PersonalAdapter <ITEM> constructor(protected var itemList: ArrayList<ITEM>,
                                         private val layoutResId: Int,
                                         private val bindId: Int) :
    RecyclerView.Adapter<PersonalAdapter.GenericViewHolder>(){

    class GenericViewHolder internal constructor(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(any: Any, id: Int) {
            binding.setVariable(id, any)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonalAdapter.GenericViewHolder {

        val view = parent inflate layoutResId
        val viewHolder = PersonalAdapter.GenericViewHolder(view)
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

    override fun onBindViewHolder(holder: PersonalAdapter.GenericViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item!!, bindId)
    }

    protected open fun onViewRecycled(itemView: View) {
    }

    protected open fun onItemClick(itemView: View, position: Int) {
    }

    protected open fun onItemClick(viewHolder: PersonalAdapter.GenericViewHolder) {
    }

    override fun onViewRecycled(holder: PersonalAdapter.GenericViewHolder) {
        super.onViewRecycled(holder)
        this.onViewRecycled(holder.itemView)
    }


    // BELLOW Size & Insert & Update & Remove
    /***
     * Initial ;  updates using diffUtil, add initial data
     * Insert;  atStart, Index, Last, RangeInsert
     * Update;  atStart, Index, Last, RangeUpdate
     * Remove;  atStart, Index, Last, RangeRemove
     * */

    override fun getItemCount() = itemList.size

    fun setList(list: List<ITEM>) {
        this.itemList.clear()
        this.itemList.addAll(list)
        notifyDataSetChanged()
    }

    @MainThread
    protected open fun updateList(items: ArrayList<ITEM>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(itemList, items), false)
        this.itemList.clear()
        this.itemList.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    // Used for with or without position
    fun insertElementAtStart(data: ITEM) {
        insertElement(data,0)
    }
    // Used for with or without position
    fun insertElement(data: ITEM, position: Int? = null) {
        if (position != null) {
            this.itemList.add(position, data)
            notifyItemInserted(position)
        } else {
            this.itemList.add(data)
            notifyItemInserted(this.itemList.size - 1)
        }
    }

    fun insertElements(data: List<ITEM>, position: Int? = null) {
        if (position != null) {
            this.itemList.addAll(position, data)
            notifyItemRangeInserted(position, data.size)
        } else {
            val index = this.itemList.size - 1
            this.itemList.addAll(data)
            notifyItemRangeInserted(index, this.itemList.size - 1)
        }
    }

    fun updateElement(data: ITEM, position: Int) {
        this.itemList[position] = data
        notifyItemChanged(position)
    }

    fun updateLastElement(data: ITEM) {
        updateElement(data,this.itemList.size-1)
    }

    fun updateElements(data: List<ITEM>, startIndex: Int) {
        var start = startIndex
        for (i in 0 until data.size) {
            if (start >= this.itemList.size) {
                this.itemList.add(data[i])
            } else {
                this.itemList[start] = data[i]
            }
            start++
        }
        notifyItemRangeChanged(startIndex, data.size)
    }

    fun remove(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeFirst() {
        itemList.removeAt(this.itemList.size-1)
        notifyItemRemoved(this.itemList.size-1)
    }

    fun removeLast() {
        itemList.removeAt(this.itemList.size-1)
        notifyItemRemoved(this.itemList.size-1)
    }

    fun removeElements(startIndex: Int, endIndex: Int = this.itemList.size - 1) {
        val iterator = this.itemList.listIterator(startIndex)
        var end = endIndex
        while (iterator.hasNext()) {
            iterator.next()
            if (startIndex <= end) {
                iterator.remove()
                end--
            } else {
                break
            }
        }

        notifyItemRangeRemoved(startIndex, endIndex - startIndex)
    }
}

infix fun ViewGroup.inflate(layoutResId: Int): ViewDataBinding =
    DataBindingUtil.inflate<ViewDataBinding>(
        LayoutInflater.from(this.context), layoutResId, this, false)


abstract class EndlessScrollListener : RecyclerView.OnScrollListener() {

    private var mPreviousTotal = 0

    private var mLoading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView!!.childCount
        val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
        val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false
                mPreviousTotal = totalItemCount
            }
        }
        val visibleThreshold = 5
        if (!mLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            onLoadMore()
            mLoading = true
        }
    }

    abstract fun onLoadMore()
}