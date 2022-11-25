package com.example.simondicetap

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simondicetap.databinding.ActivityMainBinding
//Gracias a esta librería tenemos acceso a las vistas de nuestro layout activity_main

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : AppCompatActivity() {


    private var fallo = false;
    var velocidad: Velocidades? = null
    var secuenciaCpu: MutableList<Colores> = mutableListOf()
    var secuenciaUser: MutableList<Colores> = mutableListOf()
    var score: Int = 0
    var bestScore: Int = 0

    var pausa: Boolean = true
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNormal.setOnClickListener { onClickBotonesInicio(1) }
        binding.btnLento.setOnClickListener { onClickBotonesInicio(2) }

        binding.btnRapido.setOnClickListener { onClickBotonesInicio(3) }
        binding.btnAuto.setOnClickListener { onClickBotonesInicio(0) }

        binding.btnAzul.setOnClickListener { onClickBotonesJuego(Colores.AZUL) }
        binding.btnAmarillo.setOnClickListener { onClickBotonesJuego(Colores.AMARILLO) }
        binding.btnVerde.setOnClickListener { onClickBotonesJuego(Colores.VERDE) }
        binding.btnRojo.setOnClickListener {
            onClickBotonesJuego(Colores.ROJO)
        }



    }

    /**
     * Método que se encarga de devolver el estado clickable de 4 botones.
     * Se puede elegir entre los botones de inicio o los botones de juego
     */
    fun devolverClickBotones(btn: View, btn2: View, btn3: View, btn4: View) {
        btn.isClickable = true
        btn2.isClickable = true
        btn3.isClickable = true
        btn4.isClickable = true
    }


    /**
     * Método que se encarga de quitar el estado clickable de 4 botones.
     * Se puede elegir entre los botones de inicio o los botones de juego
     */
    fun quitarClickBotones(btn: View, btn2: View, btn3: View, btn4: View) {
        btn.isClickable = false
        btn2.isClickable = false
        btn3.isClickable = false
        btn4.isClickable = false
    }


    /**
     * Método que establece la velocidad de juego a partir del boton que clicke el usuario
     *
     */
    fun onClickBotonesInicio(numBtn: Int) {
        if (secuenciaCpu.size > 0){
            secuenciaCpu.clear()
            if (secuenciaUser.size > 0) {
                secuenciaUser.clear()
            }
        }
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

        start()
    }

    fun start() {
        simularClickCpu()
        mostrarPatronCPU()
        devolverClickBotones(binding.btnAmarillo,
            binding.btnAzul,
            binding.btnVerde,
            binding.btnRojo)
    }

    /**
     *  Método que se encarga de iluminar el imageView dado un
     *  enum Colores, añadir el color a lla secuencia del usuario
     *  y llamar al método que se encarga de generar la siguiente ronda.
     *
     */
    fun onClickBotonesJuego(color: Colores) {
        secuenciaUser.add(color)
        iluminarBoton(color)
        comprobarClickUsuario(color, secuenciaUser.size - 1)

    }


    fun comprobarClickUsuario(color: Colores, ronda: Int) {
        if (color == secuenciaCpu[ronda]) {
            siguienteRonda()
        } else {
            end()
        }
    }

    /**
     * Método que se lanza cuando el usuario falla en una pulsación.
     * Finaliza el juego y lo reinicia
     */
    fun end() {
        secuenciaUser.clear()
        secuenciaCpu.clear()
        fallo = true
        var dialogo = AlertDialog.Builder(this)
            .setMessage("Has Perdido")
            .setPositiveButton("Ok") { _, _ ->
                fallo = false
                var rei = AlertDialog.Builder(this)
                    .setMessage("Volver a jugar")
                    .setPositiveButton("Sí") { _, _ ->
                        devolverClickBotones(binding.btnAmarillo,
                            binding.btnAzul,
                            binding.btnVerde,
                            binding.btnRojo)
                        simularClickCpu()
                        mostrarPatronCPU()

                    }
                rei.show()
            }
        dialogo.show()
    }

    /**
     * Método que se encarga de, en cada ronda, comprobar si el jugador se ha equivocado o no
     */
    private fun siguienteRonda(){

        if(fallo){
            end()
        }else{
            score++
            if(score > bestScore)
                bestScore = score

            this.binding.txtScore.text = "Score:  $score"
            this.binding.txtBestScore.text = "Best Score: $bestScore"
            simularClickCpu()
            mostrarPatronCPU()
            start()
        }
    }


    /**
     *
     * Método que se encarga de iluminar un imageButton a partir de un objeto enum
     * Colores.
     *
     *
     */
    private fun iluminarBoton(color: Colores) {
        //Referencia al drawable que contiene el boton apagado
        var colorApagado = R.drawable.btn_verde_apagado
        //Referencia al drawable que contiene el boton encendido
        var colorEncendido = R.drawable.btn_verde_encendido
        //REferencia al boton en el layout
        var btn = binding.btnVerde
        //Según el color que entre como paráetro se estableceran las variables
        when (color) {
            //Si color es ROJO
            Colores.ROJO -> {
                //Se cambia la referencia del drawable
                colorApagado = R.drawable.btn_rojo_apagado
                colorEncendido = R.drawable.btn_rojo_encendido
                btn = binding.btnRojo
            }
            Colores.AZUL -> {
                colorApagado = R.drawable.btn_azul_apagado
                colorEncendido = R.drawable.btn_azul_encendido
                btn = binding.btnAzul
            }
            Colores.AMARILLO -> {
                colorApagado = R.drawable.btn_amarillo_apagado
                colorEncendido = R.drawable.btn_amarillo_encendido
                btn = binding.btnAmarillo
            }
            else -> {

            }
        }

        //Se establece el color encendido del boton
        btn.setImageResource(colorEncendido)
        GlobalScope.launch {
            //Lo mantenemos los milisegundos que coincidan con la velocidad elegida
            delay(velocidad!!.milis)
            //Volvemos a establecer src de la imageView como el boton de color apagado
            btn.setImageResource(colorApagado)
        }

    }


    /**
     *
     * Método que se encarga de mostrar la secuencia del cpu
     *
     */
    private fun mostrarPatronCPU() {

        GlobalScope.launch {
            //Quitamos el click de los botones
            quitarClickBotones(binding.btnAmarillo,
                binding.btnAzul,
                binding.btnVerde,
                binding.btnRojo)
            for (color in secuenciaCpu) {
                delay(400)
                iluminarBoton(color)
            }

        }
        devolverClickBotones(binding.btnAmarillo,
            binding.btnAzul,
            binding.btnVerde,
            binding.btnRojo)

    }


    private fun simularClickCpu() {
        var rnd = Random.nextInt(Velocidades.values().size)
        var color = Colores.values()[rnd]
        secuenciaCpu.add(color)
    }

}