package com.ok.boys.delivery.base.api.image

import com.ok.boys.delivery.base.api.chat.model.UploadImageResponse
import com.ok.boys.delivery.util.ApiConstant
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ImageRetrofitAPI {

    @Multipart
    @POST(ApiConstant.API_UPLOAD_CHUNK_IMAGE)
    fun getUploadChunkImage(
        @Query("orderId") orderId: String,
        @Part("uploadType") uploadType: RequestBody,
        @Part("fileType") fileType: RequestBody,
        @Part("fileName") fileName: RequestBody,
        @Part("unqId") unqId: RequestBody,
        @Part("sequence") sequence: RequestBody,
        @Part("isLastChunk") isLastChunk: RequestBody,
        @Part file: MultipartBody.Part): Single<UploadImageResponse>



    @Multipart
    @POST(ApiConstant.API_UPLOAD_CHUNK_PAYMENT_IMAGE)
    fun getUploadChunkPaymentImage(
        @Query("orderId") orderId: String,
        @Query("jobId") jobId: String,
        @Part("uploadType") uploadType: RequestBody,
        @Part("fileType") fileType: RequestBody,
        @Part("fileName") fileName: RequestBody,
        @Part("unqId") unqId: RequestBody,
        @Part("sequence") sequence: RequestBody,
        @Part("isLastChunk") isLastChunk: RequestBody,
        @Part file: MultipartBody.Part
    ): Single<UploadImageResponse>


    @Multipart
    @POST(ApiConstant.API_DOCUMENT_UPLOAD_CHUNK_INVOICE_IMAGE)
    fun getUploadChunkInvoiceImage(
        @Query("orderId") orderId: String,
        @Query("jobId") jobId: String,
        @Part("uploadType") uploadType: RequestBody,
        @Part("fileType") fileType: RequestBody,
        @Part("fileName") fileName: RequestBody,
        @Part("unqId") unqId: RequestBody,
        @Part("sequence") sequence: RequestBody,
        @Part("isLastChunk") isLastChunk: RequestBody,
        @Part file: MultipartBody.Part
    ): Single<UploadImageResponse>
}