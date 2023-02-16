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
import models.AnnuncioViewModel



class AnnuncioAdapter(context: Context, private val mList: List<AnnuncioViewModel>) : RecyclerView.Adapter<AnnuncioAdapter.ViewHolder>() {

    private val mcontext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_annuncio, parent, false)

        return ViewHolder(view)
    }
    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text

        //per rendere le card cliccabili
        holder.itemView.setOnClickListener{
            Log.i(TAG,"hai cliccato su ${ItemsViewModel.codice}")
            val intent = Intent(mcontext, AnnuncioActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mcontext?.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }


}

