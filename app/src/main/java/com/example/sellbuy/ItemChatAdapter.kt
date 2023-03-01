package com.example.sellbuy

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.ItemChat

class ItemChatAdapter( context: Context,private val chatList: MutableList<ItemChat>): RecyclerView.Adapter<ItemChatAdapter.ViewHolder>() {
    private val mcontext:Context?=context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChat = chatList[position]
        holder.nomeUtente.text=currentChat.nomeUtente
        holder.nomeArticolo.text=currentChat.nomeArticolo

/*
        holder.itemView.setOnClickListener{
            val intent= Intent(mcontext,SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mcontext?.startActivity(intent)
        }*/
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nomeUtente: TextView = itemView.findViewById(R.id.txtName_utenteChat)
        val nomeArticolo: TextView = itemView.findViewById(R.id.txtName_annuncioChat)    }
}