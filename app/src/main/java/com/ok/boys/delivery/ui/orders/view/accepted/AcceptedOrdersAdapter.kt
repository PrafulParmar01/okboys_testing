package com.ok.boys.delivery.ui.orders.view.accepted

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.api.orders.model.OrderResponse
import com.ok.boys.delivery.base.api.orders.model.UploadInvoicePass
import com.ok.boys.delivery.base.api.orders.model.UserAddressItem
import com.ok.boys.delivery.base.extentions.autoDispose
import com.ok.boys.delivery.base.extentions.throttleClicks
import com.ok.boys.delivery.databinding.RowItemOrdersAcceptedListBinding
import com.ok.boys.delivery.ui.chat.view.ChatActivity
import com.ok.boys.delivery.ui.orders.view.deliver.DeliverListAdapter
import com.ok.boys.delivery.ui.tracking.TrackingModel
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.PrefUtil
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*


class AcceptedOrdersAdapter(var mContaxt: Context, var compositeDisposable: CompositeDisposable) : RecyclerView.Adapter<AcceptedOrdersAdapter.OrderViewHolder>() {

    private var listOfData: List<OrderResponse> = listOf()

    //private val itemClickSubjectExpand: PublishSubject<OrderResponse> = PublishSubject.create()
    //var itemClickExpand: Observable<OrderResponse> = itemClickSubjectExpand.hide()

    private lateinit var itemViewBinding: RowItemOrdersAcceptedListBinding

    private lateinit var acceptedPickupAdapter: AcceptedPickupAdapter
    private lateinit var deliverListAdapter: DeliverListAdapter

    private val itemClickUploadInvoice1Subject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemClickInvoice1: Observable<UploadInvoicePass> = itemClickUploadInvoice1Subject.hide()

    private val itemClickUploadInvoice2Subject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemClickInvoice2: Observable<UploadInvoicePass> = itemClickUploadInvoice2Subject.hide()

    private val itemClickPaymentVerifiedSubject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemClickPaymentVerified: Observable<UploadInvoicePass> = itemClickPaymentVerifiedSubject.hide()


    private val itemEventUploadInvoiceSubject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemEventInvoice: Observable<UploadInvoicePass> = itemEventUploadInvoiceSubject.hide()

    private val itemClickSubjectDelivered: PublishSubject<UserAddressItem> = PublishSubject.create()
    var itemClickDelivered: Observable<UserAddressItem> = itemClickSubjectDelivered.hide()

    private val itemClickSubjectServiceFees: PublishSubject<UserAddressItem> = PublishSubject.create()
    var itemClickServiceFees: Observable<UserAddressItem> = itemClickSubjectServiceFees.hide()

