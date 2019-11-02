package com.tuan88291.textfromimages

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import easy.asyntask.tuan88291.library.AsyncTaskEasy
import kotlin.properties.Delegates


class DetectBitmap(val context: Context, val callback: DetectBitmapListener) {
    private var textRecognizer by Delegates.notNull<TextRecognizer>()
    init {
        textRecognizer = TextRecognizer.Builder(context).build()
    }
    fun getTextFromBitmap(bitmap: Bitmap) {
        object : AsyncTaskEasy() {
            override fun doBackground(): Any? {
                val imageFrame = Frame.Builder().setBitmap(bitmap).build()
                val stringBuilder = StringBuilder()
                val textBlocks = textRecognizer.detect(imageFrame)
                for (i in 0..textBlocks.size() - 1) {
                    stringBuilder.append(textBlocks[textBlocks.keyAt(i)].value)
                }
                val msg = stringBuilder.toString()
                return msg
            }

            override fun onSuccess(s: Any?) {
                super.onSuccess(s)
                callback.detectBitmapSuccess(s.toString())
            }

            override fun onFail(err: String?) {
                super.onFail(err)
                callback.detectBitmapFail(err!!)
            }

            override fun onLoading() {
                super.onLoading()
                callback.onDetectLoading()
            }
            override fun onLoadComplete() {
                super.onLoadComplete()
               callback.onDetectLoadSuccess()
            }
        }
    }
}