package models

import com.google.firebase.Timestamp

class Utente() {
    var nome: String = "*"
    var cognome: String = "*"
    var email: String = "*"
    var numTel: Long = 0
    var isAmministratore: Boolean = false
    var credito: Double = 0.0

    constructor(nome: String, cognome: String, email: String, numTel: Long, isAmministratore: Boolean, credito: Double): this() {
        this.nome=nome
        this.cognome=cognome
        this.email=email
        this.numTel=numTel
        this.isAmministratore=isAmministratore
        this.credito=credito
    }
}
