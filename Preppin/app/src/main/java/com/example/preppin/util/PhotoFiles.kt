package com.example.preppin.util

import android.content.Context
import java.io.File

fun recipePhotoDir(context: Context): File {
    return File(context.filesDir, "recipe_photos").apply { mkdirs() }
}

fun newRecipePhotoFile(context: Context, recipeId: String): File {
    val dir = recipePhotoDir(context)
    val safeId = recipeId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
    val filename = "recipe_${safeId}_${System.currentTimeMillis()}.jpg"
    return File(dir, filename)
}