package com.example.musicplayer.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.musicplayer.R
import com.example.musicplayer.data.dto.SongDto
import com.example.musicplayer.data.mapper.toSong
import com.example.musicplayer.data.remotedatabase.MusicLocalDatabase
import com.example.musicplayer.data.remotedatabase.MusicRemoteDatabase
import com.example.musicplayer.domain.repository.MusicRepository
import com.example.musicplayer.other.Resource
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicRemoteDatabase: MusicRemoteDatabase,
    private val musicLocalDatabase: MusicLocalDatabase,
    @ApplicationContext private val context: Context
) :
    MusicRepository {
//    override fun getSongs() =
//        flow {
//            val songs = musicRemoteDatabase.getAllSongs().await().toObjects<SongDto>()
//
//            if (songs.isNotEmpty()) {
//                for (song in songs){
//                    Log.d("muscic_repository","dto song : ${song.songUrl}")
//                }
//                emit(Resource.Success(songs.map { it.toSong() }))
//            }
//
//        }
//
//        flow {
//
//            val localSongs = musicLocalDatabase.getAllSongs().map { song ->
//                SongDto(
//                    mediaId = song.mediaId,
//                    title = song.title,
//                    subtitle = song.subtitle,
//                    songUrl = Uri.parse(File(song.songUrl)),
//                    //songUrl = "https://firebasestorage.googleapis.com/v0/b/musicplayer-2672e.appspot.com/o/if%20found%20-%20Met%20You%20%5BNCS%20Release%5D.mp3?alt=media&token=d3d487ae-5894-4430-a19a-24aeb0c2a520",
//                    imageUrl = "https://media.wired.com/photos/64b1a3b46279e364728446df/master/pass/How-To-Get-A-Song-Out-Of-Your-Head-Business-1284265247.jpg"
//                )
//
//            }
//
//            if (localSongs.isNotEmpty()) {
//                for (song in localSongs){
//                    Log.d("muscic_repository","dto song : ${song.songUrl}")
//                }
//                emit(Resource.Success(localSongs.map { it.toSong() }))
//            } else {
//                emit(Resource.Error("Nenhuma música local encontrada."))
//            }
//        }


    override fun getSongs() = flow {

        val remoteSongs = try {
            musicRemoteDatabase.getAllSongs().await().toObjects<SongDto>()
        } catch (e: Exception) {
            Log.e("music_repository", "Error fetching remote songs: ${e.message}")
            emptyList<SongDto>()
        }

        // Fetch local songs asynchronously
        val localSongs = musicLocalDatabase.getAllSongs().map { song ->
            SongDto(
                mediaId = song.mediaId,
                title = song.title,
                subtitle = song.subtitle,
                songUrl = Uri.parse(File(song.songUrl).toString()).toString(), // Ensure it works for local path
                imageUrl = if(song.imageUrl.isNullOrEmpty()) convertDrawableToPath(context = context, R.drawable.abc)!! else song.imageUrl
            )
        }

        val combinedSongs = mutableListOf<SongDto>()
       // combinedSongs.addAll(remoteSongs)
        combinedSongs.addAll(localSongs)

        if (combinedSongs.isNotEmpty()) {
            Log.d("music_repository", "Combined songs fetched")
            emit(Resource.Success(combinedSongs.map { it.toSong() }))
        } else {
            emit(Resource.Error("Nenhuma música encontrada"))
        }
    }
}



fun convertDrawableToPath(context: Context, drawableId: Int): String? {
    // Decode the drawable resource into a Bitmap
    val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)

    // Create a file in the app's internal storage (you can change the path if you want to store it elsewhere)
    val file = File(context.filesDir, "image_${System.currentTimeMillis()}.png")

    try {
        // Write the bitmap to the file
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Return the file path
        return file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return ""
}