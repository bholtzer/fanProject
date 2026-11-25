package com.fansim.app.platform

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.Log
import com.fansim.app.shared.R

actual class SoundPlayer actual constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var playbackProfile: Pair<Float, Float>? = null
    private var volume = 1f

    private fun createPlayer(): MediaPlayer? {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val player = MediaPlayer.create(
            PlatformContext.appContext,
            R.raw.fan_sound,
            attributes,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        ) ?: run {
            Log.e("SoundPlayer", "Could not prepare the bundled fan loop")
            return null
        }
        mediaPlayer = player
        player.isLooping = true
        player.setVolume(volume, volume)
        player.setOnErrorListener { failed, what, extra ->
            if (mediaPlayer === failed) {
                Log.e("SoundPlayer", "Fan playback failed (what=$what, extra=$extra)")
                release()
            }
            true
        }
        return player
    }

    actual fun start(pitch: Float, speed: Float) {
        try {
            val player = mediaPlayer ?: createPlayer() ?: return
            val profile = pitch to speed
            if (profile != playbackProfile) {
                player.playbackParams = PlaybackParams().setPitch(pitch).setSpeed(speed)
                playbackProfile = profile
            }
            if (!player.isPlaying) player.start()
        } catch (error: RuntimeException) {
            Log.e("SoundPlayer", "Could not start the fan loop", error)
            release()
        }
    }

    actual fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(this.volume, this.volume)
    }

    actual fun stop() {
        mediaPlayer?.let { if (it.isPlaying) it.pause() }
    }

    actual fun release() {
        val player = mediaPlayer
        mediaPlayer = null
        playbackProfile = null
        player?.release()
    }
}
