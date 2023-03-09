package com.example.sellbuy

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import models.FirebaseDbWrapper

class ModificaAnnActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifica_ann)

        val codiceAnn= intent.getStringExtra("codiceAnn")

        val titolo= findViewById<EditText>(R.id.edit_title)
        val descrizione= findViewById<EditText>(R.id.edit_description)
        val categoriaEdit= findViewById<AutoCompleteTextView>(R.id.edit_categoria)
        val categoria= findViewById<TextInputLayout>(R.id.categoria)
        val prezzo= findViewById<EditText>(R.id.edit_price)
        val condizioniEdit= findViewById<AutoCompleteTextView>(R.id.edit_condizioni)
        val condizioni= findViewById<TextInputLayout>(R.id.condizioni)
        val spedizione= findViewById<Switch>(R.id.edit_switchSpediz)
        var sped: Boolean
        val btnSalva= findViewById<Button>(R.id.btnSalva)

        CoroutineScope(Dispatchers.IO).launch {
            val currentAnnuncio = FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(
                applicationContext,
                codiceAnn!!
            )
            withContext(Dispatchers.Main) { // quando la funz getAnn.. ha recuperato l'annuncio allora fa questo codice seguente
                titolo.setText(currentAnnuncio.nome)
                descrizione.setText(currentAnnuncio.descrizione)
                categoria.hint = currentAnnuncio.categoria
                prezzo.setText(currentAnnuncio.prezzo)
                condizioni.hint = currentAnnuncio.stato
                sped = currentAnnuncio.spedizione
                spedizione.isChecked = sped
            }
        }
    }
}
