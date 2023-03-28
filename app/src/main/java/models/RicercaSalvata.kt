package models

class RicercaSalvata() {

    var email: String = "*"
    var parolaDigitata: String = "*"
    var prezzo: String = "*"
    var spedizione: String = "*"
    var localizzazione : String? = null
    var elencoAnnunciTrovati: MutableList<Annuncio> = mutableListOf()

    constructor(email: String, parolaDigitata: String, prezzo: String, spedizione: String, localizzazione:String, elencoAnnunciTrovati: MutableList<Annuncio>) : this() {
        this.email = email
        this.parolaDigitata = parolaDigitata
        this.prezzo = prezzo
        this.spedizione = spedizione
        this.localizzazione = localizzazione
        this.elencoAnnunciTrovati = elencoAnnunciTrovati
    }

}