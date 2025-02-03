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
import androidx.fragment.app.Fragment
import com.ok.boys.delivery.base.extentions.toastShort
import com.ok.boys.delivery.base.network.ErrorViewState
import com.ok.boys.delivery.util.ApiConstant
import com.ok.boys.delivery.util.JSDialogUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber


open class BaseFragment : Fragment() {

    val compositeDisposable = CompositeDisposable()

    lateinit var baseActivity: Activity
    lateinit var dialogUtils: JSDialogUtils
    var mCurrentImageUri: Uri? = null
    var baseContext: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogUtils = JSDialogUtils(activity)
        baseActivity = requireActivity()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            baseActivity = (context as Activity)
        }
        this.baseContext = context;
    }

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    fun onCameraOpen(){
        try {
            if (isDeviceSupportCamera()) {
                mCurrentImageUri = getOutputMediaFile()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentImageUri)
                startActivityForResult(intent, ApiConstant.INTENT_PICK_CAMERA)
            } else {
                requireActivity().toastShort("Sorry! Your device doesn't support Camera,\n Choose image from Gallery.")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun imageScanned(mPath:String, mContext: Context){
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

    open fun getGetCurrentImageUri(): Uri? {
        return mCurrentImageUri
    }

    open fun isDeviceSupportCamera(): Boolean {
        return requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /*open fun getOutputMediaFile(): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera Image")
        return requireActivity().contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }*/

    open fun getOutputMediaFile(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Image_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${ApiConstant.FOLDER_NAME}")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            try {
                val outputStream = requireActivity().contentResolver.openOutputStream(it)
                outputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                requireActivity().contentResolver.update(it, values, null, null)
            }
        }
        return uri
    }

    open fun onGalleryOpen() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, ApiConstant.INTENT_PICK_GALLERY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onErrorNoData(params: String): ErrorViewState {
        return when (params) {
            ApiConstant.IS_HOME -> {
                ErrorViewState(
                    "Let's get started",
                    "Your journey to earning begins now"
                )
            }
            ApiConstant.IS_ORDER -> {
                ErrorViewState(
                    "NO ORDERS AVAILABLE",
                    "There are no orders. Please check later"
                )
            }
            ApiConstant.IS_HISTORY -> {
                ErrorViewState(
                    "NO HISTORY AVAILABLE",
                    "There are no history records found"
                )
            }
            else -> {
                ErrorViewState(
                    "NO DATA AVAILABLE",
                    "Something went wrong. Please try again later"
                )
            }
        }
    }

    fun onErrorConnection(): ErrorViewState {
        return ErrorViewState(
            "No Internet Connection",
            "Please check your network connection"
        )
    }

    override fun onDetach() {
        super.onDetach()
        this.baseContext = null
    }
}
