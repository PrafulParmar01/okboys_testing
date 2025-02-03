package com.ok.boys.delivery.ui.orders.view

import AtPickupModel
import BroadcastOrdersAdapter
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.base.BaseFragment
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.image.UploadInvoiceBox
import com.ok.boys.delivery.base.api.orders.model.JobInvoice
import com.ok.boys.delivery.base.api.orders.model.JobPaymentRequest
import com.ok.boys.delivery.base.api.orders.model.OrderAcceptRejectRequest
import com.ok.boys.delivery.base.api.orders.model.OrderListRequest
import com.ok.boys.delivery.base.api.orders.model.OrderPaymentRequest
import com.ok.boys.delivery.base.api.orders.model.OrderResponse
import com.ok.boys.delivery.base.api.orders.model.UploadInvoicePass
import com.ok.boys.delivery.base.api.orders.model.UploadInvoiceRequest
import com.ok.boys.delivery.base.api.orders.model.UserAddressItem
import com.ok.boys.delivery.base.extentions.autoDispose
import com.ok.boys.delivery.base.extentions.getViewModel
import com.ok.boys.delivery.base.extentions.startActivityWithFadeInAnimation
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.chunking.ImageManager
import com.ok.boys.delivery.chunking.viewmodel.ChunkImageViewState
import com.ok.boys.delivery.chunking.viewmodel.ImageViewModel
import com.ok.boys.delivery.chunking.viewmodel.ImageViewState
import com.ok.boys.delivery.databinding.FragmentOrdersBinding
import com.ok.boys.delivery.services.ChatCountRefreshEvent
import com.ok.boys.delivery.ui.chat.view.ChatActivity
import com.ok.boys.delivery.ui.orders.view.accepted.AcceptedOrdersAdapter
import com.ok.boys.delivery.ui.orders.viewmodel.AcceptRejectViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderAcceptedViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderCategoryViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderProcessViewState
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewModel
import com.ok.boys.delivery.ui.orders.viewmodel.OrderViewState
import com.ok.boys.delivery.ui.payment.OrderCompletedEvent
import com.ok.boys.delivery.ui.payment.PaymentActivity
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.ApiConstant.IS_ORDER
import com.ok.boys.delivery.util.ChunkManager
import com.ok.boys.delivery.util.ChunkModel
import com.ok.boys.delivery.util.JSFileUtils
import com.ok.boys.delivery.util.PrefUtil
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject


class OrdersFragment : BaseFragment() {

    private lateinit var binding: FragmentOrdersBinding
    private var listOfData: MutableList<OrderResponse> = mutableListOf()

    private lateinit var broadcastOrdersAdapter: BroadcastOrdersAdapter
    private lateinit var acceptedOrdersAdapter: AcceptedOrdersAdapter
    private var uploadInvoicePass: UploadInvoicePass? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<OrderViewModel>
    private lateinit var orderViewModel: OrderViewModel

    private var stateAcceptReject = ""
    private var isFirstTime = false
    private var uploadInvoiceList: MutableList<UploadInvoiceBox> = mutableListOf()

    private var mOrderID = ""

    private val colors = arrayOf<CharSequence>("Take from camera", "Select from gallery", "Cancel")
    private var alertSelection: AlertDialog? = null

    @Inject
    lateinit var imageModelFactory: ViewModelFactory<ImageViewModel>
    private lateinit var imageViewModel: ImageViewModel

