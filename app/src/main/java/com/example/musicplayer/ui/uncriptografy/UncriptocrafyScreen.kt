package com.example.musicplayer.ui.uncriptografy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.App
import com.example.musicplayer.R
import com.example.musicplayer.data.sevenextrator.SevenTAG
import com.example.musicplayer.data.sevenextrator.SevenZipHelper
import java.io.File
import java.io.FileOutputStream

@Preview
@Composable
fun UncriptocrafyScreen() {

    val selectedFileName = remember { mutableStateOf<String>("No file selected") }
    val context = LocalContext.current

    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data // URI do arquivo selecionado
            if (uri != null) {
                // Copiar arquivo para cache local
                val file = copyUriToFile(context, uri)
                if (file != null) {
                    selectedFileName.value = file.name
                    SevenZipHelper.extractAndSaveToAppStorage(file, context)
                } else {
                    selectedFileName.value = "Failed to copy file"
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Button(onClick = {

            }) {
                Text("Adicionar")
            }
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end= 24.dp, top = 200.dp)
        ) {
            Image(painter = painterResource(R.drawable.songs_folder), contentDescription = null, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Escolha de Filcheiros",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "carregue os ficheiros encriptados e curta a musicas\ncom a melhor experiencia",
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp).border(
                    width = 1.dp,
                    color = Color(0xFFCECECE),
                    shape = RoundedCornerShape(6.dp),
                ).clickable {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "*/*" // Accept all file types
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    fileChooserLauncher.launch(intent)
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Escolher o arquivo")
            }
        }
    }
}


fun copyUriToFile(context: Context, uri: Uri): File? {
    try {
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(context, uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)

        contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return tempFile
    } catch (e: Exception) {
        Log.e(SevenTAG, "Error copying file", e)
        return null
    }
}

// Helper para obter o nome do arquivo do URI
fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                return it.getString(nameIndex)
            }
        }
    }
    return null
}