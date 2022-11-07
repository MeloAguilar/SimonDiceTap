package com.example.simondicetap

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    var velocidad : Velocidades? = null
    var secuenciaCpu : MutableList<Colores> = mutableListOf()
    var secuenciaUser : MutableList<Colores> = mutableListOf()

    var pausa : Boolean = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*
        var btnNormal = findViewById<Button>(R.id.btnNormal)
        btnNormal.setOnClickListener { onClickBotonesInicio(1) }
        var btnLento = findViewById<Button>(R.id.btnLento)
        btnLento.setOnClickListener { onClickBotonesInicio(2) }
        var btnRapido = findViewById<Button>(R.id.btnRapido)
        btnNormal.setOnClickListener { onClickBotonesInicio(3) }
        var btnAuto = findViewById<Button>(R.id.btnAuto)
        btnLento.setOnClickListener { onClickBotonesInicio(0) }
        var btnAzul = findViewById<ImageView>(R.id.btnAzul)
        btnAzul.setOnClickListener { onClickBotonesColores(Colores.AZUL) }
        var btnAmarillo = findViewById<ImageView>(R.id.btnAmarillo)
        btnAzul.setOnClickListener { onClickBotonesColores(Colores.AMARILLO) }
        var btnVerde = findViewById<ImageView>(R.id.btnVerde)
        btnAzul.setOnClickListener { onClickBotonesColores(Colores.VERDE) }
        var btnRojo = findViewById<ImageView>(R.id.btnRojo)

 */
        var btnAzul = findViewById<ImageView>(R.id.btnAzul)
        btnAzul.setOnClickListener { //onClickBotonesColores(Colores.ROJO)
            secuenciaParaProbar()}



    }



    fun secuenciaParaProbar(){
        secuenciaCpu.add(Colores.ROJO)
        secuenciaCpu.add(Colores.VERDE)
        secuenciaCpu.add(Colores.ROJO)
        secuenciaCpu.add(Colores.ROJO)
        secuenciaCpu.add(Colores.AMARILLO)
        secuenciaCpu.add(Colores.AZUL)
        secuenciaCpu.add(Colores.VERDE)
        var btn : ImageView? = null
        for (item in secuenciaCpu){
            var colorApagado :  Int = 0
            var colorEncendido : Int = 0
            var btnRes : Int

            if(btn!=null){
                btn.setImageResource(colorApagado)
            }
            when(item){
                Colores.ROJO -> {
                    colorApagado = R.drawable.btn_rojo_apagado
                    colorEncendido = R.drawable.btn_rojo_encendido
                    btnRes = R.id.btnRojo
                }
                Colores.VERDE -> {
                    colorApagado = R.drawable.btn_verde_apagado
                    colorEncendido = R.drawable.btn_verde_encendido
                    btnRes = R.id.btnVerde
                }
                Colores.AMARILLO -> {
                    colorApagado = R.drawable.btn_amarillo_apagado
                    colorEncendido = R.drawable.btn_amarillo_encendido
                    btnRes = R.id.btnAmarillo
                }
                Colores.AZUL -> {
                    colorApagado = R.drawable.btn_azul_apagado
                    colorEncendido = R.drawable.btn_azul_encendido
                    btnRes = R.id.btnAzul
                }
            }

            btn?.setImageResource(colorEncendido)

        }
    }



    fun onClickBotonesColores(color:Colores){
        secuenciaUser.add(color)
        var colorApagadoRes : Int
        var colorEncendidoRes : Int
        var btnId : Int
        if(secuenciaUser.size > 1){
            when(color) {
                Colores.ROJO ->{
                    colorApagadoRes = R.drawable.btn_rojo_apagado
                    btnId = R.id.btnRojo
                    colorEncendidoRes = R.drawable.btn_rojo_encendido
                }
                Colores.AMARILLO ->{
                    colorApagadoRes = R.drawable.btn_amarillo_apagado
                    btnId = R.id.btnAmarillo
                    colorEncendidoRes = R.drawable.btn_amarillo_encendido
                }
                Colores.VERDE ->{
                    colorApagadoRes = R.drawable.btn_verde_apagado
                    btnId = R.id.btnVerde
                    colorEncendidoRes = R.drawable.btn_verde_encendido
                }
                Colores.AZUL ->{
                    colorApagadoRes = R.drawable.btn_azul_apagado
                    btnId = R.id.btnAzul
                    colorEncendidoRes = R.drawable.btn_azul_encendido
                }
            }
            var btn = findViewById<ImageView>(btnId)
            btn.setImageResource(colorEncendidoRes)
            TimeUnit.MILLISECONDS.sleep(100)
            btn.setImageResource(colorApagadoRes)
        }

    }

    fun onClickBotonesInicio(numBtn : Int) {
        if (pausa) {
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


            }
            var ad : AlertDialog = dialogo.create() //show() MUST be called before dialog.getButton
            ad.show()

            ad.setCancelable(false)
            generarPatronCPU()

        }

        else{

        }
    }


    fun generarPatronCPU(){
        var rnd = Random.nextInt(Velocidades.values().size)
        var color = Colores.values()[rnd]
        var btnRojo = findViewById<ImageView>(R.id.btnRojo)
        var btnAzul = findViewById<ImageView>(R.id.btnAzul)
        var btnAmarillo = findViewById<ImageView>(R.id.btnAmarillo)
        var btnVerde = findViewById<ImageView>(R.id.btnVerde)
        var vel :Velocidades = velocidad as Velocidades
        secuenciaCpu.add(color)
        when(color){
            Colores.ROJO -> {
                btnRojo.performContextClick()
            }
            Colores.AZUL ->{
                btnAzul.setImageResource(R.drawable.btn_azul_encendido)
                TimeUnit.MILLISECONDS.sleep(vel.milis)
                btnAzul.setImageResource(R.drawable.btn_azul_apagado)
            }
            Colores.VERDE -> {
                btnVerde.setImageResource(R.drawable.btn_verde_encendido)
                TimeUnit.MILLISECONDS.sleep(vel.milis)
                btnVerde.setImageResource(R.drawable.btn_verde_apagado)
            }
            Colores.AMARILLO -> {
                btnAmarillo.setImageResource(R.drawable.btn_amarillo_encendido)
                TimeUnit.MILLISECONDS.sleep(vel.milis)
                btnAmarillo.setImageResource(R.drawable.btn_amarillo_apagado)
            }
        }



    }
}