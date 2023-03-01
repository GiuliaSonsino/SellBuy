package com.example.sellbuy

import android.annotation.SuppressLint

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.*

import models.FirebaseDbWrapper
import models.ItemChat
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
        var chatList: ArrayList<ItemChat> = arrayListOf()
        chatList= createList()
        Log.i(TAG,"lista messssaggi $chatList")

        recyclerView.layoutManager = LinearLayoutManager(this)

        /*
        GlobalScope.launch {
            chatList=createList()
            chatAdapter=ItemChatAdapter(applicationContext,chatList)
            dataloaded=true
            Log.i(TAG,"1dataloaded $dataloaded")
        }
        Log.i(TAG,"dataloaded $dataloaded")
        if(dataloaded) {
            recyclerView.visibility= View.VISIBLE
        } else {
            recyclerView.visibility=View.GONE
        }*/


        chatAdapter=ItemChatAdapter(this,chatList)
        recyclerView.adapter = chatAdapter
        //adapter.updateData(mList)
        chatAdapter.notifyDataSetChanged()



        /*chatAdapter = ItemChatAdapter(chatList)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = chatAdapter
        chatAdapter.notifyDataSetChanged()*/



    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createList(): ArrayList<ItemChat> {
        var chatList:ArrayList<ItemChat> = arrayListOf()
        var emailUtente= FirebaseAuth.getInstance().currentUser?.email
        if (auth.currentUser != null) {
            GlobalScope.launch {
                     var idUtente =
                            FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(
                                applicationContext,
                                emailUtente!!
                            )


                    var elencoMessages =
                        FirebaseDbWrapper(applicationContext).getChats(
                            applicationContext,
                            idUtente!!
                        )
                    Log.i(TAG, "elenco messsagggini $elencoMessages")

                    for (record in elencoMessages) {
                        Log.i(TAG, "singolo messsaggino $record")
                        val nomeUtente = record.sender
                        Log.i(TAG, "nomeutente $nomeUtente")
                        val nomeAn = record.articolo
                        Log.i(TAG, "nomeann $nomeAn")
                        val nuovaChat = ItemChat(nomeUtente, nomeAn)
                        if (nuovaChat != null) {
                            Log.i(TAG, "ho aggiunto un messsss")
                            chatList.add(nuovaChat)
                            Log.i(TAG, "messaggiotti $chatList")
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




