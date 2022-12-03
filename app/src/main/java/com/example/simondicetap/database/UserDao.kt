package com.example.simondicetap.database

import androidx.room.*

@Dao
interface UserDao {

    @Query("Select * From users_entity order by score desc Limit 10")
    fun getAllUsers(): MutableList<UserEntity>


    @Insert
    fun insertUser(usuario: UserEntity): Long

    @Update
    fun updateScore(user: UserEntity)

    @Query("Select * From users_entity Where id =:searchid")
    fun getUserById(searchid: Long): UserEntity

    @Delete
    fun deleteUser(user: UserEntity)

    @Query("Select * From users_entity Order By score desc Limit 1")
    fun getMaxScore(): UserEntity


}