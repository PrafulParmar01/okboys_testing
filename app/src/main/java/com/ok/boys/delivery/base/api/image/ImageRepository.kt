package com.ok.boys.delivery.base.api.image

import com.ok.boys.delivery.base.api.chat.model.UploadImageResponse
import com.ok.boys.delivery.chunking.ImagePartsModel
import io.reactivex.Single


class ImageRepository(private val imageRetrofitAPI: ImageRetrofitAPI) {

    fun getUploadChunkImage(orderId: String, iModel: ImagePartsModel): Single<UploadImageResponse> {
        return imageRetrofitAPI.getUploadChunkImage(
            orderId = orderId,
            uploadType = iModel.uploadType,
            fileType = iModel.fileType,
            fileName = iModel.fileName,
            unqId = iModel.unqId,
            sequence = iModel.sequence,
            isLastChunk = iModel.isLastChunk,
            file = iModel.fileBody
        )
    }


    fun getUploadChunkPaymentImage(orderId:String, jobId:String,iModel: ImagePartsModel): Single<UploadImageResponse> {
        return imageRetrofitAPI.getUploadChunkPaymentImage(
            orderId = orderId,
            jobId = jobId,
            uploadType = iModel.uploadType,
            fileType = iModel.fileType,
            fileName = iModel.fileName,
            unqId = iModel.unqId,
            sequence = iModel.sequence,
            isLastChunk = iModel.isLastChunk,
            file = iModel.fileBody)
    }


    fun getUploadChunkInvoiceImage(orderId:String, jobId:String,iModel: ImagePartsModel): Single<UploadImageResponse> {
        return imageRetrofitAPI.getUploadChunkInvoiceImage(
            orderId = orderId,
            jobId = jobId,
            uploadType = iModel.uploadType,
            fileType = iModel.fileType,
            fileName = iModel.fileName,
            unqId = iModel.unqId,
            sequence = iModel.sequence,
            isLastChunk = iModel.isLastChunk,
            file = iModel.fileBody)
    }
}