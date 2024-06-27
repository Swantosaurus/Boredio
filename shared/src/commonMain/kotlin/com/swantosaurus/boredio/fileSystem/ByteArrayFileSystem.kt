package com.swantosaurus.boredio.fileSystem

import co.touchlab.kermit.Logger
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM

open class ByteArrayFileSystem(private val basePath: Path) {
    private val logger = Logger.withTag("FS")
    private val fs = FileSystem.SYSTEM

    suspend fun write(path: String, throwOnOverride: Boolean = true, data: ByteArray): Boolean {
        try {
            if(!exists(basePath.toString())){
                logger.i { "Creating directory: $basePath" }
                fs.createDirectory(basePath)
            }
            fs.write(basePath.resolve(path), throwOnOverride) {
                write(data)
            }
            return true
        } catch (e: Exception) {
            logger.e("Error writing File", e)
            return false
        }
    }

    suspend fun read(path: String): ByteArray? {
        try {
            val size = fs.openReadOnly(basePath.resolve(path)).size()
            val array = ByteArray(size.toInt())
            fs.read(path.toPath()) {
                read(array)
            }
            return array
        } catch (e: Exception) {
            logger.e("Error reading file", e)
            return null
        }
    }

    suspend fun exists(path: String): Boolean = fs.exists(basePath.resolve(path))

    suspend fun getSizeOf(path: String?): Long {
        if(path == null) {
            logger.i { "reading current directory" }
            fs.metadata(basePath).let {
                logger.d { "metadata: $it" }
                return it.size ?: -1
            }
        }
        return fs.metadata(basePath.resolve(path)).size ?: -1
    }

    suspend fun delete(path: String): Boolean {
        try {
            fs.delete(basePath.resolve(path))
            return true
        } catch (e: Exception) {
            logger.e("Error deleting file", e)
            return false
        }
    }
}