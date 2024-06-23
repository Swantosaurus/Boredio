package com.swantosaurus.boredio.activity.dataSource.imageGenerating.local

import com.swantosaurus.boredio.fileSystem.ByteArrayFileSystem
import com.swantosaurus.boredio.fileSystem.getBaseDataPath

val generatedImagePath = getBaseDataPath().resolve("generatedImage")

class GeneratedImageFileSystem : ByteArrayFileSystem(generatedImagePath)