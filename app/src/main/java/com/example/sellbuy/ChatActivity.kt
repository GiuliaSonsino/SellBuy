package com.example.sellbuy

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import models.FirebaseDbWrapper


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<models.Message>
    private lateinit var mDbRef : DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val receiver = intent.getStringExtra("idProprietario")
        val emailProprietarioAnn = intent.getStringExtra("emailProprietarioAnn")
        val sender = intent.getStringExtra("idCurrentUser")
        val codiceAnn = intent.getStringExtra("codiceAnn")

        senderRoom = receiver + sender + codiceAnn
        receiverRoom = sender + receiver + codiceAnn

        mDbRef= FirebaseDatabase.getInstance().getReference()

        val nomeReceiver= findViewById<TextView>(R.id.txtvChat)
        nomeReceiver.text=emailProprietarioAnn
        chatRecyclerView = findViewById(R.id.recyclerMessages)
        messageBox = findViewById(R.id.edtText)
        sendButton = findViewById(R.id.imgFreccia)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList, sender!!)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // logica per aggiungere i dati alla RecyclerView
        mDbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(models.Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            val messageObject = models.Message(message,sender,receiver,codiceAnn)
            FirebaseDbWrapper(applicationContext).creaChat(senderRoom,receiverRoom,messageObject)
            messageBox.setText("")
        }
    }
}