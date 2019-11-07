package com.tuan88291.textfromimages

interface DetectBitmapListener {
    fun detectBitmapFail(msg: String)
    fun detectBitmapSuccess(data: ModelBitmap)
    fun onDetectLoading()
    fun onDetectLoadSuccess()
}