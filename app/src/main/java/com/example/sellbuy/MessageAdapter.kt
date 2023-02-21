package com.example.sellbuy

import android.content.Context
import android.os.Message
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(context: Context): RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    var messages : ArrayList<Message> = arrayListOf()
    var mcontext : Context = context

    constructor(messages: ArrayList<Message>, mcontext: Context) : this(mcontext) {
        this.messages=messages
        this.mcontext=mcontext
    }






    class MessageHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        TODO("Not yet implemented")
    }


}