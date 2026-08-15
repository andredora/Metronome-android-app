package com.metronome.app

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.util.Log

class MetronomeEngine(context: Context) {

    private var toneGenerator: ToneGenerator? = try {
        // Aumentado o volume do ToneGenerator para 100 (máximo)
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    } catch (e: Exception) {
        Log.e("MetronomeEngine", "Failed to initialize ToneGenerator", e)
        null
    }

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5) // Aumentado o limite de streams para evitar cortes
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM) // USAGE_ALARM costuma ser mais alto e prioritário
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    // Resource IDs for custom sounds (will be checked at runtime)
    private val primarySoundId: Int = loadSound(context, "primary_click")
    private val intermediateSoundId: Int = loadSound(context, "intermediate_click")
    private val normalSoundId: Int = loadSound(context, "normal_click")

    private fun loadSound(context: Context, name: String): Int {
        val id = context.resources.getIdentifier(name, "raw", context.packageName)
        return if (id != 0) soundPool.load(context, id, 1) else -1
    }

    enum class ClickType { PRIMARY, INTERMEDIATE, NORMAL }

    fun click(type: ClickType) {
        Log.d("MetronomeEngine", "Click: type=$type")

        val customSoundId = when (type) {
            ClickType.PRIMARY -> primarySoundId
            ClickType.INTERMEDIATE -> intermediateSoundId
            ClickType.NORMAL -> normalSoundId
        }

        if (customSoundId != -1) {
            // Play custom wav if available
            soundPool.play(customSoundId, 1f, 1f, 1, 0, 1f)
        } else {
            // Fallback to system tones
            val tone = when (type) {
                ClickType.PRIMARY -> ToneGenerator.TONE_DTMF_0
                ClickType.INTERMEDIATE -> ToneGenerator.TONE_DTMF_5
                ClickType.NORMAL -> ToneGenerator.TONE_SUP_CONFIRM
            }
            toneGenerator?.startTone(tone, 100)
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
        soundPool.release()
    }

    companion object {
        fun beatDurationMs(bpm: Int): Int = (60_000 / bpm.coerceAtLeast(1))
    }
}
