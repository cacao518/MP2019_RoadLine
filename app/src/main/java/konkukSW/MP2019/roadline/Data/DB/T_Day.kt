package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Day : RealmObject() {

    var listID :String = "" // T_List의 id의 외래키

    var num: Int = 0
    var date: String = ""

}