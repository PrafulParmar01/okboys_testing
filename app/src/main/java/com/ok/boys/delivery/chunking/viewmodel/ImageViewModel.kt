package com.ok.boys.delivery.chunking.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.ok.boys.delivery.base.BaseViewModel
import com.ok.boys.delivery.base.api.chat.model.UploadImageResponse
import com.ok.boys.delivery.base.api.image.ImageRepository
import com.ok.boys.delivery.base.api.network.RetryWithDelay
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.chunking.ImageManager
import com.ok.boys.delivery.chunking.ImagePartsModel
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.BitmapHelper
import com.ok.boys.delivery.util.ChunkManager
import com.ok.boys.delivery.util.ChunkModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ImageViewModel(private val imageRepository: ImageRepository) : BaseViewModel() {


    private val imageViewStateSubject: BehaviorSubject<ImageViewState> = BehaviorSubject.create()
    val imageViewState: Observable<ImageViewState> = imageViewStateSubject.hide()


    fun getUploadChunkImage(orderId: String, iModel: ImagePartsModel) {
        imageRepository.getUploadChunkImage(orderId, iModel)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                imageViewStateSubject.onNext(ImageViewState.LoadingState(true))
            }.doAfterTerminate {
                imageViewStateSubject.onNext(ImageViewState.LoadingState(false))
            }
            .subscribe({
                imageViewStateSubject.onNext(ImageViewState.SuccessImageResponse(it))
            }, {
                imageViewStateSubject.onNext(ImageViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }


    fun getUploadChunkPaymentImage(orderId: String, jobId: String, iModel: ImagePartsModel) {
        imageRepository.getUploadChunkPaymentImage(orderId, jobId, iModel)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                imageViewStateSubject.onNext(ImageViewState.LoadingState(true))
            }.doAfterTerminate {
                imageViewStateSubject.onNext(ImageViewState.LoadingState(false))
            }
            .subscribe({
                imageViewStateSubject.onNext(ImageViewState.SuccessImageResponse(it))
            }, {
                imageViewStateSubject.onNext(ImageViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }


    fun getUploadChunkInvoiceImage(orderId: String, jobId: String, iModel: ImagePartsModel) {
        imageRepository.getUploadChunkInvoiceImage(orderId, jobId, iModel)
            .retryWhen(RetryWithDelay(ApiConstant.API_RETRY_COUNT, ApiConstant.API_RETRY_DELAY))
            .takeUntil(Flowable.timer(ApiConstant.TAKE_UNTIL_DELAY, TimeUnit.SECONDS))
            .doOnSubscribe {
                imageViewStateSubject.onNext(ImageViewState.LoadingState(true))
            }.doAfterTerminate {
                imageViewStateSubject.onNext(ImageViewState.LoadingState(false))
            }
            .subscribe({
                imageViewStateSubject.onNext(ImageViewState.SuccessImageResponse(it))
            }, {
                imageViewStateSubject.onNext(ImageViewState.ErrorMessage(onHandleError(it)))
            }).autoDispose()
    }


    private val chunkImageViewStateSubject: PublishSubject<ChunkImageViewState> = PublishSubject.create()
    val chunkImageViewState: Observable<ChunkImageViewState> = chunkImageViewStateSubject.hide()

    fun getCreateChunkManager(context: Context, imageManager: ImageManager) {
        viewModelScope.launch(Dispatchers.IO) {
            val realPathBitmap = BitmapHelper.getChunkPathBitmap(context, imageManager.sendImagePath)
            val chunkFileList = ChunkManager().createChunkFiles(
                realPathBitmap,
                "INVOICE",
                context,
                imageManager.UNIQUE_KEY
            )
            ImageManager.END_SIZE_COUNTER = chunkFileList.size - 1

            if (!realPathBitmap.isRecycled) {
                realPathBitmap.recycle()
            }
            chunkImageViewStateSubject.onNext(ChunkImageViewState.ChunkManageResponse(chunkFileList))
        }
    }
}

sealed class ImageViewState {
    data class ErrorMessage(val errorMessage: ErrorViewState) : ImageViewState()
    data class SuccessImageResponse(val successMessage: UploadImageResponse) : ImageViewState()
    data class LoadingState(val isLoading: Boolean) : ImageViewState()
}


sealed class ChunkImageViewState {
    data class ChunkManageResponse(val list: MutableList<ChunkModel>) : ChunkImageViewState()
}
