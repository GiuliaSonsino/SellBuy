package models

class Recensione {
    var acquirente: String? = null
    var votoAlVenditore: Int? = null
    var recensioneAlVenditore: String? = null
    var acquirenteHaRecensito : Boolean = false
    var venditore: String? = null
    var votoAllAcquirente: Int? = null
    var recensioneAllAcquirente: String? = null
    var venditoreHaRecensito : Boolean = false
    var idOggettoRecensito : String? = null


    constructor(){}

    constructor(acquirente: String?,votoAlVenditore: Int?,recensioneAlVenditore: String?, acquirenteHaRecensito : Boolean, venditore: String?,votoAllAcquirente: Int?,recensioneAllAcquirente: String?,venditoreeHaRecensito : Boolean, idOggettoRecensito : String) {
        this.acquirente=acquirente
        this.votoAlVenditore=votoAlVenditore
        this.recensioneAlVenditore=recensioneAlVenditore
        this.acquirenteHaRecensito=acquirenteHaRecensito
        this.venditore=venditore
        this.votoAllAcquirente=votoAllAcquirente
        this.recensioneAllAcquirente=recensioneAllAcquirente
        this.venditoreHaRecensito=venditoreeHaRecensito
        this.idOggettoRecensito=idOggettoRecensito
    }

}