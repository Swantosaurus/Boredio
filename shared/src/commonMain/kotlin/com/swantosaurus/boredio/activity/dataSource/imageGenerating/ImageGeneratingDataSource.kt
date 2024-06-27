package com.swantosaurus.boredio.activity.dataSource.imageGenerating

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.local.GeneratedImageFileSystem
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.local.generatedImagePath
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.remote.RemoteImageLoader
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.imageGenerating.ImageGenerator

class ImageGeneratingDataSource(
    private val generatedImageFS: GeneratedImageFileSystem,
    private val imageGenerator: ImageGenerator,
    private val remoteImageLoader: RemoteImageLoader
) {
    private val logger = Logger.withTag("ImageGeneratingDataSource")

    suspend fun getImageForActivity(
        activity: Activity
    ) = getImageForActivity(activity.key, activity.activity)

    /** tries to load an image from the file system,
     *  if it doesn't exist, it generates a new image and saves it to the file system */
    suspend fun getImageForActivity(
        activityKey: String,
        activityDescription: String
    ): String? {
        logger.i { "getImageForActivity" }
        val activityLocalFileName = "${activityKey}.png"
        if (generatedImageFS.exists(activityLocalFileName)) {
            logger.i("image found in file system ${generatedImagePath.resolve(activityLocalFileName)}")
            val data = generatedImageFS.read(path = activityLocalFileName)
            if(data == null) {
                logger.e("error reading image from file system")
                return null
            }
            return generatedImagePath.resolve(activityLocalFileName).toString()
        } else {
            logger.i("image not found in file system, generating new image")
            val imageUrl = imageGenerator.generate(
                "image for activity in animated style: $activityDescription",
                ImageGenerator.Model.Dalle2,
                ImageGenerator.Dimensions.XY512,
            )
            if(imageUrl == null) {
                logger.e("error generating image for activity")
                return null
            }

            val image = remoteImageLoader.loadImage(imageUrl)
            if(image == null) {
                logger.e("error loading image from url")
                return null
            }

            if(generatedImageFS.write(activityLocalFileName, data = image)){
                return generatedImagePath.resolve(activityLocalFileName).toString()//image
            } else {
                logger.e("error saving image to file system")
                return null
            }
        }
    }

    suspend fun deleteImage(activity: Activity) {
        val activityLocalFileName = "${activity.key}.png"
        if(generatedImageFS.exists(activityLocalFileName)) {
            generatedImageFS.delete(activityLocalFileName)
        }
    }
}