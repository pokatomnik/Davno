package com.github.pokatomnik.davno.services.db.dao.logs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "type") val type: LogEntryType,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "extra") val extra: String
)