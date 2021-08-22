package com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val action: String,
    val startDate: String,
    val repeat: Int = 1,
    val occurrence: String): Parcelable