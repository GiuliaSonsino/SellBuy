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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.FirebaseDbWrapper
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
        holder.nomeArticolo.text=currentChat.nomeArticolo
        holder.itemView.setOnClickListener{
            val nomeArticolo= currentChat.nomeArticolo
            val nomeUtente= currentChat.nomeReceiver
            val codiceAnn= currentChat.codiceArticolo
            var idProprietario= currentChat.idReceiver
            val emailLoggato = FirebaseAuth.getInstance().currentUser?.email
            var idCurrentUser: String? =null

            GlobalScope.launch {
                idCurrentUser =
                    FirebaseDbWrapper(mcontext!!).getIdUtenteFromEmail(mcontext, emailLoggato!!)


            }
            val intent= Intent(mcontext,ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("nomeArticolo", nomeArticolo)
            intent.putExtra("emailProprietarioAnn", nomeUtente)
            intent.putExtra("idCurrentUser", idCurrentUser)
            intent.putExtra("idProprietario", idProprietario)
            intent.putExtra("codiceAnn",codiceAnn)

            mcontext?.startActivity(intent)
        }
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nomeArticolo: TextView = itemView.findViewById(R.id.txtName_annuncioChat)    }
}