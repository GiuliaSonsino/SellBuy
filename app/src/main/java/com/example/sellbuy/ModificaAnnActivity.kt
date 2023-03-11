package com.example.sellbuy

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import models.FirebaseDbWrapper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ModificaAnnActivity: AppCompatActivity() {
    var im1: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {

        var nomeImageView :ImageView?= findViewById(R.id.iv)


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
        setContentView(R.layout.activity_modifica_ann)

        val codiceAnn= intent.getStringExtra("codiceAnn")

        val titolo= findViewById<EditText>(R.id.edit_title)
        val descrizione= findViewById<EditText>(R.id.edit_description)
        var categoria= findViewById<TextInputLayout>(R.id.categoria)
        val prezzo= findViewById<EditText>(R.id.edit_price)
        val condizioni= findViewById<TextInputLayout>(R.id.condizioni)
        val spedizione= findViewById<Switch>(R.id.edit_switchSpediz)
        var sped: Boolean
        val btnSalva= findViewById<Button>(R.id.btnSalva)
        im1=findViewById<ImageView>(R.id.edit_image1)
        val im2=findViewById<ImageView>(R.id.edit_image2)
        val im3=findViewById<ImageView>(R.id.edit_image3)
        val im4=findViewById<ImageView>(R.id.edit_image4)
        val mainImmagine= findViewById<ImageView>(R.id.editmain_image)
        val eliminaImg1= findViewById<Button>(R.id.btnElimina1)
        val sostImg1 = findViewById<Button>(R.id.btnSostituisci1)
        var immagini: MutableList<String>? = null
        var numeroImm : Int = 0
        val categorie = resources.getStringArray(R.array.categorie)
        val adapterCat = ArrayAdapter(this, R.layout.list_item, categorie)
        val categoriaEdit= findViewById<AutoCompleteTextView>(R.id.edit_categoria)
        categoriaEdit.setAdapter(adapterCat)

        val listaCondizioni = resources.getStringArray(R.array.condizioni)
        val adapterCond = ArrayAdapter(this, R.layout.list_item, listaCondizioni)
        val condizioniEdit= findViewById<AutoCompleteTextView>(R.id.edit_condizioni)
        condizioniEdit.setAdapter(adapterCond)

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
                immagini = currentAnnuncio.foto
                numeroImm=immagini!!.size

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



            /*
                var fileName: MutableList<String> = mutableListOf()
                //Choose an image from image gallery and load it into ImageView widget
                sostImg1.setOnClickListener {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.putExtra("imageViewId",R.id.edit_image1)
                    startActivityForResult(intent, 1000)
                }
                eliminaImg1!!.setOnClickListener {
                    //getImage.launch("image/*")
                    val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                    val now = Date()
                    val nameImg = formatter.format(now)
                    fileName.add(nameImg)
                    val storageReference =
                        FirebaseStorage.getInstance().getReference("images/$nameImg")
                    //Get the byte of the image shown in the ImageView widget
                    im1!!.isDrawingCacheEnabled = true
                    im1!!.buildDrawingCache()
                    val bitmap = (im1!!.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    //Upload the image
                    val uploadTask = storageReference.putBytes(data)
                    uploadTask.addOnFailureListener {
                        Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_LONG)
                            .show()
                    }.addOnSuccessListener {
                        Toast.makeText(
                            applicationContext,
                            "Uploaded successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        sostImg1!!.isEnabled = true
                        //upload!!.isEnabled=false
                    }
                }
            }
        }
         fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?,  im: ImageView) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
                val imageUri: Uri = data.data!!
                val imageViewId = data.getIntExtra("imageViewId", -1)
                if(imageViewId != -1) {
                    val imageView = findViewById<ImageView>(imageViewId)
                    imageView.setImageURI(imageUri)
                }
             */
             */


            Log.i(TAG,"numero immmmmm $numeroImm")
            if(numeroImm==1) {
                nomeImageView= findViewById<ImageView>(R.id.edit_image1)
            }
            else if(numeroImm==2) {
                nomeImageView= findViewById<ImageView>(R.id.edit_image2)
            }
            else if(numeroImm==3) {
                nomeImageView= findViewById<ImageView>(R.id.edit_image3)
            }
            else if(numeroImm==4) {
                nomeImageView= findViewById<ImageView>(R.id.edit_image4)
            }

            val pickup = findViewById<Button>(R.id.pickUpImg)
            val upload = findViewById<Button>(R.id.uploadImg)
            val imagev = findViewById<ImageView>(R.id.iv)
            val builder = AlertDialog.Builder(applicationContext)
            val dialogView = layoutInflater.inflate(R.layout.progress_bar, null)
            val message = dialogView.findViewById<TextView>(R.id.message)

            //Create a registry to act a getContent action
            /*
            val getImage = registerForActivityResult(
                ActivityResultContracts.GetContent(), //here we specify that we want pick up a content
                ActivityResultCallback {
                    nomeImageView!!.setImageURI(it) //once the user has selected the image, I get the URI of
                    // the image and I use it to set the image into the
                    //imageview widget
                    if(it!=null) {
                        upload!!.isEnabled=true
                        pickup!!.isEnabled=false
                    }
                }
            )

            //Choose an image from image gallery and load it into ImageView widget
            pickup!!.setOnClickListener{
                getImage.launch("image/*")
            }


             */
             */


            pickup.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                getImage.launch(intent)
            }


            //var fileName: MutableList<String> = mutableListOf()
            //Upload the image in the imageview widget
            upload!!.setOnClickListener{
                // execute the progress bar
                /*
                message.text = "Uploading..."
                builder.setView(dialogView)
                builder .setCancelable(false)
                // Remove dialogView from its current parent
                val parent = dialogView.parent as ViewGroup?
                parent?.removeView(dialogView)
                var dialog = builder.create()
                dialog.show()


                 */
                //Handler().postDelayed({dialog.dismiss()}, 5000)
                val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                val now = Date()
                val nameImg = formatter.format(now)
                immagini!!.add(nameImg)
                val storageReference = FirebaseStorage.getInstance().getReference("images/$nameImg")

                //Get the byte of the image shown in the ImageView widget
                //imagev.isDrawingCacheEnabled = true
                //imagev.buildDrawingCache()
                nomeImageView!!.isDrawingCacheEnabled = true
                nomeImageView!!.buildDrawingCache()
                val bitmap = (nomeImageView!!.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos)
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
                    Toast.makeText(applicationContext, "Uploaded successfully", Toast.LENGTH_LONG).show()
                    pickup!!.isEnabled=true
                    upload!!.isEnabled=false


                   GlobalScope.launch {
                            FirebaseDbWrapper(applicationContext).modificaImmagini(applicationContext,codiceAnn!!,immagini!!)
                    }
/*
                    val intent = Intent(applicationContext, AnnuncioActivity::class.java)
                    intent.putExtra("codice", codiceAnn)
                    applicationContext.startActivity(intent)
*/
                    //finish()
                }
            }


        }//chiusura global scope

        eliminaImg1!!.setOnClickListener{
            finish()
            val intent = Intent(this, AnnuncioActivity::class.java)
            intent.putExtra("codice", codiceAnn)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            /*
            val intent = Intent(this, AnnuncioActivity::class.java)
            startActivityForResult(intent, 1000)


             */
            /*
            val intent = Intent(applicationContext, AnnuncioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("codice", codiceAnn)
            applicationContext.startActivity(intent)


             */
        }



        btnSalva.setOnClickListener {
            GlobalScope.launch {
                val tit= titolo.text.toString()
                val desc = descrizione.text.toString()
                val prez = prezzo.text.toString()
                val cat = categoriaEdit.text.toString()
                val stato = condizioniEdit.text.toString()
                var switchSped = false
                spedizione.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        switchSped = true
                    }
                }
                FirebaseDbWrapper(applicationContext).modificaNome(applicationContext,codiceAnn!!,tit,desc,prez,cat,stato,switchSped)
            }
            finish()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }
    }
}