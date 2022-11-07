package com.example.simondicetap

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    var velocidad: Velocidades? = null
    var secuenciaCpu: MutableList<Colores> = mutableListOf()
    var secuenciaUser: MutableList<Colores> = mutableListOf()

    var pausa: Boolean = true
    var colorApagado: Int = 0
    var colorEncendido: Int = 0
    var btnId: Int = 0
    final var colorAzulEncendido: Int = R.drawable.btn_azul_encendido
    final var colorVerdeEncendido: Int = R.drawable.btn_verde_encendido
    final var colorAmarilloEncendido: Int = R.drawable.btn_amarillo_encendido
    final var colorRojoEncendido: Int = R.drawable.btn_rojo_encendido
    final var colorAzulApagado: Int = R.drawable.btn_azul_apagado
    final var colorVerdeApagado: Int = R.drawable.btn_verde_apagado
    final var colorRojoApagado: Int = R.drawable.btn_rojo_apagado
    final var colorAmarilloApagado: Int = R.drawable.btn_amarillo_apagado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var btnNormal = findViewById<Button>(R.id.btnNormal)
        btnNormal.setOnClickListener { onClickBotonesInicio(1) }
        var btnLento = findViewById<Button>(R.id.btnLento)
        btnLento.setOnClickListener { onClickBotonesInicio(2) }
        var btnRapido = findViewById<Button>(R.id.btnRapido)
        btnRapido.setOnClickListener { onClickBotonesInicio(3) }
        var btnAuto = findViewById<Button>(R.id.btnAuto)
        btnAuto.setOnClickListener { onClickBotonesInicio(0) }
        var btnAzul = findViewById<ImageView>(R.id.btnAzul)
        btnAzul.setOnClickListener { onClickBotonesColores(Colores.AZUL) }
        var btnAmarillo = findViewById<ImageView>(R.id.btnAmarillo)
        btnAmarillo.setOnClickListener { onClickBotonesColores(Colores.AMARILLO) }
        var btnVerde = findViewById<ImageView>(R.id.btnVerde)
        btnVerde.setOnClickListener { onClickBotonesColores(Colores.VERDE) }
        var btnRojo = findViewById<ImageView>(R.id.btnRojo)
        btnRojo.setOnClickListener {
            onClickBotonesColores(Colores.ROJO)
        }

        quitarClickBotonesColores(btnRojo, btnAzul, btnVerde, btnAmarillo)



    }

    fun quitarClickBotonesColores(btn: View, btn2 : View, btn3:View, btn4:View){
        btn.isClickable = false
        btn2.isClickable = false
        btn3.isClickable = false
        btn4.isClickable = false
    }

    fun onClickBotonesInicio(numBtn: Int) {

            when (numBtn) {
                0 -> {
                    var rnd = Random.nextInt(Velocidades.values().size)
                    onClickBotonesInicio(rnd)
                }
                1 -> {
                    velocidad = Velocidades.MEDIO
                }
                2 -> {
                    velocidad = Velocidades.LENTO
                }
                3 -> {
                    velocidad = Velocidades.RAPIDO
                }

            }
            var dialogo = AlertDialog.Builder(this)
            dialogo.setMessage("Mira")
            dialogo.setPositiveButton("Ok") { _, _ ->
                pausa = false
                generarPatronCPU()

            }
            findViewById<ImageView>(R.id.btnAmarillo).isClickable = true
            findViewById<ImageView>(R.id.btnRojo).isClickable = true
            findViewById<ImageView>(R.id.btnVerde).isClickable = true
            findViewById<ImageView>(R.id.btnAzul).isClickable =true
            var ad: AlertDialog =
                dialogo.create()
            ad.show()

            ad.setCancelable(false)
    }





    fun onClickBotonesColores(color: Colores) {
        secuenciaUser.add(color)
        when (color) {
            Colores.ROJO -> {
                colorApagado = colorRojoApagado
                btnId = R.id.btnRojo
                colorEncendido = colorRojoEncendido
            }
            Colores.AMARILLO -> {
                colorApagado = colorAmarilloApagado
                btnId = R.id.btnAmarillo
                colorEncendido = colorAmarilloEncendido
            }
            Colores.VERDE -> {
                colorApagado = colorVerdeApagado
                btnId = R.id.btnVerde
                colorEncendido = colorVerdeEncendido
            }
            Colores.AZUL -> {
                colorApagado = colorAzulApagado
                btnId = R.id.btnAzul
                colorEncendido = colorAzulEncendido
            }
        }

        var btn = findViewById<ImageView>(btnId)
        btn.setImageResource(colorEncendido)

        GlobalScope.launch {
            delay(200)
            btn?.setImageResource(colorApagado)
        }
    }


fun comprobarClickUser(color : Colores, ronda : Int){
    if(color == secuenciaCpu[ronda]){
        generarPatronCPU()
    }else{
        findViewById<ImageView>(R.id.btnRojo).isClickable = false
        findViewById<ImageView>(R.id.btnAmarillo).isClickable = false
        findViewById<ImageView>(R.id.btnVerde).isClickable = false
        findViewById<ImageView>(R.id.btnAzul).isClickable = false
        if(color != (secuenciaCpu[ronda])){
            var dialogo = AlertDialog.Builder(this)
            dialogo.setMessage("Has Perdido")
            dialogo.setPositiveButton("Ok") { _, _ ->
                pausa = false

            }

    }
}


    fun mostrarPatronCPU(){
        for (item in secuenciaCpu){
            for (itemUser in secuenciaUser){

                }
            }
        }
    }


    fun generarPatronCPU() {
        var rnd = Random.nextInt(Velocidades.values().size)
        var color = Colores.values()[rnd]
        var btn: ImageView? = null
        var vel: Velocidades = velocidad as Velocidades
        secuenciaCpu.add(color)
        when (color) {
            Colores.ROJO -> {
                btnId = R.id.btnRojo
                btn = findViewById(btnId)
                btn?.setImageResource(colorRojoEncendido)
                colorApagado = colorRojoApagado

            }
            Colores.AZUL -> {
                btnId = R.id.btnAzul
                btn = findViewById(btnId)
                btn?.setImageResource(colorAzulEncendido)
                colorApagado = colorAzulApagado
            }
            Colores.VERDE -> {
                btnId = R.id.btnVerde
                btn = findViewById(btnId)
                btn?.setImageResource(colorVerdeEncendido)
                colorApagado = colorVerdeApagado
            }
            Colores.AMARILLO -> {
                btnId = R.id.btnAmarillo
                btn = findViewById(btnId)
                btn?.setImageResource(colorAmarilloEncendido)
                colorApagado = colorAmarilloApagado
            }

        }
        GlobalScope.launch {
            delay(100)
            btn?.setImageResource(colorApagado)
        }


    }

}