package com.rmp.ui.hello

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.Header
import com.rmp.ui.components.ScreenStepVisualizer
import com.rmp.ui.components.SecondaryButton

@Preview
@Composable
private fun HelloStep(
    modifier: Modifier = Modifier,
) {
    val steps: List<HelloStepDescription> = HelloScreenStep.entries.map { getStepDescription(it) }
    val pagerState = rememberPagerState { steps.size }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            Modifier.padding(bottom = 100.dp).align(alignment = Alignment.CenterHorizontally)
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Header(
                    stringResource(steps[page].text),
                    modifier = Modifier
                        .padding(vertical = 50.dp)
                        .width(300.dp))
                Image(
                    painter = painterResource(steps[page].image),
                    "Image $page",
                    modifier = Modifier
                        .width(250.dp)
                )
            }
        }

        ScreenStepVisualizer(steps.size, pagerState.currentPage)
    }
}

@Composable
fun HelloScreen(
    uiState: HelloUiState,
    goToSignUp: () -> Unit,
    goToLogin: () -> Unit,
) {
    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HelloStep()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AccentButton(stringResource(R.string.go_to_sign_up), goToSignUp)
                    SecondaryButton(stringResource(R.string.go_to_login), goToLogin)
                }
            }
        }
    }
}
