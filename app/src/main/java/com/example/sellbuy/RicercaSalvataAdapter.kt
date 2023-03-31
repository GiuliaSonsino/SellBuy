package com.example.sellbuy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import models.RicercaSalvata

class RicercaSalvataAdapter(context: Context, private val listaRicerche: List<RicercaSalvata>) : RecyclerView.Adapter<RicercaSalvataAdapter.ViewHolder>()    {

    private val mcontext: Context?=context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ricerca_salvata, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRicerca = listaRicerche[position]
        holder.parola.text=currentRicerca.parolaDigitata
        //holder.prezzo!!.text=""
        holder.prezzo.text=currentRicerca.prezzo
        holder.spedizione.text= currentRicerca.spedizione
        holder.localizzazione.text=currentRicerca.localizzazione!!

        //per rendere le card cliccabili
        holder.itemView.setOnClickListener{
            val parolaDigitata=currentRicerca.parolaDigitata
            val prezzo=currentRicerca.prezzo
            val spedizione=currentRicerca.spedizione
            val distanza= currentRicerca.localizzazione
            val intent= Intent(mcontext,RicercaActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("parolaDigitata", parolaDigitata)
            intent.putExtra("prezzo", prezzo)
            intent.putExtra("spedizione", spedizione)
            intent.putExtra("distanza", distanza)
            mcontext?.startActivity(intent)
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return listaRicerche.size
    }



    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){

        val parola: TextView = itemView.findViewById(R.id.parola)
        val prezzo: TextView = itemView.findViewById(R.id.prezzo_massimo)
        val spedizione: TextView = itemView.findViewById(R.id.spedizione)
        val localizzazione: TextView = itemView.findViewById(R.id.localizzazione)

    }
}