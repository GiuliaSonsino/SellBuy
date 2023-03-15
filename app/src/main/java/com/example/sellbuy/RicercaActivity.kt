package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import models.Annuncio
import models.AnnuncioViewModel
import models.FirebaseDbWrapper

class RicercaActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var searchView: SearchView
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var filteredList: MutableList<AnnuncioViewModel> = mutableListOf()
    var parola:String? = null
    //lateinit var prezzoMax: String
    //var sped : Boolean? = null
    var sped : String = "Tutti"
    var annList: MutableList<Annuncio> = mutableListOf()
    var chiavi : MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerca)

        val recyclerview = findViewById<RecyclerView>(R.id.listView_search)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, filteredList)
        recyclerview.adapter = adapter


        //gestione filtri
        val opzSpedizione = resources.getStringArray(R.array.spedizioni)
        val adapterSped = ArrayAdapter(this, R.layout.list_item, opzSpedizione)
        val spedizioni = findViewById<AutoCompleteTextView>(R.id.spedizioneAcTv)
        spedizioni.setText("Tutti")
        spedizioni.setAdapter(adapterSped)
        var prezzo = findViewById<TextInputLayout>(R.id.prezzo_max)
        var prova = prezzo.editText?.text.toString()
        Log.i(TAG,"prezzo inserito $prova")



        val btnFiltri= findViewById<Button>(R.id.btn_filtri)
        searchView=findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //when user confirm the search
            override fun onQueryTextSubmit(p0: String?): Boolean {
                parola=p0
                var b=checkFilters(prezzo.editText?.text.toString())
                //Log.i(TAG,"prezzzzomaaaax $prezzoMax")
                Log.i(TAG,"speddddi $sped")
                if(b) {
                    filteredList = createList(parola!!,prezzo.editText?.text.toString(), spedizioni.text.toString())
                }
                else {
                    filteredList = createList(parola!!,"10000", spedizioni.text.toString())

                }
                //filteredList = createList(parola!!,prezzoMax,spedizioni.text.toString())
                return false
            }

            // when user is writing
            override fun onQueryTextChange(p0: String?): Boolean {
                parola=p0
                var b=checkFilters(prezzo.editText?.text.toString())
                //Log.i(TAG,"prezzzzomaaaax $prezzoMax")
                Log.i(TAG,"speddddi $sped")
                if(b) {
                    filteredList = createList(parola!!,prezzo.editText?.text.toString(), spedizioni.text.toString())
                }
                else {
                    filteredList = createList(parola!!,"10000", spedizioni.text.toString())

                }
                return false
            }
        })



        btnFiltri.setOnClickListener {
            /*val intent = Intent(applicationContext, FiltraRicercaActivity::class.java)
            intent.putExtra("parola",parola )
            startActivity(intent)*/
            var pr=prezzo.editText?.text.toString()
            Log.i(TAG,"prezzo nullo inserito $pr")
            var b=checkFilters(prezzo.editText?.text.toString())
            var s = spedizioni.text.toString()
            Log.i(TAG,"bool prezzo $b")
            Log.i(TAG,"speddddi $s")
            if(b) {
                filteredList = createList(parola!!,prezzo.editText?.text.toString(), spedizioni.text.toString())
            }
            else {
                filteredList = createList(parola!!,"10000", spedizioni.text.toString())

            }

        }



    }

    fun checkFilters(prezzo : String) : Boolean{
        if(prezzo== "") {
            return false
           // prezzoMax= "1000000"
        } else {
            //Log.i(TAG,"ci entroooo o noooooo")
            //prezzoMax=prezzo
            return true
        }
        /*
        if(spediz == "" || spediz == "Tutti") {
            sped = null
        }
        else if(spediz == "Si") {
            sped = true
        }
        else if(spediz == "No") {
            sped = false
        }*/
    }

