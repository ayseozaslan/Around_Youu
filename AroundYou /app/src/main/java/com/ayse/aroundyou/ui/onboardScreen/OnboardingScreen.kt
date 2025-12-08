package com.ayse.aroundyou.ui.onboardScreen

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayse.aroundyou.R
import com.ayse.aroundyou.data.preferences.PreferencesManager
import kotlinx.coroutines.launch
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    navController: NavController,
    backgroundColor: Color = colorResource(id = R.color.white),
    permissionLauncherOnboarding: ActivityResultLauncher<Array<String>> // MainActivity’den gelen launcher
) {
    // 🟢 İlk açılışta izin iste
    LaunchedEffect(Unit) {
        permissionLauncherOnboarding.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
            )
        )
    }

    val pages: List<OnboardingModel> = listOf(
        OnboardingModel.FirstPage,
        OnboardingModel.SecondPage,
        OnboardingModel.ThirdPage,
        OnboardingModel.FourPage,
        OnboardingModel.FivePage
    )

    val pagerState = rememberPagerState(initialPage = 0) {
        pages.size
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = backgroundColor,

        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 10.dp, bottom = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ◼️ Back butonu
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (pagerState.currentPage > 0) {
                        ButtonUI(
                            text = stringResource(id = R.string.onboarding_back),
                            backgroundColor = Color.Transparent,
                            textColor = Color.Gray
                        ) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }
                }

                // ◼️ Sayfa indicator
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
                }

                // ◼️ İleri / Başla butonu
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    val nextText = if (pagerState.currentPage < pages.size - 1) {
                        stringResource(id = R.string.onboarding_next)
                    } else {
                        stringResource(id = R.string.onboarding_start)
                    }
                    val context = LocalContext.current
                    val preferencesManager = remember { PreferencesManager(context) }
                    ButtonUI(
                        text = nextText,
                        backgroundColor = colorResource(id = R.color.lightBlue),
                        textColor = Color.White
                    ) {
                        scope.launch {

                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                // 🟢 "Başla" basılınca onboarding bitti
                                onFinished()
                               // Başla → onboarding bitti
                                preferencesManager.setOnboardingCompleted()

                                navController.navigate("loginScreen") {
                                    popUpTo("onboardingScreen") { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        },

        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                HorizontalPager(state = pagerState) { index ->
                    OnboardingGraphUI(pages[index])
                }
            }
        }
    )
}


/*
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_NO // ☀️ Koyu tema kapalı
)

@Composable
fun OnboardingsScreenPreview() {
    OnboardingScreen(onFinished = {}, navController = NavController(context = LocalContext.current))
}

 */
