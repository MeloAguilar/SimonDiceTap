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
import com.example.simondicetap.databinding.ActivityLoginBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Login : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var users: MutableList<UserEntity>
    lateinit var adapter: UsersAdapter
    var maxScore = 0

    lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        users = ArrayList()
        GlobalScope.launch {
            getUsers()
        }
        loginBinding.btnIniciar.setOnClickListener {
            hideKeyboard()
            clickInicio()
            clearFocus()

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
        loginBinding.etNick.setText("")
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

        if (loginBinding.etNick.text.isNotEmpty()) {

            GlobalScope.launch {
                intent.putExtra("INTENT_NICK", loginBinding.etNick.text.toString())
                startActivity(intent)
            }
        }

    }

}