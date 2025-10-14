package com.ninhkle.androidaudioapp.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ninhkle.androidaudioapp.R
import com.ninhkle.androidaudioapp.common.data.Audio

@Composable
fun AudioListItem(
    audio: Audio,
    modifier: Modifier = Modifier,
    onItemClick: (Audio) -> Unit = {},
    onPlayClick: (Audio) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(audio) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art placeholder
        Image(
            painter = rememberAsyncImagePainter(
                model = "content://media/external/audio/albumart/${audio.albumId}",
                placeholder = painterResource(id = R.drawable.ic_default_music_art),
                error = painterResource(id = R.drawable.ic_default_music_art)
            ),
            contentDescription = "Album art for ${audio.title}",
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = audio.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audio.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = audio.formattedDuration(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        IconButton(
            onClick = { onPlayClick(audio) },
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play Song"
            )
        }
    }
}

@Preview
@Composable
fun PreviewAudioListItem() {
    AudioListItem(
        audio = Audio(
            id = 1L,
            title = "Sample Song",
            artist = "Sample Artist",
            album = "Sample Album",
            duration = 180000L,
            uri = "",
            albumId = 1L
        ),
        onPlayClick = {},
        onItemClick = {},
    )
}