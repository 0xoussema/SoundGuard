
package com.oussema.soundguard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.oussema.soundguard.data.NoiseReading
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

private val BackgroundColor = Color(0xFF0B1220)
private val CardColor = Color(0xFF172235)
private val AccentColor = Color(0xFF38BDF8)

@Composable
fun HistoryScreen(readings: List<NoiseReading>) {

    val context = LocalContext.current

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()

    val todayStart = today.atStartOfDay(zone)
        .toInstant().toEpochMilli()

    val sevenDaysStart = today.minusDays(6)
        .atStartOfDay(zone)
        .toInstant().toEpochMilli()

    val thirtyDaysStart = today.minusDays(29)
        .atStartOfDay(zone)
        .toInstant().toEpochMilli()

    val filteredReadings = when (selectedFilter) {
        "Today" -> readings.filter {
            it.timestamp >= todayStart
        }
        "7 Days" -> readings.filter {
            it.timestamp >= sevenDaysStart
        }
        "30 Days" -> readings.filter {
            it.timestamp >= thirtyDaysStart
        }
        else -> readings
    }

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            CsvExporter.export(
                context = context,
                uri = uri,
                readings = filteredReadings
            )
        }
    }

    val groupedReadings = filteredReadings
        .sortedByDescending { it.timestamp }
        .groupBy {
            Instant.ofEpochMilli(it.timestamp)
                .atZone(zone)
                .toLocalDate()
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Noise History",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                text = "Your recorded noise levels",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 16.dp
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Today", "7 Days", "30 Days", "All")
                    .forEach { filter ->

                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                            },
                            label = {
                                Text(filter)
                            }
                        )
                    }
            }

            Text(
                text = "${filteredReadings.size} readings found",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    top = 12.dp,
                    bottom = 8.dp
                )
            )

            Button(
                onClick = {
                    csvLauncher.launch("soundguard_history.csv")
                },
                enabled = filteredReadings.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text("Export CSV")
            }

            if (filteredReadings.isEmpty()) {

                Text(
                    text = "No readings found for this period.",
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 16.dp)
                )

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupedReadings.forEach { (date, dailyReadings) ->

                        item(key = "header_$date") {

                            val dateLabel = when (date) {
                                today -> "Today"
                                today.minusDays(1) -> "Yesterday"
                                else -> date.format(
                                    DateTimeFormatter.ofPattern(
                                        "EEEE, dd MMM yyyy",
                                        Locale.getDefault()
                                    )
                                )
                            }

                            Text(
                                text = dateLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = AccentColor,
                                modifier = Modifier.padding(
                                    top = 12.dp,
                                    bottom = 4.dp
                                )
                            )
                        }

                        items(
                            items = dailyReadings,
                            key = { it.id }
                        ) { reading ->
                            HistoryReadingCard(reading)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryReadingCard(reading: NoiseReading) {

    val timeFormat = SimpleDateFormat(
        "HH:mm:ss",
        Locale.getDefault()
    )

    val time = timeFormat.format(Date(reading.timestamp))

    val noiseColor = when {
        reading.decibelLevel < 55f -> Color(0xFF4ADE80)
        reading.decibelLevel < 75f -> Color(0xFFFACC15)
        else -> Color(0xFFF87171)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = time,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Text(
                    text = "Noise reading",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Text(
                text = "${"%.1f".format(reading.decibelLevel)} dB",
                style = MaterialTheme.typography.titleMedium,
                color = noiseColor
            )
        }
    }
}
