package com.example.musicplayer.domain.usecase

import android.util.Log
import com.example.musicplayer.domain.service.MusicController
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(private  val musicController: MusicController) {
    operator fun invoke(mediaItemIndex: Int) {
        Log.d("music_repository", "PlaySong $mediaItemIndex")
        musicController.play(mediaItemIndex)
    }
}