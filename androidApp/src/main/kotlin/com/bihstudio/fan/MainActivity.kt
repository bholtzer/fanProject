package com.bihstudio.fan

import android.media.AudioManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.text.HtmlCompat
import androidx.activity.compose.setContent
import com.fansim.app.App
import com.fansim.app.presentation.fanselection.LegalDocument
import com.fansim.app.presentation.language.LanguageScreen
import com.fansim.app.di.AppContainer
import com.fansim.app.di.createSettings
import com.fansim.app.platform.PlatformContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        PlatformContext.appContext = applicationContext
        PlatformContext.currentActivity = this

        val container = AppContainer(createSettings())

        setContent {
            App(container, languageScreen = { close ->
                BackHandler(onBack = close)
                LanguageScreen(
                    selectedTag = AppCompatDelegate.getApplicationLocales().toLanguageTags(),
                    onBack = close,
                    onChoose = { tag ->
                        close()
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                    }
                )
            }, onOpenLegalDocument = ::openLegalDocument)
        }
    }

    private fun openLegalDocument(document: LegalDocument) {
        val resource = when (document) {
            LegalDocument.PrivacyPolicy -> R.raw.privacy_policy
            LegalDocument.TermsOfUse -> R.raw.terms_of_use
        }
        val html = resources.openRawResource(resource)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        val horizontalPadding = (24 * resources.displayMetrics.density).toInt()
        val content = TextView(this).apply {
            text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textSize = 16f
            setPadding(horizontalPadding, 0, horizontalPadding, 0)
            movementMethod = LinkMovementMethod.getInstance()
        }
        AlertDialog.Builder(this)
            .setTitle(document.title)
            .setView(ScrollView(this).apply { addView(content) })
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroy() {
        if (PlatformContext.currentActivity === this) {
            PlatformContext.currentActivity = null
        }
        super.onDestroy()
    }
}
