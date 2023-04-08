package com.example.sellbuy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.FirebaseDbWrapper
import models.Recensione

class RecensioniActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var adapter = RecensioneAdapter(this, mutableListOf(), String())
    var mList: MutableList<Recensione> = mutableListOf()
    private var emailRecensioni: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recensioni)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewRecensioni)
        title="Recensioni"
        //mList= createList()
        emailRecensioni = intent.getStringExtra("emailRecensioni").toString()
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RecensioneAdapter(applicationContext, mList, emailRecensioni)
        recyclerview.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        mList = createList()

    }

    fun createList(): MutableList<Recensione> {
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val recensioni =
                    FirebaseDbWrapper(applicationContext).getRecensioniFromUtente(applicationContext,emailRecensioni)
                mList.clear()
                for (record in recensioni) {
                    mList.add(record)
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return mList
    }

}