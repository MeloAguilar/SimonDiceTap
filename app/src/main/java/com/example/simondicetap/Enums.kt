package com.example.simondicetap

enum class Colores(var drawableId : Int) {
    AZUL(R.drawable.btn_azul),AMARILLO(R.drawable.btn_amarillo), VERDE(R.drawable.btn_verde), ROJO(R.drawable.btn_rojo);
}

enum class Velocidades(var milis : Long){
    RAPIDO(100), MEDIO(300), LENTO(600)
}