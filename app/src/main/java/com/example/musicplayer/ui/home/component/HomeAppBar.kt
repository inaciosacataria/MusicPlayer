package com.example.musicplayer.ui.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R


@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.letter),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    text = "Libertas Play"
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            IconButton(onClick = { /* TODO: Handle search click */ }) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdded,
                    contentDescription = "Add"
                )
            }
        },
        modifier = modifier
    )
}
