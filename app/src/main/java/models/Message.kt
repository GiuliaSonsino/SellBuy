package models

class Message {

    lateinit var sender: String
    lateinit var receiver: String
    lateinit var contenuto: String

    constructor(sender: String, receiver: String, contenuto: String) {
        this.sender=sender
        this.receiver=receiver
        this.contenuto=contenuto
    }

}