package com.example.simondicetap

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simondicetap.database.UserEntity
import com.example.simondicetap.databinding.ActivityMainBinding
import kotlinx.coroutines.*
//Gracias a esta librería tenemos acceso a las vistas de nuestro layout activity_main

import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private var rondas = 0
    private var fallo = false
    var velocidad: Velocidades? = null
    var secuenciaCpu: MutableList<Colores> = mutableListOf()
    var secuenciaUser: MutableList<Colores> = mutableListOf()
    var score: Int = 0
    var bestScore: Int = 0
    lateinit var user: UserEntity
    lateinit var bundle: Bundle

    var pausa: Boolean = true
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!

        binding.btnAtras.setOnClickListener { clickBtnAtras() }





        setClickBotonesJuego()
        setClickBotonesInicio()

        //Le quito al usuario la posibilidad de tocar los botones
        // de juego para que no exista la posibilidad de que el
        // usuario no pueda romper el juego añadiendo datos
        // a su secuencia cuando no debe.
        quitarClickBotones()


    }


    /**
     * Método que recibe el intent que llega del login
     */
    private fun getUserById() = runBlocking {
        launch {
            user = UserEntity()
            var id: Long = bundle.get("INTENT_NICK") as Long
            user = SimonSaysApp.database.userDao().getUserById(id)

        }


        binding.tvNickUser.text = user.nickname
    }


    fun clickBtnAtras() {

        val intent = Intent(this, Login::class.java)

    }

    fun setClickBotonesInicio() {
        /**
         * Set OnCLick Botones Inicio
         */
        binding.btnNormal.setOnClickListener { onClickBotonesInicio(1) }
        binding.btnLento.setOnClickListener { onClickBotonesInicio(2) }
        binding.btnRapido.setOnClickListener { onClickBotonesInicio(3) }
        binding.btnAuto.setOnClickListener { onClickBotonesInicio(0) }
    }

    fun setClickBotonesJuego() {
        /**
         * Set OnClick Botones Juego
         */
        GlobalScope.launch { getUserById() }
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
    fun devolverClickBotones() {
        binding.btnRojo.isClickable = true
        binding.btnAmarillo.isClickable = true
        binding.btnAzul.isClickable = true
        binding.btnVerde.isClickable = true
    }


    /**
     * Método que se encarga de quitar el estado clickable de 4 botones.
     * Se puede elegir entre los botones de inicio o los botones de juego
     */
    fun quitarClickBotones() {
        binding.btnRojo.isClickable = false
        binding.btnVerde.isClickable = false
        binding.btnAzul.isClickable = false
        binding.btnAmarillo.isClickable = false
    }


    /**
     * Método que establece la velocidad de juego a partir del boton que clicke el usuario
     *
     */
    fun onClickBotonesInicio(numBtn: Int) {
        fallo = false
        if (secuenciaCpu.size > 0) {
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


    /**
     *
     * Método que se encarga de iniciar el juego.
     * Cuando se lanza genera un clickk para la cpu,
     * realiza un delay de 500ms y llama al método que
     * muestra el patron del cpu.
     *
     */
    fun start() {

        //Corruntina
        GlobalScope.launch {
            //Llamo al método que añade un color a secuenciaCpu
            simularClickCpu()
            delay(500)
            //Llamo al método que se encarga de recorrer secuenciaCpu y llamar al método
            mostrarPatronCPU()

        }
        devolverClickBotones()

    }

    /**
     *  Método que se encarga de iluminar el imageView dado un
     *  enum Colores, añadir el color a lla secuencia del usuario
     *  y llamar al método que se encarga de generar la siguiente ronda.
     *
     */
    fun onClickBotonesJuego(color: Colores) {
        //añado el color a la lista de colores del usuario
        secuenciaUser.add(color)
        if (secuenciaUser.size - 1 > rondas) {
            fallo = true
            end()
        }
        //Ilumino el boton
        iluminarBoton(color)
        //Compruebo si el usuario lo hizo bien
        comprobarClickUsuario(color)


    }


    /**
     *
     * Método que, dado un entero y un objeto enum Colores, comprueba la MutableList<Colores>
     * secuenciaUser en la posicion pasada como parámetro, establece el
     *
     */
    fun comprobarClickUsuario(color: Colores) {

        var posicion = secuenciaUser.size - 1
        //Si el color que eligió el usuario es igual
        if (color != secuenciaCpu[posicion]) {
            fallo = true
        }
        siguienteRonda()


    }

    /**
     * Método que se lanza cuando el usuario falla en una pulsación.
     * Finaliza el juego y lo reinicia
     */
    fun end() {
        rondas = 0
        score = 0
        //Reinicio el TextView que contiene el score
        binding.txtScore.text = "Score:  $score"
        //Reinicio las secuencias de los jugadores
        secuenciaUser.clear()
        secuenciaCpu.clear()
        //Muestro un diálogo al usuario para indicar que ha perdido
        var dialogo = AlertDialog.Builder(this)
            .setTitle("Simon Says")
            .setMessage("Has Perdido")
            .setPositiveButton("Ok") { _, _ ->
                //No hace nada para ser solo un dialog informativo
            }
        dialogo.show()

    }

    /**
     * Método que se encarga de, en cada ronda, comprobar si el jugador se ha equivocado o no
     */
    private fun siguienteRonda() {

        //Si fallo = true llamo a la funcion end
        if (fallo) {
            GlobalScope.launch {
                end()
            }

            //Si no
        } else {

            //Consulto si el tamaño de las listas es sigual
            if (secuenciaUser.size == secuenciaCpu.size) {
                //Sumo un punto al score del usuario
                secuenciaUser.clear()
                rondas++
                score++
                //Si las rondas conseguidas por el usuario son mayores que las de la mejor puntuacion
                if (score > bestScore)
                //Ahora la mayor puntuacion es la del usuario
                    bestScore = score
                //Establezco el atributo text de los TextView pertenecientes a las puntuaciones
                this.binding.txtScore.text = "Score:  $score"
                this.binding.txtBestScore.text = "Best Score: $bestScore"
                //Limpio la secuencia del usuario y simulo un click para el cpu
                //añado un nuevo color a la lista de la cpu
                simularClickCpu()
                //Le quito el click a los botones de juego
                quitarClickBotones()

                //Muestro el patron del CPU
                mostrarPatronCPU()



                devolverClickBotones()
            }
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
            Colores.ROJO -> {
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
            else -> {}
        }
        GlobalScope.launch {

            //Se establece el color encendido del boton
            btn.setImageResource(colorEncendido)


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
            delay(400)
            //Recorro la secuencia de la CPU
            for (color in secuenciaCpu) {
                //Espero los milisegundos escogidos en el inicio
                delay(velocidad!!.milis)
                //Ilumino el boton
                iluminarBoton(color)
                delay(velocidad!!.milis)

            }


        }

    }


    /**
     *
     * Método que se encarga de añadir a la lista de Colores de
     * la cpu a partir de un numero random,
     *
     */
    private fun simularClickCpu() {
        //Genero el numero random
        var rnd = Random.nextInt(Velocidades.values().size)
        //Recojo el color a partir del numero random
        var color = Colores.values()[rnd]
        //Añado el color a la lista
        secuenciaCpu.add(color)
    }

}