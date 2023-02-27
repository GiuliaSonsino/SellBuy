package com.example.sellbuy

import android.content.ContentValues
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.AnnuncioViewModel
import models.FirebaseDbWrapper


class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    //private var storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sellbuy-abe26.appspot.com")
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var count = 0
    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        var mList: MutableList<AnnuncioViewModel> = mutableListOf()
        mList= createList()
        Log.i(TAG,"lista finale $mList")
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, mList)
        recyclerview.adapter = adapter
        //adapter.updateData(mList)
        adapter.notifyDataSetChanged()
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

        }
        return true
    }


    fun createList(): MutableList<AnnuncioViewModel> {
        var mList:MutableList<AnnuncioViewModel> = mutableListOf()

        if (auth.currentUser != null) {
            GlobalScope.launch {
                var an =
                    FirebaseDbWrapper(applicationContext).getTuttiAnnunci(applicationContext)
                var codici =
                    FirebaseDbWrapper(applicationContext).getTutteKeysAnnunci(applicationContext)
                Log.i(ContentValues.TAG, "eccoccoc i codici $codici")
                //var prova= codici[0]
                //Log.i(ContentValues.TAG, "eccoccoc i codici $prova")

                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
                    val prezzoAn = record.prezzo
                    var codice = codici[count]
                    Log.i(TAG,"contatore : $count")
                    // Log.i(TAG,"un codiceee $codice")
                    val nuovoan =
                        imageName?.let { AnnuncioViewModel(it, nomeAn, prezzoAn, codice!!) }
                    if (nuovoan != null) {
                        Log.i(TAG, "non è nullo")
                        mList.add(nuovoan)
                    }
                    count += 1
                }
                Log.i(TAG, "quiiiiiiiiiiiii $mList")
            } //qui
            Log.i(TAG, "aaaaaaa $mList")
        }
        return mList
    }




}