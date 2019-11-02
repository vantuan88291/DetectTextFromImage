package com.tuan88291.textfromimages

interface DetectBitmapListener {
    fun detectBitmapFail(msg: String)
    fun detectBitmapSuccess(msg: String)
    fun onDetectLoading()
    fun onDetectLoadSuccess()
}