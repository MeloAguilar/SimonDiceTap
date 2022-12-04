package com.example.simondicetap.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simondicetap.R

class UsersAdapter(
    val users: List<UserEntity>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        holder.bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_user, parent, false))
    }

    override fun getItemCount(): Int {
        return users.size
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser = view.findViewById<TextView>(R.id.tvUser)
        val tvScore = view.findViewById<TextView>(R.id.tvScore)
        fun bind(task: UserEntity,) {
            tvUser.text = task.nickname
            tvScore.text = task.score.toString()
        }
    }

    }