package models

class Message {

    var contenuto: String? = null
    var sender: String? = null
    var receiver: String? = null
    var articolo: String? = null


    constructor(){}

    constructor(contenuto: String?,sender: String?, receiver: String?, articolo: String?) {
        this.contenuto=contenuto
        this.sender=sender
        this.receiver=receiver
        this.articolo=articolo
    }

}