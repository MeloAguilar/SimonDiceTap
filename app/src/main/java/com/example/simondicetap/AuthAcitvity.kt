package com.example.simondicetap

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simondicetap.database.UserEntity
import com.example.simondicetap.database.UsersAdapter
import com.example.simondicetap.databinding.ActivityAuthBinding
import com.example.simondicetap.firebase.UserFirebase
import com.example.simondicetap.firebase.UsersFirebaseAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

class AuthAcitvity : AppCompatActivity() {


    private lateinit var mAuth : FirebaseAuth

    val db = Firebase.firestore

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    lateinit var recyclerView: RecyclerView
    lateinit var users: MutableList<UserFirebase>
    lateinit var adapter:UsersFirebaseAdapter
    //lateinit var adapter: UsersAdapter
    var maxScore = 0

    lateinit var loginBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        users = ArrayList()


        GlobalScope.launch {
            getUsers()
        }
        loginBinding.btnSignUp.setOnClickListener {
            hideKeyboard()
            clickRegister()
            clearFocus()
        }
        loginBinding.btnLogin.setOnClickListener {
            hideKeyboard()
            clickLogin()
            clearFocus()


        }
        loginBinding

    }

    /**
     * Método para loguear al usuario
     *
     * <pre> ninguna <pre>
     * <post> Se loguea al usuario en firebase <post>
     */
    fun clickLogin() {
        email = loginBinding.etmail.text.toString()
        password = loginBinding.etPass.text.toString()
     //Instancio el objeto de firebase
        mAuth = FirebaseAuth.getInstance()
        //Método para loguear al usuario
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            //Si el usuario se loguea correctamente, se le redirige a la pantalla principal
            if (task.isSuccessful) {
                //Logeo correcto
                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("INTENT_NICK", email)
                startActivity(intent)
            } else {
                //Si el usuario no se loguea correctamente, se le muestra un mensaje de error
                Toast.makeText(this, "Error al loguear", Toast.LENGTH_SHORT).show()
                Log.w("Error", "signInWithEmail:failure", task.exception)
            }
        }




    }



    /**
     * Método para registrar al usuario
     *
     * <pre> ninguna <pre>
     * <post> Se registra al usuario en firebase <post>
     * @param email
     */
    fun clickRegister() {
        //Recogemos los datos de email y pass
        email = loginBinding.etmail.text.toString()
        password = loginBinding.etPass.text.toString()
        mAuth = FirebaseAuth.getInstance()
        //Método para registrar al usuario
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                //Genero un hashmap con los datos del usuario
                val userJ = hashMapOf(
                    "email" to email,
                    "password" to password,
                    "score" to 0
                )

                //Recojo la colección de usuarios y añado el usuario si este no existía antes.
                db.collection("Usuarios")
                    .add(userJ)
                    .addOnSuccessListener { documentReference ->
                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                    }
                //Registro correcto
                val user = mAuth.currentUser
                //Mandamos los datos a la pantalla principal
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("INTENT_NICK", email)
                startActivity(intent)
                //Si el usuario no se registra correctamente, se le muestra un mensaje de error
            } else {
                Log.w("Error", "signInWithEmail:failure", task.exception)
            }
        }
    }



    override fun onNavigateUp(): Boolean {
        users = ArrayList()
        GlobalScope.launch { getUsers() }
        return super.onNavigateUp()
    }
       // private fun addUser(userEntity: UserEntity) = runBlocking {
       //     launch {
       //         val id = SimonSaysApp.database.userDao().insertUser(userEntity)
       //         val recoveryUser = SimonSaysApp.database.userDao().getUserById(id)
//
       //         users.add(recoveryUser)
       //         adapter.notifyItemInserted(users.size)
       //         clearFocus()
       //         hideKeyboard()
       //     }
       // }
//
    fun getUsers() = runBlocking {
        launch {
            //users = SimonSaysApp.database.userDao().getAllUsers()
            db.collection("Usuarios")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val user = UserFirebase(
                            document.data["email"].toString(),
                            document.data["password"].toString(),
                            document.data["score"].toString().toInt()
                        )
                        users.add(user)
                        Log.d("TAG", "${document.id} => ${document.data}")
                    }

                    users.sortBy { it.getScore() }
                    setUpRecyclerView(users)
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }


            //Log.d("Prueba", users.toString())
        }

    }


    fun clearFocus() {
        loginBinding.etmail.setText("")
        loginBinding.etPass.setText("")
    }


    fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun setUpRecyclerView(tasks: List<UserFirebase>) {
        adapter = UsersFirebaseAdapter(tasks)
        recyclerView = findViewById(R.id.contenedorScores)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun updateUser(user: UserEntity) = runBlocking {

    }


    //private fun deleteUser(it: UserEntity) = runBlocking {
    //    launch {
    //        val pos = users.indexOf(it)
    //        SimonSaysApp.database.userDao().deleteUser(it)
    //        users.remove(it)
    //        adapter.notifyItemRemoved(pos)
    //    }
    //}
//
    //private fun getMaxScore() = runBlocking{
    //    var us = SimonSaysApp.database.userDao().getMaxScore()
    //    var maxScore = us.score
    //}
//
    fun clickInicio() {
        val intent = Intent(this, MainActivity::class.java)

        if (loginBinding.etmail.text.isNotEmpty()) {


            GlobalScope.launch {
                intent.putExtra("INTENT_NICK", loginBinding.etmail.text.toString())
                startActivity(intent)
            }
        }

    }

}