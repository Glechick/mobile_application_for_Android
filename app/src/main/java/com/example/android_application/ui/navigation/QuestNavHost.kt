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
    const val STAGE = "stage/{stageId}"
    const val SCANNER = "scanner/{stageId}"

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
        composable(
            route = Routes.HOME,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            HomeScreen(
                onScenarioClick = { scenarioId ->
                    // передаём scenarioId — StageScreen сам найдёт первый этап
                    navController.navigate(Routes.stage(scenarioId))
                }
            )
        }

        composable(
            route = Routes.STAGE,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            // здесь stageId — это либо scenarioId (с главного), либо id этапа (из сканера)
            // различаем по договорённости: StageScreen сам решит, что делать
            val id = backStackEntry.arguments
                ?.getString("stageId")
                ?.toIntOrNull() ?: 1
            StageScreen(
                startId = id,
                onScanClick = { stageId ->
                    navController.navigate(Routes.scanner(stageId))
                },
                onScenarioFinished = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(
            route = Routes.SCANNER,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val stageId = backStackEntry.arguments
                ?.getString("stageId")
                ?.toIntOrNull() ?: 1
            ScannerScreen(
                stageId = stageId,
                onBack = { navController.popBackStack() },
                onStageCompleted = { nextStageId ->
                    if (nextStageId != null) {
                        // убираем текущий этап из стека и открываем следующий
                        navController.navigate(Routes.stage(nextStageId)) {
                            popUpTo(Routes.STAGE) { inclusive = true }
                        }
                    } else {
                        // последний этап — назад на главный
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    }
                }
            )
        }
    }
}