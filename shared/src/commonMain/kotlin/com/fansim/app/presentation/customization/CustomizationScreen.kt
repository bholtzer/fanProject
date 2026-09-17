package com.fansim.app.presentation.customization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansim.app.domain.model.FanConfig
import com.fansim.app.domain.model.FanType
import com.fansim.app.presentation.components.FanBladesShape

@Composable
fun CustomizationScreen(
    fanType: FanType,
    viewModel: CustomizationViewModel = remember(fanType) { CustomizationViewModel(fanType) },
    onDone: (FanConfig) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Make it yours", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
        Text(
            text = "Pick a color and give your ${fanType.displayName} a name.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(120.dp)) {
                FanBladesShape.draw(this, bladeCount = fanType.bladeCount.takeIf { it > 0 } ?: 4, color = Color(state.selectedColor.hex))
            }
        }

        Text("Color", fontSize = 16.sp, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.colors.forEach { color ->
                val isSelected = color == state.selectedColor
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(color.hex), CircleShape)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { viewModel.selectColor(color) }
                )
            }
        }

        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::updateName,
            label = { Text("Fan name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        )

        Button(
            onClick = { onDone(viewModel.buildConfig()) },
            enabled = state.canContinue,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Start using my fan")
        }
    }
}
