package com.baghdadhomes.Utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.baghdadhomes.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import android.content.Context

object Utility {
    fun showToast(context: Context, message: String) {
        // Snackbar.make(parentView,message, Snackbar.LENGTH_LONG).show()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getDeviceId(context: Context): String {
        //return UUID.randomUUID().toString()
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getDateInFormat(inputDate: String): String {
        //2023-06-06 16:33:56
        var output = ""
        try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            output = dateFormat.format(originalFormat.parse(inputDate)!!)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return output
    }

    fun getMessageDate(timestamp: Long): String {
        var output = ""
        try {
            val calendarToday = Calendar.getInstance().time
            val df = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
            val dateToday = df.format(calendarToday)

            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp
            val date = DateFormat.format("dd MMM yy",calendar).toString()
            val time = DateFormat.format("hh:mm a",calendar).toString()
            output = if (dateToday == date){
                time
            } else{
                "$date, $time"
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return output
    }

    fun getLastSeenTime(context: Context,timestamp: Long): String{
        var output = ""
        try {
            val calendarToday = Calendar.getInstance().time
            val df = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
            val dateToday = df.format(calendarToday)

            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp
            val date = DateFormat.format("dd MMM yy",calendar).toString()
            val time = DateFormat.format("hh:mm a",calendar).toString()
            output = if (dateToday == date){
                "${context.getString(R.string.last_seen_today)} $time"
            } else{
                "${context.getString(R.string.last_seen)} $date, $time"
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return output
    }

    fun formatViewCounts(number: Int): String? {
        return try {
            val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
            val newNumber = number.toLong()
            val value = floor(log10(newNumber.toDouble())).toInt()
            val base = value / 3
            if (value >= 3 && base < suffix.size) {
                DecimalFormat("#0.0").format(newNumber / 10.0.pow((base * 3).toDouble())) + suffix[base]
            } else {
                DecimalFormat("#,##0").format(newNumber)
            }
        } catch ( e :Exception){
            number.toString()
        }
    }

    fun formatDoubleValue(double: Double): String {
        return DecimalFormat("0.0000").format(double)
    }

    fun getBitmapDescriptorFromDrawable(context: Context, resourceId : Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, resourceId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        vectorDrawable.draw(Canvas(bitmap))
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun isYouTubeVideoUrl(url: String?): Boolean {
        try {
            if (url.isNullOrEmpty()) return false

            val regex = Regex(
                "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]{11}.*$"
            )
            return regex.matches(url.trim())
        } catch(e : Exception) {
            return false
        }
    }

}