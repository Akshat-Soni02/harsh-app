package com.heroroutine.app.media

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object CelebrationSoundPlayer {

    /**
     * Plays a cheerful 4-note ascending fanfare chime (C5 -> E5 -> G5 -> C6)
     * synthesized dynamically via AudioTrack. Zero asset dependencies, 100% offline.
     */
    fun playCelebrationChime() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 44100
                // 4 notes: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
                val notes = listOf(
                    Pair(523.25, 0.18),  // C5
                    Pair(659.25, 0.18),  // E5
                    Pair(783.99, 0.22),  // G5
                    Pair(1046.50, 0.55)  // C6 (longer sustain)
                )

                val totalSamples = notes.sumOf { (it.second * sampleRate).toInt() }
                val buffer = ShortArray(totalSamples)

                var offset = 0
                for ((freq, durSec) in notes) {
                    val noteSamples = (durSec * sampleRate).toInt()
                    val decayRate = 3.5 / durSec // exponential decay
                    for (i in 0 until noteSamples) {
                        val t = i.toDouble() / sampleRate
                        // Fundamental + gentle 2nd harmonic for rich bell tone
                        val wave = 0.75 * sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)
                        val envelope = exp(-decayRate * t)
                        // Soft attack for first 5ms to avoid click
                        val attack = if (t < 0.005) t / 0.005 else 1.0
                        val sample = (wave * envelope * attack * 26000).toInt().coerceIn(-32767, 32767)
                        buffer[offset + i] = sample.toShort()
                    }
                    offset += noteSamples
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                // Wait until playback ends, then release resources
                val totalMs = (notes.sumOf { it.second } * 1000).toLong() + 200
                delay(totalMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
