package ph.edu.auf.emman.yalung.yalung_quiz3.models

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Category : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""

    val linkedExpenses: RealmResults<Expense> by backlinks(Expense::category)
}

class Expense : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var title: String = ""
    var amount: Double = 0.0
    var date: RealmInstant = RealmInstant.now()

    var category: Category? = null
}