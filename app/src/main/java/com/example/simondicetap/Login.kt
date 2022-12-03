package com.example.simondicetap

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simondicetap.database.UserEntity
import com.example.simondicetap.database.UsersAdapter
import com.example.simondicetap.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Login : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var users: MutableList<UserEntity>
    lateinit var adapter : UsersAdapter

    lateinit var loginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        users = ArrayList()
        getUsers()
        loginBinding.btnIniciar.setOnClickListener {
            clickInicio()
            addUser(UserEntity(nickname = loginBinding.etNick.text.toString()))
        }

    }

    private fun addUser(userEntity: UserEntity) {
        val id = SimonSaysApp.database.userDao().insertUser(userEntity)
        val recoveryUser = SimonSaysApp.database.userDao().getUserById(id)

            users.add(recoveryUser)
            adapter.notifyItemInserted(users.size)
            clearFocus()
            hideKeyboard()
    }

    fun getUsers() {
            users = SimonSaysApp.database.userDao().getAllUsers()

    }


    fun clearFocus(){
        loginBinding.etNick.setText("")
    }



    fun Context.hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun setUpRecyclerView(tasks: List<UserEntity>) {
        adapter = UsersAdapter(tasks, { updateUser(it) }, {deleteUser(it)})
        recyclerView = findViewById(R.id.contenedorScores)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun deleteUser(it: UserEntity) = runBlocking {
        launch{
            val pos = users.indexOf(it)
            SimonSaysApp.database.userDao().deleteUser(it)
            users.remove(it)
            adapter.notifyItemRemoved(pos)
        }
    }

    private fun updateUser(it: UserEntity) {

    }

    fun clickInicio() {
        if (loginBinding.etNick.text.isNotEmpty()) {

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("INTENT_NICK", loginBinding.etNick.text)
            startActivity(intent)
        }

    }

}