    var chatCountValue = 0
    var showCount = false
    private var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): OrderViewHolder {
        itemViewBinding = RowItemOrdersAcceptedListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(listOfData[position], position)
    }

    fun setCountValue(count : Int,showCount:Boolean){
        this.chatCountValue = count
        this.showCount = showCount
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    fun updateListInfo(newListOfData: List<OrderResponse>) {
        val diffCallback = AcceptedOrdersDiffCallback(listOfData, newListOfData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listOfData = newListOfData
        diffResult.dispatchUpdatesTo(this)
    }

    inner class OrderViewHolder(var binding: RowItemOrdersAcceptedListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderResponse, position: Int) {
            binding.rvPickUp.layoutManager = LinearLayoutManager(mContaxt)
            binding.rvPickUp.setHasFixedSize(true)
            acceptedPickupAdapter = AcceptedPickupAdapter(mContaxt)
            item.jobAddress.sortBy { it.sequance }

            // SET DEFAULT SELECTED
            if(item.jobAddress.isNotEmpty()) {
                if (item.jobAddress.size == 1) {
                    item.jobAddress[0].selected = true
                }else{
                    for (i in 0 until item.jobAddress.size) {
                        if(i < item.jobAddress[i].sequance){
                            if(item.jobAddress[0].workflowState=="JOB_CREATED"){
                                item.jobAddress[0].selected = true
                                break
                            }else if(item.jobAddress[i].workflowState=="JOB_COMPLETED" &&   item.jobAddress[i].sequance < item.jobAddress.size){
                                item.jobAddress[i+1].selected = true
                            }
                        }
                    }
                }
            }


            acceptedPickupAdapter.updateListInfo(item.jobAddress,item.id,item.deliveryAmount)
            binding.rvPickUp.adapter = acceptedPickupAdapter


            if(item.jobAddress[item.jobAddress.size-1].workflowState == "JOB_COMPLETED"){
                item.userAddress!!.isCompleted = true
            }


            if(item.orderState == ApiConstant.ORDER_PAY_SERVICE_FEE){
                binding.layoutChat.setBackgroundResource(R.drawable.rounded_button_chat_disable)
                binding.layoutChat.isEnabled = false
                binding.layoutChat.isClickable = false

            }else{
                binding.layoutChat.setBackgroundResource(R.drawable.rounded_button_accept)
                binding.layoutChat.isEnabled = true
                binding.layoutChat.isClickable = true
            }

            // DELIVERY SELECTION
            var deliverySelected = false
            item.jobAddress.map {jobItem->
                if(jobItem.workflowState=="JOB_COMPLETED"){
                    deliverySelected = true
                }else{
                    deliverySelected= false
                }
            }

             if(deliverySelected){
                 item.userAddress!!.selected = true
             }


            binding.rvDeliver.layoutManager = LinearLayoutManager(mContaxt)
            binding.rvDeliver.setHasFixedSize(true)
            deliverListAdapter = DeliverListAdapter(mContaxt)
            deliverListAdapter.updateListInfo(listOf(item.userAddress!!),item.id)
            binding.rvDeliver.adapter = deliverListAdapter

            deliverListAdapter.itemClickServiceFees.subscribe {
                itemClickSubjectServiceFees.onNext(it)
            }.autoDispose(compositeDisposable)


            deliverListAdapter.itemClickDelivered.subscribe {
                itemClickSubjectDelivered.onNext(it)
            }.autoDispose(compositeDisposable)

            acceptedPickupAdapter.itemClickInvoice1.throttleClicks().subscribe {
                itemClickUploadInvoice1Subject.onNext(it)
            }.autoDispose(compositeDisposable)


            acceptedPickupAdapter.itemClickInvoice2.throttleClicks().subscribe {
                itemClickUploadInvoice2Subject.onNext(it)
            }.autoDispose(compositeDisposable)

            acceptedPickupAdapter.itemEventInvoice.subscribe {
                itemEventUploadInvoiceSubject.onNext(it)
            }.autoDispose(compositeDisposable)

            acceptedPickupAdapter.itemClickPaymentVerified.subscribe {
                itemClickPaymentVerifiedSubject.onNext(it)
            }.autoDispose(compositeDisposable)

            binding.txtDistanceKM.text = item.totalDistance.toString()

            if (item.deliveryAmount != null) {
                val rupees = mContaxt.getString(R.string.symbol_rupees)
                binding.txtEarnAmount.text = rupees + item.deliveryAmount.toString()
            } else {
                binding.txtEarnAmount.text = "-"
            }

            binding.layoutCall.setOnClickListener {
                val phone = item.customer?.mobileNumber
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                mContaxt.startActivity(intent)
            }

            binding.layoutChat.setOnClickListener {
                //OkBoysApplication.orderResponse = item
                OkBoysApplication.chatCounter = 0
                PrefUtil.putIntPref(PrefUtil.PREF_IS_CHAT_COUNT, OkBoysApplication.chatCounter , mContaxt)
                mContaxt.startActivity(ChatActivity.getIntent(item.id,false,1,"",mContaxt))
            }

            if (item.isExpanded) {
                binding.clPickUpDetails.visibility = View.VISIBLE
                binding.imgExpandView.setImageResource(R.drawable.ic_up_arrow_yellow)
            } else {
                binding.clPickUpDetails.visibility = View.GONE
                binding.imgExpandView.setImageResource(R.drawable.ic_down_arrow_yellow)
            }

            binding.imgExpandView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                selectedPosition = position
                notifyDataSetChanged()
            }

            binding.cardMain.setOnClickListener {
                item.isExpanded = !item.isExpanded
                selectedPosition = position
                notifyDataSetChanged()
            }

            if(showCount){
                binding.tvChatCount.visibility =View.VISIBLE
                binding.tvChatCount.text = chatCountValue.toString()
            }else{
                binding.tvChatCount.visibility =View.GONE
            }

        }
    }

    class AcceptedOrdersDiffCallback(
        private val oldList: List<OrderResponse>,
        private val newList: List<OrderResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id // Assuming 'id' is unique
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}