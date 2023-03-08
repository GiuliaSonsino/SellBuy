package models

class Utente() {
    var nome: String = "*"
    var cognome: String = "*"
    var email: String = "*"
    var numTel: Long = 0
    var isAmministratore: Boolean = false

    constructor(nome: String, cognome: String, email: String, numTel: Long, isAmministratore: Boolean): this() {
        this.nome=nome
        this.cognome=cognome
        this.email=email
        this.numTel=numTel
        this.isAmministratore=isAmministratore
    }
}
