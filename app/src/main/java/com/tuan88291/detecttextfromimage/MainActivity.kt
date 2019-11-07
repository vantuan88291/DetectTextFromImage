package com.tuan88291.detecttextfromimage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.tuan88291.textfromimages.DetectBitmap
import com.tuan88291.textfromimages.DetectBitmapListener
import com.tuan88291.textfromimages.DetectStreamListener
import com.tuan88291.textfromimages.ModelBitmap
import easy.asyntask.tuan88291.library.AsyncTaskEasy
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), DetectBitmapListener {
    val GALLERY: Int = 100
    var detect: DetectBitmap? = null
    var isStop: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detect = DetectBitmap(this, this)
        cam.setOnDetectListener(object : DetectStreamListener {
            override fun detectFail(msg: String) {
                toast(msg)
            }

            override fun requestCamera() {
                requestForPermission()
            }

            override fun detectSuccess(msg: String) {
                result.post {
                    result.text = msg
                }
            }

        })
        button.setOnClickListener {
            startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ), GALLERY
            )
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            val uri = data?.data
            try {
                processImagr(uri!!)
            } catch (e: Exception) {

            }
        }
    }

    private fun processImagr(uri: Uri) {
        object : AsyncTaskEasy() {
            override fun doBackground(): Any {
                val bytes = ByteArrayOutputStream()
                val bitmap = getBitmap(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
                return bitmap
            }

            override fun onSuccess(result: Any?) {
                super.onSuccess(result)
//                imageView.setImageBitmap(result as Bitmap)
//                detect?.getTextFromBitmap(result)
            }
        }
    }
    private fun getBitmap(uri: Uri): Bitmap {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(this.contentResolver, uri)
            return ImageDecoder.decodeBitmap(source)
        } else {
            val bm = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                uri
            )
            return bm
        }
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != 180) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                cam.startCamera()
            } else {
                toast("Permission need to grant")
                finish()
            }
        }
    }
    private fun requestForPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 180)
    }
    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }
    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
    override fun detectBitmapFail(msg: String) {

    }

    override fun detectBitmapSuccess(data: ModelBitmap) {
//        imageView.setImageBitmap(data.bitmap)
        result.text = data.msg
    }

    override fun onDetectLoading() {
    }

    override fun onDetectLoadSuccess() {
    }
}
