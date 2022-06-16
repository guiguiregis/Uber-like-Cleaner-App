package com.wolfpackdigital.kliner.partner.shared.utils.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wolfpackdigital.kliner.partner.R
import io.reactivex.disposables.Disposable

fun Fragment.snackBar(
    message: String,
    action: ((View) -> Unit)? = {},
    actionText: String? = null,
    bottomMargin: Int? = null
) {
    this.view?.let {
        Snackbar.make(
            it,
            message,
            Snackbar.LENGTH_LONG
        ).apply {

            try {
                setAnchorView(R.id.main_bottom_nav)
            } catch (iae: IllegalArgumentException) {
                Log.e("SNACKBAR_ANCHOR", "snackBar: FRAGMENT", iae)
            }
            bottomMargin?.let { bottomMargin ->
                this.view.translationY = (-bottomMargin).toFloat()
            }
            setAction(actionText, action)
            show()
        }
    }
}

@SuppressLint("CheckResult")
fun Fragment.requestCameraPermissions(onGranted: () -> Disposable, onNotGranted: () -> Unit) {
    val rxPermissions = RxPermissions(this)
    rxPermissions.request(Manifest.permission.CAMERA)
        .subscribe { granted ->
            if (granted) onGranted.invoke() else onNotGranted.invoke()
        }
}

fun View.snackBar(message: String, action: ((View) -> Unit)? = {}, actionText: String? = null) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).setAction(actionText, action).show()
}

fun Activity.snackBar(message: String, action: ((View) -> Unit)? = {}, actionText: String? = null) {
    this.window?.decorView?.let {
        Snackbar.make(
            it,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            try {
                setAnchorView(R.id.main_bottom_nav)
            } catch (iae: IllegalArgumentException) {
                Log.e("SNACKBAR_ANCHOR", "snackBar: ACTIVITY", iae)
            }
            setAnchorView(R.id.main_bottom_nav).setAction(actionText, action)
            show()
        }
    }
}

fun View.toast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun View.toast(message: Int) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@SuppressWarnings("LongParameterList")
fun Fragment.showDialog(
    title: String = "",
    message: String = "",
    isCancelable: Boolean = true,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    positiveButtonClick: () -> Unit = {},
    negativeButtonClick: () -> Unit = {}
): AlertDialog? {
    this.context?.let {
        val builder = AlertDialog.Builder(it)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(isCancelable)
        positiveButtonText?.let { text ->
            builder.setPositiveButton(text) { dialog, _ ->
                dialog.dismiss()
                positiveButtonClick()
            }
        }
        negativeButtonText?.let { text ->
            builder.setNegativeButton(text) { dialog, _ ->
                dialog.dismiss()
                negativeButtonClick()
            }
        }
        return builder.show()
    }
    return null
}

@SuppressWarnings("LongParameterList")
fun Activity.showDialog(
    title: String = "",
    message: String = "",
    isCancelable: Boolean = true,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    positiveButtonClick: () -> Unit = {},
    negativeButtonClick: () -> Unit = {}
): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
    positiveButtonText?.let { text ->
        builder.setPositiveButton(text) { dialog, _ ->
            dialog.dismiss()
            positiveButtonClick()
        }
    }
    negativeButtonText?.let { text ->
        builder.setNegativeButton(text) { dialog, _ ->
            dialog.dismiss()
            negativeButtonClick()
        }
    }
    return builder.show()
}

inline fun <reified T : Any> Fragment.extra(key: String, default: T? = null) = lazy {
    val value = arguments?.get(key)
    if (value is T) value else default
}

inline fun <reified T : Any> Fragment.extraNotNull(key: String, default: T? = null) = lazy {
    val value = arguments?.get(key)
    requireNotNull(if (value is T) value else default) { key }
}

val Fragment.navController: NavController?
    get() = runCatching { findNavController() }.getOrNull()

fun Context.isAppInForeground(): Boolean {
    val mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = mActivityManager.runningAppProcesses ?: return false
    val packageName = packageName
    for (appProcess in appProcesses)
        if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
            appProcess.processName == packageName
        )
            return true
    return false
}

fun Context.downloadFile(uri: Uri) {
    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
    val subPath = "${uri.lastPathSegment}"
    val request = DownloadManager.Request(uri)
        .apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setTitle(subPath)
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(
                this@downloadFile,
                Environment.DIRECTORY_DOWNLOADS,
                subPath
            )
        }
    downloadManager?.apply {
        Toast.makeText(this@downloadFile, R.string.downloading, Toast.LENGTH_SHORT).show()
        enqueue(request)
    }
}

fun Context.openUrl(urlResource: Int) {
    val builder = CustomTabsIntent.Builder().apply {
        setToolbarColor(
            ContextCompat.getColor(
                this@openUrl,
                R.color.colorAccent
            )
        )
    }
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this@openUrl, Uri.parse(getString(urlResource)))
}
