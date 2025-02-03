package com.ok.boys.delivery.chunking

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ok.boys.delivery.util.ChunkModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

class ImageManager(val mContext: Context) {

    companion object{
        @JvmField
        var IMAGE_QUALITY_LOW  = 480

        @JvmField
        var IMAGE_QUALITY_MEDIUM  = 512

        @JvmField
        var IMAGE_QUALITY_HIGH  = 1280

        const val CHUNK_NUMBERS = 2
        var INCREMENTAL_COUNTER = 0
        var END_SIZE_COUNTER = 0
    }


    var sendImagePath : String=""
    var sendImageBitmap : Bitmap?=null
    var UNIQUE_KEY = ""


    fun resetCounter(){
        INCREMENTAL_COUNTER = 0
        END_SIZE_COUNTER = 0
    }

    fun initiateImage(position: Int, size: Int, chunkModel: ChunkModel): ImagePartsModel {
        val mSequence = position + 1
        val mLastChunk: Boolean = position == size - 1

        val requestFile: RequestBody = chunkModel.requestFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val fileBody: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            chunkModel.fileName + chunkModel.fileType,
            requestFile
        )

        Log.e("ChunkImage : counter ", "===> " + position)
        Log.e("ChunkImage : sequence ", "===> " + mSequence)
        Log.e("ChunkImage : unqKey ", "===> " + chunkModel.chunkKey)
        Log.e("ChunkImage : isChunk ", "===> " + mLastChunk)
        Log.e("ChunkImage : fileType ", "===> " + chunkModel.fileType)
        Log.e("ChunkImage : fileName ", "===> " + chunkModel.fileName)
        Log.e("ChunkImage : uType ", "===> " + chunkModel.uploadType)


        val uploadType: RequestBody = chunkModel.uploadType.toRequestBody("text/plain".toMediaTypeOrNull())
        val fileType: RequestBody = chunkModel.fileType.toRequestBody("text/plain".toMediaTypeOrNull())
        val fileName: RequestBody = chunkModel.fileName.toRequestBody("text/plain".toMediaTypeOrNull())
        val unqId: RequestBody = chunkModel.chunkKey.toRequestBody("text/plain".toMediaTypeOrNull())
        val sequence: RequestBody = mSequence.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val isLastChunk: RequestBody = mLastChunk.toString().toRequestBody("text/plain".toMediaTypeOrNull())



        val imagePartsModel =  ImagePartsModel(
            uploadType = uploadType,
            fileType = fileType,
            fileName = fileName,
            unqId = unqId,
            sequence = sequence,
            isLastChunk = isLastChunk,
            fileBody = fileBody
        )

        return imagePartsModel
    }
}