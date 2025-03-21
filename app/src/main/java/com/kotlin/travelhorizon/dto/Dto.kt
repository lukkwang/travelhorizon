package com.kotlin.travelhorizon.dto

data class Dto (var id: Long)
{
    lateinit var date: String
    lateinit var year: String
    var hour: Int = 0
    var min: Int = 0
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var subject: String
    var revisitFlag: Boolean = false
    lateinit var content: String

    constructor(id: Long,
                date: String,
                year: String,
                hour: Int,
                min: Int,
                latitude: String,
                longitude: String,
                subject: String,
                revisitFlag: Boolean,
                content: String) : this(id)
    {
        this.date = date
        this.year = year
        this.hour = hour
        this.min = min
        this.latitude = latitude
        this.longitude = longitude
        this.subject = subject
        this.revisitFlag = revisitFlag
        this.content = content
    }

    constructor(id: Long,
                date: String,
                hour: Int,
                min: Int,
                revisitFlag: Boolean,
                subject: String) : this(id)
    {
        this.date = date
        this.hour = hour
        this.min = min
        this.revisitFlag = revisitFlag
        this.subject = subject
    }
}