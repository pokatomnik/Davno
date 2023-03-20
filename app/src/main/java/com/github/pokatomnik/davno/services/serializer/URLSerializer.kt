package com.github.pokatomnik.davno.services.serializer

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class URLSerializer : Serializer {
    override fun serialize(source: String): String {
        return URLEncoder.encode(source, StandardCharsets.UTF_8.toString())
    }

    override fun parse(serialized: String): String {
        return URLDecoder.decode(serialized, StandardCharsets.UTF_8.toString())
    }
}