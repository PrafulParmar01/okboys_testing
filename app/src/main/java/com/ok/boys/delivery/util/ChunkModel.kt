package com.ok.boys.delivery.util

import java.io.File

data class ChunkModel(
    var requestFile : File,
    var chunkKey : String = "",
    var fileType : String = "",
    var fileName : String = "",
    var uploadType : String = "",
)
