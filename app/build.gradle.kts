import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

fun localProperty(name: String): String =
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { stream ->
        Properties().apply { load(stream) }.getProperty(name, "").trim()
    }.orEmpty()

fun escapedBuildConfigString(value: String): String =
    value.replace("\\", "\\\\").replace("\"", "\\\"")

val doomsyApiBaseUrl =
    providers.gradleProperty("doomsyApiBaseUrl").orElse("").map { it.trim() }.get()
        .ifBlank { localProperty("doomsyApiBaseUrl") }

val doomsyApiKey =
    providers.gradleProperty("doomsyApiKey").orElse("").map { it.trim() }.get()
        .ifBlank { localProperty("doomsyApiKey") }

android {
    namespace = "com.mrbitches.doomsy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mrbitches.doomsy"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "1.0.0-rc.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DOOMSY_API_BASE_URL", "\"${escapedBuildConfigString(doomsyApiBaseUrl)}\"")
        buildConfigField("String", "DOOMSY_API_KEY", "\"${escapedBuildConfigString(doomsyApiKey)}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // RC / sideload: signed with debug keystore. Use a release keystore + signingConfig for Play Store.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    androidResources {
        noCompress += listOf("glb")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.animation)
    implementation(libs.sceneview)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
