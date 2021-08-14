package com.mobdeve.s15.group8.mobdeve_mp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val done: Boolean,
    val task: String,
    val everyTime: Int): Parcelable
