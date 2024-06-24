package com.swantosaurus.boredio.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.swantosaurus.boredio.android.navigation.MainNavigation
import com.swantosaurus.boredio.android.navigation.NavigationDestinations
import kotlinx.coroutines.flow.map


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                Scaffold(bottomBar = {
                    NavBar(navController = navController)
                }) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = padding.calculateBottomPadding(),
                                start = padding.calculateLeftPadding(LocalLayoutDirection.current),
                                end = padding.calculateRightPadding(LocalLayoutDirection.current),
                            )
                    ) {
                        MainNavigation(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun NavBar(navController: NavController) {
    NavigationBar {
        val currentRoute by navController.currentBackStackEntryFlow.map { it.destination.route }
            .collectAsState(
                initial = NavigationDestinations.DAY_FEED.route
            )
        NavigationDestinations.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(icon = {
                if (isSelected) {
                    Icon(
                        painter = painterResource(id = destination.bottomBarIconResSelected),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(id = destination.bottomBarIconResUnselected),
                        contentDescription = null
                    )
                }
            },
                label = {
                    Text(text = stringResource(id = destination.title))
                },
                selected = navController.currentDestination?.route == destination.route,
                onClick = {
                    navController.navigate(destination.route)
                })

        }
    }
}


//@Composable
//fun StartAnimation() {
//    Box(Modifier.fillMaxSize()) {
//    }
//}

/*
@Composable
fun ShowBored(onDone: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        with(LocalView.current) {
            val textHeight = with(LocalDensity.current) { 100.dp.toSp() }
            val ballAnimationProgression = Animatable(0f)


            val BoredPositionX = Animatable(-100f)


            LaunchedEffect(key1 = Unit) {
                delay(200)
                BoredPositionX.animateTo(width / 2f, animationSpec = tween(300))
                ballAnimationProgression.animateTo(1f, tween(2000, easing = LinearOutSlowInEasing))
                delay(100)
                delay(100)
                //animationState = AnimationPhases.Done
                onDone()
            }

            val textMeasurer = rememberTextMeasurer()
            val fontSize = with(LocalDensity.current) { 80.dp.toSp() }
            val style = MaterialTheme.typography.headlineLarge.copy(fontSize = fontSize)
            val assets = LocalContext.current.assets

            val grassTexture = remember { assets.open("grass_texture.jpg").use{
                BitmapFactory.decodeStream(it).asImageBitmap()
            }}

            Canvas(
                Modifier
                    .fillMaxSize()
                    .background(Color.Yellow)
            ) {
                val width = this.size.width
                val height = this.size.height


                val slowDownFactor = 4
                val dotStartingOffset = 600f
                val textY = height / 3f * 2f
                val textBottom = textY + fontSize.toPx() - 7.dp.toPx()

                val boredEndText = center.x + 180f

                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    shader = ImageShader(grassTexture, TileMode.Repeated, TileMode.Repeated)
                }

                drawIntoCanvas {
                    it.nativeCanvas.drawRect(0f, textBottom, width, height, paint)
                }

                val radius = 10.dp.toPx()
                val DotPositionY = textBottom - radius - (textY + dotStartingOffset) * abs(
                    sin(
                        slowDownFactor * Math.PI * ballAnimationProgression.value.toDouble()
                            .pow(2)
                    ) / (slowDownFactor * Math.PI * ballAnimationProgression.value.toDouble().pow(2)))


                drawCircle(
                    color = Color.Red,
                    center = Offset(boredEndText, DotPositionY.toFloat()),
                    radius = radius,
                )

                drawText(textMeasurer = textMeasurer, text = "Bored", style = style, topLeft = Offset(BoredPositionX.value - 150.dp.toPx(), textY))

                drawText(textMeasurer = textMeasurer, text = "io", style = style, topLeft = Offset(boredEndText + 10.dp.toPx(), textY))
            }
        }
    }
}
*/