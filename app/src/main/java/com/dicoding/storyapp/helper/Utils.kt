package com.dicoding.storyapp.helper

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import android.widget.Toast
import com.dicoding.storyapp.R.string.app_name
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Helper {

  private const val FILENAME_FORMAT = "dd-MMM-yyyy"

  private val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
  ).format(System.currentTimeMillis())

  private fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
  }

  fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
      File(it, application.resources.getString(app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
      mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
  }

  fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
      matrix.postRotate(90f)
      Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
      )
    } else {
      matrix.postRotate(-90f)
      matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
      Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
      )
    }
  }

  fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int

    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
  }

  fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
      val bmpStream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
      val bmpPicByteArray = bmpStream.toByteArray()
      streamLength = bmpPicByteArray.size
      compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
  }

  fun showToastShort(context: Context, text: String) {
    Toast.makeText(
      context,
      text,
      Toast.LENGTH_SHORT
    ).show()
  }

  fun showToastLong(context: Context, text: String) {
    Toast.makeText(
      context,
      text,
      Toast.LENGTH_LONG
    ).show()
  }

  fun isEmailValid(email: CharSequence): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
  }

  fun formatDate(currentDateString: String, targetTimeZone: String): String {
    val instant = Instant.parse(currentDateString)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
      .withZone(ZoneId.of(targetTimeZone))
    return formatter.format(instant)
  }
}

