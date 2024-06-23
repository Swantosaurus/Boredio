package com.swantosaurus.boredio.fileSystem

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatformTools

actual fun getBaseDataPath(): Path {
    val koin = KoinPlatformTools.defaultContext().get()
    val ctx = koin.get<Context>()
    return ctx.dataDir.absolutePath.toPath()
}