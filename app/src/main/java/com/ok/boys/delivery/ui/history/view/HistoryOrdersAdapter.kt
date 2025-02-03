package com.ok.boys.delivery.ui.history.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.R
import com.ok.boys.delivery.base.api.orders.model.OrderResponse
import com.ok.boys.delivery.databinding.RowItemOrdersHistoryListBinding
import com.ok.boys.delivery.util.UtilsMethod
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HistoryOrdersAdapter(private val mContext: Context) : RecyclerView.Adapter<HistoryOrdersAdapter.OrderViewHolder>() {

    private var listOfData: List<OrderResponse> = listOf()

    private val itemClickSubjectExpand: PublishSubject<OrderResponse> = PublishSubject.create()
    var itemClickExpand: Observable<OrderResponse> = itemClickSubjectExpand.hide()

    private lateinit var historyPickupAdapter: HistoryPickupAdapter

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): OrderViewHolder {
        val binding = RowItemOrdersHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(listOfData[position])
    }

    override fun getItemCount(): Int = listOfData.size

    fun updateListInfo(updatedList: List<OrderResponse>) {
        val diffCallback = HistoryOrdersDiffUtilCallback(this.listOfData, updatedList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listOfData = updatedList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class OrderViewHolder(private val binding: RowItemOrdersHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderResponse) {
            binding.rvPickUp.layoutManager = LinearLayoutManager(mContext)
            historyPickupAdapter = HistoryPickupAdapter(mContext)
            historyPickupAdapter.updateListInfo(item.jobAddress)
            binding.rvPickUp.adapter = historyPickupAdapter

            binding.txtCreated.text = UtilsMethod.timeStampFormatter(item.createdTs)
            binding.layoutDeliver.txtUserName.text = item.userAddress?.area
            binding.layoutDeliver.txtDeliveryAddess.text = item.userAddress?.address
            binding.txtDistanceKM.text = item.totalDistance.toString()

            val rupees = mContext.getString(R.string.symbol_rupees)
            binding.txtEarnAmount.text = item.deliveryAmount?.let { "$rupees$it" } ?: "-"

            if (item.isExpanded) {
                binding.clPickUpDetails.visibility = View.VISIBLE
                binding.imgExpandView.setImageResource(R.drawable.ic_up_arrow)
            } else {
                binding.clPickUpDetails.visibility = View.GONE
                binding.imgExpandView.setImageResource(R.drawable.ic_down_arrow)
            }

            binding.imgExpandView.setOnClickListener {
                itemClickSubjectExpand.onNext(item) // Notify the change
            }
        }
    }

    class HistoryOrdersDiffUtilCallback(
        private val oldList: List<OrderResponse>,
        private val newList: List<OrderResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].orderNumber == newList[newItemPosition].orderNumber
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.isExpanded == newItem.isExpanded && oldItem == newItem
        }
    }
}
