package ph.edu.auf.emman.yalung.yalung_quiz3.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import io.realm.kotlin.types.RealmInstant

fun formatCurrency(amount: Double): String {
    val locale = Locale("en", "PH")
    val format = NumberFormat.getCurrencyInstance(locale)
    return format.format(amount)
}

fun RealmInstant.toDate(): Date {
    return Date(this.epochSeconds * 1000 + this.nanosecondsOfSecond / 1_000_000)
}

fun Date.formatDate(): String {
    return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault()).format(this)
}