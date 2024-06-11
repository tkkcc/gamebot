@file:UseSerializers(LocalDateTimeSerializer::class)

package bilabila.gamebot.host.domain

import bilabila.gamebot.host.domain.crontab.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.threeten.bp.LocalDateTime

@Serializable
data class DateTimeRange(
    val start: LocalDateTime,
    val stop: LocalDateTime,
)

@Serializable
data class TaskStatus(
    val lastExecuteDateTime: DateTimeRange = DateTimeRange(
        LocalDateTime.MIN,
        LocalDateTime.MIN
    ),
    val nextStartDateTime: LocalDateTime = LocalDateTime.MIN
)


