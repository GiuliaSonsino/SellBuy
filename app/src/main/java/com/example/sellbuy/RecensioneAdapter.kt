package com.example.sellbuy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import models.Recensione

class RecensioneAdapter(context: Context, private val listaRecensioni: List<Recensione>, emailProprietario : String) : RecyclerView.Adapter<RecensioneAdapter.ViewHolder>()    {

    private val mcontext: Context?=context
    private val email : String = emailProprietario

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recensione, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRecensione = listaRecensioni[position]
        val emailProva = currentRecensione.acquirente
        val emailProva2 = currentRecensione.venditore
        if(emailProva==email) {
            holder.recensore.text =currentRecensione.venditore
            holder.voto.text= currentRecensione.votoAllAcquirente.toString()
            holder.recensione.text= currentRecensione.recensioneAllAcquirente
        }
        else {
            holder.recensore.text = currentRecensione.acquirente
            holder.voto.text = currentRecensione.votoAlVenditore.toString()
            holder.recensione.text = currentRecensione.recensioneAlVenditore
        }

        //per rendere le card cliccabili

    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return listaRecensioni.size
    }



    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){

        val recensore: TextView = itemView.findViewById(R.id.recensore)
        val voto: TextView = itemView.findViewById(R.id.voto)
        val recensione: TextView = itemView.findViewById(R.id.recensione)

    }
}