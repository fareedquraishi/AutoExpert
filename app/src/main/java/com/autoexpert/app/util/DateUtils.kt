package com.autoexpert.app.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dtFmt   = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun today(): String = sdf.format(Date())
    fun monthPrefix(): String = today().substring(0, 7)
    fun toDisplayDate(iso: String): String = runCatching {
        dtFmt.format(sdf.parse(iso)!!)
    }.getOrElse { iso }
    fun Long.toTimeString(): String = timeFmt.format(Date(this))
    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
}
