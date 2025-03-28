package com.rmp.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getCurrentDateAsNumber(): Int {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    return currentDate.format(formatter).toInt()
}