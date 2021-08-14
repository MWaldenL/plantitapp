package com.mobdeve.s15.group8.mobdeve_mp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Journal(
    val body: String,
    val date: String): Parcelable
// etc...