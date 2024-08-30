package com.example.jetpack16.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpack16.viewmodels.ImagesViewModel
import com.example.jetpack16.views.AddPhotoView
import com.example.jetpack16.views.HomeView

@Composable
fun NavManager(viewmodel:ImagesViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Home"){
        composable("Home"){
            HomeView(navController,viewmodel)
        }
        composable("AddPhotoView"){
            AddPhotoView(viewmodel)
        }
    }
}