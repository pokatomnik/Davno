package com.github.pokatomnik.davno.services.logger

import com.github.pokatomnik.davno.services.db.dao.logs.LogEntryType
import com.github.pokatomnik.davno.services.db.dao.logs.LogsDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DavnoLogger(private val logsDAO: LogsDAO) {
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun info(message: String, extra: String = "") {
        ioScope.launch {
            logsDAO.add(LogEntryType.info, message, extra)
        }
    }

    fun warn(message: String, extra: String = "") {
        ioScope.launch {
            logsDAO.add(LogEntryType.warning, message, extra)
        }
    }

    fun error(message: String, extra: String = "") {
        ioScope.launch {
            logsDAO.add(LogEntryType.error, message, extra)
        }
    }
}