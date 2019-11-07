package com.tuan88291.textfromimages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import easy.asyntask.tuan88291.library.AsyncTaskEasy
import kotlin.properties.Delegates


class DetectBitmap(val context: Context, val callback: DetectBitmapListener) {
    private var textRecognizer by Delegates.notNull<TextRecognizer>()
    init {
        textRecognizer = TextRecognizer.Builder(context).build()
    }
    private fun getBitmapText(items: SparseArray<TextBlock>, bitmap: Bitmap): Bitmap {
        val blocks: MutableList<TextBlock> = mutableListOf()
        for (i in 0..items.size() - 1) {
            blocks.add(items.valueAt(i))
        }
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val graphics = onGettingGraphics()

        for (i in blocks.indices) {
            val lines = blocks[i].components
            for (j in lines.indices) {
                val elements = lines[j].components
                for (k in elements.indices) {
                    canvas.drawRect(elements[k].boundingBox, graphics.first)
                }
            }
        }
        return mutableBitmap
    }
    private fun onGettingGraphics(): Pair<Paint, Paint> {
        val rectPaint = Paint()
        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 2F

        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 60F

        return Pair(rectPaint, textPaint)
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
                val newBitmap = getBitmapText(textBlocks, bitmap)
                return ModelBitmap(msg, newBitmap)
            }

            override fun onSuccess(s: Any?) {
                super.onSuccess(s)
                callback.detectBitmapSuccess(s as ModelBitmap)
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