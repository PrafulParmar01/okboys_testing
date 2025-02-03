package com.ok.boys.delivery.ui.orders.view.deliver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.api.orders.model.OrderResponse
import com.ok.boys.delivery.base.api.orders.model.UserAddressItem
import com.ok.boys.delivery.databinding.RowItemDeliverViewBinding
import com.ok.boys.delivery.ui.tracking.DeliverTrackingActivity
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class DeliverListAdapter(var mContaxt: Context) :
    RecyclerView.Adapter<DeliverListAdapter.DeliverViewHolder>() {

    private var listOfData: List<UserAddressItem> = listOf()
    private lateinit var itemViewBinding: RowItemDeliverViewBinding

    private val itemClickSubjectDelivered: PublishSubject<UserAddressItem> = PublishSubject.create()
    var itemClickDelivered: Observable<UserAddressItem> = itemClickSubjectDelivered.hide()

    private val itemClickSubjectServiceFees: PublishSubject<UserAddressItem> =
        PublishSubject.create()
    var itemClickServiceFees: Observable<UserAddressItem> = itemClickSubjectServiceFees.hide()

    private var orderId = ""

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DeliverViewHolder {
        itemViewBinding =
            RowItemDeliverViewBinding.inflate(LayoutInflater.from(mContaxt), parent, false)
        return DeliverViewHolder(itemViewBinding)
    }


    override fun onBindViewHolder(holder: DeliverViewHolder, position: Int) {
        holder.bind(listOfData[position], position)
        holder.itemView.setOnClickListener {
            if (listOfData[position].selected) {
                if(listOfData[position].orderState == ApiConstant.ORDER_ACCEPTED){
                    OkBoysApplication.userAddressItem = listOfData[position]
                    mContaxt.startActivity(DeliverTrackingActivity.getIntent(mContaxt, orderId))
                }
            } else {
                if (listOfData[position].isCompleted) {
                    listOfData[position].selected = !listOfData[position].selected
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        mContaxt,
                        "Please complete all pickup point first",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    fun updateListInfo(newListOfData: List<UserAddressItem>, mOrderId: String) {
        val diffCallback = UserAddressDiffCallback(this.listOfData, newListOfData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listOfData = newListOfData
        this.orderId = mOrderId

        // Dispatch updates
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class DeliverViewHolder(var binding: RowItemDeliverViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserAddressItem, position: Int) {
            binding.txtUserName.text = item.area
            binding.txtDeliveryAddess.text = item.address
            binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_white)

            if (item.availableStates.equals(ApiConstant.ORDER_DELIVERED)) {
                binding.layoutSelected.visibility = View.GONE
                binding.btnOrderDelivered.visibility = View.VISIBLE
                binding.btnWaitingDelivered.visibility = View.GONE
                binding.btnPayServiceFees.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                binding.imgLocation.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContaxt,
                        R.drawable.ic_location
                    )
                )
            } else if (item.orderState.equals(ApiConstant.ORDER_PAY_SERVICE_FEE)) {
                binding.layoutSelected.visibility = View.GONE
                binding.btnOrderDelivered.visibility = View.GONE
                binding.btnWaitingDelivered.visibility = View.GONE
                binding.btnPayServiceFees.visibility = View.VISIBLE
                binding.imgLocation.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContaxt,
                        R.drawable.ic_verified_state
                    )
                )
            } else if ((item.availableStates.equals(ApiConstant.ORDER_PAYMENT_TO_DELIVERY_BOY)) or (item.availableStates.equals(
                    ApiConstant.ORDER_PAY_SERVICE_FEE
                ))
            ) {
                binding.layoutSelected.visibility = View.GONE
                binding.btnOrderDelivered.visibility = View.GONE
                binding.btnWaitingDelivered.visibility = View.VISIBLE
                binding.btnPayServiceFees.visibility = View.GONE
                binding.imgLocation.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContaxt,
                        R.drawable.ic_verified_state
                    )
                )
            } else {
                binding.imgLocation.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContaxt,
                        R.drawable.ic_location
                    )
                )
                binding.btnOrderDelivered.visibility = View.GONE
                binding.btnWaitingDelivered.visibility = View.GONE
                binding.btnPayServiceFees.visibility = View.GONE
                if (listOfData[position].selected) {
                    binding.layoutSelected.visibility = View.VISIBLE
                    binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                } else {
                    binding.layoutSelected.visibility = View.GONE
                    binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_white)
                }
            }

            binding.btnRedirection.setOnClickListener {
                if(listOfData[position].orderState == ApiConstant.ORDER_ACCEPTED){
                    OkBoysApplication.userAddressItem = item
                    mContaxt.startActivity(DeliverTrackingActivity.getIntent(mContaxt, orderId))
                }
            }

            binding.btnOrderDelivered.setOnClickListener {
                item.orderId = orderId
                itemClickSubjectDelivered.onNext(item)
            }

            binding.btnPayServiceFees.setOnClickListener {
                item.orderId = orderId
                itemClickSubjectServiceFees.onNext(item)
            }
        }
    }


    // DiffUtil.Callback to compare the old and new list
    class UserAddressDiffCallback(
        private val oldList: List<UserAddressItem>,
        private val newList: List<UserAddressItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem // Compare content for full equality
        }
    }
}