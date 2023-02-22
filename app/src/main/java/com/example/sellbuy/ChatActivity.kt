package com.example.sellbuy

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import models.Utente



class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<models.Message>


    /*
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var messages: ArrayList<Message> = arrayListOf()
    val textview = findViewById<TextView>(R.id.txtvChat)
    val recycler = findViewById<RecyclerView>(R.id.recyclerMessages)
    val editext = findViewById<EditText>(R.id.edtText)
    val freccia = findViewById<ImageView>(R.id.imgFreccia)
*/

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val receiver = intent.getStringExtra("emailProprietarioAnn")
        Log.i(TAG,"il receiver $receiver")
        val sender = intent.getStringExtra("currentEmail")


        var nomeReceiver= findViewById<TextView>(R.id.txtvChat)
        nomeReceiver.text=receiver
        chatRecyclerView = findViewById(R.id.recyclerMessages)
        messageBox = findViewById(R.id.edtText)
        sendButton = findViewById(R.id.imgFreccia)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        //sendButton.setOnClickListener()



        //setUpChatRoom()
        //textview.text = "Nome di prova"
    }

/*
    var emailLoggato = Firebase.auth.currentUser?.email
    var emailAltroUtente = "gigi@prova.it"
    var codAnnuncio = "DNSI34U"
    var chatRoomID = ""

    fun setUpChatRoom() {
        FirebaseDatabase.getInstance().getReference("Utenti/"+auth.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomID = if(emailAltroUtente.compareTo(emailLoggato!!) > 0) {
                    emailLoggato+emailAltroUtente+codAnnuncio
                } else if(emailLoggato!!.compareTo(emailAltroUtente) == 0) {
                    emailLoggato+emailAltroUtente+codAnnuncio
                } else {
                    emailAltroUtente+emailLoggato+codAnnuncio
                }
                attachMessageListener(chatRoomID)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    fun attachMessageListener(chatRoomID: String) {
        FirebaseDatabase.getInstance().getReference("Messaggi/$chatRoomID").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (dataSnapshot in snapshot.children) {
                    dataSnapshot.getValue(Message::class.java)?.let { messages.add(it) }
                }
                recycler.scrollToPosition(messages.size-1)
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
*/

}