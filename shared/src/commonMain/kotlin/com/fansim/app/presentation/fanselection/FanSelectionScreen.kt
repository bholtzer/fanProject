package com.fansim.app.presentation.fanselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import com.fansim.app.domain.model.FanType
import com.fansim.app.presentation.components.FanBladesShape

@Composable
fun FanSelectionScreen(
    viewModel: FanSelectionViewModel = remember { FanSelectionViewModel() },
    onFanChosen: (FanType) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Choose your fan",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "From a hand-pulled antique to the latest digital model.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.fanTypes) { fan ->
                val isSelected = state.selected?.id == fan.id
                Card(
                    onClick = { viewModel.select(fan) },
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Canvas(modifier = Modifier.size(64.dp)) {
                            FanBladesShape.draw(this, bladeCount = fan.bladeCount.takeIf { it > 0 } ?: 4, color = Color(0xFF78909C))
                        }
                        Text(
                            text = fan.displayName,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = fan.era.name,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
            Button(
                onClick = { state.selected?.let(onFanChosen) },
                enabled = state.selected != null,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Next")
            }
        }
    }
}
