package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import models.AnnuncioViewModel

//RecyclerView.Adapter<AnnuncioAdapter.ViewHolder>()
class AnnuncioAdapter(context: Context, private val mList: List<AnnuncioViewModel>) : RecyclerView.Adapter<AnnuncioAdapter.ViewHolder>()    {

    private val mcontext:Context?=context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_annuncio, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAnnuncio = mList[position]
        holder.textView.text=currentAnnuncio.text
        holder.tvPrice.text=currentAnnuncio.price
        holder.tvCode.text=currentAnnuncio.codice
        var im=currentAnnuncio.image
        val storag= Firebase.storage.reference.child("images/$im")
        storag.downloadUrl.addOnSuccessListener { url ->
            if (mcontext != null) {
                Glide.with(mcontext).load(url).into(holder.imageView)
            }
        }

        //per rendere le card cliccabili
        holder.itemView.setOnClickListener{
            Log.i(TAG,"Hai cliccatoo su: ${currentAnnuncio.codice}")
            val codiceAnn=currentAnnuncio.codice
            val nomeAnn= currentAnnuncio.text
            val intent= Intent(mcontext,AnnuncioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("codice", codiceAnn)
            mcontext?.startActivity(intent)
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }



     class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){

        val textView: TextView = itemView.findViewById(R.id.textView)
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val tvPrice: TextView = itemView.findViewById(R.id.price)
        val tvCode: TextView = itemView.findViewById(R.id.code)
    }
}

