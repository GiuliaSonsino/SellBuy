package com.example.sellbuy

import android.annotation.SuppressLint

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.*

import models.FirebaseDbWrapper
import models.Message

class ElencoChatActivity : AppCompatActivity() {
    //private var adapter = ItemChatAdapter( mutableListOf())
    private lateinit var recyclerView : RecyclerView
    private lateinit var chatAdapter: ItemChatAdapter
    //private lateinit var mDbRef : DatabaseReference
    private val auth = FirebaseAuth.getInstance()

    //private var adapter = ItemChatAdapter( this,mutableListOf())
    //var chatList: MutableList<ItemChat> = mutableListOf()
    private var dataloaded=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elenco_chat)
        recyclerView = findViewById(R.id.userRecyclerView)
        var chatList: ArrayList<Message> = createList()
        Log.i(TAG,"lista messssaggi $chatList")

        recyclerView.layoutManager = LinearLayoutManager(this)

        chatAdapter=ItemChatAdapter(this,chatList)
        recyclerView.adapter = chatAdapter
        //adapter.updateData(mList)
        chatAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createList(): ArrayList<Message> {
        var chatList:ArrayList<Message> = arrayListOf()
        var emailUtente= FirebaseAuth.getInstance().currentUser?.email
        if (auth.currentUser != null) {
            GlobalScope.launch {
                     var idUserLoggato =
                            FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(
                                applicationContext,
                                emailUtente!!
                            )


                    var elencoMessages =
                        FirebaseDbWrapper(applicationContext).getChats(
                            applicationContext,
                            idUserLoggato!!
                        )

                    Log.i(TAG, "elenco messsagggini $elencoMessages")

                    for (record in elencoMessages) {
                        var idUtente:String?=null
                        var mess=record.contenuto
                        if(idUserLoggato==record.receiver) {
                            idUtente=record.sender
                        }
                        else if(idUserLoggato==record.sender) {
                            idUtente=record.receiver
                        }

                        val nomeAn = record.articolo
                        val codiceAn=record.codiceArticolo
                        val nuovaChat = Message(mess, idUserLoggato, idUtente,nomeAn,codiceAn)
                        if (nuovaChat != null) {
                            chatList.add(nuovaChat)
                        }

                    }
                }
            }

        Log.i(TAG,"stampa messaggi $chatList")
        return chatList
    }

    /*
    override fun onStart() {
        super.onStart()
        chatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }
*/
}




