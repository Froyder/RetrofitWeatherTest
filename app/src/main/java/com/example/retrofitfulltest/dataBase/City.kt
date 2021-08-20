package com.example.retrofitfulltest.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class City  (
    @PrimaryKey (autoGenerate = true)
    val id: Long,
    val name: String
)