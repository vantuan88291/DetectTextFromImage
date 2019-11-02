package com.tuan88291.textfromimages

interface DetectStreamListener {
    fun detectFail(msg: String)
    fun requestCamera()
    fun detectSuccess(msg: String)
}