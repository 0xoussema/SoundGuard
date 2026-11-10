
package com.oussema.soundguard

import android.content.Context
import android.net.Uri
import com.oussema.soundguard.data.NoiseReading
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CsvExporter {

    fun export(
        context: Context,
        uri: Uri,
        readings: List<NoiseReading>
    ) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->

            OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->

                writer.write("ID,Noise Level (dB),Date and Time\n")

                val formatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss"
                ).withZone(ZoneId.systemDefault())

                readings.forEach { reading ->
                    val date = formatter.format(
                        Instant.ofEpochMilli(reading.timestamp)
                    )

                    writer.write(
                        "${reading.id},${reading.decibelLevel},$date\n"
                    )
                }
            }
        }
    }
}
