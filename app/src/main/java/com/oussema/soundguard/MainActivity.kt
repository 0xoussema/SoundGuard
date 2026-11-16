
package com.oussema.soundguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.ViewModelProvider
import com.oussema.soundguard.data.NoiseReadingRepository
import com.oussema.soundguard.data.SoundGuardDatabase
import com.oussema.soundguard.data.viewmodel.SoundGuardViewModel
import com.oussema.soundguard.data.viewmodel.SoundGuardViewModelFactory
import com.oussema.soundguard.ui.theme.SoundGuardTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay

private val BackgroundColor = Color(0xFF0B1220)
private val CardColor = Color(0xFF172235)
private val AccentColor = Color(0xFF38BDF8)
private val MutedColor = Color(0xFF94A3B8)
private val GreenColor = Color(0xFF4ADE80)
private val YellowColor = Color(0xFFFACC15)
private val RedColor = Color(0xFFF87171)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = SoundGuardDatabase.getDatabase(applicationContext)
        val repository = NoiseReadingRepository(database.noiseReadingDao())
        val factory = SoundGuardViewModelFactory(repository)

        val viewModel = ViewModelProvider(this, factory)
            .get(SoundGuardViewModel::class.java)

        setContent {
            SoundGuardTheme {
                SoundGuardApp(viewModel)
            }
        }
    }
}

@Composable
fun SoundGuardApp(viewModel: SoundGuardViewModel) {

    var selectedScreen by remember {
        mutableStateOf("Dashboard")
    }

    val savedReadings by viewModel.readings.collectAsState()

    // Timer stays alive while switching between screens
    var exposureSeconds by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            exposureSeconds++
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF111827),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedScreen == "Dashboard",
                    onClick = { selectedScreen = "Dashboard" },
                    icon = {
                        Text("⌂", fontSize = 24.sp)
                    },
                    label = {
                        Text("Dashboard")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentColor,
                        selectedTextColor = AccentColor,
                        unselectedIconColor = MutedColor,
                        unselectedTextColor = MutedColor,
                        indicatorColor = Color(0xFF26364D)
                    )
                )

                NavigationBarItem(
                    selected = selectedScreen == "History",
                    onClick = { selectedScreen = "History" },
                    icon = {
                        Text("◷", fontSize = 24.sp)
                    },
                    label = {
                        Text("History")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentColor,
                        selectedTextColor = AccentColor,
                        unselectedIconColor = MutedColor,
                        unselectedTextColor = MutedColor,
                        indicatorColor = Color(0xFF26364D)
                    )
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreen) {
                "Dashboard" -> SoundGuardDashboard(
                    viewModel = viewModel,
                    exposureSeconds = exposureSeconds
                )

                "History" -> HistoryScreen(readings = savedReadings)
            }
        }
    }
}

