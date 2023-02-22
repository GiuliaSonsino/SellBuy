package models

class Message {

    var contenuto: String? = null
    var sender: String? = null

    constructor(){}

    constructor( contenuto: String?,sender: String?) {
        this.contenuto=contenuto
        this.sender=sender
    }

}