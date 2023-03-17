package com.example.sellbuy

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import models.FirebaseDbWrapper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AggiungiFotoActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        var nomeImageView: ImageView? = findViewById(R.id.iv)


        val getImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    // Visualizza l'immagine selezionata nella tua ImageView
                    Glide.with(this).load(imageUri).into(nomeImageView!!)
                }
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_imm)

        val codiceAnn = intent.getStringExtra("codiceAnn")

        val im1 = findViewById<ImageView>(R.id.edit_image1)
        val im2 = findViewById<ImageView>(R.id.edit_image2)
        val im3 = findViewById<ImageView>(R.id.edit_image3)
        val im4 = findViewById<ImageView>(R.id.edit_image4)
        val mainImmagine = findViewById<ImageView>(R.id.editmain_image)
        val eliminaImg1 = findViewById<Button>(R.id.btnElimina1)
        val sostImg1 = findViewById<Button>(R.id.btnSostituisci1)
        var immagini: MutableList<String>? = null
        var numeroImm: Int = 0


        CoroutineScope(Dispatchers.IO).launch {
            val currentAnnuncio = FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(
                applicationContext,
                codiceAnn!!
            )
            withContext(Dispatchers.Main) { // quando la funz getAnn.. ha recuperato l'annuncio allora fa questo codice seguente
                immagini = currentAnnuncio.foto
                numeroImm = immagini!!.size
            }

            val strMainImm = immagini?.get(0)
            val storage = Firebase.storage.reference.child("images/$strMainImm")
            storage.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(mainImmagine)
                }
            }
            if (immagini?.size!! >= 2) {
                val strImmagine1 = immagini?.get(1)
                val storage = Firebase.storage.reference.child("images/$strImmagine1")
                storage.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im1!!)
                    }
                }
            } else {
                // View.INVISIBLE.also { im1!!.visibility = it }
            }

            if (immagini?.size!! >= 3) {
                val strImmagine2 = immagini?.get(2)
                val storage = Firebase.storage.reference.child("images/$strImmagine2")
                storage.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im2)
                    }
                }
            } else {
                //im2.visibility = View.INVISIBLE
            }

            if (immagini?.size!! >= 4) {
                val strImmagine3 = immagini?.get(3)
                val storage = Firebase.storage.reference.child("images/$strImmagine3")
                storage.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im3)
                    }
                }
            } else {
                //im3.visibility = View.INVISIBLE
            }

            if (immagini?.size!! >= 5) {
                val strImmagine4 = immagini?.get(4)
                val storage = Firebase.storage.reference.child("images/$strImmagine4")
                storage.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im4)
                    }
                }
            } else {
                // im4.visibility = View.INVISIBLE
            }

            if (numeroImm == 1) {
                nomeImageView = findViewById<ImageView>(R.id.edit_image1)
            } else if (numeroImm == 2) {
                nomeImageView = findViewById<ImageView>(R.id.edit_image2)
            } else if (numeroImm == 3) {
                nomeImageView = findViewById<ImageView>(R.id.edit_image3)
            } else if (numeroImm == 4) {
                nomeImageView = findViewById<ImageView>(R.id.edit_image4)
            }

            val pickup = findViewById<Button>(R.id.pickUpImg)
            val upload = findViewById<Button>(R.id.uploadImg)
            val imagev = findViewById<ImageView>(R.id.iv)
            val builder = AlertDialog.Builder(applicationContext)
            val dialogView = layoutInflater.inflate(R.layout.progress_bar, null)
            val message = dialogView.findViewById<TextView>(R.id.message)

            pickup.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                getImage.launch(intent)
            }


            //Upload the image in the imageview widget
            upload!!.setOnClickListener {
                val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                val now = Date()
                val nameImg = formatter.format(now)
                immagini!!.add(nameImg)
                val storageReference = FirebaseStorage.getInstance().getReference("images/$nameImg")
                nomeImageView!!.isDrawingCacheEnabled = true
                nomeImageView!!.buildDrawingCache()
                val bitmap = (nomeImageView!!.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                //Upload the image
                val uploadTask = storageReference.putBytes(data)
                //When the image is uploaded the dialog will be closed
                uploadTask.addOnCompleteListener {
                    // dialog.dismiss()
                }
                uploadTask.addOnFailureListener {
                    Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_LONG).show()
                }.addOnSuccessListener {
                    Toast.makeText(applicationContext, "Uploaded successfully", Toast.LENGTH_LONG)
                        .show()
                    pickup!!.isEnabled = true
                    upload!!.isEnabled = false


                    GlobalScope.launch {
                        FirebaseDbWrapper(applicationContext).modificaImmagineFromAnnuncio(
                            applicationContext,
                            codiceAnn!!,
                            immagini!!
                        )
                    }
                }
            }
        }

        eliminaImg1!!.setOnClickListener {
            val intent = Intent(this, AreaPersonaleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //intent.putExtra("codice", codiceAnn)
            startActivity(intent)

        }
    }
}