package com.jetpack.beziercurves

import android.graphics.PathMeasure
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jetpack.beziercurves.ui.theme.BezierCurvesTheme
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BezierCurvesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Bezier Curves",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        BezierCurves()
                    }
                }
            }
        }
    }
}

@Composable
fun BezierCurves() {
    var lines by remember { mutableStateOf(50) }
    var canvasCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var radius by remember { mutableStateOf(0f) }
    var firstHandleCenter by remember { mutableStateOf(Offset(0f, 0f)) }
    var secondHandleCenter by remember { mutableStateOf(Offset(00f, 0f)) }
    var firstCurveCenter by remember { mutableStateOf(Offset(00f, 0f)) }
    var secondCurveCenter by remember { mutableStateOf(Offset(00f, 0f)) }
    var path by remember { mutableStateOf(Path()) }

    LaunchedEffect(key1 = canvasCenter) {
        firstHandleCenter = Offset(0f, canvasCenter.y)
        secondHandleCenter = Offset(canvasCenter.x * 2, canvasCenter.y)
        firstCurveCenter = Offset(canvasCenter.x / 2, canvasCenter.y / 2)
        secondCurveCenter = Offset((canvasCenter.x * 4) / 3, (canvasCenter.y * 4) / 3)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(50.dp)
    ) {
        Canvas(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .pointerInput(true) {
                detectDragGestures { change, _ ->
                    when {
                        firstHandleCenter.isTouched(change.previousPosition, radius) ->
                            firstHandleCenter = change.position
                        secondHandleCenter.isTouched(change.previousPosition, radius) ->
                            secondHandleCenter = change.position
                        firstCurveCenter.isTouched(change.previousPosition, radius) ->
                            firstCurveCenter = change.position
                        secondCurveCenter.isTouched(change.previousPosition, radius) ->
                            secondCurveCenter = change.position
                    }
                }
            }) {
            canvasCenter = center
            radius = 20.dp.toPx()

            drawCircle(color = Blue, radius = radius, center = firstCurveCenter)
            drawCircle(color = Blue, radius = radius, center = secondCurveCenter)

            drawCircle(color = Red, radius = radius, center = firstHandleCenter)
            drawCircle(color = Red, radius = radius, center = secondHandleCenter)

            path = Path().apply {
                moveTo(firstHandleCenter.x, firstHandleCenter.y)
                cubicTo(
                    firstCurveCenter.x,
                    firstCurveCenter.y,
                    secondCurveCenter.x,
                    secondCurveCenter.y,
                    secondHandleCenter.x,
                    secondHandleCenter.y
                )
            }

            for (i in 0..lines) {
                val x = firstHandleCenter.x + ((secondHandleCenter.x - firstHandleCenter.x) * i / lines)
                val y = firstHandleCenter.y + ((secondHandleCenter.y - firstHandleCenter.y) * i / lines)
                val offset = Offset(x, y)

                val normalizedI: Float = i / lines.toFloat()
                val pos = FloatArray(2)
                val tan = FloatArray(2)
                PathMeasure().apply {
                    setPath(path.asAndroidPath(), false)
                    getPosTan(normalizedI * length, pos, tan)
                }
                val curveOffset = Offset(pos[0], pos[1])

                drawLine(color = Gray, start = offset, end = curveOffset, strokeWidth = 2.dp.toPx())
            }
        }
        Slider(
            value = lines.toFloat(),
            onValueChange = { lines = it.roundToInt() },
            valueRange = 50f..200f
        )
    }
}

private fun Offset.distanceFrom(point: Offset): Float =
    sqrt((point.x - x).pow(2) + (point.y - y).pow(2))

private fun Offset.isTouched(touchPoint: Offset, touchAreaRadius: Float) =
    this.distanceFrom(touchPoint) <= touchAreaRadius




















