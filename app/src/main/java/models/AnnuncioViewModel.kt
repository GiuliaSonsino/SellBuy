package models

import android.graphics.Bitmap

/*
data class AnnuncioViewModel(val image: String, val text: String, val codice: String?) {
}*/
class AnnuncioViewModel() {

    var image: String? = null
    var text: String? = null
    var codice : String?=null



    constructor(image: String, text: String, codice:String) : this(){
        this.image=image
        this.text=text
        this.codice=codice

    }


}