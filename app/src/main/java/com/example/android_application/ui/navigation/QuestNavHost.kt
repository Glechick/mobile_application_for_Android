package com.example.android_application.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.android_application.ui.home.HomeScreen
import com.example.android_application.ui.scanner.ScannerScreen
import com.example.android_application.ui.stage.StageScreen

object Routes {
    const val HOME = "home"
    const val SCENARIO = "scenario/{scenarioId}"
    const val STAGE = "stage/{stageId}"
    const val SCANNER = "scanner/{stageId}"

    fun scenario(scenarioId: Int) = "scenario/$scenarioId"
    fun stage(stageId: Int) = "stage/$stageId"
    fun scanner(stageId: Int) = "scanner/$stageId"
}

@Composable
fun QuestNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onScenarioClick = { scenarioId ->
                    // с главного всегда идём как SCENARIO
                    navController.navigate(Routes.scenario(scenarioId))
                }
            )
        }

        // Переход с главного: знаем только scenarioId
        composable(Routes.SCENARIO) { entry ->
            val scenarioId = entry.arguments?.getString("scenarioId")?.toIntOrNull() ?: 1
            StageScreen(
                scenarioId = scenarioId,
                stageId = null,
                onScanClick = { stageId ->
                    navController.navigate(Routes.scanner(stageId))
                },
                onScenarioFinished = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        // Переход из сканера: знаем конкретный stageId
        composable(Routes.STAGE) { entry ->
            val stageId = entry.arguments?.getString("stageId")?.toIntOrNull() ?: 1
            StageScreen(
                scenarioId = null,
                stageId = stageId,
                onScanClick = { id ->
                    navController.navigate(Routes.scanner(id))
                },
                onScenarioFinished = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(Routes.SCANNER) { entry ->
            val stageId = entry.arguments?.getString("stageId")?.toIntOrNull() ?: 1
            ScannerScreen(
                stageId = stageId,
                onBack = { navController.popBackStack() },
                onStageCompleted = { nextStageId ->
                    if (nextStageId != null) {
                        // убираем текущий этап и открываем следующий как STAGE
                        navController.navigate(Routes.stage(nextStageId)) {
                            popUpTo(Routes.SCANNER) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    }
                }
            )
        }
    }
}