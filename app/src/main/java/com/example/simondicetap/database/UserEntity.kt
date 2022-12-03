package com.example.simondicetap.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_entity")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var nickname: String = "",
    var score: Int = 0,
)