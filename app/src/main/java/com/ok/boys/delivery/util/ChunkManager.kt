package com.ok.boys.delivery.util

import android.content.Context
import android.graphics.Bitmap
import com.ok.boys.delivery.chunking.ImageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.sqrt


class ChunkManager {

    fun generateUniqueKey(): String {
        return UUID.randomUUID().toString()
    }

    private fun splitImage(resizedBitmap: Bitmap): ArrayList<Bitmap> {
        val chunkNumbers = ImageManager.CHUNK_NUMBERS
        val chunkedImages: ArrayList<Bitmap> = ArrayList(chunkNumbers)

        //withContext(Dispatchers.IO) {
            val scaledBitmap = Bitmap.createScaledBitmap(
                resizedBitmap,
                resizedBitmap.width,
                resizedBitmap.height,
                true
            )
            val chunkWidth: Int = resizedBitmap.width
            val chunkHeight: Int = resizedBitmap.height / chunkNumbers

            for (x in 0 until chunkNumbers) {
                chunkedImages.add(
                    Bitmap.createBitmap(
                        scaledBitmap,
                        0,
                        x * chunkHeight,
                        chunkWidth,
                        chunkHeight
                    )
                )
            }
            if (!scaledBitmap.isRecycled) {
                scaledBitmap.recycle()
            }
        //}
        return chunkedImages
    }

    fun createChunkFiles(resizedBitmap: Bitmap, uploadType: String, mContext: Context, uniqueKey: String): MutableList<ChunkModel> {
        val chunkFileList: MutableList<ChunkModel> = mutableListOf()
        val splitImage = ChunkManager().splitImage(resizedBitmap)

        //withContext(Dispatchers.IO) {
            for (bitmap in splitImage) {
                val bitmapToFile = UtilsMethod.bitmapToFile(bitmap, mContext)
                val chunkModel = ChunkModel(
                    requestFile = bitmapToFile,
                    chunkKey = uniqueKey,
                    fileType = "jpg",
                    fileName = bitmapToFile.name + ".jpg",
                    uploadType = uploadType
                )
                chunkFileList.add(chunkModel)

                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        //}
        return chunkFileList
    }
}