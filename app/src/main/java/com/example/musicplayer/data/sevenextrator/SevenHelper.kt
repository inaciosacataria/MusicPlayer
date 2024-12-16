package com.example.musicplayer.data.sevenextrator

import android.content.Context
import android.os.Environment
import android.util.Log
import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.FileHeader
import net.lingala.zip4j.exception.ZipException


val SevenTAG = "SevenZipHelper"

object SevenZipHelper {
    fun extractAndSaveToPrivateStorages(
        archiveFile: File,
        context: Context,
        password: String = "123456789"
    ) {
        try {
            val randomAccessFile = RandomAccessFile(archiveFile, "r")
            val inStream = RandomAccessFileInStream(randomAccessFile)

            val callback = ArchiveOpenCallback(password)
            val inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, inStream, callback)

            val itemCount = inArchive.numberOfItems
            Log.i(SevenTAG, "Number of items: $itemCount")

            for (i in 0 until itemCount) {
                val filePath = inArchive.getStringProperty(i, PropID.PATH)
                val isEncrypted = inArchive.getStringProperty(i, PropID.ENCRYPTED)
                val outputStream = context.openFileOutput(filePath, Context.MODE_PRIVATE)
                inArchive.extractSlow(i) { data ->
                    outputStream.write(data)
                    data.size
                }
                outputStream.close()
                Log.i(SevenTAG, "File extracted and saved: $filePath")

            }

            inArchive.close()
            inStream.close()
        } catch (e: Exception) {
            Log.e(SevenTAG, "Error extracting archive", e)
        }
    }

    fun extractAndSaveToDownloadss(
        archiveFile: File,
        context: Context,
        password: String = "123456789"
    ) {
        try {
            val randomAccessFile = RandomAccessFile(archiveFile, "r")
            val inStream = RandomAccessFileInStream(randomAccessFile)

            val callback = ArchiveOpenCallback(password)
            val inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, inStream, callback)

            val itemCount = inArchive.numberOfItems
            Log.i(SevenTAG, "Number of items: $itemCount")

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            for (i in 0 until itemCount) {
                val filePath = inArchive.getStringProperty(i, PropID.PATH)

                // Verificando se o arquivo é válido
                if (filePath.isNullOrEmpty()) {
                    Log.e(SevenTAG, "File path is empty or invalid for item: $i")
                    continue
                }

                val outputFile = File(downloadsDir, filePath)
                val parentDir = outputFile.parentFile
                if (parentDir?.exists() == false) {
                    parentDir.mkdirs()  // Cria diretórios, se necessário
                }

                val outputStream = FileOutputStream(outputFile)
                inArchive.extractSlow(i) { data ->
                    if (data.isNotEmpty()) {
                        Log.i(SevenTAG, "Writing data to file: ${data.size} bytes")
                        outputStream.write(data)
                    } else {
                        Log.e(SevenTAG, "No data extracted for this file!")
                    }
                    data.size
                }
                outputStream.close()
                Log.i(SevenTAG, "File extracted and saved to Downloads: ${outputFile.absolutePath}")
            }

            inArchive.close()
            inStream.close()
        } catch (e: Exception) {
            Log.e(SevenTAG, "Error extracting archive", e)
        }
    }

    fun extractAndSaveToAppStorage(
        archiveFile: File,
        context: Context,
        password: String = "123456789"
    ) {
        try {
            // Get the app's private storage directory (internal storage)
            val appStorageDir = context.filesDir // This is your app's internal storage directory

            // Create a ZipFile object for the archive file
            val zipFile = ZipFile(archiveFile)

            // Check if the ZIP file is encrypted
            if (zipFile.isEncrypted) {
                // Set the password for the ZIP file
                zipFile.setPassword(password.toCharArray())
            }

            // Get the file headers of the ZIP file
            val fileHeaders: List<FileHeader> = zipFile.fileHeaders

            // Iterate over each file and extract
            for (fileHeader in fileHeaders) {
                val filePath = fileHeader.fileName

                // Check if the file path is valid
                if (filePath.isNullOrEmpty()) {
                    Log.e("ZipExtraction", "File path is empty or invalid: $filePath")
                    continue
                }

                // Define the output file path within the app's private storage
                val outputFile = File(appStorageDir, filePath)
                val parentDir = outputFile.parentFile
                if (!parentDir.exists()) {
                    parentDir.mkdirs()  // Create directories if necessary
                }

                // Extract the file to the app's private storage directory
                zipFile.extractFile(fileHeader, appStorageDir.toString())

                Log.i("ZipExtraction", "Extracted and saved to app storage: ${outputFile.absolutePath}")
            }

        } catch (e: ZipException) {
            Log.e("ZipExtraction", "Error extracting ZIP file", e)
        }
    }


    private class ArchiveOpenCallback(private val password: String = "123456789") :
        IArchiveOpenCallback, ICryptoGetTextPassword {

        override fun setTotal(files: Long?, bytes: Long?) {
            Log.i(SevenTAG, "Total work: $files files, $bytes bytes")
        }

        override fun setCompleted(files: Long?, bytes: Long?) {
            Log.i(SevenTAG, "Completed: $files files, $bytes bytes")
        }

        // Log para verificar a senha
        override fun cryptoGetTextPassword(): String {
            Log.i(SevenTAG, "Password provided to 7zip: $password")
            return password
        }
    }

}