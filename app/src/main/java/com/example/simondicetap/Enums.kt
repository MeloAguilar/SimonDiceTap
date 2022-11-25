package com.example.simondicetap

enum class Colores(var drawableId : Int) {
    AZUL(R.drawable.btn_azul_apagado),AMARILLO(R.drawable.btn_amarillo_apagado), VERDE(R.drawable.btn_verde_apagado), ROJO(R.drawable.btn_rojo_apagado);
}

enum class Velocidades(var milis : Long){
    RAPIDO(100), MEDIO(300), LENTO(600)
}