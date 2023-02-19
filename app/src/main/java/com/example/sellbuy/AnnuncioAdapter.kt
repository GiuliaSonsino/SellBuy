package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import models.AnnuncioViewModel
import com.bumptech.glide.Glide



/*
class AnnuncioAdapter(context: Context, val annuncioList: ArrayList<Annuncio>) : BaseAdapter(){
    override fun getCount(): Int {
        return annuncioList.size
    }

    override fun getItem(position: Int): Annuncio? {
        return annuncioList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var returnedView: View
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()

            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            //QUESTO CODICE SERVE A CAMBIARE IL LAYOUT SE LA RIGA I-ESIMA È PARI O DISPARI
            //ATTENZIONE: ENTRAMBI I LAYOUT HANNO GLI STESSI ID
            /*
            returnedView = if (position % 2 == 0)
                inflater.inflate(R.layout.lv_item, null, true)
            else
                inflater.inflate(R.layout.lv_item2, null, true)

             */

            returnedView = inflater.inflate(R.layout.item_annuncio, null, true)

            holder.imageView = returnedView.findViewById(R.id.imageView)
            holder.tvName = returnedView.findViewById(R.id.title)

            returnedView.tag = holder


        } else {
            holder = convertView.tag as ViewHolder
            returnedView = convertView
        }

       // holder.imageView.setImageResource(annuncioList[position].foto)
        holder.tvName.text = annuncioList[position].nome



        return returnedView
    }


    private inner class ViewHolder {
        lateinit var tvName: TextView
        lateinit var imageView: ImageView
    }

}*/

class AnnuncioAdapter(context: Context, private val mList: List<AnnuncioViewModel>) : RecyclerView.Adapter<AnnuncioAdapter.ViewHolder>() {

    private val mcontext:Context?=context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_annuncio, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val currentItem = mList[position]
        //holder.bind(currentItem)

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)
        //holder.imageView.setImageBitmap(ItemsViewModel.image)
        holder.textView.text=ItemsViewModel.image
        holder.textView.text=ItemsViewModel.text




/*
        val ONE_MEGABYTE: Long = 1024 * 1024
        // Carica l'immagine dal Firebase Storage utilizzando il percorso memorizzato nell'oggetto Annuncio

        val storageReference = ItemsViewModel.image?.let { Firebase.storage.reference.child(it) }

        if (storageReference != null) {
            storageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener { imageData ->
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    holder.imageView.setImageBitmap(bitmap)
                }
                .addOnFailureListener { exception ->
                    // Gestisci l'errore
                }
        }
        */




        //per rendere le card cliccabili
        holder.itemView.setOnClickListener{
            Log.i(TAG,"Hai cliccatoo su: ${ItemsViewModel.codice}")
           // Toast.makeText( this@AnnuncioAdapter,"hai cliccato su ${ItemsViewModel.text}",Toast.LENGTH_SHORT).show()
            val intent= Intent(mcontext,AnnuncioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mcontext?.startActivity(intent)
        }

    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }






    // Holds the views for adding it to image and text
     class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){


        /*
        fun bind(annuncio: AnnuncioViewModel) {
            itemView.findViewById<TextView>(R.id.textView).text = annuncio.text
            val imageView = itemView.findViewById<ImageView>(R.id.imageview)
            Glide.with(itemView.context)
                .load(annuncio.image)
                .into(imageView)
            itemView.setOnClickListener {
                //gestione click sull'annuncio
            }
        }*/


        val textView: TextView = itemView.findViewById(R.id.textView)
        val imageView: ImageView = itemView.findViewById(R.id.imageview)

    }
}

