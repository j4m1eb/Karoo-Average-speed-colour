package com.j4m1eb.averagespeedcolour.screens


import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.j4m1eb.averagespeedcolour.KarooSystemServiceProvider
import com.j4m1eb.averagespeedcolour.R
import com.j4m1eb.averagespeedcolour.data.ConfigData
import com.j4m1eb.averagespeedcolour.managers.ConfigurationManager
import io.hammerhead.karooext.models.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import timber.log.Timber
import kotlin.math.roundToInt

private fun Double.roundValue(): Double {
    return (this * 10).roundToInt() / 10.0
}

@Composable
fun MainScreen(
    configManager: ConfigurationManager = koinInject(),
    karooSystem: KarooSystemServiceProvider = koinInject()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val isScrolledToBottom = scrollState.value == scrollState.maxValue

    val loadedConfig by produceState(initialValue = ConfigData.DEFAULT, key1 = configManager) {
        Timber.d("Starting to load initial config via produceState.")
        value = configManager.getConfigFlow().first()
        Timber.d("Initial config loaded: $value")
    }

    val karooDistanceUnit by produceState(initialValue = UserProfile.PreferredUnit.UnitType.IMPERIAL, key1 = karooSystem) {
        Timber.d("Starting to load Karoo distance units via produceState.")
        karooSystem.streamUserProfile()
            .collect { profile ->
                value = profile.preferredUnit.distance
                Timber.d("Karoo distance units loaded: $value")
            }
    }

    var originalConfig by remember { mutableStateOf(ConfigData.DEFAULT) }
    var currentConfig by remember { mutableStateOf(ConfigData.DEFAULT) }
    var saved by remember { mutableStateOf(false) }
    var speedMultiplier by remember { mutableDoubleStateOf(if (karooDistanceUnit == UserProfile.PreferredUnit.UnitType.IMPERIAL) 2.23694 else 3.6) }

    val configIsGood by remember(currentConfig) {
        derivedStateOf { currentConfig.validate() }
    }

    var targetSpeedInput by remember(loadedConfig, speedMultiplier) {
        mutableStateOf(loadedConfig.targetSpeed.times(speedMultiplier).roundValue().toString())
    }
    var targetSpeedError by remember { mutableStateOf(false) }

    LaunchedEffect(loadedConfig, karooDistanceUnit) {
        speedMultiplier = when (karooDistanceUnit) {
            UserProfile.PreferredUnit.UnitType.IMPERIAL -> 2.23694
            else -> 3.6
        }
        originalConfig = loadedConfig
        currentConfig = loadedConfig
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = context.getString(R.string.color_speed_settings_title),
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Target speed
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetSpeedInput,
                    onValueChange = { newValue ->
                        targetSpeedInput = newValue
                        val parsedValue = newValue.toDoubleOrNull()
                        if (parsedValue != null) {
                            currentConfig = currentConfig.copy(targetSpeed = parsedValue.div(speedMultiplier).roundValue())
                            targetSpeedError = false
                        } else {
                            targetSpeedError = true
                        }
                    },
                    suffix = {
                        Text(if (karooDistanceUnit == UserProfile.PreferredUnit.UnitType.IMPERIAL) "mph" else "kmh")
                    },
                    label = { Text(context.getString(R.string.target_speed)) },
                    placeholder = {
                        Text(loadedConfig.targetSpeed.times(speedMultiplier).roundValue().toString())
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = targetSpeedError,
                    supportingText = {
                        if (targetSpeedError) Text(context.getString(R.string.enter_valid_number))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teal colour toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = context.getString(R.string.use_teal),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Switch(
                    checked = currentConfig.useTeal,
                    onCheckedChange = { checked ->
                        currentConfig = currentConfig.copy(useTeal = checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show icons toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = context.getString(R.string.show_icons),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Switch(
                    checked = currentConfig.showIcons,
                    onCheckedChange = { checked ->
                        currentConfig = currentConfig.copy(showIcons = checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Swap rows toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = context.getString(R.string.swap_rows),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Switch(
                    checked = currentConfig.swapRows,
                    onCheckedChange = { checked ->
                        currentConfig = currentConfig.copy(swapRows = checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save button — turns green briefly after a successful save.
            FilledTonalButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                onClick = {
                    val configToSave = currentConfig
                    if (currentConfig.validate()) {
                        coroutineScope.launch {
                            Timber.d("Attempting to save config: $configToSave")
                            configManager.saveConfig(configToSave)
                            Timber.i("Configuration save initiated.")
                            originalConfig = configToSave
                            saved = true
                            delay(1500)
                            saved = false
                        }
                    } else {
                        Timber.w("Save blocked due to input validation errors.")
                    }
                },
                enabled = saved || (configIsGood && currentConfig != originalConfig),
                colors = if (saved) {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF1EFF00),
                        contentColor = Color.Black
                    )
                } else {
                    ButtonDefaults.filledTonalButtonColors()
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
                Spacer(modifier = Modifier.width(5.dp))
                Text(if (saved) context.getString(R.string.saved) else context.getString(R.string.save))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        if (isScrolledToBottom) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 10.dp)
                    .size(54.dp)
                    .clickable {
                        val activity = context as? ComponentActivity
                        activity?.onBackPressedDispatcher?.onBackPressed()
                    }
            )
        }
    }
}

@Preview(locale = "en", name = "karoo", device = "spec:width=480px,height=800px,dpi=300")
@Composable
private fun Preview_MyComposable_Enabled() {
    val context = LocalContext.current
    MainScreen(
        configManager = ConfigurationManager(context),
        karooSystem = KarooSystemServiceProvider(context)
    )
}
