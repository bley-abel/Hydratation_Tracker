package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.WaterLog
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkTealText
import com.example.ui.theme.PrimaryTurquoise
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayTotal by viewModel.todayTotalMl.collectAsState()
    val dailyGoal by viewModel.dailyGoalMl.collectAsState()
    val logs by viewModel.todayLogs.collectAsState()
    val isGoalAchieved by viewModel.isGoalAchieved.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state for resetting logs
    var showResetDialog by remember { mutableStateOf(false) }

    // Dialog state for editing daily goal
    var showGoalDialog by remember { mutableStateOf(false) }
    var goalInputText by remember { mutableStateOf(TextFieldValue(dailyGoal.toString())) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Custom Header design matching the exact HTML pattern
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water Icon",
                                tint = PrimaryTurquoise,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Hydratation",
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    // Right settings/editing button as outlined in HTML
                    IconButton(
                        onClick = {
                            goalInputText = TextFieldValue(dailyGoal.toString())
                            showGoalDialog = true
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = TextMuted
                        ),
                        modifier = Modifier
                            .testTag("edit_goal_button")
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier l'objectif",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: Progress Circle
            item {
                ProgressSection(
                    currentWaterMl = todayTotal,
                    goalWaterMl = dailyGoal,
                    isGoalAchieved = isGoalAchieved
                )
            }

            // Section 2: Quick Stats Card (Restant / Dernier)
            item {
                val lastLogTime = logs.firstOrNull()?.formattedTime ?: "--:--"
                QuickStatsCard(
                    currentWaterMl = todayTotal,
                    goalWaterMl = dailyGoal,
                    lastLogTime = lastLogTime
                )
            }

            // Section 3: Footer Action Buttons (Reset and +250ml)
            item {
                FooterActionsRow(
                    onAddWater = { amount ->
                        viewModel.addWater(amount)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "+${amount}ml d'eau enregistré !"
                            )
                        }
                    },
                    onResetClick = {
                        if (logs.isNotEmpty()) {
                            showResetDialog = true
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Aucun enregistrement à réinitialiser."
                                )
                            }
                        }
                    }
                )
            }

            // Section 4: Preset options chips
            item {
                PresetsBar(onAddWater = { amount ->
                    viewModel.addWater(amount)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "+${amount}ml d'eau enregistré !"
                        )
                    }
                })
            }

            // Section 5: Today's History Logs Title
            item {
                Text(
                    text = "Historique d'aujourd'hui",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            if (logs.isEmpty()) {
                item {
                    EmptyHistoryState()
                }
            } else {
                items(logs, key = { it.id }) { log ->
                    HistoryItem(
                        log = log,
                        onDeleteClick = {
                            viewModel.deleteLog(log)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Entrée de ${log.amountMl}ml supprimée."
                                )
                            }
                        }
                    )
                }
            }

            // Bottom Navigation Indicator style from design HTML
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(128.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }

    // Goal Editing Dialog
    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(
                    text = "Modifier l'objectif",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Entrez votre nouvel objectif quotidien en ml (ex: 2000 pour 2L)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = goalInputText,
                        onValueChange = { goalInputText = it },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryTurquoise,
                            unfocusedBorderColor = TextMuted,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = PrimaryTurquoise
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_input_field")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val inputVal = goalInputText.text.toIntOrNull()
                        if (inputVal != null && inputVal > 0) {
                            viewModel.updateDailyGoal(inputVal)
                            showGoalDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Objectif mis à jour à ${inputVal}ml !")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Veuillez entrer une valeur valide supérieure à 0.")
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryTurquoise),
                    modifier = Modifier.testTag("confirm_goal_button")
                ) {
                    Text("Enregistrer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showGoalDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                ) {
                    Text("Annuler")
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Tout réinitialiser ?",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Cette action va effacer tout votre historique d'hydratation d'aujourd'hui. Êtes-vous sûr ?",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToday()
                        showResetDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Historique réinitialisé.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Oui, Réinitialiser", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetDialog = false },
                    border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.4f))
                ) {
                    Text("Annuler", color = TextWhite)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProgressSection(
    currentWaterMl: Int,
    goalWaterMl: Int,
    isGoalAchieved: Boolean
) {
    val progressPercent = if (goalWaterMl > 0) {
        (currentWaterMl.toFloat() / goalWaterMl.toFloat() * 100).toInt()
    } else {
        0
    }

    val progressFraction = if (goalWaterMl > 0) {
        (currentWaterMl.toFloat() / goalWaterMl.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgressFraction by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "WaterCircleProgress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Vibrant progress indicator circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
        ) {
            // Background Track Circle (gray-800)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                drawCircle(
                    color = Color(0xFF1F2937),
                    radius = radius,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Foreground progress stroke
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val sizeValue = androidx.compose.ui.geometry.Size(diameter, diameter)
                val offset = (size.minDimension - diameter) / 2

                drawArc(
                    color = PrimaryTurquoise,
                    startAngle = -90f,
                    sweepAngle = animatedProgressFraction * 360f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(offset, offset),
                    size = sizeValue,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Central metrics matching HTML
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$currentWaterMl",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 54.sp
                )
                Text(
                    text = "ml / ${goalWaterMl}ml",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryTurquoise.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryTurquoise
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Achieved or progress message
        AnimatedVisibility(
            visible = isGoalAchieved,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryTurquoise.copy(alpha = 0.1f))
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Félicitations",
                    tint = PrimaryTurquoise,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Super ! Objectif atteint 🎉",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTurquoise,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun QuickStatsCard(
    currentWaterMl: Int,
    goalWaterMl: Int,
    lastLogTime: String
) {
    val remaining = (goalWaterMl - currentWaterMl).coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(32.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Remaining Stat
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "RESTANT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$remaining ml",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Divider matching HTML design
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(Color(0xFF1F2937))
            )

            // Last recorded intake time
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "DERNIER",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lastLogTime,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FooterActionsRow(
    onAddWater: (Int) -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset Button
        OutlinedButton(
            onClick = onResetClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .testTag("reset_tracker_button"),
            border = BorderStroke(1.dp, Color(0xFF374151)),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFD1D5DB)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Réinitialiser",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Réinitialiser",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        }

        // Action Add 250ml Button (Large FAB style)
        Button(
            onClick = { onAddWater(250) },
            modifier = Modifier
                .weight(1.5f)
                .height(56.dp)
                .testTag("add_250ml_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryTurquoise,
                contentColor = DarkTealText
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "250 ml",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun PresetsBar(
    onAddWater: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Raccourcis :",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryTurquoise.copy(alpha = 0.08f))
                .border(1.dp, PrimaryTurquoise.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .clickable { onAddWater(100) }
                .padding(vertical = 10.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+100 ml",
                color = PrimaryTurquoise,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryTurquoise.copy(alpha = 0.08f))
                .border(1.dp, PrimaryTurquoise.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .clickable { onAddWater(500) }
                .padding(vertical = 10.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+500 ml",
                color = PrimaryTurquoise,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun EmptyHistoryState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = TextMuted.copy(alpha = 0.3f),
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Aucune boisson enregistrée aujourd'hui",
                style = MaterialTheme.typography.bodyMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Restez hydraté ! Touchez les raccourcis ou le bouton +250ml.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun HistoryItem(
    log: WaterLog,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryTurquoise.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = PrimaryTurquoise,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Verre d'eau",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = log.formattedTime,
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${log.amountMl} ml",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTurquoise,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDeleteClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = TextMuted.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("delete_log_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
