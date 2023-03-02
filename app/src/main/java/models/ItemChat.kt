package models

class ItemChat() {

    var idSender:String?=null
    var nomeSender: String? = null
    var idReceiver:String?=null
    var nomeReceiver:String?=null
    var nomeArticolo: String? = null
    var codiceArticolo: String? = null



    constructor(idSender:String?,nomeSender: String?,idReceiver:String?,nomeReceiver: String?,nomeArticolo: String?, codiceArticolo: String?):this() {
        this.idSender=idSender
        this.nomeSender=nomeSender
        this.idReceiver=idReceiver
        this.nomeReceiver=nomeReceiver
        this.nomeArticolo=nomeArticolo
        this.codiceArticolo=codiceArticolo
    }

}