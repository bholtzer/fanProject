package com.fansim.app.presentation.fanselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fansim.app.domain.model.FanEra
import com.fansim.app.presentation.localizedName
import com.fansim.app.domain.model.FanType

enum class LegalDocument(val title: String) {
    PrivacyPolicy("Privacy Policy"),
    TermsOfUse("Terms of Use")
}

@Composable
fun FanSelectionScreen(
    viewModel: FanSelectionViewModel = remember { FanSelectionViewModel() },
    onOpenLanguage: (() -> Unit)? = null,
    onOpenLegalDocument: ((LegalDocument) -> Unit)? = null,
    onFanChosen: (FanType) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val palette = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.personal_breeze), style = MaterialTheme.typography.labelMedium,
                color = palette.primary, modifier = Modifier.weight(1f))
            if (onOpenLanguage != null) {
                TextButton(onClick = onOpenLanguage) { Text(stringResource(Res.string.language)) }
            }
        }
        Text(stringResource(Res.string.find_fan), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(stringResource(Res.string.explore_designs), style = MaterialTheme.typography.bodyMedium,
            color = palette.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp, bottom = 16.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = state.era == null, onClick = { viewModel.filter(null) }, label = { Text(stringResource(Res.string.all_fans)) })
            FanEra.entries.forEach { era ->
                FilterChip(selected = state.era == era, onClick = { viewModel.filter(era) },
                    label = { Text(era.localizedName()) })
            }
        }
        val fans = state.fanTypes.filter { state.era == null || it.era == state.era }
        Text(stringResource(Res.string.design_count, fans.size), style = MaterialTheme.typography.labelMedium,
            color = palette.onSurfaceVariant, modifier = Modifier.padding(vertical = 12.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(148.dp), modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(fans, key = { it.id }) { fan ->
                val selected = state.selected?.id == fan.id
                val finish = Color(com.fansim.app.domain.model.FanColorCatalog.originalFor(fan).hex)
                Card(onClick = { viewModel.select(fan) }, shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) palette.primary else palette.surfaceVariant),
                    colors = CardDefaults.cardColors(containerColor = palette.surface)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Box(Modifier.fillMaxWidth().height(150.dp).background(
                            Brush.verticalGradient(listOf(
                                lerp(finish, Color.White, if (selected) .70f else .86f),
                                lerp(finish, Color.White, if (selected) .35f else .52f)
                            )), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center) {
                            FanProductImage(fan, Modifier.size(138.dp))
                            if (selected) Surface(color = palette.primary, shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                                Text(stringResource(Res.string.selected), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(5.dp))
                            }
                        }
                        Text(fan.era.localizedName(),
                            style = MaterialTheme.typography.labelSmall, color = palette.primary,
                            modifier = Modifier.padding(top = 12.dp))
                        Text(fan.localizedName(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
        Surface(color = palette.background) {
            Column(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Text(state.selected?.localizedName() ?: stringResource(Res.string.select_fan),
                    style = MaterialTheme.typography.bodySmall, color = palette.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = { state.selected?.let(onFanChosen) }, enabled = state.selected != null,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text(stringResource(Res.string.customize_fan)) }
                if (onOpenLegalDocument != null) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        LegalDocument.entries.forEach { document ->
                            TextButton(onClick = { onOpenLegalDocument(document) }) {
                                Text(document.title, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
