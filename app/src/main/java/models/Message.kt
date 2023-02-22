package models

class Message {

    var sender: String? = null
    var contenuto: String? = null

    constructor(){}

    constructor(sender: String?, contenuto: String?) {
        this.sender=sender
        this.contenuto=contenuto
    }

}