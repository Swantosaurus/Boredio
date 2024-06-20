package com.swantosaurus.boredio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform