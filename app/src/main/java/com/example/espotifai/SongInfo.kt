package com.example.espotifai

import android.icu.text.CaseMap
import java.security.AuthProvider

class SongInfo {
    var title : String? = null
    var author : String? = null
    var songUrl : String? = null
    var coverUrl : ByteArray? = null

    constructor(title: String?, author: String?, songUrl: String?, coverUrl: ByteArray?) {
        this.title = title
        this.author = author
        this.songUrl = songUrl
        this.coverUrl = coverUrl
    }

}