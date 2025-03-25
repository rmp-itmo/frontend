package com.rmp.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SpinningCirclesLoader(
    modifier: Modifier = Modifier,
    circleColors: List<Color> = listOf(Color.Red, Color.Green, Color.Blue),
    spec: DurationBasedAnimationSpec<Float> = tween(800, easing = FastOutLinearInEasing)
) {
    val infiniteTransition = rememberInfiniteTransition("SpinningCirclesTransition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = spec,
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinningCirclesRotation"
    )

    Box(modifier) {
        circleColors.forEachIndexed { index, color ->
            Canvas(
                modifier = Modifier
                    .offset(x = 25.dp, y = 25.dp)
                    .rotate(rotation + (index * 120f))
            ) {
                drawCircle(color = color, radius = 25f)
            }
        }
    }
}