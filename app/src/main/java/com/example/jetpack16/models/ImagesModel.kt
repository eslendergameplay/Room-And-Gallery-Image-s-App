package com.example.jetpack16.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Images")
data class ImagesModel(@PrimaryKey(autoGenerate = true)
                       val id:Long = 0,
                       val ruta:String)
