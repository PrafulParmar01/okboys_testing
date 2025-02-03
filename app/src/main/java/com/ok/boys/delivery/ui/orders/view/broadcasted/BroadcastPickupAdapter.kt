package com.ok.boys.delivery.ui.orders.view.broadcasted

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


class BroadcastPickupAdapter(var mContaxt: Context)  : RecyclerView.Adapter<BroadcastPickupAdapter.PickupViewHolder>() {

    private var listOfData: List<JobAddressItem> = listOf()
    private lateinit var itemViewBinding: RowItemBroadcastedPickupViewBinding

    private lateinit var tagsAdapter: TagsAdapter
    private var listOfTags: MutableList<CategoryItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PickupViewHolder {
        itemViewBinding = RowItemBroadcastedPickupViewBinding.inflate(LayoutInflater.from(mContaxt),parent,false)
        return PickupViewHolder(itemViewBinding)
    }


    override fun onBindViewHolder(holder: PickupViewHolder, position: Int) {
        holder.bind(listOfData[position],position)
    }

    override fun getItemCount(): Int {
    return listOfData.size
    }

    fun updateListInfo(newListOfData: List<JobAddressItem>) {
        val diffCallback = PickupDiffCallback(listOfData, newListOfData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listOfData = newListOfData
        diffResult.dispatchUpdatesTo(this) // Apply the diff result to the adapter
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class PickupViewHolder(var binding: RowItemBroadcastedPickupViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : JobAddressItem, position: Int){
            setupData(binding)
            listOfTags.clear()
            OkBoysApplication.categoryList.map {
                if(item.categoryId== it.id){
                    listOfTags.add(it)
                }
            }
            tagsAdapter.updateListInfo(listOfTags)
            binding.txtUserName.text = item.name.toString()
            binding.txtDeliveryAddess.text = item.address
        }
    }
    private fun setupData(binding: RowItemBroadcastedPickupViewBinding) {
        binding.rvTags.layoutManager = LinearLayoutManager(mContaxt)
        binding.rvTags.setHasFixedSize(true)
        tagsAdapter = TagsAdapter()
        binding.rvTags.adapter = tagsAdapter
    }

    class PickupDiffCallback(
        private val oldList: List<JobAddressItem>,
        private val newList: List<JobAddressItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Compare by unique identifier (e.g., orderId)
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Compare the content of the items (e.g., name, address, etc.)
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}