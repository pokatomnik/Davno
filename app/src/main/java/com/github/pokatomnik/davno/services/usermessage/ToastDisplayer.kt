package com.github.pokatomnik.davno.services.usermessage

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class ToastDisplayer(private val context: Context) : MessageDisplayer {
    private val handler = Handler(Looper.getMainLooper())

    override fun display(message: String) {
        handler.post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}