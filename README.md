
# DetectTextFromImage
Detect text from bitmap images and stream camera for android, using kotlin
[![](https://jitpack.io/v/vantuan88291/DetectTextFromImage.svg)](https://jitpack.io/#vantuan88291/DetectTextFromImage)

Make detect text from camera and image bitmap easier, see the code bellow to understand how to use it.
Preference from ***google play-services-vision***
### To use font resources add support library to your dependencies:
add maven in to your **build.gradle** project

```gradle
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
   }
}
```

Add the dependency:

`implementation 'com.github.vantuan88291:DetectTextFromImage:1.3'`

Add permission into ***AndroidManifest***:

`<uses-permission android:name="android.permission.CAMERA" />`


 `<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />`


### Usage
Detect Text from Image Bitmap:

In your activity, implement ***DetectBitmapListener*** and overwrite methods

```
val detect = DetectBitmap(this, this)
detect.getTextFromBitmap(bitmap)

override fun detectBitmapSuccess(msg: String) {
        result.text = msg
    }
```
Detect Text from live camera:

In your layout xml, add it:
```
<com.tuan88291.textfromimages.DetectStream
            android:id="@+id/cam"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
```

And your Activity:

```
cam.setOnDetectListener(object : DetectStreamListener {
            override fun detectFail(msg: String) {
                toast(msg)
            }

            override fun requestCamera() {
                //Call request camera permission
            }

            override fun detectSuccess(msg: String) {
                result.post {
                    result.text = msg
                }
            }

        })

@SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != CAMERA) {
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
```

