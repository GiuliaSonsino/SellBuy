package models

import android.content.ContentValues
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
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
import kotlinx.coroutines.tasks.await
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class FirebaseAuthWrapper(private val context: Context) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }



/*
    fun deleteUser(id: String) {
        FirebaseAuth.getInstance().dele
        FirebaseAuth.getInstance().deleteUser(id)
        FirebaseAuth.getInstance().del
        var us = auth.currentUser
        us.de
        var user = FirebaseAuth.getInstance().cur
        auth.currentUser.deleteUser(id)
    }

 */


    fun signUp(email: String, password: String) {
        this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

            }
            else {
                Toast.makeText(
                    context,
                    "Registrazione fallita: ${task.exception!!.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
}

class FirebaseDbWrapper(context: Context) {
    private val db = Firebase.database("https://sellbuy-abe26-default-rtdb.firebaseio.com/")
    var dbref = db.reference

    fun getTutteEmailUtenti(context: Context): MutableList<String> {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    val List: MutableList<String> = mutableListOf()
    if (context != null) {
        GlobalScope.launch {
            FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.child("Utenti").children
                    for (child in children) {
                        val list = child.value as HashMap<*, *>
                        for (record in list) {
                            val valore = record.value.toString()
                            if (record.key!! == "email") {
                                List.add(valore)
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
    return List
    }


    fun getTutteEmailUtentiEliminati(context: Context): MutableList<String>? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val List: MutableList<String> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("UtentiEliminati").children
                        for (child in children) {
                            val list = child.value as HashMap<*, *>
                            for (record in list) {
                                val valore = record.value.toString()
                                if (record.key!! == "email") {
                                    List.add(valore)
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
        return List
    }


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
                            val list = child.getValue() as HashMap<*, *>
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


    fun isAmministratore(context: Context): Boolean  {
        var ris = false
        val utenteLoggato = FirebaseAuth.getInstance().currentUser?.email
        val lock = ReentrantLock()
        val condition = lock.newCondition()

        GlobalScope.launch {
            FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.child("Utenti").children
                    var utenteAmm = false
                    for (child in children) {
                        val list = child.getValue() as HashMap<*, *>
                        for (record in list) {
                            if (record.key.equals("amministratore")) {
                                utenteAmm = record.value as Boolean
                            }
                        }
                        for (record in list) {
                            if (record.key.equals("email") && record.value.equals(utenteLoggato)) {
                                if (utenteAmm) {
                                    ris = utenteAmm
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

        return ris
    }


    fun getTuttiAnnunci(context: Context): MutableList<Annuncio> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val annList: MutableList<Annuncio> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            annList.add(child.getValue(Annuncio::class.java)!!)
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
                            val list = child.getValue() as HashMap<*, *>
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

    fun getUtenteFromCodice(context: Context, codice : String): Utente {
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
                            val key = child.key.toString()
                            if(key == codice ) {
                                prova = child.getValue(Utente::class.java)!!
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

        val annList: MutableList<Annuncio> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                annList.clear()
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("email") && record.value.equals(maill)) {
                                        annList.add(child.getValue(Annuncio::class.java)!!)

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
        val keyList: MutableList<String?> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            codicetmp=child.key.toString()
                            val list = child.getValue() as HashMap<*, *>
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
        val keyList: MutableList<String?> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            codicetmp = child.key.toString()
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

    fun getRecensioniFromUtente(context: Context, emailutente:String): MutableList<Recensione> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val rec : MutableList<Recensione> = mutableListOf()
        if (emailutente != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Recensioni").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            var boolVend =false
                            var boolAcq = false
                            for (record in list) {
                                if(record.key.equals("acquirenteHaRecensito") && record.value==true) {
                                    boolAcq = true
                                }
                                if(record.key.equals("venditoreHaRecensito") && record.value==true) {
                                    boolVend = true
                                }
                            }
                            for (record in list) {
                                if((record.key.equals("acquirente") && record.value.equals(emailutente)) && (boolVend == true) ) {
                                    rec.add(child.getValue(Recensione::class.java)!!)
                                }
                                if((record.key.equals("venditore") && record.value.equals(emailutente)) && (boolAcq== true) ) {
                                    rec.add(child.getValue(Recensione::class.java)!!)
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
        return rec
    }

    fun getIdUtenteFromEmail(context: Context, email:String): String? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var idUtente: String?
        val keyList: MutableList<String?> = mutableListOf()
        if (email != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Utenti").children
                        for (child in children) {
                            idUtente=child.key.toString()
                            val list = child.getValue() as HashMap<*, *>
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
        val email: MutableList<String?> = mutableListOf()
        var idUtente: String?
        var mail: String?
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
                                val list = child.getValue() as HashMap<*, *>
                                for (record in list) {
                                    if(!record.key.equals("foto")) {
                                        if(record.key.equals("email")) {
                                            mail= record.value as String?
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

    //restituisce true se l'utente è nel DB. serve per le chat per non mostrare chat con utenti eliminati
    fun isUtenteinDB(context: Context, id:String): Boolean {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var ris = false
        var idUtente: String?
        if (id != null) {
            ris=false
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Utenti").children
                        for (child in children) {
                            idUtente=child.key.toString()
                            if(idUtente==id) {
                                ris=true
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
        return ris
    }


    fun getChats(context: Context, id:String): MutableList<Message> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val chatList: MutableList<Message> = mutableListOf()
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
                                    val e = message.getValue() as HashMap<*, *>
                                    for (y in e) {
                                        if ((y.key.equals("receiver") && y.value.equals(id)) || (y.key.equals(
                                                "sender"
                                            ) && y.value.equals(id))
                                        ) {
                                            chatList.add(message.getValue(Message::class.java)!!)
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
                            val key = child.key.toString()
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

    //aggiorna credito utente
    suspend fun modificaCreditoUtente(context: Context, codiceUtente: String, creditoNuovo : Double) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Utenti").child(codiceUtente).child("credito")
        dbRef.setValue(creditoNuovo).await()

    }

    //per aggiungere o eliminare img da un Annuncio
    suspend fun modificaImmagineFromAnnuncio(context: Context, codice: String, immagini: List<String>?) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("foto")
        if (immagini != null) {
            dbRef.setValue(immagini).await()
        } else {
            dbRef.removeValue().await()
        }
    }

    suspend fun segnaComeVenduto(context: Context, codice: String) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("venduto")
        dbRef.setValue(true).await()
    }

    // Viene aggiornata la lista di annunci in una specifica RicercaSalvata
    suspend fun modificaRicercaSalvata(context: Context, codice: String, annunci : MutableList<Annuncio>) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("RicercheSalvate").child(codice).child("elencoAnnunciTrovati")
        dbRef.setValue(annunci).await()
    }

    suspend fun modificaInfoAnnuncio(context: Context, codice:String, nome: String, descrizione : String, prezzo: String, categoria : String, condizioni: String, spedizione: Boolean, luogo : String) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("nome")
        dbRef.setValue(nome).await()
        val dbRef2 = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("descrizione")
        dbRef2.setValue(descrizione).await()
        val dbRef3 = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("prezzo")
        dbRef3.setValue(prezzo).await()
        val dbRef4 = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("stato")
        dbRef4.setValue(condizioni).await()
        val dbRef5 = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("categoria")
        dbRef5.setValue(categoria).await()
        val dbRef6 = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("spedizione")
        dbRef6.setValue(spedizione).await()
        val dbRef7 = FirebaseDbWrapper(context).dbref.child("Annunci").child(codice).child("localizzazione")
        dbRef7.setValue(luogo).await()
    }


    //funzione per aggiornare i parametri della recensione quando un acquirente vota
    suspend fun modificaRecensioneAcquirente(context: Context, idOggetto: String,voto :Int, frase : String, haRecensito : Boolean) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Recensioni").child(idOggetto).child("votoAlVenditore")
        dbRef.setValue(voto).await()
        val dbRef2 = FirebaseDbWrapper(context).dbref.child("Recensioni").child(idOggetto).child("recensioneAlVenditore")
        dbRef2.setValue(frase).await()
        val dbRef3 = FirebaseDbWrapper(context).dbref.child("Recensioni").child(idOggetto).child("acquirenteHaRecensito")
        dbRef3.setValue(haRecensito).await()
    }

    //funzione per aggiornare i parametri della recensione quando un venditore vota
    suspend fun modificaRecensioneVenditore(context: Context, idOggetto: String,voto :Int, frase : String, haRecensito : Boolean) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Recensioni").child(idOggetto).child("votoAllAcquirente")
        dbRef.setValue(voto).await()
        val dbRef2 = FirebaseDbWrapper(context).dbref.child("Recensioni").child(idOggetto).child("recensioneAllAcquirente")
        dbRef2.setValue(frase).await()
        val dbRef3 = FirebaseDbWrapper(context).dbref.child("Recensioni").child(idOggetto).child("venditoreHaRecensito")
        dbRef3.setValue(haRecensito).await()
    }

    // restituisce true se il proprietario dell'annuncio deve recensire perchè qualcuno ha eseguito l'acquisto
    fun bisognaRecensire(context: Context, idOggetto:String): Boolean {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var ris= false
        if (idOggetto != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Recensioni").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            for (record in list) {
                                if(record.key.equals("idOggettoRecensito") && record.value.equals(idOggetto) ) {
                                    ris =true
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
        return ris
    }


/*
    fun ricercaConFiltri(context: Context, parola: String, prezzo: String, spedizione: String?): MutableList<Annuncio> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val annList: MutableList<Annuncio> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            var nome:String?=null
                            var prez:String?=null
                            var spediz: Boolean? = false
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("nome")) {
                                        nome = record.value.toString()
                                    }
                                    if(record.key.equals("prezzo")) {
                                        prez = record.value.toString()
                                    }
                                    if(record.key.equals("spedizione") && record.value==true) {
                                        spediz = true
                                    }
                                }
                            }
                            if(spedizione.equals("Tutti")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() ) {
                                    annList.add(child.getValue(Annuncio::class.java)!!)
                                }
                            }
                            if(spedizione.equals("Si")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() && spediz!!) {
                                    annList.add(child.getValue(Annuncio::class.java)!!)
                                }
                            }
                            if(spedizione.equals("No")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() && !spediz!!) {
                                    annList.add(child.getValue(Annuncio::class.java)!!)
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

 */


    fun stringToLatLng(inputString: String): LatLng? {
        val regex = ".*\\(([^,]*),([^)]*)\\).*".toRegex()
        val matchResult = regex.find(inputString)
        if (matchResult != null && matchResult.groupValues.size >= 3) {
            val lat = matchResult.groupValues[1].toDoubleOrNull()
            val lng = matchResult.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) {
                return LatLng(lat, lng)
            }
        }
        return null
    }

    fun ricercaConFiltriELocalizzazione(context: Context, parola: String, prezzo: String, spedizione: String?, distanzaInserita: String, posizioneAttuale: LatLng): MutableList<Annuncio> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val annList: MutableList<Annuncio> = mutableListOf()
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            var nome:String?=null
                            var prez:String?=null
                            var spediz: Boolean? = false
                            val distance = FloatArray(1)
                            var luogoLatLng: LatLng? = null
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("nome")) {
                                        nome = record.value.toString()
                                    }
                                    if(record.key.equals("prezzo")) {
                                        prez = record.value.toString()
                                    }
                                    if(record.key.equals("spedizione") && record.value==true) {
                                        spediz = true
                                    }
                                    if(record.key.equals("localizzazione")) {
                                        luogoLatLng = stringToLatLng(record.value.toString())
                                    }
                                }
                            }
                            if(spedizione.equals("Tutti")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() ) {
                                    if(distanzaInserita=="") {
                                        annList.add(child.getValue(Annuncio::class.java)!!)
                                    }
                                    else {
                                        Location.distanceBetween(posizioneAttuale.latitude, posizioneAttuale.longitude, luogoLatLng!!.latitude, luogoLatLng.longitude, distance)
                                        if (distance[0] < distanzaInserita.toInt() * 1000) {
                                            annList.add(child.getValue(Annuncio::class.java)!!)
                                        }
                                    }
                                }
                            }
                            if(spedizione.equals("Si")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() && spediz!!) {
                                    if(distanzaInserita=="") {
                                        annList.add(child.getValue(Annuncio::class.java)!!)
                                    }
                                    else {
                                        Location.distanceBetween(posizioneAttuale.latitude, posizioneAttuale.longitude, luogoLatLng!!.latitude, luogoLatLng.longitude, distance)
                                        if (distance[0] < distanzaInserita.toInt() * 1000) {
                                            annList.add(child.getValue(Annuncio::class.java)!!)
                                        }
                                    }
                                }
                            }
                            if(spedizione.equals("No")) {
                                if(nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() && !spediz!!) {
                                    if (distanzaInserita == "") {
                                        annList.add(child.getValue(Annuncio::class.java)!!)
                                    } else {
                                        Location.distanceBetween(
                                            posizioneAttuale.latitude,
                                            posizioneAttuale.longitude,
                                            luogoLatLng!!.latitude,
                                            luogoLatLng.longitude,
                                            distance
                                        )
                                        if (distance[0] < distanzaInserita.toInt() * 1000) {
                                            annList.add(child.getValue(Annuncio::class.java)!!)
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
        return annList
    }

    fun ricercaKeysConFiltriELocalizzazione(context: Context, parola: String, prezzo: String, spedizione: String?, distanzaInserita: String, posizioneAttuale: LatLng): MutableList<String> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val chiavi: MutableList<String> = mutableListOf()
        var codicetmp: String
        if (context != null) {
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            codicetmp=child.key.toString()
                            val list = child.getValue() as HashMap<*, *>
                            var nome:String?=null
                            var prez:String?=null
                            var spediz: Boolean? = false
                            val distance = FloatArray(1)
                            var luogoLatLng: LatLng? = null
                            for (record in list) {
                                if(!record.key.equals("foto")) {
                                    if(record.key.equals("nome")) {
                                        nome = record.value.toString()
                                    }
                                    if(record.key.equals("prezzo")) {
                                        prez = record.value.toString()
                                    }
                                    if(record.key.equals("spedizione") && record.value==true) {
                                        spediz = true
                                    }
                                    if(record.key.equals("localizzazione")) {
                                        luogoLatLng = stringToLatLng(record.value.toString())
                                    }
                                }
                            }
                            if(spedizione.equals("Tutti")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() ) {
                                    if(distanzaInserita == "") {
                                        chiavi.add(codicetmp)
                                    }
                                    else {
                                        Location.distanceBetween(posizioneAttuale.latitude, posizioneAttuale.longitude, luogoLatLng!!.latitude, luogoLatLng.longitude, distance)
                                        if (distance[0] < distanzaInserita.toInt() * 1000) {
                                            chiavi.add(codicetmp)
                                        }
                                    }
                                }
                            }
                            if(spedizione.equals("Si")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() && spediz!!) {
                                    if(distanzaInserita=="") {
                                        chiavi.add(codicetmp)
                                    }
                                    else {
                                        Location.distanceBetween(posizioneAttuale.latitude, posizioneAttuale.longitude, luogoLatLng!!.latitude, luogoLatLng.longitude, distance)
                                        if (distance[0] < distanzaInserita.toInt() * 1000) {
                                            chiavi.add(codicetmp)
                                        }
                                    }
                                }
                            }
                            if(spedizione.equals("No")) {
                                if( nome!!.lowercase().contains(parola.lowercase()) && prez!!.toDouble()<=prezzo.toDouble() && !spediz!!) {
                                    if (distanzaInserita == "") {
                                        chiavi.add(codicetmp)
                                    }
                                    else {
                                        Location.distanceBetween(
                                            posizioneAttuale.latitude,
                                            posizioneAttuale.longitude,
                                            luogoLatLng!!.latitude,
                                            luogoLatLng.longitude,
                                            distance
                                        )
                                        if (distance[0] < distanzaInserita.toInt() * 1000) {
                                            chiavi.add(codicetmp)
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
        return chiavi
    }


    // Ritorna proprio le RicercheSalvate di un utente
    fun getRicercheSalvateFromEmail(context: Context): MutableList<RicercaSalvata> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email

        val ricercheList: MutableList<RicercaSalvata> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                ricercheList.clear()
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("RicercheSalvate").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            for (record in list) {
                                if(record.key.equals("email") && record.value.equals(maill)) {
                                    ricercheList.add(child.getValue(RicercaSalvata::class.java)!!)
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
        return ricercheList
    }

    // Ritorna le chiavi di RicercheSalvate
    fun getKeysRicercheSalvateFromEmail(context: Context): MutableList<String> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email

        val keysRicercheList: MutableList<String> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                keysRicercheList.clear()
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("RicercheSalvate").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            val key = child.key.toString()
                            for (record in list) {
                                if(record.key.equals("email") && record.value.equals(maill)) {
                                    keysRicercheList.add(key)
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
        return keysRicercheList
    }

    fun getCategorie(context: Context): MutableList<String> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email

        val catList: MutableList<String> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                catList.clear()
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Categorie").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            for (record in list) {
                                if(record.key.equals("nome")) {
                                    catList.add(record.value.toString())
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
        return catList
    }


    suspend fun deleteCategoria(context: Context, nomeCategoria: String) {
        val dbRef = FirebaseDbWrapper(context).dbref.child("Categorie").child(nomeCategoria)
        dbRef.removeValue().await()
    }


    fun getUtenti(context: Context): MutableList<String> {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        val maill = Firebase.auth.currentUser?.email

        val listaUtenti: MutableList<String> = mutableListOf()
        if (maill != null) {
            GlobalScope.launch {
                listaUtenti.clear()
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Utenti").children
                        var nome = ""
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            for (record in list) {
                                if (record.key.equals("email")) {
                                    nome = record.value.toString()
                                }
                            }
                            for (record in list) {
                                if(record.key.equals("amministratore") && record.value==false) {
                                    listaUtenti.add(nome)
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
        return listaUtenti
    }

    fun getNumeroAnnunciFromEmailUtente(context: Context, email: String): Int {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var count=0
        if (email != null) {
            count=0
            GlobalScope.launch {
                FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.child("Annunci").children
                        for (child in children) {
                            val list = child.getValue() as HashMap<*, *>
                            for (record in list) {
                                if (record.key.equals("email") && record.value.equals(email)) {
                                    count+=1
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
        return count
    }


    fun deleteUtente(context: Context, nomeUtente: String){
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        GlobalScope.launch {
            FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.child("Utenti").children
                    for (child in children) {
                        val list = child.getValue() as HashMap<*,*>
                        for (record in list) {
                            if(record.key.equals("email") && record.value.equals(nomeUtente)) {
                                child.ref.removeValue()
                            }
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


    fun deleteAnnunciUtente(context: Context, nomeUtente: String) : MutableList<String>{
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var tmp: Boolean
        val immaginiDaCancellare : MutableList<String> = mutableListOf()
        GlobalScope.launch {
            immaginiDaCancellare.clear()
            FirebaseDbWrapper(context).dbref.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.child("Annunci").children
                    for (child in children) {
                        tmp = false
                        val list = child.getValue() as HashMap<*,*>
                        for (record in list) {
                            if(record.key.equals("email") && record.value.equals(nomeUtente)) {
                                tmp=true
                                child.ref.removeValue()
                            }
                        }
                        if(tmp) {
                            for (record in list) {
                                if(record.key.equals("foto")) {
                                    val photos = record.value as MutableList<*>
                                    for(photo in photos) {
                                        immaginiDaCancellare.add(photo.toString())
                                    }
                                }
                            }
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
        return immaginiDaCancellare
    }


    fun creaUtente(utente: Utente) {
        dbref.child("Utenti").push().setValue(utente)
    }


    fun creaAnnuncio(annuncio: Annuncio) {
        dbref.child("Annunci").push().setValue(annuncio)
    }

    fun creaChat(senderRoom: String?, receiverRoom: String?, messaggio: Message) {
        dbref.child("chats").child(senderRoom!!).child("messages").push().setValue(messaggio).addOnSuccessListener {
            dbref.child("chats").child(receiverRoom!!).child("messages").push().setValue(messaggio)
        }
    }

    fun creaRicercaSalvata(ricerca: RicercaSalvata) {
        dbref.child("RicercheSalvate").push().setValue(ricerca)
    }

    fun creaRecensione(recensione : Recensione) {
        dbref.child("Recensioni").child(recensione.idOggettoRecensito!!).setValue(recensione)
    }

    fun creaCategoria(categoria : Categoria) {
        dbref.child("Categorie").child(categoria.nome).setValue(categoria)
    }

    fun creaUtenteEliminato(utente : UtenteEliminato) {
        dbref.child("UtentiEliminati").push().setValue(utente)
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


    suspend fun deleteImmagineFromStorage(context: Context, immagine: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images").child(immagine)
        storageRef.delete().await()
    }
}










