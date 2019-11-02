package com.tuan88291.textfromimages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlin.properties.Delegates

class DetectStream: SurfaceView {
    private var mCameraSource by Delegates.notNull<CameraSource>()
    private var textRecognizer by Delegates.notNull<TextRecognizer>()
    private var mContext: Context? = null
    var callback: DetectStreamListener? = null
    constructor(context: Context) : super(context) {
        this.mContext = context
        startCameraSource()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        startCameraSource()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.mContext = context
        startCameraSource()
    }
    private fun startCameraSource() {

        //  Create text Recognizer
        textRecognizer = TextRecognizer.Builder(mContext).build()

        if (!textRecognizer.isOperational) {
            callback?.detectFail("Dependencies are not loaded yet...please try after few moment!!")
            return
        }

        //  Init camera source to use high resolution and auto focus
        mCameraSource = CameraSource.Builder(mContext, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setAutoFocusEnabled(true)
            .setRequestedFps(2.0f)
            .build()

        this.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(this@DetectStream.holder)
                    } else {
                        callback?.requestCamera()
                    }
                } catch (e: Exception) {
                    callback?.detectFail("Error:  ${e.message}")
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                callback?.detectSuccess(stringBuilder.toString())
            }
        })
    }
    private fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }
    fun setOnDetectListener(callback: DetectStreamListener) {
        this.callback = callback
    }
    fun startCamera() {
        mCameraSource.start(this.holder)
    }
}