package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mailEditText = findViewById<TextInputLayout>(R.id.login_mail)
        val passwordEditText = findViewById<TextInputLayout>(R.id.login_password)
        val accediBtn = findViewById<Button>(R.id.loginBtn)
        val registratiBtn=findViewById<Button>(R.id.registrationBtn)

        val auth= FirebaseAuth.getInstance()

        accediBtn.setOnClickListener {
            val mail = mailEditText.editText?.text.toString()
            val password = passwordEditText.editText?.text.toString()
            if(mail.isBlank() || password.isBlank()) {
                Toast.makeText(this,"Inserire email e password",Toast.LENGTH_SHORT).show()
            }
            else {
                auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(this, "Ciao $mail",Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this,"${it.exception!!.message}",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        registratiBtn.setOnClickListener{
            val intent= Intent(this,SplashActivity::class.java)
            startActivity(intent)
        }
    }
}
