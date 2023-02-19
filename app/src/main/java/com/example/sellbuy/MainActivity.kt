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
import models.AnnuncioViewModel
import models.FirebaseDbWrapper


class MainActivity : AppCompatActivity() {

    private val auth= FirebaseAuth.getInstance()
    //private var storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sellbuy-abe26.appspot.com")

    private var adapter=AnnuncioAdapter(this, mutableListOf())
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        var mList: MutableList<AnnuncioViewModel> = mutableListOf()
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        adapter = AnnuncioAdapter(applicationContext, mList)
        recyclerview.adapter = adapter


        if (auth.currentUser != null) {
            GlobalScope.launch {
                var an =
                    FirebaseDbWrapper(applicationContext).getAnnunciFromEmail(applicationContext)
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
                    val nuovoan = imageName?.let { AnnuncioViewModel(it, nomeAn, "codic") }
                    if (nuovoan != null) {
                        mList.add(nuovoan)
                    }

                }
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
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
        }
        return true
    }



}