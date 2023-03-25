package models

class Annuncio() {

    var nome: String = "*"
    var categoria: String = "*"
    var localizzazione : String? = null
    var descrizione: String = "*"
    var prezzo: String = "*"
    var stato : String = "*"
    var foto : MutableList<String>?= mutableListOf()
    var email : String = "*"
    var numTel : Long = 0
    var spedizione : Boolean = false
    var venduto : Boolean = false


    constructor(nome: String, categoria: String, localizzazione:String, descrizione: String, prezzo: String, stato: String, foto: MutableList<String>, email: String, numTel: Long, spedizione: Boolean, venduto: Boolean) : this(){
        this.nome=nome
        this.categoria=categoria
        this.localizzazione=localizzazione
        this.descrizione=descrizione
        this.prezzo=prezzo
        this.stato=stato
        this.foto=foto
        this.email=email
        this.numTel=numTel
        this.spedizione=spedizione
        this.venduto = venduto
    }

}