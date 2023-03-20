package com.github.pokatomnik.davno.services.serializer

interface Serializer {
    fun serialize(source: String): String
    fun parse(serialized: String): String
}