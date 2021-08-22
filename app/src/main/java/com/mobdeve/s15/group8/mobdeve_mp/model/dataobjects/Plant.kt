package com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects

import android.os.Parcelable
import kotlinx.parcelize.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: String,
    val userId: String,
    val imageUrl: String,
    val filePath: String,
    var name: String,
    var nickname: String,
    val dateAdded: String,
    var death: Boolean,
    val tasks: @RawValue ArrayList<String>,
    val journal: @RawValue ArrayList<Journal>
): Parcelable
