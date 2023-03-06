package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.AnnuncioViewModel
import models.FirebaseDbWrapper


class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    //private var storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sellbuy-abe26.appspot.com")
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    //private var adapter= AnnuncioAdapter()
    var count = 0
    var mList: MutableList<AnnuncioViewModel> = mutableListOf()


    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        //mList= createList()
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, mList )
        recyclerview.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        mList=createList()
    }


    fun createList(): MutableList<AnnuncioViewModel>{
        //var mList:MutableList<AnnuncioViewModel> = mutableListOf()

        if (auth.currentUser != null) {
            GlobalScope.launch {
                var an =
                    FirebaseDbWrapper(applicationContext).getTuttiAnnunci(applicationContext)
                var codici =
                    FirebaseDbWrapper(applicationContext).getTutteKeysAnnunci(applicationContext)
                mList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
                    val prezzoAn = record.prezzo
                    val codice = codici[count]
                    val nuovoan =
                        imageName?.let { AnnuncioViewModel(it, nomeAn, prezzoAn, codice!!) }
                    if (nuovoan != null) {
                        mList.add(nuovoan)
                    }
                    count += 1
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return mList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
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
            R.id.plus -> {
                val intent = Intent(applicationContext, AddActivity::class.java)
                startActivity(intent)
            }
            R.id.chat -> {
                val intent = Intent(applicationContext, ElencoChatActivity::class.java)
                startActivity(intent)
            }

        }
        return true
    }
}
