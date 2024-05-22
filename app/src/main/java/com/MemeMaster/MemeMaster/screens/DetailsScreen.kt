package com.MemeMaster.MemeMaster.screens

import kotlinx.coroutines.CoroutineScope
import android.content.Context
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.MemeMaster.MemeMaster.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel as viewModelProvider



@SuppressLint("WrongConstant")
@Composable
fun DetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    name: String?,
    url: String?
) {

    val viewModel = viewModelProvider<ImageViewModel>()
    val pickedImageUrl by remember { viewModel.pickedImageUrl }
    val pickedImageSize by remember { viewModel.pickedImageSize }
    val scale by remember { viewModel.scale }
    val offsetX by remember { viewModel.offsetX }
    val offsetY by remember { viewModel.offsetY }

    // Separate variables for confirmed position
    val confirmedOffsetX = remember { mutableFloatStateOf(0f) }
    val confirmedOffsetY = remember { mutableFloatStateOf(0f) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                viewModel.pickedImageUrl.value =
                    uri.toString()  // Store the URI of the picked image
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                imagePickerLauncher.launch(Intent(Intent.ACTION_PICK).setType("image/*"))
            }
        }
    )
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp)) // Add space for status bar

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            url?.let {
                AsyncImage(
                    model = url,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            name?.let {
                Text(
                    text = name,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))


// Display the picked image and allow resizing, dragging, and zooming
            pickedImageUrl?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                viewModel.scale.floatValue *= zoom
                                viewModel.offsetX.floatValue += pan.x
                                viewModel.offsetY.floatValue += pan.y
                            }
                        }
                ) {
                    AsyncImage(
                        model = it,
                        contentDescription = "Picked Image",
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (confirmedOffsetX.floatValue + offsetX).roundToInt(),
                                    (confirmedOffsetY.floatValue + offsetY).roundToInt()
                                )
                            }
                            .size(pickedImageSize)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
        var positionConfirmed by remember { mutableStateOf(false) }
        // Row containing the Confirm and Delete buttons
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                if (positionConfirmed) {
                    // Reset current position if confirmed
                    viewModel.offsetX.floatValue = 0f
                    viewModel.offsetY.floatValue = 0f
                } else {
                    // Confirm the current position
                    confirmedOffsetX.floatValue += viewModel.offsetX.floatValue
                    confirmedOffsetY.floatValue += viewModel.offsetY.floatValue
                    // Reset current position
                    viewModel.offsetX.floatValue = 0f
                    viewModel.offsetY.floatValue = 0f
                    // Mark position as confirmed
                    positionConfirmed = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Confirm",
                    tint = Color.Green
                )
            }

            IconButton(onClick = {
                // Delete the picked image and reset the state
                viewModel.pickedImageUrl.value = null
                viewModel.scale.floatValue = 1f
                viewModel.offsetX.floatValue = 0f
                viewModel.offsetY.floatValue = 0f
                viewModel.pickedImageSize.value = 200.dp
                // Reset position confirmed status
                positionConfirmed = false
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }

        // Bottom app bar placed outside the column
        Surface(color = Color.White) {
            MyBottomAppBar(
                navController = navController,
                onImagePickClick = {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                },
                onShareClick = {
                    context.let {
                        if (!url.isNullOrEmpty()) {
                            shareImageWithToast(it, url)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun MyBottomAppBar(navController: NavController, onImagePickClick: () -> Unit,  onShareClick: () -> Unit) {
    BottomAppBar(
        modifier = Modifier.height(60.dp),
        contentPadding = PaddingValues(2.dp),

        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomAppButton(
                    icon = painterResource(id = R.drawable.home_ic),
                    text = "Home",
                    iconSize = 22.dp, // Increase icon size
                    textSize = 8.sp, // Increase text size
                    onClick = { navController.popBackStack("MainScreen", inclusive = false) }
                )
                BottomAppButton(
                    icon = painterResource(id = R.drawable.text_ic),
                    text = "Text",
                    iconSize = 22.dp,
                    textSize = 8.sp,
                    onClick = { }
                )
                BottomAppButton(
                    icon = painterResource(id = R.drawable.image_ic),
                    text = "Image",
                    iconSize = 22.dp,
                    textSize = 8.sp,
                    onClick = onImagePickClick
                )
                BottomAppButton(
                    icon = painterResource(id = R.drawable.save_ic),
                    text = "Save",
                    iconSize = 22.dp,
                    textSize = 8.sp,
                    onClick = {
                    }
                )
                BottomAppButton(
                    icon = painterResource(id = R.drawable.share_ic),
                    text = "Share",
                    iconSize = 22.dp,
                    textSize = 8.sp,
                    onClick = onShareClick
                )
            }
        }
    )
}

fun shareImageWithToast(context: Context, imageUrl: String) {
    CoroutineScope(Dispatchers.Main).launch {
        val imageFile = withContext(Dispatchers.IO) {
            downloadImage(context, imageUrl)
        }

        if (imageFile != null) {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.file-provider",
                imageFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_SUBJECT, "Sharing Image")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Image"))

            // Show a toast message
            Toast.makeText(context, "Image shared", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to share image", Toast.LENGTH_SHORT).show()
        }
    }
}

private suspend fun downloadImage(context: Context, imageUrl: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()

            val file = File(context.externalCacheDir, "shared_image.png")
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            inputStream.close()

            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun BottomAppButton(icon: Painter, text: String, iconSize: Dp = 24.dp, textSize: TextUnit = 14.sp,onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.padding(1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize))
            Spacer(modifier = Modifier.height(0.dp))
            Text(
                text,
                fontSize = textSize,
                textAlign = TextAlign.Center
            )
        }
    }
}





