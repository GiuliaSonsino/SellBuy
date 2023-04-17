package com.example.sellbuy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(val context: Context, private val messageList: ArrayList<models.Message>, val id:String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemReceive = 1
    private val itemSent = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1) {
            val view: View = LayoutInflater.from(context)
                .inflate(R.layout.message_receive, parent, false)
            return ReceiveViewHolder(view)
        }
        else {
            val view: View = LayoutInflater.from(context)
                .inflate(R.layout.message_sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (id == currentMessage.sender) {
            return itemSent
        }
        else {
            return itemReceive
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.contenuto
        }
        else {
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.contenuto
        }
    }


    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
    }


    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
    }
}