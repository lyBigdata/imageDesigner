package yzx.app.image.design.logicViews

import yzx.app.image.design.ui.xo_ImageCacheDir
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


object ImageCacheMgr {


    fun getCacheFileName() = "C_${System.currentTimeMillis()}.cpng"


    fun getAllCacheFiles(): List<File> {
        val dir = xo_ImageCacheDir
        val files = dir.listFiles()
        if (files == null || files.isEmpty())
            return emptyList()
        val result = ArrayList<File>()
        files.forEach { result.add(it) }
        result.sortWith(Comparator { o1, o2 ->
            (o2.lastModified() - o1.lastModified()).toInt()
        })
        return result
    }


    fun existCacheFile(): Boolean {
        return !xo_ImageCacheDir.listFiles().isNullOrEmpty()
    }


    fun getCacheFilesCount(): Int {
        return xo_ImageCacheDir.listFiles()?.size ?: 0
    }

}