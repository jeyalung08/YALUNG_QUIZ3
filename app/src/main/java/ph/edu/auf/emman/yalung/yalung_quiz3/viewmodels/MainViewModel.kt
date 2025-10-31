package ph.edu.auf.emman.yalung.yalung_quiz3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ph.edu.auf.emman.yalung.yalung_quiz3.models.Category
import ph.edu.auf.emman.yalung.yalung_quiz3.models.Expense
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

data class CategoryUi(
    val category: Category,
    val totalAmount: Double = 0.0
)

class MainViewModel : ViewModel() {

    private val realm: Realm = BidaSmart.realm

    private val _categoryList = MutableStateFlow<List<CategoryUi>>(emptyList())
    val categoryList = _categoryList.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses = _totalExpenses.asStateFlow()

    private val _expensesForCategory = MutableStateFlow<List<Expense>>(emptyList())
    val expensesForCategory = _expensesForCategory.asStateFlow()

    init {
        observeCategoryTotals()
        observeGlobalTotal()
    }

    private fun observeGlobalTotal() {
        viewModelScope.launch {
            realm.query<Expense>().asFlow()
                .map { expenseResults ->
                    expenseResults.list.sumOf { it.amount }
                }
                .collect { total ->
                    _totalExpenses.value = total
                }
        }
    }

    private fun observeCategoryTotals() {
        viewModelScope.launch {
            val categoryFlow = realm.query<Category>().asFlow()
            val expenseFlow = realm.query<Expense>().asFlow()

            categoryFlow.combine(expenseFlow) { categoryResults, expenseResults ->
                val allCategories = categoryResults.list
                val allExpenses = expenseResults.list

                allCategories.map { category ->
                    val categoryTotal = allExpenses
                        .filter { it.category?._id == category._id }
                        .sumOf { it.amount }

                    CategoryUi(category = category, totalAmount = categoryTotal)
                }
            }.collect { _categoryList.value = it }
        }
    }

    fun addNewCategory(categoryName: String) {
        if (categoryName.isBlank()) return
        viewModelScope.launch {
            realm.write {
                copyToRealm(Category().apply {
                    name = categoryName
                })
            }
        }
    }

    fun removeCategory(category: Category) {
        viewModelScope.launch {
            realm.write {
                val latestCategory = findLatest(category)
                latestCategory?.let {
                    val relatedExpenses = query<Expense>("category._id == $0", it._id).find()
                    relatedExpenses.forEach { expense -> delete(expense) }
                    delete(it)
                }
            }
        }
    }

    fun observeExpensesByCategory(categoryId: ObjectId) {
        viewModelScope.launch {
            realm.query<Expense>("category._id == $0", categoryId)
                .sort("date", Sort.DESCENDING)
                .asFlow()
                .map { it.list }
                .collect { _expensesForCategory.value = it }
        }
    }

    fun addNewExpense(
        expenseTitle: String,
        expenseAmountStr: String,
        categoryId: ObjectId
    ) {
        val expenseAmount = expenseAmountStr.toDoubleOrNull()
        if (expenseTitle.isBlank() || expenseAmount == null || expenseAmount <= 0) return

        viewModelScope.launch {
            realm.write {
                val category = query<Category>("_id == $0", categoryId).first().find()
                category?.let {
                    val newExpense = Expense().apply {
                        title = expenseTitle
                        amount = expenseAmount
                        this.category = it
                    }
                    copyToRealm(newExpense)
                }
            }
        }
    }

    fun removeExpense(expense: Expense) {
        viewModelScope.launch {
            realm.write {
                findLatest(expense)?.let { delete(it) }
            }
        }
    }

    fun modifyExpense(expense: Expense, updatedTitle: String, updatedAmountStr: String) {
        val updatedAmount = updatedAmountStr.toDoubleOrNull()
        if (updatedTitle.isBlank() || updatedAmount == null || updatedAmount <= 0) return

        viewModelScope.launch {
            realm.write {
                findLatest(expense)?.apply {
                    title = updatedTitle
                    amount = updatedAmount
                }
            }
        }
    }
}