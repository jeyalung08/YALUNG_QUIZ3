package ph.edu.auf.emman.yalung.yalung_quiz3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ph.edu.auf.emman.yalung.yalung_quiz3.MainViewModel
import ph.edu.auf.emman.yalung.yalung_quiz3.models.Expense
import ph.edu.auf.emman.yalung.yalung_quiz3.util.formatCurrency
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import io.realm.kotlin.types.RealmInstant


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: MainViewModel,
    categoryId: ObjectId,
    categoryName: String,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(categoryId) {
        viewModel.observeExpensesByCategory(categoryId)
    }

    val expenseItems by viewModel.expensesForCategory.collectAsState()
    var isExpenseDialogVisible by remember { mutableStateOf(false) }
    var editableExpense by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = categoryName,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                color = Color.White
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editableExpense = null
                isExpenseDialogVisible = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (expenseItems.isEmpty()) {
                item {
                    Text(
                        text = "No expenses recorded.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(expenseItems, key = { it._id.toHexString() }) { expense ->
                ExpenseCard(
                    expense = expense,
                    onEdit = {
                        editableExpense = expense
                        isExpenseDialogVisible = true
                    },
                    onDelete = {
                        viewModel.removeExpense(expense)
                    }
                )
            }
        }
    }

    if (isExpenseDialogVisible) {
        ExpenseDialog(
            expense = editableExpense,
            onDismiss = { isExpenseDialogVisible = false },
            onConfirm = { title, amount ->
                if (editableExpense == null) {
                    viewModel.addNewExpense(title, amount, categoryId)
                } else {
                    viewModel.modifyExpense(editableExpense!!, title, amount)
                }
                isExpenseDialogVisible = false
            }
        )
    }
}

@Composable
fun ExpenseCard(
    expense: Expense,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = expense.date.toDate().formatDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatCurrency(expense.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ExpenseDialog(
    expense: Expense?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var expenseTitle by remember { mutableStateOf(expense?.title ?: "") }
    var expenseAmount by remember { mutableStateOf(expense?.amount?.toString() ?: "") }

    val dialogTitle = if (expense == null) "Add Expense" else "Edit Expense"
    val confirmText = if (expense == null) "Add" else "Update"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                OutlinedTextField(
                    value = expenseTitle,
                    onValueChange = { expenseTitle = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = expenseAmount,
                    onValueChange = { expenseAmount = it },
                    label = { Text("Amount") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(expenseTitle, expenseAmount) }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun RealmInstant.toDate(): Date {
    return Date(this.epochSeconds * 1000 + this.nanosecondsOfSecond / 1_000_000)
}

fun Date.formatDate(): String {
    return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault()).format(this)
}
