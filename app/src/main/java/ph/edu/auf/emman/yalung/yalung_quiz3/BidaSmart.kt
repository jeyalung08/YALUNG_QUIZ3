package ph.edu.auf.emman.yalung.yalung_quiz3

import android.app.Application
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import ph.edu.auf.emman.yalung.yalung_quiz3.models.Category
import ph.edu.auf.emman.yalung.yalung_quiz3.models.Expense

class BidaSmart : Application() {

    companion object {
        lateinit var realm: Realm
            private set
    }

    override fun onCreate() {
        super.onCreate()

        val config = RealmConfiguration.Builder(
            schema = setOf(Category::class, Expense::class)
        )
            .name("bida_expense.realm")
            .schemaVersion(1)
            .build()

        realm = Realm.open(config)
    }
}