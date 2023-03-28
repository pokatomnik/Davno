package com.github.pokatomnik.davno.services.db.dao.logs

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LogsDAO {
    @Query("SELECT * FROM logs ORDER BY id DESC LIMIT :limit OFFSET :offset")
    suspend fun list(offset: Long, limit: Long): List<LogEntry>

    @Query("INSERT INTO logs VALUES (null, :logEntryType, :message, :extra)")
    suspend fun add(
        logEntryType: LogEntryType,
        message: String,
        extra: String
    )
}