/*
    fun ricercaFromFiltri(context: Context, parola: String, prezzo: String, spedizione :String): MutableList<Annuncio> {


        CoroutineScope(Dispatchers.IO).launch {
            val annunci = FirebaseDbWrapper(context).ricercaFromNome(
                context,
                parola
            )

            withContext(Dispatchers.Main) {
                annList.clear()
                /* for (ann in annunci) {
                    if (spedizione.equals("Tutti")) {
                        if (ann.prezzo <= prezzo) {
                            annList.add(ann)
                            Log.i(TAG,"aggiungo lgli annunciiiii null")
                        }
                    } else {
                        if (ann.spedizione == spedizione && ann.prezzo <= prezzo) {
                            Log.i(TAG,"aggiungo lgli annunciiiii")
                            annList.add(ann)
                        }
                    }
                } */
                for (ann in annunci) {
                    if (spedizione.equals("Tutti") || spedizione.equals("")) {
                        if (ann.prezzo <= prezzo) {
                            annList.add(ann)
                            Log.i(TAG, "aggiungo lgli annunciiiii null")
                        }
                    } else if (spedizione.equals("Si")) {
                        if (ann.spedizione && ann.prezzo <= prezzo) {
                            Log.i(TAG, "aggiungo lgli annunciiiii")
                            annList.add(ann)
                        }
                    } else if (spedizione.equals("No")) {
                        if (!(ann.spedizione) && ann.prezzo <= prezzo) {
                            Log.i(TAG, "aggiungo lgli annunciiiii")
                            annList.add(ann)
                        }
                    }
                }
            }
        }

        return annList
    }

    fun ricercaChiaviFromFiltr(context: Context, parola: String, prezzo: String, spedizione : String): MutableList<String> {


        CoroutineScope(Dispatchers.IO).launch {
            val annunci= FirebaseDbWrapper(context).ricercaFromNome(context,parola)
            val codici =
                FirebaseDbWrapper(context).ricercaChiaviFromNome(context, parola)
            Log.i(TAG,"annunci in courotine $annunci")
            Log.i(TAG,"codici in courotine $codici")

            withContext(Dispatchers.Main) {
                Log.i(TAG,"annunci in courotine $annunci")
                Log.i(TAG,"codici in courotine $codici")
                var count=0
                chiavi.clear()
                /* for(ann in annunci) {
                     if(spedizione) {
                         if(ann.prezzo<=prezzo) {
                             Log.i(TAG,"aggiungo le chiavi null")
                             chiavi.add(codici[count])
                         }
                     }
                     else {
                         if(ann.spedizione==spedizione && ann.prezzo<=prezzo) {
                             Log.i(TAG,"aggiungo le chiavii")
                             chiavi.add(codici[count])
                         }
                     }
                     count += 1
                 }*/
                for (ann in annunci) {
                    if (spedizione.equals("Tutti") || spedizione.equals("")) {
                        if (ann.prezzo <= prezzo) {
                            chiavi.add(codici[count])
                            Log.i(TAG, "aggiungo codice null")
                        }
                    }
                    else if (spedizione.equals("Si")) {
                        if (ann.spedizione && ann.prezzo <= prezzo) {
                            Log.i(TAG, "aggiungo codice")
                            chiavi.add(codici[count])
                        }
                    }
                    else if (spedizione.equals("No")) {
                        if (!(ann.spedizione) && ann.prezzo <= prezzo) {
                            Log.i(TAG, "aggiungo codicee")
                            chiavi.add(codici[count])
                        }
                    }
                    count += 1

                }
            }



            }
        Log.i(TAG,"return chiavi $chiavi")
        return chiavi
    }

    suspend fun ricercaChiaviFromFiltri(context: Context, parola: String, prezzo: String, spedizione : String): MutableList<String> {
        val deferredChiavi = CoroutineScope(Dispatchers.IO).async {
            val annunci= FirebaseDbWrapper(context).ricercaFromNome(context,parola)
            val codici =
                FirebaseDbWrapper(context).ricercaChiaviFromNome(context, parola)
            Log.i(TAG,"annunci in courotine $annunci")
            Log.i(TAG,"codici in courotine $codici")

            val chiavi = mutableListOf<String>()
            var count = 0

            for (ann in annunci) {
                if (spedizione.equals("Tutti") || spedizione.equals("")) {
                    if (ann.prezzo <= prezzo) {
                        chiavi.add(codici[count])
                        Log.i(TAG, "aggiungo codice null")
                    }
                }
                else if (spedizione.equals("Si")) {
                    if (ann.spedizione && ann.prezzo <= prezzo) {
                        Log.i(TAG, "aggiungo codice")
                        chiavi.add(codici[count])
                    }
                }
                else if (spedizione.equals("No")) {
                    if (!(ann.spedizione) && ann.prezzo <= prezzo) {
                        Log.i(TAG, "aggiungo codicee")
                        chiavi.add(codici[count])
                    }
                }
                count += 1
            }

            chiavi
        }

        val chiavi = deferredChiavi.await()
        Log.i(TAG,"return chiavi $chiavi")
        return chiavi
    }

*/

    fun createList(parola: String, prezzo: String, spedizione : String): MutableList<AnnuncioViewModel> {
        //var mList:MutableList<AnnuncioViewModel> = mutableListOf()
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val an = FirebaseDbWrapper(applicationContext).ricercaFromNome(applicationContext, parola, prezzo, spedizione)
                val codici = FirebaseDbWrapper(applicationContext).ricercaKeysFromNome(applicationContext, parola, prezzo, spedizione)


                Log.i(TAG, "numero annunci filt $an")
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