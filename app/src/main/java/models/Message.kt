package models

class Message {

    var contenuto: String? = null
    var sender: String? = null
    var receiver: String? = null
    var articolo: String? = null
    var codiceArticolo: String? = null


    constructor(){}

    constructor(contenuto: String?,sender: String?, receiver: String?, articolo: String?, codiceArticolo: String?) {
        this.contenuto=contenuto
        this.sender=sender
        this.receiver=receiver
        this.articolo=articolo
        this.codiceArticolo=codiceArticolo
    }

}