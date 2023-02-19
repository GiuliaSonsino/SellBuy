package com.example.sellbuy

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import models.AnnuncioViewModel
import models.FirebaseDbWrapper
import models.FirebaseStorageWrapper
import java.io.File
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), LifecycleOwner {

    private val auth= FirebaseAuth.getInstance()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val scope = MainScope()
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<AnnuncioViewModel>()


        // This loop will create 20 Views containing
        // the image with the count of view
        // for (i in 1..20) {
        // data.add(AnnuncioViewModel(R.drawable.ic_launcher_background, "Item " + i),)
        // }

        if (auth.currentUser != null) {
            GlobalScope.launch {
                var count = 0
                var an =
                    FirebaseDbWrapper(applicationContext).getAnnunciFromEmail(applicationContext)
                //val codici=
                //    FirebaseDbWrapper(applicationContext).getKeyFromEmail(applicationContext)
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
                    data.add(AnnuncioViewModel("bo", nomeAn,"prova codice"),)
/*
                   scope.launch {
                        val annuncio = withContext(Dispatchers.IO) {
                            // Chiamare la funzione getFotoFromName all'interno della coroutine

                            FirebaseStorageWrapper(applicationContext).getFotoFromName(
                                imageName,
                                nomeAn
                            )
                        }
                        var imm = annuncio?.image
                        var nom = annuncio?.text
                        var codic = annuncio?.codice
                        Log.i(TAG, "nomeeeee $imm")

                        // Gestire il risultato dell'operazione
                        if (annuncio != null) {

                            if (imm != null && nom != null && codic != null) {
                                Log.i(TAG, "yesssssss")
                                //data.add(AnnuncioViewModel(imm, nom,codic),)}
                                val adapter = AnnuncioAdapter(applicationContext, data)

                                // L'operazione è stata completata con successo
                                // Puoi utilizzare l'istanza di AnnuncioViewModel restituita dalla funzione
                            } else {
                                Log.i(TAG, "nooo")
                                // Si è verificato un errore durante l'operazione
                            }
                        }


                        //data.add(foto)


                        //    var codice=codici[count]
                        // count += 1
                        // val nomePrimaImg= record.foto?.get(0)

                        data.add(AnnuncioViewModel("bo", nomeAn,"prova codice"),)
                    }
                    */
                }

            }
            /*
        else {
           for (i in 1..20) {
                data.add(AnnuncioViewModel(R.drawable.ic_launcher_background, "Item " + i,"prova"),)
            }
        }*/


            // This will pass the ArrayList to our Adapter
            val adapter = AnnuncioAdapter(applicationContext, data)

            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter


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