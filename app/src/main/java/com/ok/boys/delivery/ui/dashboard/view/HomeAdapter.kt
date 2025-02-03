package com.ok.boys.delivery.ui.dashboard.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.R
import com.ok.boys.delivery.base.api.home.model.EarningsItem
import com.ok.boys.delivery.databinding.RowItemHomeViewBinding
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HomeAdapter(val mContext: Context) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var listOfData: List<EarningsItem> = listOf()

    private val itemClickSubject: PublishSubject<Int> = PublishSubject.create()
    var itemClick: Observable<Int> = itemClickSubject.hide()

    lateinit var binding: RowItemHomeViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HomeViewHolder {
        binding = RowItemHomeViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(listOfData[position], position)
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    fun updateListInfo(newListOfData: List<EarningsItem>) {
        val diffCallback = EarningsItemDiffCallback(listOfData, newListOfData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listOfData = newListOfData
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HomeViewHolder(var binding: RowItemHomeViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EarningsItem, position: Int) {
            binding.rlHome.setOnClickListener {
                itemClickSubject.onNext(position)
            }

            val rupees = mContext.getString(R.string.symbol_rupees)
            binding.txtAmount.text = rupees + item.amount
            binding.txtMonth.text = item.month.trim()
            binding.txtYear.text = item.year.trim()
        }
    }

    class EarningsItemDiffCallback(private val oldList: List<EarningsItem>, private val newList: List<EarningsItem>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.amount == newItem.amount && oldItem.month == newItem.month && oldItem.year == newItem.year
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}
