package com.example.musicplayer.data.remotedatabase

import android.app.Application
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.musicplayer.domain.model.Song
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot

class MusicRemoteDatabase(private val songCollection: CollectionReference) {
    fun getAllSongs(): Task<QuerySnapshot> = songCollection.get()
}


class MusicLocalDatabase(private val context: Context) {

    fun getAllSongsaa(): List<Song> {
        val songs = mutableListOf<Song>()


        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
       // val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        // Realize a consulta ao MediaStore
        val query = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Media.TITLE + " ASC" // Ordenação
        )

        // Processar os resultados
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val path = cursor.getString(dataColumn)
                val duration = cursor.getLong(durationColumn)
                Log.d("music_repository", "music: id $id path $path")
                songs.add(Song(mediaId =id.toString(),title= title, subtitle =  artist, songUrl =  path, imageUrl = ""))
            }
        }

        return songs
    }

    fun getAllSongs(): List<Song> {
        val songs = mutableListOf<Song>()


        val privateDir = context.filesDir


        if (privateDir.exists() && privateDir.isDirectory) {

            val audioFiles = privateDir.listFiles { file, name ->
                name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".flac") ||
                        name.endsWith(".aac") || name.endsWith(".m4a") || name.endsWith(".opus") ||
                        name.endsWith(".amr") || name.endsWith(".ogg") || name.endsWith(".wma") ||
                        name.endsWith(".midi") || name.endsWith(".mid")
            }

            audioFiles?.forEach { file ->

                val title = file.nameWithoutExtension
                val path = file.absolutePath

                val song = Song(
                    mediaId = file.name,
                    title = title,
                    subtitle = "Unknown Artist",
                    songUrl = path,
                    imageUrl = ""
                )
                Log.d("music_repository", "music: path $path title $title")
                songs.add(song)
            }
        }

        return songs
    }
}