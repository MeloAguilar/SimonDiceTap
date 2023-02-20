package com.example.simondicetap

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simondicetap.firebase.UserFirebase
import com.example.simondicetap.database.UserEntity
import com.example.simondicetap.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlin.io.path.Path
//Gracias a esta librería tenemos acceso a las vistas de nuestro layout activity_main

import kotlin.random.Random

/**
 *
 * Activity que contiene la lógica del juego
 *
 */
class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private lateinit var mp: MediaPlayer

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


    //Usuario actual
    //lateinit var user: UserEntity

    //Usuario actual de firebase
    lateinit var userFireBase: UserFirebase

    //Mejor usuario de firebase
    lateinit var bestFireBaseUser: UserFirebase

    //Mejor usuario
    //lateinit var bestUser: UserEntity

    //Datos que vienen desde la activity Login
    lateinit var bundle: Bundle

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bestFireBaseUser = UserFirebase("0", "0", 0)
        userFireBase = UserFirebase("0", "0", 0)
        getUserFromLogin()
        //Instancio el binding conel layoutInflater de la activity actual
        binding = ActivityMainBinding.inflate(layoutInflater)
        //Establezco que el atributo root, que contiene la ruta de activity_main.xml
        //Serña lo que se verá en pantalla
        setContentView(binding.root)



        //Establezco el onclick del boton que nos da la opcion de volver a login
        binding.btnAtras.setOnClickListener { clickBtnAtras() }





        //Establezco lo que haran los botones de juego y de inicio
        setClickBotonesJuego()
        setClickBotonesInicio()

        //Le quito al usuario la posibilidad de tocar los botones
        // de juego para que no exista la posibilidad de que el
        // usuario no pueda romper el juego añadiendo datos
        // a su secuencia cuando no debe.
        quitarClickBotones()


    }





    //metodo que recoge los datos del login y los establece en la activity


    /**
     * Método que se encarga de recoger el string que viene del activity Login
     * establece que el usuario que jugará será ese,y
     * llama a la corrutina que se encarga de llamar al dao para
     * introducir este en la base de datos room
     */
    private fun getUserFromLogin() {
        //Recojo los extras del intent
        bundle = intent.extras!!
        //Establezco que el nick del usuario será un extra con el nombre "INTENT_NICK"
        //No se porque perso cuando le digo getString no me devuelve nada, aunque estoy 100%seguro de qye es un String
        var nick: String = bundle.get("INTENT_NICK").toString()
        //Recojo los usuarios de la base de datos
        db.collection("Usuarios").get()
            .addOnSuccessListener { result ->
            for (document in result) {
                    if (document.data["email"].toString() == nick) {
                        userFireBase = UserFirebase(document.data["email"].toString(),document.data["password"].toString(), document.data["score"].toString().toInt())
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }




            //Añado el usuario a la base de datos y recojo el usuario con mayor puntuación
            //addUser(user)
            GetMaxScore()

    }

    ///**
    // * Método que se encarga de añadir el usuario actual a la base de datos
    // *
    // */
    //private fun addUser(userEntity: UserEntity) = runBlocking {
    //    launch {
    //        val id: Long = SimonSaysApp.database.userDao().insertUser(userEntity)
    //        user = SimonSaysApp.database.userDao().getUserById(id)
    //    }
    //}

    ///**
    // * Método que envia una petición update para actualizar el score del usuario actual segun sea necesario
    // */
    //private fun UpdateScore(user: UserEntity) = runBlocking {
    //    launch {
    //        SimonSaysApp.database.userDao().updateScore(user)
//
    //    }
    //}

    private fun UpdateScore(user: UserFirebase){

        var doc = db.collection("Usuarios").document(user.getEmail())
        doc.update("score", user.getScore())

    }







    /**
     * Método que establece la mejor puntuación de la base de datos
     */
    private fun GetMaxScore() = runBlocking {
        launch {
            db.collection("Usuarios").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.data["score"].toString().toInt() > bestFireBaseUser.getScore()) {
                            bestFireBaseUser = UserFirebase(document.data["email"].toString(),document.data["password"].toString(), document.data["score"].toString().toInt())
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }

            //recojo la puntacion del usuario de firebase
        }


    }

    //metodo que se encarga de recoja el usuario con la puntuacion mas alta de firebase y la establece en la activity


    /**
     * Método que controla el click del ImageButton
     * con id btnAtras
     */
    fun clickBtnAtras() {
        //Si el user no tiene id se llamará al método que
        //obtiene el usuario del activity Login y establecerá el
        //correspondiente
        //if (userFireBase.getEmail() == "")
        //    getUserFromLogin()
        //Después llamaremos al método que finaliza el juego,
        // para registrar la puntuacion del usuario si este le diese en
        // medio de una partida en la que llevase una alta puntuación
        end()
        //Creamos el intent para viajar de nuevo al login
        var intent = Intent(this, AuthAcitvity::class.java)
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
            //No hago nada ya que si el usuario le
            // diese sin querer a fuera del alertlos
            // botones se quedarian inutiles
            }
        dialog.show()
        dialog.setCancelable(false)
        binding.btnNormal.setOnClickListener { onClickBotonesInicio(1) }
        binding.btnLento.setOnClickListener { onClickBotonesInicio(2) }
        binding.btnRapido.setOnClickListener { onClickBotonesInicio(3) }
        binding.btnAuto.setOnClickListener { onClickBotonesInicio(0) }
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
        //Por si el usuario le da cuando ya ha comenzado la partida
        if (secuenciaCpu.size > 0) {
            secuenciaCpu.clear()
            if (secuenciaUser.size > 0) {
                secuenciaUser.clear()
            }
        }
        //PAra establecer la velocidad de juego
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
        //Directamente si la longitud de la secuencia del usuario es mayor al numero de rondas
        //iremos directamente al
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
        //Si el score que el usuario ha conseguido en esta partida
        if (score > userFireBase.getScore()) {
            userFireBase.addScore(score)
            //Se actualizará su atributo score dentrp de la base de datos

                UpdateScore(userFireBase)

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



        } else {

            //Consulto si el tamaño de las listas es sigual
            if (secuenciaUser.size == secuenciaCpu.size) {
                //Sumo un punto al score del usuario
                secuenciaUser.clear()
                rondas++
                score++
                //Si las rondas conseguidas por el usuario son mayores que las de la mejor puntuacion
                if (score > bestFireBaseUser.getScore())
                //Ahora la mayor puntuacion es la del usuario
                    bestFireBaseUser = userFireBase
                //Establezco el atributo text de los TextView pertenecientes a las puntuaciones
                this.binding.txtScore.text = "Score:  ${userFireBase.getScore()} \n ${userFireBase.getEmail()}"
                this.binding.txtBestScore.text =
                    "Best Score: ${bestFireBaseUser.getScore()} \n ${bestFireBaseUser.getEmail()}"

                /**
                 * Toda esta zona no me hace ni puto caso.
                 * Funciona cuando quiere y cuando quiere no
                 */
                //Limpio la secuencia del usuario y simulo un click para el cpu
                //añado un nuevo color a la lista de la cpu
                simularClickCpu()


                //Le quito el click a los botones de juego
                quitarClickBotones()
                //Muestro el patron del CPU
                mostrarPatronCPU()


                //Devuelvo el click a los botones
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
    private fun iluminarBoton(color: Colores) = runBlocking {

        //Recogemos el id del sonido
        var idRaw = R.raw.boton_verde_sonido
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
                idRaw = R.raw.boton_rojo_sonido
            }
            Colores.AZUL -> {
                colorApagado = R.drawable.btn_azul_apagado
                colorEncendido = R.drawable.btn_azul_encendido
                btn = binding.btnAzul
                idRaw = R.raw.boton_azul_sonido
            }
            Colores.AMARILLO -> {
                colorApagado = R.drawable.btn_amarillo_apagado
                colorEncendido = R.drawable.btn_amarillo_encendido
                btn = binding.btnAmarillo
                idRaw = R.raw.boton_amarillo_sonido
            }
            else -> {}
        }
        launch {
            mp = MediaPlayer.create(this@MainActivity, idRaw)

            //Inicio el sonido
            mp.start()
            //Se establece el color encendido del boton
            btn.setImageResource(colorEncendido)

            delay(velocidad!!.milis)
            //Volvemos a establecer src de la imageView como el boton de color apagado
            btn.setImageResource(colorApagado)

            delay(100)
            mp.reset()
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

                    GlobalScope.launch {
                        delay(500)
                        //Recorro la secuencia de la CPU
                        for (color in secuenciaCpu) {
                            //Espero los milisegundos escogidos en el inicio
                            delay(velocidad!!.milis / 2)
                            //Ilumino el boton
                            iluminarBoton(color)
                            delay(velocidad!!.milis / 2)

                        }


                    }


                } catch (_: InterruptedException) {
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