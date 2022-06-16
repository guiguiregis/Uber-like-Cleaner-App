package com.wolfpackdigital.kliner.partner.shared.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

private const val IMAGE_QUALITY = 100

fun Context.buildImageBodyPart(
    paramName: String,
    fileName: String,
    bitmap: Bitmap
): MultipartBody.Part {
    val leftImageFile = convertBitmapToFile(this, fileName, bitmap)
    val reqFile = leftImageFile.asRequestBody("image/*".toMediaType())
    return MultipartBody.Part.createFormData(paramName, leftImageFile.name, reqFile)
}

private fun convertBitmapToFile(context: Context, fileName: String, bitmap: Bitmap): File {
    // create a file to write bitmap data
    val file = File(context.cacheDir, fileName)
    file.createNewFile()

    // Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos)
    val bitMapData = bos.toByteArray()

    // write the bytes in file
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos?.write(bitMapData)
        fos?.flush()
        fos?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}
