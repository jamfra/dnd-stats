package com.dndstats.android.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dndstats.android.AppContainer
import com.dndstats.android.ui.characterform.CharacterFormScreen
import com.dndstats.android.ui.characterform.CharacterFormViewModel
import com.dndstats.android.ui.characterlist.CharacterListScreen
import com.dndstats.android.ui.characterlist.CharacterListViewModel
import com.dndstats.android.ui.charactersheet.CharacterSheetScreen
import com.dndstats.android.ui.charactersheet.CharacterSheetViewModel

private object Routes {
    const val CHARACTER_LIST = "characters"
    const val CHARACTER_SHEET = "characters/{id}"
    const val CHARACTER_CREATE = "characters/new"
    const val CHARACTER_EDIT = "characters/{id}/edit"
}

@Composable
fun DndStatsApp(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.CHARACTER_LIST) {

        composable(Routes.CHARACTER_LIST) {
            val viewModel: CharacterListViewModel = viewModel(
                factory = CharacterListViewModel.Factory(container.listCharacters, container.deleteCharacter)
            )
            CharacterListScreen(
                viewModel = viewModel,
                onNavigateToSheet = { id -> navController.navigate("characters/$id") },
                onNavigateToCreate = { navController.navigate("characters/new") },
            )
        }

        composable(
            route = Routes.CHARACTER_SHEET,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val viewModel: CharacterSheetViewModel = viewModel(
                factory = CharacterSheetViewModel.Factory(container.getCharacterSheet, id)
            )
            CharacterSheetScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("characters/$id/edit") },
            )
        }

        composable(Routes.CHARACTER_CREATE) {
            val viewModel: CharacterFormViewModel = viewModel(
                factory = CharacterFormViewModel.Factory(container.createCharacter, container.editCharacter, null)
            )
            CharacterFormScreen(
                viewModel = viewModel,
                onSuccess = { id ->
                    // Pop new-form, navigate to the newly created character's sheet
                    navController.popBackStack()
                    navController.navigate("characters/$id")
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.CHARACTER_EDIT,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val viewModel: CharacterFormViewModel = viewModel(
                factory = CharacterFormViewModel.Factory(
                    createCharacter = container.createCharacter,
                    editCharacter = container.editCharacter,
                    editId = id,
                )
            )
            // Pre-populate the form by loading the existing sheet before showing form
            CharacterFormScreen(
                viewModel = viewModel,
                onSuccess = {
                    // After edit, go back to the sheet
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
