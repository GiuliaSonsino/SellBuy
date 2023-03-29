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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recensioni)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewRecensioni)
        title="RECENSIONI"
        //mList= createList()
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RecensioneAdapter(applicationContext, mList, FirebaseAuth.getInstance().currentUser?.email!!)
        recyclerview.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        mList = createList()

    }

    fun createList(): MutableList<Recensione> {
        //var mList:MutableList<AnnuncioViewModel> = mutableListOf()
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                var recensioni =
                    FirebaseDbWrapper(applicationContext).getRecensioniFromUtente(applicationContext,FirebaseAuth.getInstance().currentUser?.email!! )
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