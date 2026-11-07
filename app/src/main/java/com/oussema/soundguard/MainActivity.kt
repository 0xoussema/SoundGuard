
package com.oussema.soundguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.oussema.soundguard.ui.theme.SoundGuardTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoundGuardTheme {
                SoundGuardDashboard()
            }
        }
    }
}

@Composable
fun SoundGuardDashboard() {
    var noiseLevel by remember { mutableIntStateOf(48) }
    var exposureSeconds by remember { mutableIntStateOf(0) }

    val noiseHistory = remember {
        mutableStateListOf(48f, 52f, 55f, 49f, 60f, 57f, 63f)
    }

    val backgroundColor = Color(0xFF0B1220)
    val cardColor = Color(0xFF172235)
    val accentColor = Color(0xFF38BDF8)

    // Simulate noise readings every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)

            val newLevel = (40..95).random()
            noiseLevel = newLevel

            noiseHistory.add(newLevel.toFloat())

            if (noiseHistory.size > 20) {
                noiseHistory.removeAt(0)
            }
        }
    }

    // Exposure timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            exposureSeconds++
        }
    }

    val noiseStatus = when {
        noiseLevel < 55 -> "Quiet"
        noiseLevel < 75 -> "Moderate"
        else -> "High"
    }

    val statusColor = when {
        noiseLevel < 55 -> Color(0xFF4ADE80)
        noiseLevel < 75 -> Color(0xFFFACC15)
        else -> Color(0xFFF87171)
    }

    val exposureTime = String.format(
        "%02d:%02d",
        exposureSeconds / 60,
        exposureSeconds % 60
    )

    // Exposure statistics based on stored readings
    val averageNoise = if (noiseHistory.isNotEmpty()) {
        noiseHistory.average().toInt()
    } else {
        0
    }

    val peakNoise = noiseHistory.maxOrNull()?.toInt() ?: 0

    val highNoiseReadings = noiseHistory.count {
        it >= 85f
    }

    val totalReadings = noiseHistory.size

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = "SoundGuard",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Smart Noise Exposure Monitor",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            // Current noise level
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CURRENT NOISE LEVEL",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "$noiseLevel",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )

                    Text(
                        text = "dB",
                        fontSize = 20.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = noiseStatus,
                        color = statusColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Monitoring overview
            Text(
                text = "Monitoring Overview",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    title = "Exposure Time",
                    value = exposureTime,
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )

                InfoCard(
                    title = "Current Level",
                    value = "$noiseLevel dB",
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )
            }

            // Sensor status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sensor Status",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "● LIVE SIMULATION",
                        color = Color(0xFF4ADE80)
                    )

                    Text(
                        text = "Receiving simulated sensor readings",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Live noise history chart
            NoiseHistoryChart(readings = noiseHistory)

            // Daily exposure statistics
            Text(
                text = "Daily Exposure Statistics",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    title = "Average Noise",
                    value = "$averageNoise dB",
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )

                InfoCard(
                    title = "Peak Noise",
                    value = "$peakNoise dB",
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    title = "High Readings",
                    value = "$highNoiseReadings",
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )

                InfoCard(
                    title = "Total Readings",
                    value = "$totalReadings",
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun NoiseHistoryChart(readings: List<Float>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Noise History",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Last 20 readings · Updates every 2 seconds",
                color = Color.LightGray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { context ->
                    LineChart(context).apply {
                        description = Description().apply {
                            text = ""
                        }

                        setTouchEnabled(false)
                        setScaleEnabled(false)
                        setPinchZoom(false)

                        axisRight.isEnabled = false

                        axisLeft.apply {
                            axisMinimum = 30f
                            axisMaximum = 100f
                            textColor = android.graphics.Color.WHITE
                            setDrawGridLines(true)
                            gridColor = android.graphics.Color.DKGRAY
                        }

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            textColor = android.graphics.Color.WHITE
                            setDrawGridLines(false)
                            granularity = 1f
                        }

                        legend.isEnabled = false
                    }
                },
                update = { chart ->
                    val entries = readings.mapIndexed { index, value ->
                        Entry(index.toFloat(), value)
                    }

                    val dataSet = LineDataSet(entries, "Noise").apply {
                        color = android.graphics.Color.CYAN
                        lineWidth = 2.5f
                        setDrawCircles(false)
                        setDrawValues(false)
                        setDrawFilled(true)
                        fillColor = android.graphics.Color.CYAN
                        fillAlpha = 35
                    }

                    chart.data = LineData(dataSet)

                    chart.xAxis.axisMinimum = 0f
                    chart.xAxis.axisMaximum =
                        (readings.size - 1).coerceAtLeast(1).toFloat()

                    chart.invalidate()
                }
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
