package com.example.jetpack16.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.jetpack16.R
import com.example.jetpack16.models.ImagesModel
import com.example.jetpack16.viewmodels.ImagesViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun AddPhotoView(viewModel:ImagesViewModel){
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(Objects.requireNonNull(context),context.packageName + ".provider",file)
    var image by remember { mutableStateOf<Uri>(Uri.EMPTY)}
    val imageDefault = R.drawable.photo
    val permissionCheckResult = ContextCompat.checkSelfPermission(context,android.Manifest.permission.CAMERA)
    val permissionCheckResult2 = ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val permissionCheckResult3 = ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
        image = uri
    }
    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
        if(it != null){
            cameraLauncher.launch(uri)
        }else{
            Toast.makeText(context,"Permiso Denegado por el usuario",Toast.LENGTH_SHORT).show()
        }
    }

    val saveImageInGallery = {imageUri:Uri->
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(context.createImageFileInGallery())

            inputStream?.use { input->
                outputStream.use { output->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context,"Guardado en Galeria.",Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(context,"Error al guardar la imagen en galeria : ${e.message}",Toast.LENGTH_SHORT).show()
        }
    }

    val saveImageRoom = {imageUri:Uri->
        try {
            val imagePath = context.saveImageToRoom(imageUri)
            viewModel.insertImages(ImagesModel(ruta = imagePath))
            Toast.makeText(context,"Guardado en Room",Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(context,"Error al guardar la imagen en Room : ${e.message}",Toast.LENGTH_SHORT).show()
        }

    }


    Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
        Image(modifier = Modifier
            .clickable {
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED &&
                    permissionCheckResult2 == PackageManager.PERMISSION_GRANTED &&
                    permissionCheckResult3 == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                    //El dice si estas debajo del Sdk 32 debes pedir permiso sino no
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
            .padding(16.dp, 8.dp), 
            painter = rememberAsyncImagePainter(if(image.path?.isNotEmpty() == true) image else imageDefault),
            contentDescription = "")
        Button(onClick = {
            //saveImageRoom(image)
            saveImageInGallery(image)
        }) {
            Text(text = "Guardar Foto.")
        }
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile():File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(imageFileName,".jpg",externalCacheDir)
}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFileInGallery():File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File(imageDir,"${imageFileName}.jpg")
}

@SuppressLint("SimpleDateFormat")
fun Context.saveImageToRoom(imageUri:Uri):String{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_$timeStamp.jpg"
    val outputDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val outputFile = File(outputDir,imageFileName)
    val inputStream = contentResolver.openInputStream(imageUri)
    val outputStream = FileOutputStream(outputFile)

    inputStream?.use {input->
        outputStream.use { output->
            input.copyTo(output)
        }

    }

    return outputFile.absolutePath
}