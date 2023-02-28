package models

class ItemChat() {

    var nomeUtente: String? = null
    var nomeArticolo: String? = null



    constructor(nomeUtente: String?,nomeArticolo: String?):this() {
        this.nomeUtente=nomeUtente
        this.nomeArticolo=nomeArticolo
    }

}