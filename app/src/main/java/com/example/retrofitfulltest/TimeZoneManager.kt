package com.example.retrofitfulltest

import us.dustinj.timezonemap.TimeZoneMap
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object TimeZoneManager {

    private val timeZoneMap = TimeZoneMap.forEverywhere()

    fun getLocalTime(lat: Double, lon: Double) : String {

        val zoneId = timeZoneMap.getOverlappingTimeZone(lat, lon)?.zoneId

        val timezone = if (zoneId == null)
            ZoneId.systemDefault()
        else
            ZoneId.of(zoneId)

        val zoneDateTime = ZonedDateTime.now(timezone)
            .format(DateTimeFormatter
            .ofPattern("HH:mm, dd MMM yyyy"))

        return "Local time: $zoneDateTime"
    }
}
