package com.example.simondicetap

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simondicetap.database.UserEntity
import com.example.simondicetap.database.UsersAdapter
import com.example.simondicetap.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

class AuthAcitvity : AppCompatActivity() {


    private lateinit var mAuth : FirebaseAuth


    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    lateinit var recyclerView: RecyclerView
    lateinit var users: MutableList<UserEntity>
    lateinit var adapter: UsersAdapter
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
            clearFocus()
            clickLogin()

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
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            //Si el usuario se loguea correctamente, se le redirige a la pantalla principal
            if (task.isSuccessful) {
                //Logeo correcto
                val user = mAuth.currentUser
                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("INTENT_NICK", loginBinding.etmail.text.toString())
                startActivity(intent)
            } else {
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
                //Registro correcto
                val user = mAuth.currentUser
                //Mandamos los datos a la pantalla principal
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("INTENT_NICK", loginBinding.etmail.text.toString())
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
        private fun addUser(userEntity: UserEntity) = runBlocking {
            launch {
                val id = SimonSaysApp.database.userDao().insertUser(userEntity)
                val recoveryUser = SimonSaysApp.database.userDao().getUserById(id)

                users.add(recoveryUser)
                adapter.notifyItemInserted(users.size)
                clearFocus()
                hideKeyboard()
            }
        }

    fun getUsers() = runBlocking {
        launch {
            users = SimonSaysApp.database.userDao().getAllUsers()
            setUpRecyclerView(users)
            Log.d("Prueba", users.toString())
        }

    }


    fun clearFocus() {
        loginBinding.etmail.setText("")
    }


    fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun setUpRecyclerView(tasks: List<UserEntity>) {
        adapter = UsersAdapter(tasks)
        recyclerView = findViewById(R.id.contenedorScores)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun updateUser(user: UserEntity) = runBlocking {

    }


    private fun deleteUser(it: UserEntity) = runBlocking {
        launch {
            val pos = users.indexOf(it)
            SimonSaysApp.database.userDao().deleteUser(it)
            users.remove(it)
            adapter.notifyItemRemoved(pos)
        }
    }

    private fun getMaxScore() = runBlocking{
        var us = SimonSaysApp.database.userDao().getMaxScore()
        var maxScore = us.score
    }

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