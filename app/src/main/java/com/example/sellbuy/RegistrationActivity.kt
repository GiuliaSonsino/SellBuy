package com.example.sellbuy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.*
import models.FirebaseAuthWrapper
import models.FirebaseDbWrapper
import models.Utente

/*
class RegistrationActivity: AppCompatActivity() {
    var list : MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ruolo = intent.getStringExtra("ruolo")
        if (ruolo!!.equals("Amministratore")) {
            setContentView(R.layout.activity_registration)
            title = ruolo
            val nomeReg = findViewById<TextInputLayout>(R.id.reg_name)
            val cognomeReg = findViewById<TextInputLayout>(R.id.reg_cognome)
            val emailReg = findViewById<TextInputLayout>(R.id.reg_email)
            val numTelReg = findViewById<TextInputLayout>(R.id.reg_phoneNo)
            val pwdReg = findViewById<TextInputLayout>(R.id.reg_password)
            val confPwdReg = findViewById<TextInputLayout>(R.id.reg_conferma_password)
            val btnReg = findViewById<Button>(R.id.regBtn)

            val correctEmail = "[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^-]+@[a-z]+\\.+[a-z]+".toRegex()



            fun checkRegistrationAmm(): Boolean {
                if (nomeReg.editText?.text.isNullOrBlank() || cognomeReg.editText?.text.isNullOrBlank() || emailReg.editText?.text.isNullOrBlank() || numTelReg.editText?.text.isNullOrBlank() || pwdReg.editText?.text.isNullOrBlank() || confPwdReg.editText?.text.isNullOrBlank()) {
                    Toast.makeText(
                        applicationContext,
                        "Devi compilare tutti i campi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                } else if (!correctEmail.matches(emailReg.editText?.text.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "Email non valida",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                } else if (numTelReg.editText?.text.toString().length < 10) {
                    Toast.makeText(
                        applicationContext,
                        "Numero di telefono troppo breve",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                } else if (pwdReg.editText?.text.toString() != confPwdReg.editText?.text.toString()) {
                    Toast.makeText(
                        applicationContext,
                        "La password e la conferma devono corrispondere",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else {
                    return true
                }
            }


            btnReg.setOnClickListener {
                if (checkRegistrationAmm()) {
                    val ris = FirebaseAuthWrapper(applicationContext).signUp(
                        emailReg.editText?.text.toString(),
                        pwdReg.editText?.text.toString()
                    )
                    if (ris) {
                        val amministratore = Utente(
                            nomeReg.editText?.text.toString(),
                            cognomeReg.editText?.text.toString(),
                            emailReg.editText?.text.toString(),
                            numTelReg.editText?.text.toString().toLong(),
                            true
                        )
                        FirebaseDbWrapper(applicationContext).creaUtente(amministratore)
                        Toast.makeText(
                            applicationContext,
                            "Registrazione avvenuta con successo",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                finish()
            }
        }

        else {
            setContentView(R.layout.activity_registration)
            title = ruolo
            val nomeReg = findViewById<TextInputLayout>(R.id.reg_name)
            val cognomeReg = findViewById<TextInputLayout>(R.id.reg_cognome)
            val emailReg = findViewById<TextInputLayout>(R.id.reg_email)
            val numTelReg = findViewById<TextInputLayout>(R.id.reg_phoneNo)
            val pwdReg = findViewById<TextInputLayout>(R.id.reg_password)
            val confPwdReg = findViewById<TextInputLayout>(R.id.reg_conferma_password)
            val btnReg = findViewById<Button>(R.id.regBtn)

            val correctEmail = "[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^-]+@[a-z]+\\.+[a-z]+".toRegex()


            fun checkRegistrationUt(): Boolean {
                if (nomeReg.editText?.text.isNullOrBlank() || cognomeReg.editText?.text.isNullOrBlank() || emailReg.editText?.text.isNullOrBlank() || numTelReg.editText?.text.isNullOrBlank() || pwdReg.editText?.text.isNullOrBlank() || confPwdReg.editText?.text.isNullOrBlank()) {
                    Toast.makeText(
                        applicationContext,
                        "Devi compilare tutti i campi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (!correctEmail.matches(emailReg.editText?.text.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "Email non valida",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (numTelReg.editText?.text.toString().length < 10) {
                    Toast.makeText(
                        applicationContext,
                        "Numero di telefono troppo breve",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (pwdReg.editText?.text.toString() != confPwdReg.editText?.text.toString()) {
                    Toast.makeText(
                        applicationContext,
                        "La password e la conferma devono corrispondere",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if(list.contains(emailReg.editText?.text.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "Email già usata",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else {
                    return true
                }
            }


            btnReg.setOnClickListener {
                if (checkRegistrationUt()) {
                    val ris = FirebaseAuthWrapper(applicationContext).signUp(
                        emailReg.editText?.text.toString(),
                        pwdReg.editText?.text.toString()
                    )
                    if (ris) {
                        val utente = Utente(
                            nomeReg.editText?.text.toString(),
                            cognomeReg.editText?.text.toString(),
                            emailReg.editText?.text.toString(),
                            numTelReg.editText?.text.toString().toLong(),
                            false
                        )
                        FirebaseDbWrapper(applicationContext).creaUtente(utente)
                        Toast.makeText(
                            applicationContext,
                            "Registrazione avvenuta con successo",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            list = FirebaseDbWrapper(applicationContext).getTutteEmailUtenti(applicationContext)
        }
    }


    suspend fun checkEmailUnica(context: Context, email: String): Boolean {
        val deferred = CompletableDeferred<Boolean>()

        FirebaseDbWrapper(context).dbref.child("Utenti")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val exists = snapshot.exists() && snapshot.childrenCount > 0
                    deferred.complete(!exists)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to read value.", error.toException())
                    deferred.complete(false)
                }
            })

        return deferred.await()
    }

}
*/



