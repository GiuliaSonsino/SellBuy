package com.example.sellbuy

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class AnnuncioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annuncio)
        /*
        val dialog = Dialog(this)

        // Imposta le proprietà del dialog, ad esempio le dimensioni
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        val image1 = dialog.findViewById<ImageView>(R.id.image1)
        image1.setOnClickListener{
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)

            // Trova la ImageView nella vista del dialog
            val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
            // Imposta l'immagine ingrandita sulla ImageView del dialog
            dialogImage.setImageResource(R.drawable.person)
            // Mostra il dialog
            dialog.show()
        }

        val image2 = findViewById<ImageView>(R.id.image2)
        image2.setOnClickListener{

        }

        val image3 = findViewById<ImageView>(R.id.image3)
        image3.setOnClickListener{

        }

        val image4 = findViewById<ImageView>(R.id.image4)
        image4.setOnClickListener{

        }*/
    }
}