package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast


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
                DynamicToast.makeWarning(this,"Inserire email e password",Toast.LENGTH_SHORT).show()
            }
            else {
                auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener {
                    if(it.isSuccessful) {
                        DynamicToast.makeSuccess(this, "Ciao $mail",Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        finish()
                    }
                    else {
                        DynamicToast.makeError(this,"Email o password errati",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        registratiBtn.setOnClickListener{
            auth.signOut()
            val intent = Intent(applicationContext, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
}
