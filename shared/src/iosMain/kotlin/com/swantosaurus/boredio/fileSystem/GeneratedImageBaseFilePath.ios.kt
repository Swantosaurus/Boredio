package com.swantosaurus.boredio.fileSystem

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getBaseDataPath(): Path {
    val documents =  NSFileManager.defaultManager.URLForDirectory(
        NSDocumentDirectory,
        NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )?.path
    if(documents == null) {
        throw IllegalStateException("no documents dir")
    }
    return documents.toPath()
}