package yzx.app.image.design.utils

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat


inline fun <reified T : Activity> Context.launchActivity(vararg params: Pair<String, String>) {
    startActivity(Intent(this, T::class.java).apply {
        if (this !is Activity)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        params.forEach { putExtra(it.first, it.second) }
    })
}


fun permissionGranted(p: String) = ActivityCompat.checkSelfPermission(application, p) == PackageManager.PERMISSION_GRANTED


private var toast: Toast? = null
fun toast(str: String) {
    toast?.cancel()
    toast = Toast.makeText(application, str, Toast.LENGTH_SHORT)
    toast!!.show()
}


private var longToast: Toast? = null

fun longToast(str: String) {
    longToast?.cancel()
    longToast = Toast.makeText(application, str, Toast.LENGTH_LONG)
    longToast!!.show()
}


fun dp2px(dp: Int): Int {
    return (application.resources.displayMetrics.density * dp + 0.5f).toInt()
}


fun inflateView(context: Context = application, res: Int, parent: ViewGroup? = null, attach: Boolean = false): View {
    return LayoutInflater.from(context).inflate(res, parent, attach)
}


fun ValueAnimator?.cancel2() {
    this?.removeAllUpdateListeners()
    this?.removeAllListeners()
    this?.cancel()
}


val singletonLinearInterpolator = LinearInterpolator()


private val mainHandler = Handler(Looper.getMainLooper())
fun runOnMainThread(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        mainHandler.post(block)
    }
}


inline fun runCacheOutOfMemory(block: () -> Unit, onOutOfMemory: () -> Unit = {}) {
    try {
        block()
    } catch (e: OutOfMemoryError) {
        onOutOfMemory()
    }
}













