package com.example.musicplayer.data.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.data.mapper.toSong
import com.example.musicplayer.domain.model.Song
import com.example.musicplayer.domain.service.MusicController
import com.example.musicplayer.other.PlayerState
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.io.File


class MusicControllerImpl(context: Context) : MusicController {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )? = null


    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ controllerListener() }, MoreExecutors.directExecutor())
    }


    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                with(player) {
                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        currentMediaItem?.toSong(),
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        shuffleModeEnabled,
                        repeatMode == Player.REPEAT_MODE_ONE
                    )
                }
            }
        })
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }

    override fun addMediaItems(songs: List<Song>) {
        val mediaItems = songs.map {

//            val uri = if (it.songUrl.startsWith("file://")) {
//                Uri.parse(it.songUrl)
//
//            } else {
//                Uri.fromFile(File(it.songUrl))
//            }

         //   Log.d("music_repository", "MusicControllerImpl : $uri")

            MediaItem.Builder()
                .setMediaId(it.songUrl)
                .setUri(it.songUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setSubtitle(it.subtitle)
                        .setArtist(it.subtitle)
                        .setArtworkUri(Uri.parse(it.imageUrl))
                        .build()
                )
                .build()
        }

        mediaController?.setMediaItems(mediaItems)
    }

    override fun play(mediaItemIndex: Int) {
        mediaController!!.apply {
            try {
                seekToDefaultPosition(mediaItemIndex)
                playWhenReady = true
                prepare()
                Log.d("music_repository", "Impl preparing")
            }catch (e: Exception){
                Log.d("music_repository", "Impl ${e.message}")
            }
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun getCurrentSong(): Song? = mediaController?.currentMediaItem?.toSong()

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

    override fun skipToNextSong() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousSong() {
        mediaController?.seekToPrevious()
    }

}