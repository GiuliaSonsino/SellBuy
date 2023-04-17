package com.example.sellbuy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import models.FirebaseAuthWrapper

class SplashActivity: AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val unwrappeddrawable: Drawable? = AppCompatResources.getDrawable(applicationContext, android.R.drawable.ic_dialog_alert)
        val wrappedDrawable = DrawableCompat.wrap(unwrappeddrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.rgb(204, 119, 34))
        if (!isNetworkAvailable) {
            AlertDialog.Builder(this)
                .setIcon(wrappedDrawable)
                .setTitle("Questa applicazione ha bisogno di una connessione ad Internet per funzionare in modo corretto")
                .setMessage("Connettiti ad Internet e riapri l'applicazione")
                .setPositiveButton(
                    "Chiudi"
                ) { dialogInterface, i -> finish() }.show()
        }

        else if (isNetworkAvailable) {

            val firebaseWrapper = FirebaseAuthWrapper(this)
            if (!firebaseWrapper.isAuthenticated()) {
                setContentView(R.layout.activity_splash)

                val items = resources.getStringArray(R.array.ruoli)
                val adapter = ArrayAdapter(this, R.layout.list_item, items)
                val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
                autoCompleteTextView.setAdapter(adapter)

                val switch2LoginBtn = findViewById<TextView>(R.id.switchToLogin)
                switch2LoginBtn.setOnClickListener {
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                }

                val nextBtn: Button = findViewById(R.id.prosegui)
                nextBtn.setOnClickListener{
                    if (autoCompleteTextView.text.toString() != "") {
                        val ruoloselez = autoCompleteTextView.text.toString()
                        val intent = Intent(applicationContext, RegistrationActivity::class.java)
                        intent.putExtra("ruolo", ruoloselez)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(
                            applicationContext,
                            "Devi selezionare un ruolo prima di proseguire",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                finish()
            }
        }
    }

    private val isNetworkAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}