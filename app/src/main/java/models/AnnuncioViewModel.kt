package models

class AnnuncioViewModel() {

    var image: String? = null
    var text: String? = null
    var price: String? = null
    var codice : String?=null


    constructor(image: String, text: String, price: String, codice:String) : this(){
        this.image=image
        this.text=text
        this.price=price
        this.codice=codice
    }

}