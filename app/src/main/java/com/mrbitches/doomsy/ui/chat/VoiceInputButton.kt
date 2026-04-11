package com.mrbitches.doomsy.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalLight
import com.mrbitches.doomsy.ui.theme.OffWhite
import java.util.Locale

@Composable
fun VoiceInputButton(
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isListening) Gold else GunmetalLight,
        label = "micBg",
    )
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            startListening(
                context = context,
                speechRecognizer = speechRecognizer,
                recognizerIntent = recognizerIntent,
                setListening = { isListening = it },
                onResult = onResult,
            )
        } else {
            Toast.makeText(context, "Mic permission is needed for Doomsy to listen.", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose { speechRecognizer?.destroy() }
    }

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(0.5.dp, GlassWhiteBorder, CircleShape)
            .clickable {
                if (speechRecognizer == null) {
                    Toast.makeText(context, "Speech recognition is unavailable on this device.", Toast.LENGTH_SHORT).show()
                    return@clickable
                }
                if (isListening) {
                    speechRecognizer.stopListening()
                    isListening = false
                } else if (
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    startListening(
                        context = context,
                        speechRecognizer = speechRecognizer,
                        recognizerIntent = recognizerIntent,
                        setListening = { isListening = it },
                        onResult = onResult,
                    )
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isListening) "\u2026" else "mic",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isListening) DeepBlack else Gold,
            ),
        )
    }
}

private fun startListening(
    context: android.content.Context,
    speechRecognizer: SpeechRecognizer?,
    recognizerIntent: Intent,
    setListening: (Boolean) -> Unit,
    onResult: (String) -> Unit,
) {
    val recognizer = speechRecognizer ?: return

    recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            setListening(false)
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.firstOrNull()?.let { onResult(it) }
        }

        override fun onError(error: Int) {
            setListening(false)
            if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                Toast.makeText(context, "Doomsy could not catch that. Try again.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onReadyForSpeech(params: Bundle?) = Unit
        override fun onBeginningOfSpeech() = Unit
        override fun onRmsChanged(rmsdB: Float) = Unit
        override fun onBufferReceived(buffer: ByteArray?) = Unit
        override fun onEndOfSpeech() = Unit
        override fun onPartialResults(partialResults: Bundle?) = Unit
        override fun onEvent(eventType: Int, params: Bundle?) = Unit
    })

    setListening(true)
    recognizer.startListening(recognizerIntent)
}