@Composable
fun SoundGuardDashboard(
    viewModel: SoundGuardViewModel,
    exposureSeconds: Int
) {

    var noiseLevel by remember {
        mutableIntStateOf(48)
    }

    var showClearConfirmation by remember {
        mutableStateOf(false)
    }

    val savedReadings by viewModel.readings.collectAsState()

    val startOfToday = remember {
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val todayReadings = savedReadings.filter {
        it.timestamp >= startOfToday
    }

    val noiseHistory = savedReadings
        .asReversed()
        .takeLast(20)
        .map { it.decibelLevel }

    LaunchedEffect(viewModel) {
        while (true) {
            delay(2000)
            val newLevel = (40..95).random()
            noiseLevel = newLevel
            viewModel.saveReading(newLevel.toFloat())
        }
    }

    val noiseStatus = when {
        noiseLevel < 55 -> "Quiet"
        noiseLevel < 75 -> "Moderate"
        else -> "High"
    }

    val statusColor = when {
        noiseLevel < 55 -> GreenColor
        noiseLevel < 75 -> YellowColor
        else -> RedColor
    }

    val exposureTime = String.format(
        "%02d:%02d",
        exposureSeconds / 60,
        exposureSeconds % 60
    )

    val averageNoise = if (todayReadings.isNotEmpty()) {
        todayReadings.map { it.decibelLevel }.average().toInt()
    } else 0

    val peakNoise = todayReadings
        .maxOfOrNull { it.decibelLevel }?.toInt() ?: 0

    val highNoiseReadings = todayReadings.count {
        it.decibelLevel >= 85f
    }

    val totalReadings = todayReadings.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        AccentColor.copy(alpha = 0.15f),
                        RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♫",
                    color = AccentColor,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "SoundGuard",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Noise Exposure Monitor",
                    color = MutedColor,
                    fontSize = 12.sp
                )
            }

            Surface(
                color = GreenColor.copy(alpha = 0.12f),
                shape = CircleShape
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(GreenColor, CircleShape)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "ACTIVE",
                        color = GreenColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // CURRENT NOISE LEVEL

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT NOISE LEVEL",
                        color = MutedColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "LIVE",
                        color = AccentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$noiseLevel",
                        color = Color.White,
                        fontSize = 76.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "dB",
                        color = MutedColor,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            bottom = 14.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = noiseStatus.uppercase(),
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 15.dp,
                                vertical = 7.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = when {
                            noiseLevel < 55 -> "Low noise detected"
                            noiseLevel < 75 -> "Moderate noise detected"
                            else -> "Elevated noise detected"
                        },
                        color = MutedColor,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            Color(0xFF334155),
                            CircleShape
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                noiseLevel.coerceIn(0, 100) / 100f
                            )
                            .fillMaxHeight()
                            .background(statusColor, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0 dB", color = MutedColor, fontSize = 10.sp)
                    Text("Noise intensity", color = MutedColor, fontSize = 10.sp)
                    Text("100 dB", color = MutedColor, fontSize = 10.sp)
                }
            }
        }

        // MONITORING OVERVIEW

        SectionTitle("Monitoring Overview")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                title = "Exposure Time",
                value = exposureTime,
                modifier = Modifier.weight(1f),
                cardColor = CardColor,
                accentColor = AccentColor
            )

            InfoCard(
                title = "Current Level",
                value = "$noiseLevel dB",
                modifier = Modifier.weight(1f),
                cardColor = CardColor,
                accentColor = AccentColor
            )
        }

        // SENSOR STATUS

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            GreenColor.copy(alpha = 0.13f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "●",
                        color = GreenColor,
                        fontSize = 19.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Sensor Status",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Receiving simulated readings",
                        color = MutedColor,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "SIMULATION",
                    color = GreenColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // NOISE CHART

        NoiseHistoryChart(readings = noiseHistory)

        // DAILY STATISTICS

        SectionTitle("Daily Exposure Statistics")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                title = "Average Noise",
                value = "$averageNoise dB",
                modifier = Modifier.weight(1f),
                cardColor = CardColor,
                accentColor = AccentColor
            )

            InfoCard(
                title = "Peak Noise",
                value = "$peakNoise dB",
                modifier = Modifier.weight(1f),
                cardColor = CardColor,
                accentColor = RedColor
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
                cardColor = CardColor,
                accentColor = YellowColor
            )

            InfoCard(
                title = "Total Readings",
                value = "$totalReadings",
                modifier = Modifier.weight(1f),
                cardColor = CardColor,
                accentColor = AccentColor
            )
        }

        // CLEAR HISTORY

        OutlinedButton(
            onClick = {
                showClearConfirmation = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = RedColor
            )
        ) {
            Text(
                text = "Clear History",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showClearConfirmation = false
            },
            title = {
                Text("Clear History?")
            },
            text = {
                Text(
                    "Are you sure you want to delete all saved noise readings?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHistory()
                        showClearConfirmation = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun NoiseHistoryChart(readings: List<Float>) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
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
                color = MutedColor,
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
    cardColor: Color,
    accentColor: Color = AccentColor
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = MutedColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                color = accentColor,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
