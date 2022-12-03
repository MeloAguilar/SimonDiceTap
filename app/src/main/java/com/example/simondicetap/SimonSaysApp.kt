package com.example.simondicetap

import android.app.Application
import androidx.room.Room
import com.example.simondicetap.database.UserDatabase

class SimonSaysApp : Application() {
    companion object {
        lateinit var database: UserDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, UserDatabase::class.java, "myusers-db").build()
    }
}