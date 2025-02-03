package com.ok.boys.delivery.base

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.services.LocationRequestEvent
import com.ok.boys.delivery.services.LocationUtils
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.JSDialogUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


abstract class BaseActivity : AppCompatActivity() {

    val compositeDisposable = CompositeDisposable()
    lateinit var mContext: Activity
    lateinit var jsDialogUtils: JSDialogUtils

    var mCurrentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        jsDialogUtils = JSDialogUtils(this)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    fun replaceFragment(fragment: Fragment?, mContainerId: Int) {
        try {
            if (fragment != null) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(mContainerId, fragment)
                transaction.commit()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationUtils.LOCATION_SETTINGS_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    EventBus.getDefault().postSticky(LocationRequestEvent(true))
                }

                Activity.RESULT_CANCELED -> {
                    EventBus.getDefault().postSticky(LocationRequestEvent(false))
                }
            }
        }
    }

    fun onErrorNoData(): ErrorViewState {
        return ErrorViewState(
            "NO DATA AVAILABLE",
            "Something went wrong. Please try again later"
        )
    }

    fun onCameraOpen() {
        try {
            if (isDeviceSupportCamera()) {
                mCurrentImageUri = getOutputMediaFile()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentImageUri)
                startActivityForResult(intent, ApiConstant.INTENT_PICK_CAMERA)
            } else {
                toastShort("Sorry! Your device doesn't support Camera,\n Choose image from Gallery.")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    open fun getGetCurrentImageUri(): Uri? {
        return mCurrentImageUri
    }

    open fun isDeviceSupportCamera(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /*open fun getOutputMediaFile(): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera Image")
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }*/

    open fun getOutputMediaFile(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Image_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${ApiConstant.FOLDER_NAME}")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            try {
                val outputStream = contentResolver.openOutputStream(it)
                outputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(it, values, null, null)
            }
        }
        return uri
    }

    fun imageScanned(mPath: String, mContext: Context) {
        if (mPath.isNotEmpty()) {
            Timber.e("Gallery image path: $mPath")
            MediaScannerConnection.scanFile(
                mContext,
                arrayOf(mPath),
                arrayOf("image/jpeg")
            ) { _, _ ->
                Timber.e("Gallery file scanned")
            }
        } else {
            Timber.e("Failed to retrieve gallery image path")
        }
    }
}