package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Photo : RealmObject() {

    var listNum :Int = 0 // T_List의 num의 외래키
    var dayNum: Int = 0 // T_Day의 num의 외래키

    var img: String = ""

}