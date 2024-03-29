package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.*
import models.FirebaseAuthWrapper
import models.FirebaseDbWrapper
import models.Utente


class RegistrationActivity: AppCompatActivity() {
    var list : MutableList<String> = mutableListOf()
    var listaUtentiEliminati: MutableList<String> = mutableListOf()
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
                else if(listaUtentiEliminati.contains(emailReg.editText?.text.toString())) {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Utente eliminato, è necessaria un'altra email",
                        Toast.LENGTH_LONG
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
            listaUtentiEliminati = FirebaseDbWrapper(applicationContext).getTutteEmailUtentiEliminati(applicationContext)!!
        }
    }
}