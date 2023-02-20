package com.example.simondicetap.firebase

class UserFirebase {
    var score: Int = 0
    var email: String = ""
    var password: String = ""

    //Constructor vacío
    constructor()


    //Constructor con parámetros
    constructor(email: String, password: String) {
        this.score = 0
        this.email = email
        this.password = password
    }

    //getters and setters
    fun getScore(): Int {
        return score
    }

    fun setScore(score: Int) {
        this.score = score
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }



}