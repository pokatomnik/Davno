package com.github.pokatomnik.davno.services.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.pokatomnik.davno.services.db.dao.logs.LogEntry
import com.github.pokatomnik.davno.services.db.dao.logs.LogsDAO
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.services.db.dao.vaults.VaultsDAO

@Database(
    entities = [Vault::class, LogEntry::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        )
    ],
)
abstract class DavnoDatabase : RoomDatabase() {
    abstract fun vaultsDAO(): VaultsDAO

    abstract fun logsDAO(): LogsDAO
}