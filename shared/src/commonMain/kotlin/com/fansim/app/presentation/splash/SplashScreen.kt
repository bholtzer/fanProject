package com.fansim.app.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansim.app.domain.model.FanCatalog
import com.fansim.app.presentation.fanselection.FanProductImage
import kotlinx.coroutines.delay
import fansimulator.shared.generated.resources.Res
import fansimulator.shared.generated.resources.app_name
import fansimulator.shared.generated.resources.tagline
import org.jetbrains.compose.resources.stringResource

/** Branded opening screen using the current product image. */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

    val pedestalFan = FanCatalog.ALL.first { it.id == 6 }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF103C49), Color(0xFF176A79), Color(0xFFB8E9E5)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier.size(252.dp).background(Color.White.copy(alpha = .16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(218.dp).background(Color.White.copy(alpha = .90f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    FanProductImage(
                        fan = pedestalFan,
                        modifier = Modifier.size(178.dp)
                    )
                }
            }
            Text(
                text = stringResource(Res.string.app_name),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 28.dp)
            )
            Text(
                text = stringResource(Res.string.tagline),
                color = Color(0xFFE1FAF7),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
