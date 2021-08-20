package com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Task(
    val action: String,
    val startDate: String,
    val repeat: Int,
    val occurrence: String,
    var lastCompleted: Date
): Parcelable