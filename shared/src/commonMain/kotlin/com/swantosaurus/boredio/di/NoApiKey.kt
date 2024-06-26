package com.swantosaurus.boredio.di

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.imageGenerating.ImageGenerator

private val logger = Logger.withTag("NoApiKey")

class NoApiKey : ImageGenerator.OpenAiApiKey {
    init {
        logger.e { "No API key provided -- defaulting to NoApiKey" }
    }
    override val key: String = ""
}