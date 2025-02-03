package com.ok.boys.delivery.ui.history.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.api.orders.model.CategoryItem
import com.ok.boys.delivery.base.api.orders.model.JobAddressItem
import com.ok.boys.delivery.databinding.RowItemBroadcastedPickupViewBinding
import com.ok.boys.delivery.ui.orders.view.tags.TagsAdapter

class HistoryPickupAdapter(var mContext: Context) : RecyclerView.Adapter<HistoryPickupAdapter.HistoryViewHolder>() {

    private var listOfData: List<JobAddressItem> = listOf()
    private lateinit var itemViewBinding: RowItemBroadcastedPickupViewBinding

    private lateinit var tagsAdapter: TagsAdapter
    private var listOfTags: MutableList<CategoryItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HistoryViewHolder {
        itemViewBinding = RowItemBroadcastedPickupViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HistoryViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listOfData[position])
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    fun updateListInfo(newList: List<JobAddressItem>) {
        val diffCallback = HistoryPickupDiffUtilCallback(this.listOfData, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listOfData = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class HistoryViewHolder(var binding: RowItemBroadcastedPickupViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JobAddressItem) {
            setupData(binding)
            listOfTags.clear()
            OkBoysApplication.categoryList.map {
                if (item.categoryId == it.id) {
                    listOfTags.add(it)
                }
            }
            tagsAdapter.updateListInfo(listOfTags)
            binding.txtUserName.text = item.area
            binding.txtDeliveryAddess.text = item.address
        }
    }

    private fun setupData(binding: RowItemBroadcastedPickupViewBinding) {
        binding.rvTags.layoutManager = LinearLayoutManager(mContext)
        binding.rvTags.setHasFixedSize(true)
        tagsAdapter = TagsAdapter()
        binding.rvTags.adapter = tagsAdapter
    }

    class HistoryPickupDiffUtilCallback(
        private val oldList: List<JobAddressItem>,
        private val newList: List<JobAddressItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].categoryId == newList[newItemPosition].categoryId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
