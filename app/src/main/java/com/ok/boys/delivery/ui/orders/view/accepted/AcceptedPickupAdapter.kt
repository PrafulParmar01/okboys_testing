package com.ok.boys.delivery.ui.orders.view.accepted

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.api.orders.model.CategoryItem
import com.ok.boys.delivery.base.api.orders.model.JobAddressItem
import com.ok.boys.delivery.base.api.orders.model.UploadInvoicePass
import com.ok.boys.delivery.databinding.RowItemAcceptedPickupViewBinding
import com.ok.boys.delivery.ui.orders.view.payment.view.PaymentRequestActivity
import com.ok.boys.delivery.ui.orders.view.tags.TagsAdapter
import com.ok.boys.delivery.ui.tracking.TrackingActivity
import com.ok.boys.delivery.ui.tracking.TrackingModel
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class AcceptedPickupAdapter(var mContaxt: Context) :
    RecyclerView.Adapter<AcceptedPickupAdapter.PickupViewHolder>() {

    private var listOfData: List<JobAddressItem> = listOf()
    private lateinit var itemViewBinding: RowItemAcceptedPickupViewBinding

    private lateinit var tagsAdapter: TagsAdapter
    private var listOfTags: MutableList<CategoryItem> = mutableListOf()

    private var orderId = ""
    private var deliveryAmount = 0.0

    private val itemClickUploadInvoice1Subject: PublishSubject<UploadInvoicePass> =
        PublishSubject.create()
    var itemClickInvoice1: Observable<UploadInvoicePass> = itemClickUploadInvoice1Subject.hide()

    private val itemClickUploadInvoice2Subject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemClickInvoice2: Observable<UploadInvoicePass> = itemClickUploadInvoice2Subject.hide()

    private val itemClickPaymentVerifiedSubject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemClickPaymentVerified: Observable<UploadInvoicePass> = itemClickPaymentVerifiedSubject.hide()


    private val itemEventUploadInvoiceSubject: PublishSubject<UploadInvoicePass> = PublishSubject.create()
    var itemEventInvoice: Observable<UploadInvoicePass> = itemEventUploadInvoiceSubject.hide()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PickupViewHolder {
        itemViewBinding = RowItemAcceptedPickupViewBinding.inflate(LayoutInflater.from(mContaxt), parent, false)
        return PickupViewHolder(itemViewBinding)
    }


    override fun onBindViewHolder(holder: PickupViewHolder, position: Int) {
        holder.bind(listOfData[position], holder.adapterPosition)

        if (listOfData[position].workflowState == "JOB_COMPLETED") {
            listOfData[position].isCompleted = true
        }


        holder.binding.layoutTop.setOnClickListener {
            if (listOfData[position].selected) {
                if (listOfData[position].workflowState == ApiConstant.JOB_CREATED) {
                    OkBoysApplication.jobAddressList = listOfData

                    val trackingModel = TrackingModel(
                        orderId,
                        position,
                        listOfData[position].workflowState.toString()
                    )
                    mContaxt.startActivity(
                        TrackingActivity.getIntent(
                            mContaxt,
                            orderId,
                            position,
                            listOfData[position].workflowState!!
                        )
                    )
                }
            } else {
                if (listOfData.size == 1) {
                    listOfData[position].selected = !listOfData[position].selected
                    notifyDataSetChanged()
                } else if (listOfData.size > 1) {
                    if (position == 0 && !listOfData[position].isCompleted) {
                        listOfData[position].selected = !listOfData[position].selected
                        notifyDataSetChanged()
                    } else if (listOfData[position].isCompleted) {
                        Toast.makeText(mContaxt, "Pickup Completed", Toast.LENGTH_LONG).show()
                    } else if (position > 0 && listOfData[position - 1].isCompleted) {
                        listOfData[position].selected = !listOfData[position].selected
                        notifyDataSetChanged()
                    } else if (listOfData.size == position - 1 && listOfData[position].isCompleted) {
                        Toast.makeText(mContaxt, "All pickup point completed", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            mContaxt,
                            "Please complete " + listOfData[position - 1].sequance + " pickup point",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return listOfData.size
    }

    fun updateListInfo(newListOfData: List<JobAddressItem>, orderId: String, mDeliveryAmount: Double) {
        val diffCallback = PickupDiffCallback(this.listOfData, newListOfData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listOfData = newListOfData
        this.orderId = orderId
        this.deliveryAmount = mDeliveryAmount
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class PickupViewHolder(var binding: RowItemAcceptedPickupViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JobAddressItem, position: Int) {
            setupData(binding)
            binding.txtUserName.text = item.name
            binding.txtDeliveryAddess.text = item.address
            binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_white)

            listOfTags.clear()
            OkBoysApplication.categoryList.map {
                if (item.categoryId == it.id) {
                    listOfTags.add(it)
                }
            }

            if (item.invoiceDetails.isNotEmpty())
            {
                binding.layoutInvoices.visibility = View.VISIBLE
                if (item.invoiceDetails.size == 1) {

                    if (item.invoiceDetails[0].psUrl1.isNullOrEmpty()) {
                        binding.imgInvoice1.visibility = View.GONE
                        binding.txtInvoice1.visibility = View.GONE
                    } else {
                        Glide
                            .with(mContaxt)
                            .load(item.invoiceDetails[0].psUrl1)
                            .placeholder(R.drawable.ic_ok_boys_logo)
                            .error(R.drawable.ic_ok_boys_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(CenterCrop(), RoundedCorners(14))
                            .into(binding.imgInvoice1)
                    }


                    if (item.invoiceDetails[0].psUrl2.isNullOrEmpty()) {
                        binding.imgInvoice2.visibility = View.GONE
                        binding.txtInvoice2.visibility = View.GONE

                    } else {
                        Glide
                            .with(mContaxt)
                            .load(item.invoiceDetails[0].psUrl2)
                            .placeholder(R.drawable.ic_ok_boys_logo)
                            .error(R.drawable.ic_ok_boys_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(CenterCrop(), RoundedCorners(14))
                            .into(binding.imgInvoice2)
                    }

                    binding.imgInvoice1.isEnabled = false
                    binding.imgInvoice2.isEnabled = false
                }

                if (item.invoiceDetails.size == 2) {
                    Glide
                        .with(mContaxt)
                        .load(item.invoiceDetails[position].psUrl1)
                        .placeholder(R.drawable.ic_ok_boys_logo)
                        .error(R.drawable.ic_ok_boys_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(CenterCrop(), RoundedCorners(14))
                        .into(binding.imgInvoice1)

                    Glide
                        .with(mContaxt)
                        .load(item.invoiceDetails[position].psUrl2)
                        .placeholder(R.drawable.ic_ok_boys_logo)
                        .error(R.drawable.ic_ok_boys_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(CenterCrop(), RoundedCorners(14))
                        .into(binding.imgInvoice2)
                }
                binding.imgInvoice1.isEnabled = false
                binding.imgInvoice2.isEnabled = false

            } else {
                binding.layoutInvoices.visibility = View.GONE
                binding.imgInvoice1.isEnabled = true
                binding.imgInvoice2.isEnabled = true
            }

            tagsAdapter.updateListInfo(listOfTags)

            if (item.workflowState == ApiConstant.ORDER_UPLOAD_INVOICE) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                //binding.layoutSelected.setBackgroundResource(R.drawable.bg_right_rounded_disable)
                binding.btnRedirection.isClickable = false

                binding.layoutManualPayment.visibility = View.GONE
                binding.layoutFailedPayment.visibility = View.GONE
                binding.btnUploadPictures.visibility = View.VISIBLE
                binding.layoutRequestPayment.visibility = View.GONE
                binding.layoutVerifyPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

                binding.layoutInvoices.visibility = View.VISIBLE
                binding.btnUploadPictures.visibility = View.VISIBLE
            } else if (item.workflowState == ApiConstant.ORDER_PAYMENT_REQUEST) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                //binding.layoutSelected.setBackgroundResource(R.drawable.bg_right_rounded_disable)

                binding.btnRequestPayment.setBackgroundResource(R.drawable.rounded_yellow)
                binding.btnRequestPayment.setTextColor(Color.parseColor("#3F3D56"))
                binding.btnRedirection.isClickable = false

                binding.layoutManualPayment.visibility = View.GONE
                binding.layoutFailedPayment.visibility = View.GONE
                binding.layoutRequestPayment.visibility = View.VISIBLE
                binding.layoutPaymentStarted.visibility = View.GONE
                binding.layoutVerifyPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

        //        binding.layoutInvoices.visibility = View.GONE
                binding.btnUploadPictures.visibility = View.GONE
            } else if (item.workflowState == ApiConstant.ORDER_PAYMENT_STARTED) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                //binding.layoutSelected.setBackgroundResource(R.drawable.bg_right_rounded_disable)

                binding.btnRequestPayment.setBackgroundResource(R.drawable.rounded_yellow)
                binding.btnRequestPayment.setTextColor(Color.parseColor("#3F3D56"))
                binding.btnRedirection.isClickable = false

                binding.layoutManualPayment.visibility = View.GONE
                binding.layoutFailedPayment.visibility = View.GONE
                binding.layoutPaymentStarted.visibility = View.VISIBLE
                binding.layoutVerifyPayment.visibility = View.GONE
                binding.layoutRequestPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

         //       binding.layoutInvoices.visibility = View.GONE
                binding.btnUploadPictures.visibility = View.GONE
            } else if (item.workflowState == ApiConstant.ORDER_PAYMENT_CONFIRMED) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                // binding.layoutSelected.setBackgroundResource(R.drawable.bg_right_rounded_disable)

                binding.btnRequestPayment.setBackgroundResource(R.drawable.rounded_yellow)
                binding.btnRequestPayment.setTextColor(Color.parseColor("#3F3D56"))
                binding.btnRedirection.isClickable = false

                if (item.paymentDetails.isNotEmpty()){
                    val amount = item.paymentDetails[0].amount.toString()
                    val msgCustomerPaid: String = mContaxt.resources.getString(R.string.msg_customer_paid, amount)
                    binding.txtCustomerPaid.text = msgCustomerPaid
                }


                binding.layoutManualPayment.visibility = View.GONE
                binding.layoutFailedPayment.visibility = View.GONE
                binding.layoutPaymentStarted.visibility = View.GONE
                binding.layoutVerifyPayment.visibility = View.VISIBLE
                binding.layoutRequestPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

      //          binding.layoutInvoices.visibility = View.GONE
                binding.btnUploadPictures.visibility = View.GONE
            }

            else if (item.workflowState == ApiConstant.ORDER_PAYMENT_FAILED) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)

                binding.btnRequestPayment.setBackgroundResource(R.drawable.rounded_yellow)
                binding.btnRequestPayment.setTextColor(Color.parseColor("#3F3D56"))
                binding.btnRedirection.isClickable = false

                binding.layoutManualPayment.visibility = View.GONE
                binding.layoutFailedPayment.visibility = View.VISIBLE
                binding.layoutPaymentStarted.visibility = View.GONE
                binding.layoutVerifyPayment.visibility = View.GONE
                binding.layoutRequestPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

      //          binding.layoutInvoices.visibility = View.GONE
                binding.btnUploadPictures.visibility = View.GONE
            }
            else if (item.workflowState == ApiConstant.MAKE_MANUAL_PAYMENT) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutTop.setBackgroundResource(R.drawable.bg_rounded_border_yellow)

                binding.btnRequestPayment.setBackgroundResource(R.drawable.rounded_yellow)
                binding.btnRequestPayment.setTextColor(Color.parseColor("#3F3D56"))
                binding.btnRedirection.isClickable = false

                binding.layoutManualPayment.visibility = View.VISIBLE
                binding.layoutFailedPayment.visibility = View.GONE
                binding.layoutPaymentStarted.visibility = View.GONE
                binding.layoutVerifyPayment.visibility = View.GONE
                binding.layoutRequestPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

    //            binding.layoutInvoices.visibility = View.GONE
                binding.btnUploadPictures.visibility = View.GONE
            }
            else if (item.workflowState == ApiConstant.ORDER_JOB_COMPLETED) {
                binding.layoutSelected.visibility = View.GONE
                binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_white)

                binding.imgLocation.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContaxt,
                        R.drawable.ic_verified_state
                    )
                )

                binding.layoutManualPayment.visibility = View.GONE
                binding.layoutFailedPayment.visibility = View.GONE
                binding.layoutPaymentStarted.visibility = View.GONE
                binding.layoutVerifyPayment.visibility = View.GONE
                binding.layoutRequestPayment.visibility = View.GONE
                binding.btnRedirection.isEnabled = false

                binding.layoutInvoices.visibility = View.VISIBLE
                binding.btnUploadPictures.visibility = View.GONE
            } else {
                binding.layoutInvoices.visibility = View.GONE
                binding.btnRedirection.isEnabled = true
                binding.imgLocation.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContaxt,
                        R.drawable.ic_shop
                    )
                )
                if (listOfData[position].selected) {
                    binding.layoutSelected.visibility = View.VISIBLE
                    binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_border_yellow)
                } else {
                    binding.layoutSelected.visibility = View.GONE
                    binding.layoutCardMain.setBackgroundResource(R.drawable.bg_rounded_white)
                }
            }


            binding.btnUploadPictures.setOnClickListener {
                itemEventUploadInvoiceSubject.onNext(
                    UploadInvoicePass(
                        "",
                        orderId,
                        deliveryAmount,
                        item
                    )
                )
            }

            binding.imgInvoice1.setOnClickListener {
                itemClickUploadInvoice1Subject.onNext(
                    UploadInvoicePass(
                        "",
                        orderId,
                        deliveryAmount,
                        item,
                        binding.imgInvoice1,
                        0
                    )
                )
            }

            binding.imgInvoice2.setOnClickListener {
                itemClickUploadInvoice2Subject.onNext(
                    UploadInvoicePass(
                        "",
                        orderId,
                        deliveryAmount,
                        item,
                        binding.imgInvoice2,
                        1
                    )
                )
            }

            binding.btnRedirection.setOnClickListener { order ->
                if (listOfData[position].workflowState == ApiConstant.JOB_CREATED) {
                    OkBoysApplication.jobAddressList = listOfData


                    val trackingModel = TrackingModel(
                        orderId,
                        adapterPosition,
                        item.workflowState.toString()
                    )

                    mContaxt.startActivity(
                        TrackingActivity.getIntent(
                            mContaxt,
                            orderId,
                            adapterPosition,
                            item.workflowState!!
                        )
                    )
                }

            }



            binding.btnRequestPayment.setOnClickListener {
                OkBoysApplication.jobAddressItem = item
                mContaxt.startActivity(PaymentRequestActivity.getIntent(mContaxt, orderId))
            }

            binding.layoutVerifyPayment.setOnClickListener {
                itemClickPaymentVerifiedSubject.onNext(
                    UploadInvoicePass(
                        item.paymentDetails[0].id.toString(),
                        orderId,
                        deliveryAmount,
                        item,
                        binding.imgInvoice2
                    )
                )
            }
        }
    }

    private fun setupData(binding: RowItemAcceptedPickupViewBinding) {
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
            return oldList[oldItemPosition].orderJobId == newList[newItemPosition].orderJobId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}