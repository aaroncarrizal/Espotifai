package com.example.espotifai

import android.media.MediaPlayer

interface MainAux {
    fun pauseOrUnpause(mp : MediaPlayer? = null)
    fun playSong(pos: Int)
    fun setDataMini(pos : Int)

}