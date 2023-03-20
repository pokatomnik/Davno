package com.github.pokatomnik.davno.services.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.services.db.dao.vaults.VaultsDAO

@Database(
    entities = [Vault::class],
    version = 1,
    exportSchema = true,
    autoMigrations = [],
)
abstract class DavnoDatabase : RoomDatabase() {
    abstract fun vaultsDAO(): VaultsDAO
}