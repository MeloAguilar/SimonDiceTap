package com.example.simondicetap.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simondicetap.R

class UsersAdapter(
    val tasks: List<UserEntity>,
    val checkUser: (UserEntity) -> Unit,
    val deleteUser: (UserEntity) -> Unit) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tasks[position]
        holder.bind(item, checkUser, deleteUser)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_user, parent, false))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTask = view.findViewById<TextView>(R.id.tvUser)
        val tvScore = view.findViewById<TextView>(R.id.tvScore)
        fun bind(task: UserEntity, checkTask: (UserEntity) -> Unit, deleteTask: (UserEntity) -> Unit) {
            tvTask.text = task.nickname
            tvScore.text = task.score.toString()
            tvScore.setOnClickListener{checkTask(task)}
            itemView.setOnClickListener { deleteTask(task) }
        }
    }

    }