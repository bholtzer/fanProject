package com.fansim.app.platform

/** Loops the whirring fan sound; volume tracks speed level + proximity. */
expect class SoundPlayer() {
    fun start(pitch: Float, speed: Float)
    fun setVolume(volume: Float)
    fun stop()
    fun release()
}