    private lateinit var imageManager: ImageManager
    private var isBroadcasted = false
    private var chunkFileList: MutableList<ChunkModel> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OkBoysApplication.component.inject(this)
        initView()
        viewModelObservers()
        onCallOrders()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(broadcastReceiverOrderCompleted, IntentFilter("order_completed"))
        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(broadcastReceiverOrderRefresh, IntentFilter("ORDER_REFRESH"))
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }


    override fun onResume() {
        super.onResume()
        if (OkBoysApplication.isOrderRefreshed) {
            onCallOrders()
        }
    }

    private fun initView() {
        orderViewModel = getViewModel(viewModelFactory)
        imageViewModel = getViewModel(imageModelFactory)
        imageManager = ImageManager(baseActivity)
        ImageManager.INCREMENTAL_COUNTER = 0
        ImageManager.END_SIZE_COUNTER = 0

        binding.swipeRefreshLayout.setOnRefreshListener { direction ->
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                isFirstTime = true
                onCallOrders()
            }
        }

        binding.btnOrderOkay.setOnClickListener {
            EventBus.getDefault().postSticky(OrderCompletedEvent(true))
        }
        orderViewModel.getOrderCategory()
    }


    private fun onSyncBroadcastOrders() {
        broadcastOrdersAdapter = BroadcastOrdersAdapter(baseActivity)
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(baseActivity)
        binding.recyclerViewOrders.setHasFixedSize(true)
        binding.recyclerViewOrders.adapter = broadcastOrdersAdapter

        broadcastOrdersAdapter.itemClickAccept.subscribe {
            onCallOrdersAcceptReject(it.orderId, it.status)
        }.autoDispose(compositeDisposable)
    }


    private fun onSyncAcceptedOrders() {
        acceptedOrdersAdapter = AcceptedOrdersAdapter(baseActivity, compositeDisposable)
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(baseActivity)
        binding.recyclerViewOrders.setHasFixedSize(true)
        binding.recyclerViewOrders.adapter = acceptedOrdersAdapter

        acceptedOrdersAdapter.itemEventInvoice.subscribe {
            if (!isCheckInvoiceValidation()) {
                onCallUploadInvoiceEvent(it)
            }
        }.autoDispose(compositeDisposable)

        acceptedOrdersAdapter.itemClickDelivered.subscribe {
            onCallOrderDelivered(it)
        }.autoDispose(compositeDisposable)

        acceptedOrdersAdapter.itemClickServiceFees.subscribe {
            this.mOrderID = it.orderId
            baseActivity.startActivityWithFadeInAnimation(
                PaymentActivity.getIntent(
                    mOrderID,
                    baseActivity
                )
            )
        }.autoDispose(compositeDisposable)

        acceptedOrdersAdapter.itemClickInvoice1.subscribe { orderItem ->
            uploadInvoicePass = orderItem
            selectInvoiceImage()
            Timber.v("uploadInvoicePass 1: ===> $orderItem")
        }.autoDispose(compositeDisposable)

        acceptedOrdersAdapter.itemClickInvoice2.subscribe { orderItem ->
            uploadInvoicePass = orderItem
            selectInvoiceImage()
            Timber.v("uploadInvoicePass 2: ===> $orderItem")
        }.autoDispose(compositeDisposable)

        acceptedOrdersAdapter.itemClickPaymentVerified.subscribe { orderItem ->
            callPaymentVerified(orderItem)
        }.autoDispose(compositeDisposable)
    }

    private fun onCallOrderDelivered(it: UserAddressItem?) {
        val requestOrder = OrderPaymentRequest()
        requestOrder.event = ApiConstant.ORDER_DELIVERED
        orderViewModel.getOrderDeliveredRequest(orderId = it?.orderId, requestOrder)
    }

    private fun callPaymentVerified(orderItem: UploadInvoicePass) {
        val requestConfirmOrder = OrderPaymentRequest()
        val jobPaymentReq = JobPaymentRequest()
        jobPaymentReq.id = orderItem.upiID

        requestConfirmOrder.orderJobId = orderItem.jobItem?.orderJobId
        requestConfirmOrder.event = ApiConstant.ORDER_PAYMENT_CONFIRMED
        requestConfirmOrder.jobPayment = jobPaymentReq
        orderViewModel.getPaymentConfirmRequest(orderId = orderItem.orderId, requestConfirmOrder)
    }

    private fun isCheckInvoiceValidation(): Boolean {
        var isCheck = false
        if (uploadInvoiceList.isEmpty()) {
            requireActivity().toastShort("Please upload invoice image")
            isCheck = true
        }
        return isCheck
    }

    private fun selectInvoiceImage() {
        val permissionX = PermissionX.init(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionX.permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
            )
        } else {
            permissionX.permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    openSelectionDialog()
                } else {
                    Toast.makeText(
                        baseActivity,
                        "Please allow permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    private fun openSelectionDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Select Image")
        builder.setItems(colors) { dialog, which ->
            when (which) {
                0 -> {
                    dialog.dismiss()
                    onCameraOpen()
                }

                1 -> {
                    dialog.dismiss()
                    onGalleryOpen()
                }

                2 -> {
                    dialog.dismiss()
                }
            }
        }
        alertSelection = builder.create()
        alertSelection?.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ApiConstant.INTENT_PICK_CAMERA && resultCode == RESULT_OK) {
            if (mCurrentImageUri != null) {
                val mPath = JSFileUtils.getPath(baseActivity, mCurrentImageUri)
                Timber.v("uploadInvoicePass camera path : ===> $mPath")
                onUploadInvoiceImage(mPath)
                imageScanned(mPath, baseActivity)
            }
        } else if (requestCode == ApiConstant.INTENT_PICK_GALLERY && resultCode == RESULT_OK) {
            if (data?.data != null) {
                val mPath = JSFileUtils.getPath(baseActivity, data.data)
                Timber.v("uploadInvoicePass gallery path : ===> $mPath")
                onUploadInvoiceImage(mPath)
                imageScanned(mPath, baseActivity)
            }
        }
    }


    private fun onUploadInvoiceImage(mPath: String) {
        if (uploadInvoicePass != null) {
            OkBoysApplication.isOrderRefreshed = false
            imageManager.sendImagePath = mPath
            ImageManager.INCREMENTAL_COUNTER = 0
            imageManager.UNIQUE_KEY = ChunkManager().generateUniqueKey()
            imageViewModel.getCreateChunkManager(baseActivity,imageManager)
        }
    }


    private suspend fun onSyncingImageUpload(incrementalCounter: Int) {
        if (imageManager.sendImagePath.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                if (chunkFileList.isNotEmpty()) {
                    val imagePartsModel = imageManager.initiateImage(
                        position = incrementalCounter,
                        size = chunkFileList.size,
                        chunkModel = chunkFileList[incrementalCounter]
                    )

                    imageViewModel.getUploadChunkInvoiceImage(
                        orderId = uploadInvoicePass!!.orderId,
                        jobId = uploadInvoicePass?.jobItem?.orderJobId.toString(),
                        iModel = imagePartsModel
                    )
                }
            }
        } else {
            baseActivity.toastShort("Something went wrong while uploading image")
        }
    }


    private fun onCallUploadInvoiceEvent(data: UploadInvoicePass) {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                if (uploadInvoiceList.isNotEmpty()) {
                    var invoice1mage1 = ""
                    var invoice1mage2 = ""

                    if (uploadInvoiceList.size == 1) {
                        val invoiceBox = uploadInvoiceList[0]
                        if (invoiceBox.uploadInvoicePass?.position == 0) {
                            invoice1mage1 = invoiceBox.uploadInvoiceResponse?.response?.filePath.toString()
                            invoice1mage2 = ""
                        }
                        if (invoiceBox.uploadInvoicePass?.position == 1) {
                            invoice1mage1 = ""
                            invoice1mage2 = invoiceBox.uploadInvoiceResponse?.response?.filePath.toString()
                        }
                    } else {
                        val invoiceBox1 = uploadInvoiceList[0]
                        val invoiceBox2 = uploadInvoiceList[1]
                        invoice1mage1 = invoiceBox1.uploadInvoiceResponse?.response?.filePath.toString()
                        invoice1mage2 = invoiceBox2.uploadInvoiceResponse?.response?.filePath.toString()
                    }

                    val jobInvoice = JobInvoice(
                        id = "",
                        jobId = "",
                        invoice1mage1 = invoice1mage1,
                        invoice1mage2 = invoice1mage2,
                        itemTotalAmount = data.deliveryAmount?.times(2)
                    )


                    val uploadInvoiceRequest = UploadInvoiceRequest(
                        orderJobId = data.jobItem?.orderJobId.toString(),
                        jobInvoice = jobInvoice,
                        event = "UPLOAD_INVOICE"
                    )

                    orderViewModel.getUploadInvoiceEvent(
                        orderId = data.orderId,
                        uploadInvoiceRequest
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun onCallOrders() {
        uploadInvoiceList.clear()
        isBroadcasted = PrefUtil.getBooleanPref(PrefUtil.PREF_IS_BROADCASTED, baseActivity)
        if (isBroadcasted) {
            onSyncBroadcastOrders()
            val request = OrderListRequest()
            request.pageNumber = 1
            request.noOfRecordsPerPage = 100
            orderViewModel.getOrderList(request)
        } else {
            onSyncAcceptedOrders()
            orderViewModel.getOrderAcceptedList()
        }
    }

    private fun onCallOrdersAcceptReject(orderId: String?, reqStatus: String) {
        stateAcceptReject = reqStatus
        val request = OrderAcceptRejectRequest()
        request.event = reqStatus
        orderViewModel.getOrderAcceptRejectAPI(orderId, request)
    }

    private fun viewModelObservers() {
        orderViewModel.orderViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        val responseList = it.successMessage.responseList
                        if (responseList.isNullOrEmpty()) {
                            onHandleError(false, onErrorNoData(IS_ORDER))
                            binding.layoutOrderCompleted.visibility = View.GONE
                        } else {
                            binding.recyclerViewOrders.visibility = View.VISIBLE
                            binding.layoutData.parentView.visibility = View.GONE
                            binding.layoutOrderCompleted.visibility = View.GONE
                            listOfData.clear()
                            listOfData.addAll(it.successMessage.responseList)
                            broadcastOrdersAdapter.updateListInfo(listOfData)
                        }
                    } else {
                        onHandleError(false, onErrorNoData(IS_ORDER))
                    }
                }

                is OrderViewState.ErrorMessage -> {
                    onHandleError(true, it.errorMessage)
                }

                is OrderViewState.LoadingState -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (!isFirstTime) {
                        if (it.isLoading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }.autoDispose()

        orderViewModel.orderAcceptedViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderAcceptedViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        PrefUtil.putBooleanPref(
                            PrefUtil.PREF_IS_BROADCASTED,
                            false,
                            requireContext()
                        )

                        val responseList = it.successMessage.responseList
                        if (responseList == null) {
                            onHandleError(false, onErrorNoData(IS_ORDER))
                            binding.layoutOrderCompleted.visibility = View.GONE
                        } else {
                            binding.recyclerViewOrders.visibility = View.VISIBLE
                            binding.layoutData.parentView.visibility = View.GONE
                            binding.layoutOrderCompleted.visibility = View.GONE
                            listOfData.clear()
                            val data = it.successMessage.responseList
                            data.isExpanded = true
                            listOfData.add(data)
                            acceptedOrdersAdapter.updateListInfo(listOfData)
                        }
                    } else {
                        onHandleError(false, onErrorNoData(IS_ORDER))
                    }
                }

                is OrderAcceptedViewState.ErrorMessage -> {
                    onHandleError(true, it.errorMessage)
                }

                is OrderAcceptedViewState.LoadingState -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (!isFirstTime) {
                        if (it.isLoading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    } else {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }.autoDispose()

        orderViewModel.acceptRejectViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is AcceptRejectViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        if (stateAcceptReject == ApiConstant.ORDER_ACCEPT) {
                            onSyncAcceptedOrders()
                            orderViewModel.getOrderAcceptedList()
                        } else {
                            onSyncBroadcastOrders()
                            val request = OrderListRequest()
                            request.pageNumber = 1
                            request.noOfRecordsPerPage = 100
                            orderViewModel.getOrderList(request)
                        }
                    }
                }

                is AcceptRejectViewState.ErrorMessage -> {
                    requireActivity().toastShort(it.errorMessage.errorTitle)
                }

                is AcceptRejectViewState.LoadingState -> {
                    if (it.isLoading) {
                        dialogUtils.showProgressDialog()
                    } else {
                        dialogUtils.dismissDialog()
                    }
                }
            }
        }

        orderViewModel.orderCategoryViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderCategoryViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        OkBoysApplication.categoryList = it.successMessage.response!!.category
                    }
                }

                is OrderCategoryViewState.ErrorMessage -> {
                }

                is OrderCategoryViewState.LoadingState -> {
                }
            }
        }

        imageViewModel.imageViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is ImageViewState.SuccessImageResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        if (ImageManager.INCREMENTAL_COUNTER == ImageManager.END_SIZE_COUNTER) {
                            Timber.e("UploadImage : ===> Total Response ${ImageManager.END_SIZE_COUNTER}")
                            if (it.successMessage.response != null) {
                                ImageManager.INCREMENTAL_COUNTER = 0
                                ImageManager.END_SIZE_COUNTER = 0
                                imageManager.sendImagePath = ""

                                uploadInvoiceList.add(
                                    UploadInvoiceBox(
                                        uploadInvoicePass,
                                        it.successMessage
                                    )
                                )

                                try {
                                    if (uploadInvoicePass != null) {
                                        if (it.successMessage.response.psUrl != null) {
                                            Glide.with(baseActivity)
                                                .load(it.successMessage.response.psUrl.toString())
                                                .placeholder(R.drawable.ic_ok_boys_logo)
                                                .error(R.drawable.ic_ok_boys_logo)
                                                .override(180, 180)
                                                .into(uploadInvoicePass?.imageView!!)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            //NEXT CALL...
                            ImageManager.INCREMENTAL_COUNTER++
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    onSyncingImageUpload(ImageManager.INCREMENTAL_COUNTER)
                                }
                            }
                        }
                    } else {
                        dialogUtils.dismissDialog()
                        imageManager.resetCounter()
                        requireContext().toastShort(onErrorNoData(IS_ORDER).errorDescription)
                    }
                }

                is ImageViewState.ErrorMessage -> {
                    dialogUtils.dismissDialog()
                    imageManager.resetCounter()
                    requireContext().toastShort(it.errorMessage.errorTitle)
                }

                is ImageViewState.LoadingState -> {
                    if (ImageManager.INCREMENTAL_COUNTER == ImageManager.END_SIZE_COUNTER) {
                        dialogUtils.dismissDialog()
                    } else {
                        dialogUtils.showProgressDialog()
                    }
                }
            }
        }

        orderViewModel.orderProcessViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is OrderProcessViewState.SuccessResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        uploadInvoicePass = null
                        uploadInvoiceList.clear()
                        onCallOrders()
                    } else {
                        baseActivity.toastShort(onErrorNoData(IS_ORDER).errorDescription)
                    }
                }

                is OrderProcessViewState.ErrorMessage -> {
                    baseActivity.toastShort(it.errorMessage.errorTitle)
                }

                is OrderProcessViewState.LoadingState -> {
                    if (it.isLoading) {
                        dialogUtils.showProgressDialog()
                    } else {
                        dialogUtils.dismissDialog()
                    }
                }
            }
        }

        imageViewModel.chunkImageViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is ChunkImageViewState.ChunkManageResponse -> {
                    if (it.list.isNotEmpty()) {
                        chunkFileList = it.list
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                onSyncingImageUpload(ImageManager.INCREMENTAL_COUNTER)
                            }
                        }
                    }
                }
            }
        }
    }


    private val broadcastReceiverOrderCompleted: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            binding.layoutOrderCompleted.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
            binding.layoutData.parentView.visibility = View.GONE
        }
    }

    private val broadcastReceiverOrderRefresh: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onCallOrders()
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onChatCountRefreshEvent(event: ChatCountRefreshEvent) {
        if (event.isSuccess) {
            OkBoysApplication.chatCounter++
            PrefUtil.putIntPref(
                PrefUtil.PREF_IS_CHAT_COUNT,
                OkBoysApplication.chatCounter,
                baseActivity
            )
            val chatCount = PrefUtil.getIntPref(PrefUtil.PREF_IS_CHAT_COUNT, baseActivity)
            try {
                if (chatCount != 0) {
                    acceptedOrdersAdapter.setCountValue(chatCount, true)
                } else {
                    acceptedOrdersAdapter.setCountValue(chatCount, false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onAtPickupPointEvent(event: AtPickupModel) {
        Log.e("onPickupCompleted: ", "===> " + event.completed)
        if (event.completed) {
            val builder = AlertDialog.Builder(baseActivity)
            builder.setMessage("Customer is looking for the menu card. Please share it in the chat section.")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    baseActivity.startActivity(ChatActivity.getIntent(event.orderId, false, 0, event.name, baseActivity))
                }
            val alert = builder.create()
            alert.show()
        }
        EventBus.getDefault().removeStickyEvent(event)
    }


    private fun onHandleError(isError: Boolean, it: ErrorViewState) {
        if (isError) {
            binding.layoutData.parentView.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.layoutData.txtMsgOne.text = it.errorTitle.uppercase()
            binding.layoutData.txtMsgTwo.text = it.errorDescription
        } else {
            binding.layoutData.parentView.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.layoutData.txtMsgOne.text = it.errorTitle.uppercase()
            binding.layoutData.txtMsgTwo.text = it.errorDescription
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(broadcastReceiverOrderCompleted)
        LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(broadcastReceiverOrderRefresh)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        alertSelection?.dismiss()
    }
}