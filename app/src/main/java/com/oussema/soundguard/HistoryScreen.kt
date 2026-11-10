
package com.oussema.soundguard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val today = LocalDate.now()

    val groupedReadings = readings
        .sortedByDescending { it.timestamp }
        .groupBy {
            Instant.ofEpochMilli(it.timestamp)
                .atZone(ZoneId.systemDefault())
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

            if (readings.isEmpty()) {

                Text(
                    text = "No readings recorded yet.",
                    color = Color.LightGray
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