class RegistrationActivity: AppCompatActivity() {
    var list : MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ruolo = intent.getStringExtra("ruolo")
        if (ruolo!!.equals("Amministratore")) {
            setContentView(R.layout.activity_registration)
            title = ruolo
            val nomeReg = findViewById<TextInputLayout>(R.id.reg_name)
            val cognomeReg = findViewById<TextInputLayout>(R.id.reg_cognome)
            val emailReg = findViewById<TextInputLayout>(R.id.reg_email)
            val numTelReg = findViewById<TextInputLayout>(R.id.reg_phoneNo)
            val pwdReg = findViewById<TextInputLayout>(R.id.reg_password)
            val confPwdReg = findViewById<TextInputLayout>(R.id.reg_conferma_password)
            val btnReg = findViewById<Button>(R.id.regBtn)

            val correctEmail = "[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^-]+@[a-z]+\\.+[a-z]+".toRegex()


            val infoButton = findViewById<ImageButton>(R.id.info_button)
            infoButton.setOnClickListener {
                showPasswordConditions()
            }




            fun checkRegistrationAmm(): Boolean {
                if (nomeReg.editText?.text.isNullOrBlank() || cognomeReg.editText?.text.isNullOrBlank() || emailReg.editText?.text.isNullOrBlank() || numTelReg.editText?.text.isNullOrBlank() || pwdReg.editText?.text.isNullOrBlank() || confPwdReg.editText?.text.isNullOrBlank()) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Devi compilare tutti i campi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (!correctEmail.matches(emailReg.editText?.text.toString())) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Email non valida",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (numTelReg.editText?.text.toString().length < 10) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Numero di telefono troppo breve",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (pwdReg.editText?.text.toString() != confPwdReg.editText?.text.toString()) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "La password e la conferma devono corrispondere",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if(list.contains(emailReg.editText?.text.toString())) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Email già presente",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else {
                    return true
                }
            }


            btnReg.setOnClickListener {
                if (checkRegistrationAmm()) {
                    FirebaseAuthWrapper(applicationContext).signUp(
                        emailReg.editText?.text.toString(),
                        pwdReg.editText?.text.toString()
                    )
                    val amministratore = Utente(
                        nomeReg.editText?.text.toString(),
                        cognomeReg.editText?.text.toString(),
                        emailReg.editText?.text.toString(),
                        numTelReg.editText?.text.toString().toLong(),
                        true,
                        0.0
                    )
                    FirebaseDbWrapper(applicationContext).creaUtente(amministratore)
                    DynamicToast.makeSuccess(
                        applicationContext,
                        "Registrazione avvenuta con successo",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()

                }

            }
        }
        else {
            setContentView(R.layout.activity_registration)
            title = ruolo
            val infoButton = findViewById<ImageButton>(R.id.info_button)
            infoButton.setOnClickListener {
                showPasswordConditions()
            }
            val nomeReg = findViewById<TextInputLayout>(R.id.reg_name)
            val cognomeReg = findViewById<TextInputLayout>(R.id.reg_cognome)
            val emailReg = findViewById<TextInputLayout>(R.id.reg_email)
            val numTelReg = findViewById<TextInputLayout>(R.id.reg_phoneNo)
            val pwdReg = findViewById<TextInputLayout>(R.id.reg_password)
            val confPwdReg = findViewById<TextInputLayout>(R.id.reg_conferma_password)
            val btnReg = findViewById<Button>(R.id.regBtn)

            val correctEmail = "[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^-]+@[a-z]+\\.+[a-z]+".toRegex()


            fun checkRegistrationUt(): Boolean {
                if (nomeReg.editText?.text.isNullOrBlank() || cognomeReg.editText?.text.isNullOrBlank() || emailReg.editText?.text.isNullOrBlank() || numTelReg.editText?.text.isNullOrBlank() || pwdReg.editText?.text.isNullOrBlank() || confPwdReg.editText?.text.isNullOrBlank()) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Devi compilare tutti i campi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (!correctEmail.matches(emailReg.editText?.text.toString())) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Email non valida",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (numTelReg.editText?.text.toString().length < 10) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Numero di telefono troppo breve",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (pwdReg.editText?.text.toString() != confPwdReg.editText?.text.toString()) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "La password e la conferma devono corrispondere",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if (pwdReg.editText?.text.toString().length<6) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "La password deve contenere almeno 6 caratteri",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else if(list.contains(emailReg.editText?.text.toString())) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Email già presente",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
                else {
                    return true
                }
            }


            btnReg.setOnClickListener {
                if (checkRegistrationUt()) {
                    FirebaseAuthWrapper(applicationContext).signUp(
                        emailReg.editText?.text.toString(),
                        pwdReg.editText?.text.toString()
                    )
                    val utente = Utente(
                        nomeReg.editText?.text.toString(),
                        cognomeReg.editText?.text.toString(),
                        emailReg.editText?.text.toString(),
                        numTelReg.editText?.text.toString().toLong(),
                        false,
                        0.0
                    )
                    FirebaseDbWrapper(applicationContext).creaUtente(utente)
                    DynamicToast.makeSuccess(
                        applicationContext,
                        "Registrazione avvenuta con successo",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showPasswordConditions() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Condizioni password")
        builder.setMessage("La password deve contenere almeno 6 caratteri.")
        builder.setPositiveButton("Chiudi") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            list = FirebaseDbWrapper(applicationContext).getTutteEmailUtenti(applicationContext)
        }
    }
}