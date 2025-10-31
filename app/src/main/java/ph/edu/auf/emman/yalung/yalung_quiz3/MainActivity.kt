package ph.edu.auf.emman.yalung.yalung_quiz3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.mongodb.kbson.ObjectId
import ph.edu.auf.emman.yalung.yalung_quiz3.ui.screens.CategoryScreen
import ph.edu.auf.emman.yalung.yalung_quiz3.ui.screens.ExpenseScreen
import ph.edu.auf.emman.yalung.yalung_quiz3.ui.theme.EarthToneTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EarthToneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val sharedViewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "categoryList") {
        composable("categoryList") {
            CategoryScreen(
                viewModel = sharedViewModel,
                onCategorySelected = { category ->
                    navController.navigate("expenseList/${category._id.toHexString()}/${category.name}")
                }
            )
        }
        composable(
            route = "expenseList/{categoryId}/{categoryName}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryIdStr = backStackEntry.arguments?.getString("categoryId")
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Expenses"
            categoryIdStr?.let {
                ExpenseScreen(
                    viewModel = sharedViewModel,
                    categoryId = ObjectId(it),
                    categoryName = categoryName,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}