package com.example.musicplayer.data.sevenextrator

import android.os.Environment
import android.util.Log
import java.io.File



object EncriptedFileSearch {

    fun findEncryptedFilesInRoot(): List<File> {
        Log.d(SevenTAG, "Searching for encrypted files in root directory...")
        val encryptedFiles = mutableListOf<File>()

        // Caminho do armazenamento externo
        val rootDir = Environment.getExternalStorageDirectory()

        if (rootDir.exists() && rootDir.isDirectory) {
            Log.d(SevenTAG, "Root directory exists, starting search.")
            val files = rootDir.listFiles() // Lista apenas os arquivos na raiz

            files?.forEach { file ->
                // Log para cada arquivo encontrado na raiz
                Log.d(SevenTAG, "File path: ${file.absolutePath}, File name: ${file.name}, File extension: ${file.extension}")

                // Verifica se o arquivo tem a extensão desejada
                if (!file.isDirectory) { // Ignora pastas
                    if (file.extension.equals("7z", ignoreCase = true) || file.extension.equals("apk", ignoreCase = true)) {
                        Log.d(SevenTAG, "Found encrypted file: ${file.name}")
                        encryptedFiles.add(file)
                    }
                }
            }
        } else {
            Log.d(SevenTAG, "Root directory does not exist or is not a directory.")
        }

        return encryptedFiles
    }
}
