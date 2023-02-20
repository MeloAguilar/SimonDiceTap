package com.example.simondicetap.firebase

public class UserFirebase {
    private var score: Int = 0
    private var email: String = ""
    private var password: String = ""

    //Constructor vacío
    constructor(){}


    //Constructor con parámetros
    constructor(email: String, password: String) {
        this.score = 0
        this.email = email
        this.password = password
    }
    constructor(email: String, password: String, score: Int) {
        this.score = score
        this.email = email
        this.password = password
    }

    fun addScore(score: Int) {
        this.score += score
    }

    fun setScore(score: Int) {
        this.score = score
    }
    fun addOneScore() {
        this.score += 1
    }

    fun getScore(): Int {
        return this.score
    }

    fun getEmail(): String {
        return this.email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String {
        return this.password
    }

    fun setPassword(password: String) {
        this.password = password
    }


}