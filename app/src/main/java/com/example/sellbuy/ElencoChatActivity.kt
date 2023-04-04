package com.example.sellbuy

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    var chatList: MutableList<Message> = mutableListOf()

    private var dataloaded=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elenco_chat)
        title= "Chat"
        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter=ItemChatAdapter(this,chatList)
        recyclerView.adapter = chatAdapter

    }

    override fun onStart() {
        super.onStart()
        chatList=createList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun createList(): MutableList<Message> {
        val emailUtente= FirebaseAuth.getInstance().currentUser?.email
        val listaKeyChat: MutableList<String> = mutableListOf()
        if (auth.currentUser != null) {
            GlobalScope.launch {
                 val idUserLoggato =
                        FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(
                            applicationContext,
                            emailUtente!!
                        )
                val elencoMessages =
                    FirebaseDbWrapper(applicationContext).getChats(
                        applicationContext,
                        idUserLoggato!!
                    )
                chatList.clear()
                for (record in elencoMessages) {
                    var idUtente:String?=null
                    var mess=record.contenuto
                    if(idUserLoggato==record.receiver) {
                        idUtente=record.sender
                    }
                    else if(idUserLoggato==record.sender) {
                        idUtente=record.receiver
                    }
                    val codiceAn=record.codiceArticolo

                    val nuovaChat = Message(mess, idUserLoggato, idUtente,codiceAn)
                    var id= idUtente + codiceAn
                    //check if nuovaChat exists and if it is already present in the list
                    if (nuovaChat != null && id !in listaKeyChat)  {
                        listaKeyChat.add(id)
                        chatList.add(nuovaChat)
                    }
                }
                withContext(Dispatchers.Main) {
                    chatAdapter.notifyDataSetChanged()
                }
            }
        }
        return chatList
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_to_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var firebaseAuth = FirebaseAuth.getInstance()
        when (item.itemId) {
            R.id.logout -> {
                firebaseAuth.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.home -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }


}




