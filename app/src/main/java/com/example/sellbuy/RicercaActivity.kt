package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
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

class RicercaActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var searchView: SearchView
    private lateinit var listView: RecyclerView
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var filteredList: MutableList<AnnuncioViewModel> = mutableListOf()
    var parola:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerca)

        val recyclerview = findViewById<RecyclerView>(R.id.listView_search)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, filteredList)
        recyclerview.adapter = adapter

        val btnFiltri= findViewById<Button>(R.id.btn_filtri)
        searchView=findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //when user confirm the search
            override fun onQueryTextSubmit(p0: String?): Boolean {
                parola=p0
                filteredList = createList(p0!!)
                return false
            }

            // when user is writing
            override fun onQueryTextChange(p0: String?): Boolean {

                return false
            }
        })
        listView=findViewById(R.id.listView_search)



        btnFiltri.setOnClickListener {
            val intent = Intent(applicationContext, FiltraRicercaActivity::class.java)
            intent.putExtra("parola",parola )
            startActivity(intent)
        }
    }


    fun createList(parola: String): MutableList<AnnuncioViewModel> {
        //var mList:MutableList<AnnuncioViewModel> = mutableListOf()
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                var an =
                    FirebaseDbWrapper(applicationContext).ricercaFromNome(applicationContext, parola)
                var codici =
                    FirebaseDbWrapper(applicationContext).ricercaChiaviFromNome(applicationContext, parola)
                filteredList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
                    val prezzoAn = record.prezzo
                    val codice = codici[count]
                    val nuovoan =
                        imageName?.let { AnnuncioViewModel(it, nomeAn, prezzoAn, codice!!) }
                    if (nuovoan != null) {
                        filteredList.add(nuovoan)
                    }
                    count += 1
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return filteredList
    }
}