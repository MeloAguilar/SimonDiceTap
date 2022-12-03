package com.example.simondicetap.database

import androidx.room.*

@Dao
interface UserDao {

    @Query("Select * From user_entity")
    fun getAllUsers(): MutableList<UserEntity>

    @Insert
    fun insertUser(usuario: UserEntity) : Long

    @Update
    fun updateScore(score: Int, id: Long)

    @Query("Select * From user_entity Where id =:searchid" )
    fun getUserById(searchid: Long) : UserEntity

    @Delete
    fun deleteUser(user: UserEntity)


}