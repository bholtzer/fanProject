package com.fansim.app.presentation.language

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import fansimulator.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

private val languages = listOf(
    "ar" to "العربية",
    "bn" to "বাংলা",
    "en" to "English",
    "en-AU" to "English (Australia)",
    "es" to "Español",
    "fr" to "Français",
    "hi" to "हिन्दी",
    "it" to "Italiano",
    "ja" to "日本語",
    "pt" to "Português",
    "ru" to "Русский",
    "uk" to "Українська",
    "ur" to "اردو",
    "zh" to "中文"
)

@Composable
fun LanguageScreen(selectedTag: String, onBack: () -> Unit, onChoose: (String) -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp)) {
        TextButton(onClick = onBack) { Text(stringResource(Res.string.back)) }
        Text(stringResource(Res.string.choose_language), style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 12.dp))
        val deviceLanguage = stringResource(Res.string.device_language)
        LazyColumn(Modifier.fillMaxWidth().selectableGroup()) {
            items(listOf("" to deviceLanguage) + languages, key = { it.first }) { (tag, label) ->
                val selected = tag.equals(selectedTag.substringBefore(','), ignoreCase = true)
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .selectable(selected, role = Role.RadioButton, onClick = { onChoose(tag) })
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected, onClick = null)
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
