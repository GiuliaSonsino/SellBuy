package com.example.sellbuy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
    private lateinit var recyclerView : RecyclerView
    private lateinit var chatAdapter: ItemChatAdapter
    private val auth = FirebaseAuth.getInstance()
    var chatList: MutableList<Message> = mutableListOf()

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
                    val mess=record.contenuto
                    if(idUserLoggato==record.receiver) {
                        idUtente=record.sender
                    }
                    else if(idUserLoggato==record.sender) {
                        idUtente=record.receiver
                    }
                    val codiceAn=record.codiceArticolo

                    val nuovaChat = Message(mess, idUserLoggato, idUtente,codiceAn)
                    val isUtenteInDB = FirebaseDbWrapper(applicationContext).isUtenteinDB(applicationContext,idUtente!!)
                    val id= idUtente + codiceAn
                    // controllo se nuovaChat esiste e se è già presente nella lista
                    if (nuovaChat != null && id !in listaKeyChat && isUtenteInDB)  {
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
        val firebaseAuth = FirebaseAuth.getInstance()
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




