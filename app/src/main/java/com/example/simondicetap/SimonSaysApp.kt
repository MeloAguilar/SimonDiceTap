package com.example.simondicetap

import android.app.Application
import android.arch.persistence.room.Room
import com.example.simondicetap.database.UserDatabase

class SimonSaysApp : Application() {
    companion object {
        lateinit var database: UserDatabase
    }

    override fun onCreate() {
        super.onCreate()
        SimonSaysApp.database = Room.databaseBuilder(this, UserDatabase::class.java, "users-db").build()
    }
}