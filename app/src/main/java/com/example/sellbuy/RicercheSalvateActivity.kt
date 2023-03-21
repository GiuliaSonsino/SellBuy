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
import models.AnnuncioViewModel
import models.FirebaseDbWrapper
import models.RicercaSalvata

class RicercheSalvateActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var adapter = RicercaSalvataAdapter(this, mutableListOf())
    var listaRic: MutableList<RicercaSalvata> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerche_salvate)
        /*
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview_ricerche)
        title = "Ricerche Salvate"
        //mList= createList()
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RicercaSalvataAdapter(applicationContext, listaRic)
        recyclerview.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        listaRic = createList()
    }


    fun createList(): MutableList<RicercaSalvata> {
        var count = 0
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
*/

    }
}