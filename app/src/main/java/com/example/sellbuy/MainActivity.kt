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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.AnnuncioViewModel
import models.FirebaseDbWrapper

class MainActivity : AppCompatActivity() {

    private val auth= FirebaseAuth.getInstance()
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        if(auth.currentUser!=null) {
            GlobalScope.launch {
                var count=0
                var an =
                    FirebaseDbWrapper(applicationContext).getAnnunciFromEmail(applicationContext)
                //val codici=
                //    FirebaseDbWrapper(applicationContext).getKeyFromEmail(applicationContext)
                for (record in an) {
                    val nomeAn = record.nome

                //    var codice=codici[count]
                    count += 1
                   // val nomePrimaImg= record.foto?.get(0)

                    data.add(AnnuncioViewModel(R.drawable.ic_launcher_background, nomeAn,"prova codice"),)
                }
            }
        }
        else {
           for (i in 1..20) {
                data.add(AnnuncioViewModel(R.drawable.ic_launcher_background, "Item " + i,"prova"),)
            }
        }


        // This will pass the ArrayList to our Adapter
        val adapter = AnnuncioAdapter(applicationContext,data)
        recyclerview.adapter = adapter



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