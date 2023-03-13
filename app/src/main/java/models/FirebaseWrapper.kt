package models

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FirebaseAuthWrapper(private val context: Context) {

    //private var auth: FirebaseAuth = Firebase.auth
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun getUid(): String {
        return auth.currentUser!!.uid
    }


    fun signUp(email: String, password: String) {
        this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {


            } else {
                Toast.makeText(
                    context,
                    "Sign-up failed. Error message: ${task.exception!!.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

class FirebaseDbWrapper(context: Context) {
    private val db = Firebase.database("https://sellbuy-abe26-default-rtdb.firebaseio.com/")
    var dbref = db.reference



    fun isProprietarioAnnuncio(context: Context, codice: String): Boolean  {
        var flag = false
        val utenteLoggato = FirebaseAuth.getInstance().currentUser?.email
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        if (codice != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<String, String>
                            if(child.key==codice) {
                                for (record in list) {
                                    if (record.key!!.equals("email") && record.value!!.equals(utenteLoggato)) {
                                        flag = true
                                    }
                                }
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return flag
    }



    fun getTuttiAnnunci(context: Context): MutableList<Annuncio> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var annList: MutableList<Annuncio> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            annList!!.add(child.getValue(Annuncio::class.java)!!)
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return annList
    }

    fun getUtenteFromEmail(context: Context): Utente? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email
        var prova=Utente()
        if (maill != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Utenti").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<String, String>
                            for (record in list) {
                                if (record.key.equals("email") && record.value.equals(maill)) {
                                    prova = child.getValue(Utente::class.java)!!
                                }
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return prova
    }



    fun getAnnunciFromEmail(context: Context): MutableList<Annuncio> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email

        var annList: MutableList<Annuncio> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            Log.i(TAG,"il codice univoco ${child.key}")
                            val list = child.getValue() as HashMap<String, String>
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("email") && record.value.equals(maill)) {
                                        annList!!.add(child.getValue(Annuncio::class.java)!!)

                                    }
                                }
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return annList
    }


    fun getKeyFromEmail(context: Context): MutableList<String?> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email
        var codicetmp=""
        var keyList: MutableList<String?> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            codicetmp=child.key.toString()
                            val list = child.getValue() as HashMap<String, String>
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("email") && record.value.equals(maill)) {
                                        keyList.add(codicetmp)
                                    }
                                }
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return keyList
    }


    fun getTutteKeysAnnunci(context: Context): MutableList<String?> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var codicetmp=""
        var keyList: MutableList<String?> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            codicetmp = child.key.toString()
                            Log.i(TAG, "prova codiciiii $codicetmp")
                            keyList.add(codicetmp)
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return keyList
    }


    fun getAnnuncioFromCodice(context: Context, codice:String): Annuncio {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var annuncio= Annuncio()
        if (codice != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            if(child.key==codice) {
                                annuncio = child.getValue(Annuncio::class.java)!!
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return annuncio
    }


    fun getIdUtenteFromEmail(context: Context, email:String): String? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var annuncio= Annuncio()
        var idUtente:String?=null
        var keyList: MutableList<String?> = mutableListOf()
        if (email != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Utenti").children
                        for (child in children) {
                            idUtente=child.key.toString()
                            val list = child.getValue() as HashMap<String, String>
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("email") && record.value.equals(email)) {
                                        keyList.add(idUtente)
                                    }
                                }
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return keyList[0]
    }

    fun getEmailFromIdUtente(context: Context, id:String): String? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var annuncio= Annuncio()
        var email: MutableList<String?> = mutableListOf()
        var idUtente:String?=null
        var mail:String?=null
        if (id != null) {
            email.clear()
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Utenti").children
                        for (child in children) {
                            idUtente=child.key.toString()
                            if(idUtente==id) {
                                val list = child.getValue() as HashMap<String, String>
                                for (record in list) {
                                    if(!record.key.equals("foto")) {
                                        if(record.key.equals("email")) {
                                            mail=record.value
                                            Log.i(TAG,"emaillllllllll $mail")
                                            email.add(mail)
                                        }
                                    }
                                }
                            }

                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return email[0]
    }


    fun getChats(context: Context, id:String): MutableList<Message> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var chatList: MutableList<Message> = mutableListOf()
        if (id != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatList.clear()
                        val children = snapshot.child("chats").children
                        for (child in children) {
                            val messages=child.child("messages").children
                            var c=0
                            for(message in messages) {
                                if(c==0) {
                                    var e = message.getValue() as HashMap<String, String>
                                    for (y in e) {
                                        if ((y.key.equals("receiver") && y.value.equals(id)) || (y.key.equals(
                                                "sender"
                                            ) && y.value.equals(id))
                                        ) {
                                            chatList!!.add(message.getValue(Message::class.java)!!)
                                        }
                                    }
                                }
                                c+=1
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return chatList
    }


    fun deleteAnnuncio(context: Context, codice: String): Boolean {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var ris=false
        if (codice != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            var key = child.key.toString()
                            if (key == codice) {
                                child.ref.removeValue()
                                ris = true
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to delete value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }
        return ris
    }

    fun deleteImmagineFromAnnuncio(context: Context,codice: String, immagini: MutableList<String>) {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        if (immagini != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            if(child.key==codice) {
                                val cod= child.ref
                                cod.child("foto").setValue(immagini)
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to delete value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }

    }

    fun modificaNome(context: Context, codice:String, nome: String, descrizione : String, prezzo: String, categoria : String, condizioni: String, spedizione: Boolean) {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        if (codice != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            if(child.key==codice) {
                                val cod= child.ref
                                cod.child("nome").setValue(nome)
                                cod.child("descrizione").setValue(descrizione)
                                cod.child("categoria").setValue(categoria)
                                cod.child("prezzo").setValue(prezzo)
                                cod.child("stato").setValue(condizioni)
                                cod.child("spedizione").setValue(spedizione)
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }

    }
    fun modificaImmagini(context: Context, codice:String, immagini: MutableList<String>) {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        if (codice != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            if(child.key==codice) {
                                val cod= child.ref
                                cod.child("foto").setValue(immagini)
                            }
                        }
                        lock.withLock { condition.signal() }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value", error.toException())
                    }
                })
            }
            lock.withLock { condition.await() }
        }

    }



    fun creaUtente(utente: Utente) {
        dbref.child("Utenti").push().setValue(utente)
    }


    fun creaAnnuncio(annuncio: Annuncio) {
        dbref.child("Annunci").push().setValue(annuncio)
    }

    fun creaChat( senderRoom: String?, receiverRoom: String?, messaggio: Message) {
        dbref.child("chats").child(senderRoom!!).child("messages").push().setValue(messaggio).addOnSuccessListener {
            dbref.child("chats").child(receiverRoom!!).child("messages").push().setValue(messaggio)
        }
    }
}




class FirebaseStorageWrapper(private val context: Context) {
    private var storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sellbuy-abe26.appspot.com")

    fun deleteImgsFromStorage(context: Context, images: MutableList<String>) {
        GlobalScope.launch {
            for (image in images) {
                storage.child("images").child(image).delete()
            }
        }
    }

    fun deleteImmagineFromStorage(context: Context, immagine: String) {
        GlobalScope.launch {
            storage.child("images").child(immagine).delete()
        }
    }
}










