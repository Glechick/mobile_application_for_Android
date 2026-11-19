package com.example.android_application.ui.stage

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android_application.ui.hints.LlmUiState
import com.example.android_application.ui.voice.VoiceUiState
import com.example.android_application.ui.voice.VoiceViewModel
import kotlinx.coroutines.delay
import com.example.android_application.ui.hints.HintViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageScreen(
    scenarioId: Int? = null,
    stageId: Int? = null,
    onScanClick: (stageId: Int) -> Unit = {},
    onScenarioFinished: () -> Unit = {},
    onStageCompleted: (nextStageId: Int?) -> Unit = {},
    viewModel: StageViewModel = hiltViewModel(),
    hintViewModel: HintViewModel = hiltViewModel(),
    voiceViewModel: VoiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hintState by hintViewModel.state.collectAsState()
    val voiceState by voiceViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(scenarioId, stageId) {
        when {
            stageId != null -> viewModel.loadStageById(stageId)
            scenarioId != null -> viewModel.loadFirstStage(scenarioId)
        }
    }

    // Автопереход при верном голосовом ответе
    LaunchedEffect(voiceState) {
        val state = voiceState
        if (state is VoiceUiState.Success && state.isCorrect) {
            // Ждем 2.5 секунды, чтобы пользователь услышал вердикт от TTS
            delay(2500)
            onStageCompleted(state.nextStageId)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val stage = uiState.stage
            if (stage != null) {
                voiceViewModel.startListening(stage.id, stage.voiceAnswer)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.stage != null && uiState.totalStages > 0) {
                        Text("Этап ${uiState.stageNumber} из ${uiState.totalStages}")
                    } else {
                        Text("Этап")
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onScenarioFinished) {
                        Text("К списку")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.stage != null -> {
                val stage = uiState.stage!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- ЗАГАДКА ---
                    SectionTitle("Загадка")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(text = stage.riddle, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }

                    // --- ГОЛОСОВОЙ ВВОД ---
                    SectionTitle("Голосовой ответ")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (val vState = voiceState) {
                                is VoiceUiState.Idle -> {
                                    Button(
                                        onClick = {
                                            val hasPermission = ContextCompat.checkSelfPermission(
                                                context, Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED

                                            if (hasPermission) {
                                                voiceViewModel.startListening(stage.id, stage.voiceAnswer)
                                            } else {
                                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("🎤 Ответить голосом")
                                    }
                                }
                                is VoiceUiState.Listening -> {
                                    CircularProgressIndicator(modifier = Modifier.padding(bottom = 8.dp))
                                    Text("Слушаю...")
                                    OutlinedButton(onClick = { voiceViewModel.stopListening() }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Остановить запись")
                                    }
                                }
                                is VoiceUiState.Processing -> {
                                    CircularProgressIndicator(modifier = Modifier.padding(bottom = 8.dp))
                                    Text("Обработка ответа...")
                                }
                                is VoiceUiState.Success -> {
                                    Text("Вы сказали: \"${vState.recognizedText}\"", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = vState.verdict,
                                        color = if (vState.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    if (!vState.isCorrect) {
                                        Button(onClick = { voiceViewModel.reset() }, modifier = Modifier.fillMaxWidth()) {
                                            Text("Сказать еще раз")
                                        }
                                    }
                                }
                                is VoiceUiState.Error -> {
                                    Text("Ошибка: ${vState.message}", color = MaterialTheme.colorScheme.error)
                                    Button(onClick = { voiceViewModel.reset() }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Попробовать снова")
                                    }
                                }
                            }
                        }
                    }

                    // --- ИИ-ПОДСКАЗКИ ---
                    SectionTitle("Умные подсказки (ИИ)")
                    Button(
                        onClick = { hintViewModel.generateHints(stage.riddle) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("✨ Сгенерировать ИИ-подсказки")
                    }

                    when (val state = hintState) {
                        is LlmUiState.LoadingModel, is LlmUiState.Generating -> {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(Modifier.height(8.dp))
                                    Text(if (state is LlmUiState.LoadingModel) "Загрузка модели..." else "Генерация...", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        is LlmUiState.Success -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("ИИ-Подсказки:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(8.dp))
                                    state.hints.forEachIndexed { index, hint ->
                                        Text("${index + 1}. $hint", modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    // КНОПКА ПЕРЕГЕНЕРАЦИИ
                                    OutlinedButton(
                                        onClick = { hintViewModel.generateHints(stage.riddle, forceRefresh = true) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("🔄 Перегенерировать подсказки")
                                    }
                                }
                            }
                        }
                        is LlmUiState.Error -> {
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                                Text("Ошибка ИИ: ${state.message}", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                        else -> {}
                    }

                    // --- ОБЫЧНЫЕ ПОДСКАЗКИ ---
                    SectionTitle("Подсказки (по требованию)")
                    stage.hints.forEachIndexed { index, hint ->
                        if (index in uiState.revealedHints) {
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(hint, modifier = Modifier.weight(1f))
                                    Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            OutlinedButton(onClick = { viewModel.revealHint(index) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Показать подсказку ${index + 1}")
                            }
                        }
                    }

                    // --- УСЛОВИЕ ПЕРЕХОДА ---
                    SectionTitle("Условие перехода")
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Text(text = stage.transitionCondition, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(Modifier.height(8.dp))

                    // --- СКАНЕР / ЗАВЕРШЕНИЕ ---
                    if (uiState.isScenarioCompleted) {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Text("Сценарий пройден", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(onClick = onScenarioFinished, modifier = Modifier.fillMaxWidth()) {
                            Text("На главный экран")
                        }
                    } else if (uiState.isStageCompleted) {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Text("Этот этап пройден", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(onClick = { onScanClick(stage.id) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Сканировать QR-код")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}