package dev.shreyaspatil.compose.keyboard

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.content.MediaType
import androidx.compose.foundation.content.receiveContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import dev.shreyaspatil.compose.keyboard.ui.theme.ComposeKeyboardImageDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeKeyboardImageDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Demo()
                }
            }
        }
    }
}

@Composable
fun Demo() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        var selectedContentUrl by remember {
            mutableStateOf<String?>(null)
        }

        selectedContentUrl?.let { url ->
            Text(text = "Selected image")
            Spacer(modifier = Modifier.size(8.dp))
            Image(url = url, modifier = Modifier.fillMaxWidth())
        }

        ChatMessageField(onReceiveContent = { uri ->
            selectedContentUrl = uri
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageField(onReceiveContent: (uri: String) -> Unit) {
    var value by remember {
        mutableStateOf("")
    }
    BasicTextField2(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .receiveContent(setOf(MediaType.Image)) { content ->
                content.platformTransferableContent
                    ?.linkUri
                    ?.toString()
                    ?.let(onReceiveContent)
                null
            },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.primary),
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)

            ) {
                if (value.isBlank()) {
                    Text(
                        text = "Write or select image from keyboard",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                innerTextField()
            }
        })
}

@Composable
fun Image(url: String, modifier: Modifier) {
    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Selected image",
        contentScale = ContentScale.Fit,
        imageLoader = imageLoader,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeKeyboardImageDemoTheme {
        Demo()
    }
}

