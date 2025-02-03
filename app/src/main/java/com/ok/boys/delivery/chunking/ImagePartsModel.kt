package com.ok.boys.delivery.chunking

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class ImagePartsModel(
    var uploadType: RequestBody,
    var fileType: RequestBody,
    var fileName: RequestBody,
    var unqId: RequestBody,
    var sequence: RequestBody,
    var isLastChunk: RequestBody,
    var fileBody: MultipartBody.Part
)
