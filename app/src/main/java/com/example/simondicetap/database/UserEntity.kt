package com.example.simondicetap.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_entity")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nickname: String = "",
    var score: Int = 0,
)