package com.example.jetpack16.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jetpack16.models.ImagesModel

@Database(entities = [ImagesModel::class], version = 1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){
    abstract fun imagesDao():ImagesDao
}