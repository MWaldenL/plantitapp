package com.mobdeve.s15.group8.mobdeve_mp.model

import android.os.Parcelable
import kotlinx.parcelize.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val imageUrl: String,
    val name: String,
    val nickname: String,
    val tasks: @RawValue ArrayList<Task>,
    val journal: @RawValue ArrayList<Journal>): Parcelable
