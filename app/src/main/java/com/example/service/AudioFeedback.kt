package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R
import com.example.data.local.QuizPreferences

class AudioFeedback(private val context: Context, private val preferences: QuizPreferences) {

    private var toneGenerator: ToneGenerator? = null
    private var correctMediaPlayer: MediaPlayer? = null
    private var incorrectMediaPlayer: MediaPlayer? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initMediaPlayers()
    }

    private fun initMediaPlayers() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            correctMediaPlayer = MediaPlayer.create(context, R.raw.snd_correct)?.apply {
                setAudioAttributes(audioAttributes)
                setVolume(0.45f, 0.45f)
            }

            incorrectMediaPlayer = MediaPlayer.create(context, R.raw.snd_incorrect)?.apply {
                setAudioAttributes(audioAttributes)
                setVolume(0.40f, 0.40f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playCorrect() {
        if (preferences.isSoundEnabled) {
            playMediaPlayer(correctMediaPlayer) {
                // Fallback to ToneGenerator if MediaPlayer unavailable
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (preferences.isVibrationEnabled) {
            vibrate(50)
        }
    }

    fun playWrong() {
        if (preferences.isSoundEnabled) {
            playMediaPlayer(incorrectMediaPlayer) {
                // Fallback to ToneGenerator if MediaPlayer unavailable
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (preferences.isVibrationEnabled) {
            vibrateDouble()
        }
    }

    private fun playMediaPlayer(player: MediaPlayer?, fallback: () -> Unit) {
        try {
            if (player != null) {
                if (player.isPlaying) {
                    player.pause()
                }
                player.seekTo(0)
                player.start()
            } else {
                fallback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fallback()
        }
    }

    fun playTick() {
        if (preferences.isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playVictory() {
        if (preferences.isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (preferences.isVibrationEnabled) {
            vibrate(100)
        }
    }

    fun playGameOver() {
        if (preferences.isSoundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 300)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (preferences.isVibrationEnabled) {
            vibrate(200)
        }
    }

    fun playClick() {
        if (preferences.isVibrationEnabled) {
            vibrate(25)
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrateDouble() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 60, 60, 80), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 60, 60, 80), -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            correctMediaPlayer?.release()
            correctMediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            incorrectMediaPlayer?.release()
            incorrectMediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
