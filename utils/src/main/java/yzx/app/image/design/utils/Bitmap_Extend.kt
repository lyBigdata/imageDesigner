package yzx.app.image.design.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


fun getFileBitmapWH(path: String): Array<Int>? {
    val op = BitmapFactory.Options()
    BitmapFactory.decodeFile(path, op.apply { inJustDecodeBounds = true })
    val w = op.outWidth
    val h = op.outHeight
    if (w <= 0 || h <= 0)
        return null
    return arrayOf(w, h)
}


fun decodeFileBitmapWithMaxLength(context: Context = application, path: String, max: Int = 1920, cb: (Bitmap?) -> Unit) {
    val wh = getFileBitmapWH(path)
    if (wh == null) {
        cb.invoke(null)
    } else {
        val w = Math.min(wh[0], max)
        val h = Math.min(wh[1], max)
        GlobalScope.launch(Dispatchers.Default) {
            val bmp = Glide.with(context).asBitmap().load(File(path)).submit(w, h).get()
            launch(Dispatchers.Main) { cb.invoke(bmp) }
        }
    }
}


/**
 * 获取指定角度旋转的bitmap
 */
fun makeRotatingBitmap(source: Bitmap, degree: Float): Bitmap {
    if (degree % 360f == 0f)
        return Bitmap.createBitmap(source)
    val resultW: Int
    val resultH: Int
    if (degree % 90f == 0f) {
        resultW = if (degree % 180f == 0f) source.width else source.height
        resultH = if (degree % 180f == 0f) source.height else source.width
    } else {
        resultW = Math.sqrt((source.width * source.width + source.height * source.height).toDouble()).toInt()
        resultH = resultW
    }
    val result = Bitmap.createBitmap(resultW, resultH, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    canvas.rotate(degree, result.width / 2f, result.height / 2f)
    canvas.drawBitmap(source, (result.width - source.width) / 2f, (result.height - source.height) / 2f, Paint(Paint.ANTI_ALIAS_FLAG))
    return result
}