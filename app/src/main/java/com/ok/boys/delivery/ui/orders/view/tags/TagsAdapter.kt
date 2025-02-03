package com.ok.boys.delivery.ui.orders.view.tags

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.base.api.orders.model.CategoryItem

class TagsAdapter : RecyclerView.Adapter<TagsAdapter.NavigationViewHolder>() {

    private var listOfData: List<CategoryItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NavigationViewHolder {
        return NavigationViewHolder(TagsItemView(parent.context))
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    fun updateListInfo(newList: List<CategoryItem>) {
        val diffCallback = TagsDiffUtilCallback(this.listOfData, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listOfData = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: NavigationViewHolder, position: Int) {
        (holder.itemView as TagsItemView).bind(position, listOfData[position])
    }

    class NavigationViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class TagsDiffUtilCallback(private val oldList: List<CategoryItem>, private val newList: List<CategoryItem>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
