package com.fansim.app.platform

import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

actual class SoundPlayer actual constructor() {

    // iosApp must add fan_sound.mp3 (seamless loop) to the Xcode project bundle resources.
    private var player: AVAudioPlayer? = null

    private fun ensurePlayer(): AVAudioPlayer? {
        var current = player
        if (current == null) {
            val path = NSBundle.mainBundle.pathForResource("fan_sound", ofType = "mp3") ?: return null
            AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryAmbient, error = null)
            current = AVAudioPlayer(contentsOfURL = NSURL.fileURLWithPath(path), error = null)
            current.numberOfLoops = -1
            player = current
        }
        return current
    }

    actual fun start() {
        ensurePlayer()?.play()
    }

    actual fun setVolume(volume: Float) {
        player?.volume = volume.coerceIn(0f, 1f)
    }

    actual fun stop() {
        player?.pause()
    }

    actual fun release() {
        player?.stop()
        player = null
    }
}
