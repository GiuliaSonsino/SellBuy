package models

class Message {

    var contenuto: String? = null
    var sender: String? = null
    var receiver: String? = null
    var codiceArticolo: String? = null

    constructor(){}

    constructor(contenuto: String?,sender: String?, receiver: String?, codiceArticolo: String?) {
        this.contenuto=contenuto
        this.sender=sender
        this.receiver=receiver
        this.codiceArticolo=codiceArticolo
    }

}