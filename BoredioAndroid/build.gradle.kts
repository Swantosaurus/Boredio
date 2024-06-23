import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.swantosaurus.boredio.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.swantosaurus.boredio.android"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //API key is in the local.properties file

        //Who knows if its safe to put this here --
        //best way would be to create own auth server and make requests from there
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField(
            "String",
            "OPEN_AI_API_KEY",
            "\"${properties.getProperty("OPEN_AI_API_KEY")!!}\""
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        //isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(platform(libs.compose.bom))

    implementation(libs.coil.compose)
    implementation(libs.compose.animation)
    implementation(libs.compose.navigation)
    implementation(libs.koin.compsoe)
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.compose.ui.tooling)
}