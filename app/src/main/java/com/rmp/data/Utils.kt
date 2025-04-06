package com.rmp.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun getCurrentDateAsNumber(): Int {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    return currentDate.format(formatter).toInt()
}

fun getCurrentYearPlusMonth(): Pair<Int, String> {
    val currentDate = LocalDate.now()
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
    val monthFormatter = DateTimeFormatter.ofPattern("MM")
    return currentDate.format(yearFormatter).toInt() to currentDate.format(monthFormatter)
}

fun getLastDayOfMonth(date: Int): Int {
    val input = "$date"

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    return LocalDate.parse(input, formatter).lengthOfMonth()
}

fun getAsDate(date: Int): LocalDate {
    val input = "$date"

    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    return LocalDate
        .parse(input, formatter)
}

data class UploadedImage(
    val image: String,
    val imageName: String,
) {
    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun buildFromUri(context: Context, uri: Uri): UploadedImage {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val byteArrayOutputStream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            return UploadedImage(
                Base64.encode(byteArray),
                "image.jpeg"
            )
        }

    }
}