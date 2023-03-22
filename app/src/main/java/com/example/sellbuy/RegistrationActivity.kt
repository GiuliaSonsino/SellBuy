package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import models.FirebaseAuthWrapper
import models.FirebaseDbWrapper
import models.Utente

class RegistrationActivity: AppCompatActivity() {
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
                else {
                    return true
                }
            }


            btnReg.setOnClickListener {
                if (checkRegistrationAmm()) {
                    var ris = FirebaseAuthWrapper(applicationContext).signUp(
                        emailReg.editText?.text.toString(),
                        pwdReg.editText?.text.toString()
                    )
                    if(ris) {
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
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
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
                else {
                    return true
                }
            }


            btnReg.setOnClickListener {
                if (checkRegistrationUt()) {
                    var ris = FirebaseAuthWrapper(applicationContext).signUp(
                        emailReg.editText?.text.toString(),
                        pwdReg.editText?.text.toString()
                    )
                    if(ris) {
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
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                finish()
            }
        }
    }
}
