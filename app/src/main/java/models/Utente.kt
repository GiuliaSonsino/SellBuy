package models


class Utente() {
    var nome: String = "*"
    var cognome: String = "*"
    var email: String = "*"
    var numTel: Long = 0
    var amministratore: Boolean = false
    var credito: Double = 0.0

    constructor(nome: String, cognome: String, email: String, numTel: Long, amministratore: Boolean, credito: Double): this() {
        this.nome=nome
        this.cognome=cognome
        this.email=email
        this.numTel=numTel
        this.amministratore=amministratore
        this.credito=credito
    }
}
