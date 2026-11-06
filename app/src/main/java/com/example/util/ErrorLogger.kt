package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Singleton ErrorLogger that captures application errors and unhandled exceptions,
 * persists them to internal storage, and provides an export flow via Android Share Intent.
 */
object ErrorLogger {

    private const val TAG = "ErrorLogger"
    private const val LOG_DIR = "logs"
    private const val LOG_FILE_NAME = "s20_raw_cam_errors.log"

    private val lock = ReentrantLock()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    private var isInitialized = false

    /**
     * Initializes the logger and registers the uncaught exception handler.
     */
    fun init(context: Context) {
        if (isInitialized) return
        val appContext = context.applicationContext

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logError(
                tag = "CRASH_HANDLER",
                message = "FATAL UNCAUGHT EXCEPTION in thread '${thread.name}' [id=${thread.id}]",
                throwable = throwable,
                context = appContext
            )
            previousHandler?.uncaughtException(thread, throwable)
        }

        // Record session start with device diagnostics
        logInfo(
            tag = "SESSION_INIT",
            message = "Application session started on ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})",
            context = appContext
        )

        isInitialized = true
    }

    /**
     * Appends an error message and optional throwable stack trace to the internal log file.
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null, context: Context? = null) {
        Log.e(tag, message, throwable)
        writeEntry(level = "ERROR", tag = tag, message = message, throwable = throwable, context = context)
    }

    /**
     * Appends a warning message to the internal log file.
     */
    fun logWarning(tag: String, message: String, context: Context? = null) {
        Log.w(tag, message)
        writeEntry(level = "WARN", tag = tag, message = message, throwable = null, context = context)
    }

    /**
     * Appends an informational diagnostic message to the internal log file.
     */
    fun logInfo(tag: String, message: String, context: Context? = null) {
        Log.i(tag, message)
        writeEntry(level = "INFO", tag = tag, message = message, throwable = null, context = context)
    }

    /**
     * Returns the File handle for the internal log file.
     */
    fun getLogFile(context: Context): File {
        val dir = File(context.filesDir, LOG_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, LOG_FILE_NAME)
    }

    /**
     * Reads all recorded log entries, prepending system diagnostic header info.
     */
    fun readLogs(context: Context): String {
        return lock.withLock {
            val file = getLogFile(context)
            val header = buildString {
                appendLine("=== S20-RAW-CAM DIAGNOSTIC REPORT ===")
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
                appendLine("Android OS: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                appendLine("Build ID: ${Build.DISPLAY}")
                appendLine("Report Generated: ${dateFormat.format(Date())}")
                appendLine("Log File: ${file.absolutePath}")
                appendLine("File Size: ${if (file.exists()) "${file.length()} bytes" else "0 bytes"}")
                appendLine("======================================")
                appendLine()
            }

            if (!file.exists() || file.length() == 0L) {
                header + "No error events recorded. Application running nominally.\n"
            } else {
                try {
                    header + file.readText()
                } catch (e: Exception) {
                    header + "Error reading log file: ${e.message}\n"
                }
            }
        }
    }

    /**
     * Clears all log entries from the local file.
     */
    fun clearLogs(context: Context): Boolean {
        return lock.withLock {
            try {
                val file = getLogFile(context)
                if (file.exists()) {
                    file.writeText("")
                }
                logInfo("LOG_SYSTEM", "Logs cleared by user", context)
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear logs", e)
                false
            }
        }
    }

    /**
     * Exports the diagnostic error logs via an Android ACTION_SEND share sheet.
     * Attaches both the raw log file via FileProvider and formatted text.
     */
    fun exportLogs(context: Context) {
        try {
            val logFile = getLogFile(context)
            if (!logFile.exists() || logFile.length() == 0L) {
                // Ensure there is at least an initial diagnostic snapshot in the file
                logInfo("DIAGNOSTIC", "Manual diagnostic export triggered", context)
            }

            val logText = readLogs(context)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "S20-RAW-CAM Diagnostic Error Log")
                putExtra(Intent.EXTRA_TEXT, logText)

                try {
                    val authority = "${context.packageName}.fileprovider"
                    val contentUri = FileProvider.getUriForFile(context, authority, logFile)
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) {
                    Log.w(TAG, "Could not attach file URI, sending as plain text: ${e.message}")
                }
            }

            val chooser = Intent.createChooser(shareIntent, "Export S20-RAW-CAM Error Logs").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            Toast.makeText(context, "Opening share sheet for error logs...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to export logs via share Intent, copying to clipboard as fallback", e)
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("S20-RAW-CAM Error Log", readLogs(context))
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Log copied to clipboard (Share intent unavailable)", Toast.LENGTH_LONG).show()
            } catch (clipEx: Exception) {
                Toast.makeText(context, "Could not export logs: ${clipEx.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun writeEntry(
        level: String,
        tag: String,
        message: String,
        throwable: Throwable?,
        context: Context?
    ) {
        val targetContext = context ?: return
        lock.withLock {
            try {
                val file = getLogFile(targetContext)
                val timestamp = dateFormat.format(Date())
                val threadName = Thread.currentThread().name

                FileWriter(file, true).use { fw ->
                    fw.write("[$timestamp] [$level] [$threadName] [$tag]: $message\n")
                    throwable?.let { t ->
                        val sw = StringWriter()
                        val pw = PrintWriter(sw)
                        t.printStackTrace(pw)
                        fw.write(sw.toString())
                        fw.write("\n")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write log entry to file", e)
            }
        }
    }
}
