package com.example.simondicetap

import android.app.Application
import android.content.Context
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

/**
 *
 * Activity que contiene la lógica del juego
 *
 */
class MainActivity : AppCompatActivity() {

    //Numero de rpndas
    private var rondas = 0

    //Booleano que muestra cuando finaliza el juego
    private var fallo = false

    //velocidad del cambio de color
    var velocidad: Velocidades? = null

    //Lista de colores que representa los botones que toco el CPU
    var secuenciaCpu: MutableList<Colores> = mutableListOf()

    //Lista de colores que representa los botones que toco el usuario
    var secuenciaUser: MutableList<Colores> = mutableListOf()

    //Puntuacion del usuario
    var score: Int = 0

    //Máxima puntuacion registrada
    var bestScore: Int = 0

    //Usuario actual
    lateinit var user: UserEntity

    //Datos que vienen desde la activity Login
    lateinit var bundle: Bundle

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



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
     * Método que se encarga de recoger el string que viene del activity Login
     * establece que el usuario que jugará será ese,y
     * llama a la corrutina que se encarga de llamar al dao para
     * introducir este en la base de datos room
     */
    private fun getUserFromLogin() {
        bundle = intent.extras!!
        var nick: String = bundle.get("INTENT_NICK").toString()
        user = UserEntity(nickname = nick)
        binding.tvNickUser.text = user.nickname
        GlobalScope.launch { addUser(user) }

    }

    /**
     * Corrutina que se encarga de añadir el usuario actual a la base de datos
     *
     */
    private fun addUser(userEntity: UserEntity) = runBlocking {
        launch {
            val id: Long = SimonSaysApp.database.userDao().insertUser(userEntity)
            user = SimonSaysApp.database.userDao().getUserById(id)
        }
    }

    private fun UpdateScore(user: UserEntity) = runBlocking {
        launch {
            SimonSaysApp.database.userDao().updateScore(user)

        }
    }


    /**
     * Método que controla el click del ImageButton
     * con id btnAtras
     */
    fun clickBtnAtras() {
        //Si el user no tiene id se llamará al método que
        //obtiene el usuario del activity Login y establecerá el
        //correspondiente
        if (user.id == 0)
            getUserFromLogin()
        //Después llamaremos al método que finaliza el juego,
        // para registrar la puntuacion del usuario si este le diese en
        // medio de una partida en la que llevase una alta puntuación
        end()
        //Creamos el intent para viajar de nuevo al login
        var intent = Intent(this, Login::class.java)
        //Establezco que se borrará toda la pila de actividades
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //Le decimos a la App que comience una nueva actividad
        //y le mandamos el intent que nos devuelve al Login
        startActivity(intent)
    }

    /**
     * Método que se encarga de darle la accion necesaria a los botones de inicio
     */
    fun setClickBotonesInicio() {
        /**
         * Set OnCLick Botones Inicio
         */
        var dialog = AlertDialog.Builder(this)
            .setTitle("Simon Says")
            .setMessage("Elige la rapidez de los cambios de color")
            .setPositiveButton("Ok") { _, _ ->
                binding.btnNormal.setOnClickListener { onClickBotonesInicio(1) }
                binding.btnLento.setOnClickListener { onClickBotonesInicio(2) }
                binding.btnRapido.setOnClickListener { onClickBotonesInicio(3) }
                binding.btnAuto.setOnClickListener { onClickBotonesInicio(0) }
            }
        dialog.show()
        dialog.setCancelable(false)
    }

    fun setClickBotonesJuego() {
        /**
         * Set OnClick Botones Juego
         */
        getUserFromLogin()
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
    fun start() = runBlocking {

        //Corruntina
        launch {
            //Llamo al método que añade un color a secuenciaCpu
            simularClickCpu()
            delay(velocidad!!.milis)
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
        GlobalScope.launch {
            //Ilumino el boton
            iluminarBoton(color)

        }

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


        if (score > user.score) {
            user.score = score
            GlobalScope.launch {
                UpdateScore(user)
            }
        }
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
            end()


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


                runBlocking {
                    //Le quito el click a los botones de juego
                    quitarClickBotones()
                    //Muestro el patron del CPU
                    mostrarPatronCPU()
launch {
     }


                }


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
    private fun iluminarBoton(color: Colores) = runBlocking {
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
        launch {

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

        var dialogo = AlertDialog.Builder(this)
            .setTitle("Simon Says")
            .setMessage("Observa la secuencia!!")
            .setPositiveButton("Ok") { _, _ ->
                try {
                    runBlocking {
                        launch {
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
                }catch(_: InterruptedException){
                    //No hacemos nada
                }


            }
            .setCancelable(false)
        dialogo.show()

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