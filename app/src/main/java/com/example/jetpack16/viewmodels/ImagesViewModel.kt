package com.example.jetpack16.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.jetpack16.models.ImagesModel
import com.example.jetpack16.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ImagesViewModel(application:Application) :AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application,AppDatabase::class.java,"Images_database").build()
    private val _imagesList = MutableStateFlow<List<ImagesModel>>(emptyList())
    val imagesList = _imagesList.asStateFlow()

    init {
        viewModelScope.launch (Dispatchers.IO){
            db.imagesDao().getImages().collect{ items->
                _imagesList.value = items
            }
        }
    }

    fun insertImages(item:ImagesModel){
        viewModelScope.launch (Dispatchers.IO){
            db.imagesDao().insertImage(item)
        }
    }

    fun deleteImage(item:ImagesModel){
        viewModelScope.launch (Dispatchers.IO){
            deletePhoto(item.ruta)
            db.imagesDao().deleteImage(item)
        }
    }

    private fun deletePhoto(photoPath:String){
        val file = File(photoPath)
        if(file.exists()){
            file.delete()
        }
    }
}