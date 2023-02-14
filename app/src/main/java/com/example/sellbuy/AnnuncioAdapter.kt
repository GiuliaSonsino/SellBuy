package com.example.sellbuy

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.AnnuncioViewModel


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
class AnnuncioAdapter(private val mList: List<AnnuncioViewModel>) : RecyclerView.Adapter<AnnuncioAdapter.ViewHolder>() {

    // create new views
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
           // Toast.makeText( this@AnnuncioAdapter,"hai cliccato su ${ItemsViewModel.text}",Toast.LENGTH_SHORT).show()
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

