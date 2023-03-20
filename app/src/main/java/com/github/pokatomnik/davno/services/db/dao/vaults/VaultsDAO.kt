package com.github.pokatomnik.davno.services.db.dao.vaults

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlin.math.max

@Dao
abstract class VaultsDAO {
    @Query("SELECT * FROM vaults")
    abstract suspend fun getAll(): List<Vault>

    @Query("INSERT INTO vaults VALUES (null, :name, :rootUrl, :userName, :password)")
    abstract suspend fun add(name: String, rootUrl: String, userName: String, password: String)

    @Query("SELECT * FROM vaults WHERE id=:id")
    abstract suspend fun getOneById(id: Long): Vault?

    @Query("UPDATE vaults SET name=:name, rootUrl=:rootUrl, userName=:userName, password=:password WHERE id=:id")
    abstract suspend fun editOneById(
        id: Long,
        name: String,
        rootUrl: String,
        userName: String,
        password: String
    )

    @Query("DELETE FROM vaults WHERE id=:id")
    abstract suspend fun deleteById(id: Long)

    @Transaction
    open suspend fun duplicateById(id: Long) {
        val vault = getOneById(id) ?: return
        val (name) = getNameWithVersion(vault.name)
        val maxVersion = getAll().fold(0) { currentMaxVersion, currentVault ->
            val (currentName, currentVersion) = getNameWithVersion(currentVault.name)
            if (currentName == name) {
                max(currentMaxVersion, currentVersion)
            } else {
                currentMaxVersion
            }
        }
        add(
            name = getNameWithVersion(name, maxVersion + 1),
            rootUrl = vault.rootUrl,
            userName = vault.userName,
            password = vault.password
        )
    }

    private fun getNameWithVersion(nameRaw: String): Pair<String, Int> {
        val words = nameRaw.split(" ")
        if (words.isEmpty()) return Pair(first = nameRaw, second = 0)
        val lastWord = words.last()
        val hasVersion = lastWord.startsWith(VERSION_PREFIX) && lastWord.endsWith(VERSION_SUFFIX)
        if (!hasVersion) return Pair(first = nameRaw, second = 0)
        val version = try {
            lastWord.removePrefix(VERSION_PREFIX).removeSuffix(VERSION_SUFFIX).toInt()
        } catch (e: Exception) { 0 }

        return try {
            val name = words.dropLast(1).joinToString(" ")
            Pair(first = name, second = version)
        } catch (e: Exception) {
            Pair(first = nameRaw, second = 0)
        }
    }

    private fun getNameWithVersion(name: String, version: Int): String {
        return "$name $VERSION_PREFIX$version$VERSION_SUFFIX"
    }

    private companion object {
        private const val VERSION_PREFIX = "("
        private const val VERSION_SUFFIX = ")"
    }
}