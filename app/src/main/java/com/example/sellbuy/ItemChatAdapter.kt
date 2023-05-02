package com.example.sellbuy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import models.FirebaseDbWrapper
import models.Message

class ItemChatAdapter( context: Context,private val chatList: MutableList<Message>): RecyclerView.Adapter<ItemChatAdapter.ViewHolder>() {
    private val mcontext:Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChat = chatList[position]
        val idUtenteSullaChat = currentChat.receiver
        val codiceArticolo = currentChat.codiceArticolo
        CoroutineScope(Dispatchers.IO).launch {
            val nomeUtenteSullaChat =
                FirebaseDbWrapper(mcontext).getEmailFromIdUtente(
                    mcontext,
                    idUtenteSullaChat!!
                )
            val articolo =
                FirebaseDbWrapper(mcontext).getAnnuncioFromCodice(
                    mcontext,
                    codiceArticolo!!
                )

            withContext(Dispatchers.Main) {
                holder.nomeSullaChat.text = nomeUtenteSullaChat
                holder.nomeArticolo.text = articolo.nome
            }
        }
        var emailProprietario:String?
        holder.itemView.setOnClickListener{
        val codiceAnn= currentChat.codiceArticolo
        val idProprietario= currentChat.receiver

        GlobalScope.launch {
            emailProprietario =
                FirebaseDbWrapper(mcontext).getEmailFromIdUtente(
                    mcontext,
                    idProprietario!!
                )

            val idCurrentUser = currentChat.sender

            val intent = Intent(mcontext, ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("emailProprietarioAnn", emailProprietario)
            intent.putExtra("idCurrentUser", idCurrentUser)
            intent.putExtra("idProprietario", idProprietario)
            intent.putExtra("codiceAnn", codiceAnn)

            mcontext.startActivity(intent)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nomeArticolo: TextView = itemView.findViewById(R.id.txtName_annuncioChat)
        val nomeSullaChat: TextView = itemView.findViewById(R.id.txtName_Utente)
    }
}