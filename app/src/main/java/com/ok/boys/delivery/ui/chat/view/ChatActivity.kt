package com.ok.boys.delivery.ui.chat.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ok.boys.delivery.R
import com.ok.boys.delivery.application.OkBoysApplication
import com.ok.boys.delivery.application.OkBoysApplication.Companion.isOrderRefreshed
import com.ok.boys.delivery.base.BaseActivity
import com.ok.boys.delivery.base.ViewModelFactory
import com.ok.boys.delivery.base.api.chat.model.BaseMessage
import com.ok.boys.delivery.base.extentions.addOnBackPressedDispatcher
import com.ok.boys.delivery.base.extentions.autoDispose
import com.ok.boys.delivery.base.extentions.getViewModelFromFactory
import com.ok.boys.delivery.base.extentions.hideKeyboard
import com.ok.boys.delivery.base.extentions.startActivityWithFadeInAnimation
import com.ok.boys.delivery.base.extentions.subscribeAndObserveOnMainThread
import com.ok.boys.delivery.base.extentions.throttleClicks
import com.ok.boys.delivery.base.extentions.toastLong
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.chunking.ImageManager
import com.ok.boys.delivery.chunking.viewmodel.ImageViewModel
import com.ok.boys.delivery.chunking.viewmodel.ImageViewState
import com.ok.boys.delivery.databinding.ActivityChatBinding
import com.ok.boys.delivery.ui.chat.viewmodel.ChatViewModel
import com.ok.boys.delivery.ui.chat.viewmodel.ChatViewState
import com.ok.boys.delivery.ui.dashboard.HomeActivity
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.BitmapHelper
import com.ok.boys.delivery.util.ChunkManager
import com.ok.boys.delivery.util.JSFileUtils
import com.ok.boys.delivery.util.PrefUtil
import com.ok.boys.delivery.util.UtilsMethod
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class ChatActivity : BaseActivity() {

    lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<ChatViewModel>
    private lateinit var chatViewModel: ChatViewModel
    private var userId: String = ""
    private var orderId: String = ""
    private val chatModelList: MutableList<BaseMessage> = mutableListOf()

    private var isFirstTime = false
    private var isBackStack = false
    private var isStaticMessage = 0
    private var isUsername = ""


    @Inject
    lateinit var imageModelFactory: ViewModelFactory<ImageViewModel>
    private lateinit var imageViewModel: ImageViewModel

    private lateinit var imageManager: ImageManager


    companion object {
        fun getIntent(
            orderId: String,
            isBackStack: Boolean,
            isStaticMessage: Int,
            isUsername: String,
            context: Context
        ): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("order_id", orderId)
            intent.putExtra("is_backstack", isBackStack)
            intent.putExtra("isStaticMessage", isStaticMessage)
            intent.putExtra("isUsername", isUsername)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OkBoysApplication.component.inject(this)
        initViews()
    }

    private fun initViews() {
        chatViewModel = getViewModelFromFactory(viewModelFactory)
        imageViewModel = getViewModelFromFactory(imageModelFactory)

        imageManager = ImageManager(applicationContext)
        ImageManager.INCREMENTAL_COUNTER = 0
        ImageManager.END_SIZE_COUNTER = 0

        binding.chatToolBar.txtTitle.text = getString(R.string.label_chat)
        binding.chatToolBar.btnBack.setOnClickListener {
            isOrderRefreshed = true
            finish()
        }

        addOnBackPressedDispatcher {
            isOrderRefreshed = true
            if (isBackStack) {
                if (!UtilsMethod.appInForeground(this)) {
                    finish()
                } else {
                    finish()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivityWithFadeInAnimation(intent)
                }
            } else {
                finish()
            }
        }


        userId = PrefUtil.getStringPref(PrefUtil.PREF_USER_ID, mContext)
        orderId = intent.getStringExtra("order_id") as String
        isBackStack = intent.getBooleanExtra("is_backstack", false)
        isStaticMessage = intent.getIntExtra("isStaticMessage", 0)
        isUsername = intent.getStringExtra("isUsername")as String
        ApiConstant.IS_TOKEN_PASSED = true

        binding.btnSend.throttleClicks().subscribe {
            if (binding.editChat.text.isNullOrBlank()) {
                Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show()
            } else {
                hideKeyboard()
                getAddCommentListAPI(binding.editChat.text.toString())
            }
        }.autoDispose(compositeDisposable)

        binding.btnAttachment.setOnClickListener {
            hideKeyboard()
            onPermissionCameraOpen()
        }

        binding.swipeRefreshLayout.setOnRefreshListener { direction ->
            if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                isFirstTime = true
                getCommentsListAPI()
            }
        }

        listenToViewModel()
        setupData()
    }


    override fun onResume() {
        super.onResume()
        getCommentsListAPI()
    }

    private fun onPermissionCameraOpen() {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    onCameraOpen()
                } else {
                    toastLong("Please allow camera permission")
                }
            }
    }

    private fun getCommentsListAPI() {
        chatViewModel.getCommentsList(orderId = orderId)
    }

    private fun getAddCommentListAPI(message: String) {
        chatViewModel.getAddComment(orderId = orderId, message)
    }

    private fun listenToViewModel() {
        chatViewModel.getChatViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is ChatViewState.SuccessGetCommentsResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        chatModelList.clear()
                        val commentsModelList = it.successMessage.commentsModelList
                        val transformedMessages = commentsModelList.map { data ->
                            BaseMessage().apply {
                                isSender = if (data.createdBy == userId) 0 else 1
                                message = data
                            }
                        }

                        chatModelList.addAll(transformedMessages)
                        messagesAdapter.updateMessages(chatModelList)
                        if (chatModelList.isNotEmpty()) {
                            binding.rvChat.scrollToPosition(chatModelList.size - 1)
                        }
                    } else {
                        toastShort(onErrorNoData().errorDescription)
                    }
                }

                is ChatViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }

                is ChatViewState.LoadingState -> {
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

                else -> {}
            }
        }.autoDispose()



        chatViewModel.addChatViewState.subscribeAndObserveOnMainThread {
            when (it) {
                is ChatViewState.SuccessAddCommentsResponse -> {
                    if (it.successMessage.statusCode == 200) {
                        binding.editChat.setText("")
                        getCommentsListAPI()
                    } else {
                        toastShort(onErrorNoData().errorDescription)
                    }
                }

                is ChatViewState.ErrorMessage -> {
                    toastShort(it.errorMessage.errorTitle)
                }

                is ChatViewState.LoadingState -> {
                    if (it.isLoading) {
                        jsDialogUtils.showProgressDialog()
                    } else {
                        jsDialogUtils.dismissDialog()
                    }
                }

                else -> {}
            }
        }.autoDispose()


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
                                if (isStaticMessage == 0) {
                                    getAddCommentListAPI("$isUsername: Menu card")
                                    isStaticMessage = 1
                                }
                                getCommentsListAPI()
                            }
                        } else {
                            //NEXT CALL...
                            ImageManager.INCREMENTAL_COUNTER++
                            onSyncingImageUpload(ImageManager.INCREMENTAL_COUNTER)
                        }
                    } else {
                        jsDialogUtils.dismissDialog()
                        imageManager.resetCounter()
                        toastShort(onErrorNoData().errorDescription)
                    }
                }

                is ImageViewState.ErrorMessage -> {
                    jsDialogUtils.dismissDialog()
                    imageManager.resetCounter()
                    toastShort(it.errorMessage.errorTitle)
                }

                is ImageViewState.LoadingState -> {
                    if (ImageManager.INCREMENTAL_COUNTER == ImageManager.END_SIZE_COUNTER) {
                        jsDialogUtils.dismissDialog()
                    } else {
                        jsDialogUtils.showProgressDialog()
                    }
                }
            }
        }.autoDispose()
    }

    private fun setupData() {
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.setHasFixedSize(true)
        messagesAdapter = MessagesAdapter(mContext)
        binding.rvChat.adapter = messagesAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ApiConstant.INTENT_PICK_CAMERA && resultCode == RESULT_OK) {
            if (mCurrentImageUri != null) {
                imageManager.sendImagePath = JSFileUtils.getPath(applicationContext, mCurrentImageUri) as String
                imageManager.UNIQUE_KEY = ChunkManager().generateUniqueKey()
                ImageManager.INCREMENTAL_COUNTER = 0
                imageScanned(imageManager.sendImagePath,mContext)
                onSyncingImageUpload(ImageManager.INCREMENTAL_COUNTER)
            }
        }
    }

    private fun onSyncingImageUpload(incrementalCounter: Int) {
        if (imageManager.sendImagePath.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val realPathBitmap = BitmapHelper.getChunkPathBitmap(applicationContext, imageManager.sendImagePath)
                val createChunkFiles = ChunkManager().createChunkFiles(
                    realPathBitmap,
                    "UPLOADS",
                    applicationContext,
                    imageManager.UNIQUE_KEY
                )
                ImageManager.END_SIZE_COUNTER = createChunkFiles.size - 1
                if (createChunkFiles.isNotEmpty()) {
                    val imagePartsModel = imageManager.initiateImage(
                        position = incrementalCounter,
                        size = createChunkFiles.size,
                        chunkModel = createChunkFiles[incrementalCounter]
                    )
                    imageViewModel.getUploadChunkImage(orderId, imagePartsModel)
                }

                if (!realPathBitmap.isRecycled) {
                    realPathBitmap.recycle()
                }
            }
        } else {
            toastShort("Something went wrong while uploading image")
        }
    }

}