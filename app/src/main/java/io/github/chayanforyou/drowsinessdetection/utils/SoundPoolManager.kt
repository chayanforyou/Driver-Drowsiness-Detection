package io.github.chayanforyou.drowsinessdetection.utils

import android.content.Context
import android.media.SoundPool
import io.github.chayanforyou.drowsinessdetection.R

class SoundPoolManager private constructor(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .build()

    private var soundId: Int = 0
    private var isSoundLoaded: Boolean = false

    init {
        soundId = soundPool.load(context, R.raw.beep, 1)

        // Listen for when the sound is loaded
        soundPool.setOnLoadCompleteListener { _, loadedSoundId, _ ->
            if (soundId == loadedSoundId) {
                isSoundLoaded = true
            }
        }
    }

    /**
     * Play the loaded sound.
     * @param volume - Playback volume (range: 0.0 to 1.0).
     */
    fun playSound(volume: Float = 1.0f) {
        if (isSoundLoaded) {
            soundPool.play(soundId, volume, volume, 1, 0, 1.0f)
        }
    }

    /**
     * Stop the SoundPool resources.
     */
    fun stop() {
        soundPool.stop(soundId)
    }

    /**
     * Release the SoundPool resources.
     */
    fun release() {
        soundPool.release()
    }

    companion object {
        @Volatile
        private var instance: SoundPoolManager? = null

        /**
         * Singleton access to the SimpleSoundPoolManager.
         * @param context - Application context.
         * @return The single instance of SimpleSoundPoolManager.
         */
        fun getInstance(context: Context): SoundPoolManager {
            return instance ?: synchronized(this) {
                instance ?: SoundPoolManager(context).also { instance = it }
            }
        }
    }
}
