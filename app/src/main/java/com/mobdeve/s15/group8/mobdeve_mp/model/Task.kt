package com.mobdeve.s15.group8.mobdeve_mp.model

import java.util.*

data class Task(
    val action: String,
    val startDate: String,
    val repeat: Int,
    val occurrence: String
)