package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Money : RealmObject() {

    var listID :String = ""// T_List의 id의 외래키
    var dayNum: Int = 0 // T_Day의 id의 외래키

    var id : String = ""
    var priceType: String = ""
    var img: String = ""
    var price: Int = 0
    var cate: String = ""
    var date: String = ""

}