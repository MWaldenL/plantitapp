package com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Task(
    val id: String,
    var plantId: String,
    val userId: String,
    val action: String,
    val startDate: Date,
    val repeat: Int,
    val occurrence: String,
    var lastCompleted: Date,
    val weeklyRecurrence: ArrayList<Int>?
): Parcelable