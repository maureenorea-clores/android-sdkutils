package com.rakuten.tech.mobile.sdkutils.sample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.rakuten.tech.mobile.sdkutils.eventlogger.EventLogger
import com.rakuten.tech.mobile.sdkutils.sample.databinding.ActivityEventLoggerBinding
import kotlinx.android.synthetic.main.activity_event_logger.*
import org.json.JSONObject

@Suppress(
    "UndocumentedPublicClass",
    "UndocumentedPublicFunction"
)
class EventLoggerActivity : Activity() {

    private lateinit var binding: ActivityEventLoggerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_logger)
        binding.activity = this
        setDefaultsOrHints()

        // EventLogger: use default API Key+URL
        EventLogger.configure(this)
    }

    fun onLogEventButtonClick() {

        val infoString = addtnl_info_text.text.toString()
        val info = if (infoString.isEmpty()) {
            null
        } else {
            jsonStringToMap(infoString)
        }

        val sdkName = binding.sdkNameText.text.toString().ifEmpty { "sdkutils" }
        val sdkVersion = binding.sdkVerText.text.toString().ifEmpty {
            com.rakuten.tech.mobile.sdkutils.BuildConfig.VERSION_NAME }
        val errorCode = binding.errorCodeText.text.toString()
        val errorMessage = binding.errorMsgText.text.toString()
        val numTimes = binding.numTimesText.text.toString().toIntOrNull() ?: 1
        val eventTypeRadId = binding.eventTypeRadioGrp.checkedRadioButtonId
        val eventType = findViewById<RadioButton>(eventTypeRadId).text.toString().lowercase()

        Toast.makeText(this, "Event processed!", Toast.LENGTH_SHORT).show()

        when (eventType) {
            "critical" -> repeat(numTimes) {
                EventLogger.sendCriticalEvent(sdkName, sdkVersion, errorCode, errorMessage, info) }
            "warning" -> repeat(numTimes) {
                EventLogger.sendWarningEvent(sdkName, sdkVersion, errorCode, errorMessage, info)
            }
        }
    }

    fun onCustomErrorMessageButtonClick() {
        binding.errorMsgText.setText("")
    }

    fun onException1ButtonClick() {
        binding.errorMsgText.setText(
            ArithmeticException().stackTraceToString().take(1000)
        )
    }

    fun onException2ButtonClick() {
        binding.errorMsgText.setText(
            IllegalArgumentException().stackTraceToString().take(1000)
        )
    }

    @Suppress("MagicNumber")
    fun onShowEventsCacheClick() {
        val eventsCache = this
            .getSharedPreferences("com.rakuten.tech.mobile.sdkutils.eventlogger.events", Context.MODE_PRIVATE)

        val textBuilder = StringBuilder(0)
        for (event in eventsCache.all) {
            textBuilder.append(event.key)
            textBuilder.append("\n")
            textBuilder.append(
                JSONObject(event.value.toString()).toString(4)
            )
            textBuilder.append("\n\n\n")
        }

        binding.eventsStorageText.text = textBuilder
    }

    private fun setDefaultsOrHints() {
        binding.apply {
            sdkNameText.setText("sdkutils")
            sdkVerText.setText(com.rakuten.tech.mobile.sdkutils.BuildConfig.VERSION_NAME)
            numTimesText.setText("1")
            addtnlInfoText.hint = """{ "key": "value" }"""
        }
    }

    private fun jsonStringToMap(jsonString: String): Map<String, String>? {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return try {
            Gson().fromJson(jsonString, type)
        } catch (e: JsonSyntaxException) {
            Toast.makeText(this, "Not a valid Json representation!", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}
