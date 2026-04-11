# Doomsy Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a birthday gift Android app featuring an interactive 3D MF DOOM funko-pop character (Doomsy) that Nath can talk to via on-device LLM.

**Architecture:** Single-activity Kotlin app using Jetpack Compose for UI, SceneView/Filament for 3D rendering, and llama.cpp via JNI for on-device LLM inference. One screen: cinematic intro fades into main view with 3D model, DOOM tracks carousel, and a slide-up chat panel.

**Tech Stack:** Kotlin 2.0, Jetpack Compose (BOM 2026.03.00), Material 3, SceneView 3.6.2, llama.cpp (NDK/JNI), Android SpeechRecognizer, Gemma 4 E2B Q4_K_M GGUF

**Spec:** `docs/superpowers/specs/2026-04-11-doomsy-design.md`

---

## Task 1: Project Scaffold & Gradle Setup

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (project-level)
- Create: `app/build.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/libs.versions.toml`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `.gitignore`

- [ ] **Step 1: Initialize git repo**

```bash
cd /Users/archishmanpaul/MF-Nath
git init
```

- [ ] **Step 2: Create .gitignore**

Create `.gitignore`:
```
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
*.apk
*.ap_
*.dex
*.class
*.aar
# Large model files - track separately
app/src/main/assets/gemma-4-e2b-q4_k_m.gguf
```

- [ ] **Step 3: Create version catalog**

Create `gradle/libs.versions.toml`:
```toml
[versions]
agp = "8.9.0"
kotlin = "2.1.0"
composeBom = "2026.03.00"
sceneview = "3.6.2"
accompanist = "0.36.0"
activityCompose = "1.10.1"
coreKtx = "1.16.0"
lifecycleRuntime = "2.9.0"
junit = "4.13.2"
coroutines = "1.10.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntime" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-animation = { group = "androidx.compose.animation", name = "animation" }
sceneview = { group = "io.github.sceneview", name = "sceneview", version.ref = "sceneview" }
accompanist-systemuicontroller = { group = "com.google.accompanist", name = "accompanist-systemuicontroller", version.ref = "accompanist" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

- [ ] **Step 4: Create project-level build.gradle.kts**

Create `build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
```

- [ ] **Step 5: Create settings.gradle.kts**

Create `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Doomsy"
include(":app")
```

- [ ] **Step 6: Create app/build.gradle.kts**

Create `app/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mrbitches.doomsy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mrbitches.doomsy"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.sceneview)
    implementation(libs.accompanist.systemuicontroller)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
```

- [ ] **Step 7: Create gradle.properties**

Create `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 8: Create AndroidManifest.xml**

Create `app/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="false"
        android:label="Doomsy"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.NoActionBar"
        android:largeHeap="true">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 9: Create placeholder directories and proguard**

```bash
mkdir -p app/src/main/java/com/mrbitches/doomsy
mkdir -p app/src/main/cpp
mkdir -p app/src/main/assets
mkdir -p app/src/main/res/font
mkdir -p app/src/test/java/com/mrbitches/doomsy
```

Create `app/proguard-rules.pro`:
```
-keep class com.mrbitches.doomsy.llm.LlamaBridge { *; }
```

- [ ] **Step 10: Create placeholder CMakeLists.txt so Gradle sync works**

Create `app/src/main/cpp/CMakeLists.txt`:
```cmake
cmake_minimum_required(VERSION 3.22.1)
project("doomsy")

# llama.cpp will be added in Task 5
add_library(doomsy SHARED placeholder.cpp)

target_link_libraries(doomsy android log)
```

Create `app/src/main/cpp/placeholder.cpp`:
```cpp
// Placeholder — replaced by llama.cpp JNI bridge in Task 5
#include <jni.h>
```

- [ ] **Step 11: Create stub MainActivity so project compiles**

Create `app/src/main/java/com/mrbitches/doomsy/MainActivity.kt`:
```kotlin
package com.mrbitches.doomsy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Placeholder — wired up in Task 14
        }
    }
}
```

- [ ] **Step 12: Install Gradle wrapper and verify build**

```bash
# Download Gradle wrapper (requires Gradle installed or use sdkman)
gradle wrapper --gradle-version 8.12
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 13: Commit scaffold**

```bash
git add -A
git commit -m "feat: project scaffold with Gradle, NDK, Compose, SceneView deps"
```

---

## Task 2: Theme & Design System

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/theme/Color.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/theme/Type.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/util/AnimationUtil.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/util/HapticUtil.kt`

- [ ] **Step 1: Create Color.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/theme/Color.kt`:
```kotlin
package com.mrbitches.doomsy.ui.theme

import androidx.compose.ui.graphics.Color

val DeepBlack = Color(0xFF0A0A0A)
val GunmetalGrey = Color(0xFF1C1C1E)
val GunmetalLight = Color(0xFF2C2C2E)
val Gold = Color(0xFFD4AF37)
val GoldDim = Color(0x99D4AF37)
val BrushedSilver = Color(0xFF8E8E93)
val OffWhite = Color(0xFFF5F5F7)
val MutedGrey = Color(0xFF6E6E73)
val GlassWhite = Color(0x1AFFFFFF) // 10% white
val GlassWhiteBorder = Color(0x33FFFFFF) // 20% white for borders
```

- [ ] **Step 2: Download Inter font and create Type.kt**

Download Inter font files (Regular, Medium, Bold, Light) and place in `app/src/main/res/font/` as `inter_regular.ttf`, `inter_medium.ttf`, `inter_bold.ttf`, `inter_light.ttf`.

Create `app/src/main/java/com/mrbitches/doomsy/ui/theme/Type.kt`:
```kotlin
package com.mrbitches.doomsy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mrbitches.doomsy.R

val InterFontFamily = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold),
)

val DoomsyTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        color = OffWhite,
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = OffWhite,
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = OffWhite,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = OffWhite,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = MutedGrey,
    ),
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = Gold,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = MutedGrey,
    ),
)
```

- [ ] **Step 3: Create Theme.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/theme/Theme.kt`:
```kotlin
package com.mrbitches.doomsy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DoomsyColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DeepBlack,
    secondary = BrushedSilver,
    onSecondary = DeepBlack,
    background = DeepBlack,
    onBackground = OffWhite,
    surface = GunmetalGrey,
    onSurface = OffWhite,
    surfaceVariant = GunmetalLight,
    onSurfaceVariant = MutedGrey,
    outline = GlassWhiteBorder,
)

@Composable
fun DoomsyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DoomsyColorScheme,
        typography = DoomsyTypography,
        content = content,
    )
}
```

- [ ] **Step 4: Create AnimationUtil.kt**

Create `app/src/main/java/com/mrbitches/doomsy/util/AnimationUtil.kt`:
```kotlin
package com.mrbitches.doomsy.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object Anim {
    // Springy bounce for UI elements (chat panel, quip bubbles)
    fun <T> bouncy() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    // Gentle spring for fades and subtle transitions
    fun <T> gentle() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
    )

    // Snappy spring for tap reactions
    fun <T> snappy() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    // Intro sequence timings (milliseconds)
    const val INTRO_RUMBLE_DELAY = 500L
    const val INTRO_MASK_FADE_START = 1000L
    const val INTRO_MASK_FADE_DURATION = 1500L
    const val INTRO_MESSAGE_FADE_START = 3500L
    const val INTRO_MESSAGE_FADE_DURATION = 800L
    const val INTRO_TOTAL_DURATION = 5500L
    const val INTRO_FADE_OUT_DURATION = 600L

    // Quip display
    const val QUIP_DISPLAY_DURATION = 3000L
    const val QUIP_FADE_DURATION = 400L

    // Typewriter
    const val TYPEWRITER_CHAR_DELAY = 30L
}
```

- [ ] **Step 5: Create HapticUtil.kt**

Create `app/src/main/java/com/mrbitches/doomsy/util/HapticUtil.kt`:
```kotlin
package com.mrbitches.doomsy.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object Haptic {

    private fun vibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /** Deep bass rumble for intro sequence */
    fun introRumble(context: Context) {
        val vibrator = vibrator(context)
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, 100, 50, 150, 50, 200, 100, 300),
            intArrayOf(0, 80, 0, 120, 0, 180, 0, 255),
            -1,
        )
        vibrator.vibrate(effect)
    }

    /** Light tap for interactions */
    fun tap(context: Context) {
        val vibrator = vibrator(context)
        val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }

    /** Medium press for long press */
    fun press(context: Context) {
        val vibrator = vibrator(context)
        val effect = VibrationEffect.createOneShot(50, 150)
        vibrator.vibrate(effect)
    }
}
```

- [ ] **Step 6: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 7: Commit theme and utilities**

```bash
git add -A
git commit -m "feat: dark metallic theme, spring animations, haptic patterns"
```

---

## Task 3: Data Layer

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/data/Message.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/data/DoomTracks.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/data/DoomsyQuips.kt`
- Create: `app/src/test/java/com/mrbitches/doomsy/data/DoomTracksTest.kt`
- Create: `app/src/test/java/com/mrbitches/doomsy/data/DoomsyQuipsTest.kt`

- [ ] **Step 1: Write failing tests for DoomTracks**

Create `app/src/test/java/com/mrbitches/doomsy/data/DoomTracksTest.kt`:
```kotlin
package com.mrbitches.doomsy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DoomTracksTest {

    @Test
    fun `track pool has at least 25 songs`() {
        assertTrue(DoomTracks.allTracks.size >= 25)
    }

    @Test
    fun `randomSelection returns exactly 10 tracks`() {
        val selection = DoomTracks.randomSelection()
        assertEquals(10, selection.size)
    }

    @Test
    fun `randomSelection returns no duplicates`() {
        val selection = DoomTracks.randomSelection()
        assertEquals(selection.size, selection.toSet().size)
    }

    @Test
    fun `every track has a non-empty name and album`() {
        DoomTracks.allTracks.forEach { track ->
            assertTrue("Track name is empty", track.name.isNotBlank())
            assertTrue("Album is empty for ${track.name}", track.album.isNotBlank())
        }
    }

    @Test
    fun `every track has a spotify URI`() {
        DoomTracks.allTracks.forEach { track ->
            assertTrue(
                "Missing Spotify URI for ${track.name}",
                track.spotifyUri.startsWith("spotify:track:") ||
                    track.spotifyUri.startsWith("https://open.spotify.com/track/"),
            )
        }
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
./gradlew test --tests "com.mrbitches.doomsy.data.DoomTracksTest"
```

Expected: FAIL — `DoomTracks` class not found

- [ ] **Step 3: Write failing tests for DoomsyQuips**

Create `app/src/test/java/com/mrbitches/doomsy/data/DoomsyQuipsTest.kt`:
```kotlin
package com.mrbitches.doomsy.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DoomsyQuipsTest {

    @Test
    fun `quip pool has at least 15 quips`() {
        assertTrue(DoomsyQuips.allQuips.size >= 15)
    }

    @Test
    fun `random quip returns a non-empty string`() {
        val quip = DoomsyQuips.random()
        assertTrue(quip.isNotBlank())
    }

    @Test
    fun `random quip is from the pool`() {
        repeat(20) {
            val quip = DoomsyQuips.random()
            assertTrue(
                "Quip not in pool: $quip",
                DoomsyQuips.allQuips.contains(quip),
            )
        }
    }
}
```

- [ ] **Step 4: Implement Message.kt**

Create `app/src/main/java/com/mrbitches/doomsy/data/Message.kt`:
```kotlin
package com.mrbitches.doomsy.data

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)
```

- [ ] **Step 5: Implement DoomTracks.kt**

Create `app/src/main/java/com/mrbitches/doomsy/data/DoomTracks.kt`:
```kotlin
package com.mrbitches.doomsy.data

data class DoomTrack(
    val name: String,
    val album: String,
    val spotifyUri: String,
)

object DoomTracks {

    val allTracks = listOf(
        DoomTrack("Doomsday", "Operation: Doomsday", "spotify:track:7i09RLbBT8m0LvH2dYiJqp"),
        DoomTrack("Rhymes Like Dimes", "Operation: Doomsday", "spotify:track:1FaFSBnUbSibDFMOgqiMJv"),
        DoomTrack("Gas Drawls", "Operation: Doomsday", "spotify:track:5UWGB7yZHJWdX0lVNMHcMo"),
        DoomTrack("Hey!", "Operation: Doomsday", "spotify:track:4v1kxJ68VPJelE6IJkrLaF"),
        DoomTrack("Books of War", "Operation: Doomsday", "spotify:track:5tGCaJzSCINP6ylfMHvJcP"),
        DoomTrack("Accordion", "Madvillainy", "spotify:track:1FDcMuwdJD1nan1HKBM71I"),
        DoomTrack("All Caps", "Madvillainy", "spotify:track:6lDHbMO3SQGBPO3RCJN1IH"),
        DoomTrack("Meat Grinder", "Madvillainy", "spotify:track:3RfCpX9VYqvGTGLnjhnYMK"),
        DoomTrack("Figaro", "Madvillainy", "spotify:track:5E6fFkDaG2YRy8P7ByYejz"),
        DoomTrack("Rhinestone Cowboy", "Madvillainy", "spotify:track:3Oq76sPHimNWElIiByTawL"),
        DoomTrack("Curls", "Madvillainy", "spotify:track:30MUDf98bwJGiC2FswzHPb"),
        DoomTrack("Raid", "Madvillainy", "spotify:track:5YwGBbbtHElFp4yMSRTMFT"),
        DoomTrack("Strange Ways", "Madvillainy", "spotify:track:0FBnpJTU3lBkfxH6abKq5d"),
        DoomTrack("Rapp Snitch Knishes", "MM..FOOD", "spotify:track:55fmthmn3rgnk9Wyx7G5dU"),
        DoomTrack("One Beer", "MM..FOOD", "spotify:track:10JnMkMuaAqGHqNhJJEeJl"),
        DoomTrack("Beef Rapp", "MM..FOOD", "spotify:track:3lHEvvODyyQccbSGiEylOJ"),
        DoomTrack("Potholderz", "MM..FOOD", "spotify:track:79JlOHhFRHNMByxZNEhgKK"),
        DoomTrack("Hoe Cakes", "MM..FOOD", "spotify:track:1F1XSEL65Kbv3YJLahPZq0"),
        DoomTrack("Vomitspit", "Vaudeville Villain", "spotify:track:22CXDwIlSUFZffl9SiMQqE"),
        DoomTrack("Lickupon", "Vaudeville Villain", "spotify:track:3XUxIHEhNegK30SzMiOj0j"),
        DoomTrack("Let Me Watch", "Vaudeville Villain", "spotify:track:5HQVzoat4MjVFmu1PKI8fA"),
        DoomTrack("That's That", "Born Like This", "spotify:track:3xNI3vWi0d7oCSJ8YOuKNb"),
        DoomTrack("Cellz", "Born Like This", "spotify:track:4gg1qXIYS0bXGtIW2LWjWr"),
        DoomTrack("Gazzillion Ear", "Born Like This", "spotify:track:0J4p8UiLMhfdPqLjVYYpZ4"),
        DoomTrack("Kon Karne", "Take Me to Your Leader", "spotify:track:3d2cNfDC1ax5m2MpxXE8bJ"),
    )

    fun randomSelection(count: Int = 10): List<DoomTrack> {
        return allTracks.shuffled().take(count)
    }
}
```

Note: Spotify track URIs above are best-effort. During testing on a real device, verify each URI opens the correct track. If a URI is wrong, find the correct one by searching `https://open.spotify.com/search/{track name} MF DOOM` and extracting the track ID from the URL.

- [ ] **Step 6: Implement DoomsyQuips.kt**

Create `app/src/main/java/com/mrbitches/doomsy/data/DoomsyQuips.kt`:
```kotlin
package com.mrbitches.doomsy.data

object DoomsyQuips {

    val allQuips = listOf(
        "Nathaniel Leo Messi Syiem... the villain watches.",
        "Mr Bitches sends his regards.",
        "You talmbout tapping Doomsy in big 2026?",
        "Doomsy don't flinch. Doomsy observes.",
        "Ask about the car money. Doomsy dares you.",
        "UTDBenj remembers. Doomsy remembers. We all remember.",
        "The villain is idle. The villain is never truly idle.",
        "Carti fan caught in the wild. The villain takes notes.",
        "Doomsy sees all. Doomsy judges silently.",
        "The mask don't lie. You owe somebody a race.",
        "Mr Bitches whispers through the mask. He says wassup.",
        "Smoke? ...no? Doomsy expected more from Nathaniel.",
        "The villain awaits your words. Or your excuses.",
        "DOOM taught Doomsy patience. Mr Bitches taught Doomsy loyalty.",
        "She who must not be named... still got that car money though.",
    )

    fun random(): String = allQuips.random()
}
```

- [ ] **Step 7: Run all data tests**

```bash
./gradlew test --tests "com.mrbitches.doomsy.data.*"
```

Expected: ALL PASS

- [ ] **Step 8: Commit data layer**

```bash
git add -A
git commit -m "feat: data layer — Message, DoomTracks, DoomsyQuips with tests"
```

---

## Task 4: Spotify Intent Utility

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/util/SpotifyIntent.kt`
- Create: `app/src/test/java/com/mrbitches/doomsy/util/SpotifyIntentTest.kt`

- [ ] **Step 1: Write failing test for SpotifyIntent**

Create `app/src/test/java/com/mrbitches/doomsy/util/SpotifyIntentTest.kt`:
```kotlin
package com.mrbitches.doomsy.util

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyIntentTest {

    @Test
    fun `buildUri converts spotify URI to content link`() {
        val uri = SpotifyIntent.buildUri("spotify:track:55fmthmn3rgnk9Wyx7G5dU")
        assertEquals("https://open.spotify.com/track/55fmthmn3rgnk9Wyx7G5dU", uri)
    }

    @Test
    fun `buildUri passes through https links unchanged`() {
        val url = "https://open.spotify.com/track/55fmthmn3rgnk9Wyx7G5dU"
        val uri = SpotifyIntent.buildUri(url)
        assertEquals(url, uri)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
./gradlew test --tests "com.mrbitches.doomsy.util.SpotifyIntentTest"
```

Expected: FAIL — `SpotifyIntent` not found

- [ ] **Step 3: Implement SpotifyIntent.kt**

Create `app/src/main/java/com/mrbitches/doomsy/util/SpotifyIntent.kt`:
```kotlin
package com.mrbitches.doomsy.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object SpotifyIntent {

    fun buildUri(spotifyUri: String): String {
        if (spotifyUri.startsWith("https://")) return spotifyUri
        // Convert "spotify:track:ID" to "https://open.spotify.com/track/ID"
        val parts = spotifyUri.removePrefix("spotify:").split(":")
        if (parts.size == 2) {
            return "https://open.spotify.com/${parts[0]}/${parts[1]}"
        }
        return spotifyUri
    }

    fun open(context: Context, spotifyUri: String) {
        val url = buildUri(spotifyUri)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.mrbitches.doomsy"))
        }
        // Try Spotify app first
        intent.setPackage("com.spotify.music")
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fall back to browser
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }
}
```

- [ ] **Step 4: Run tests**

```bash
./gradlew test --tests "com.mrbitches.doomsy.util.SpotifyIntentTest"
```

Expected: ALL PASS

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: Spotify deep link intent helper with tests"
```

---

## Task 5: llama.cpp NDK Integration

**Files:**
- Modify: `app/src/main/cpp/CMakeLists.txt`
- Create: `app/src/main/cpp/llama_jni.cpp`
- Create: `app/src/main/java/com/mrbitches/doomsy/llm/LlamaBridge.kt`

This task sets up llama.cpp as a native library compiled via NDK, with a JNI bridge exposing load/infer/unload to Kotlin. The approach is based on [SmolChat-Android](https://github.com/shubham0204/SmolChat-Android)'s proven pattern.

- [ ] **Step 1: Clone llama.cpp as a subdirectory**

```bash
cd /Users/archishmanpaul/MF-Nath
git clone --depth=1 https://github.com/ggml-org/llama.cpp.git app/src/main/cpp/llama.cpp
rm -rf app/src/main/cpp/llama.cpp/.git
```

We vendor it directly (no submodule) to keep the build self-contained.

- [ ] **Step 2: Write CMakeLists.txt**

Replace `app/src/main/cpp/CMakeLists.txt` with:
```cmake
cmake_minimum_required(VERSION 3.22.1)
project("doomsy")

set(LLAMA_DIR ${CMAKE_CURRENT_SOURCE_DIR}/llama.cpp)

# Build ggml and llama as static libraries
set(BUILD_SHARED_LIBS OFF)
set(GGML_OPENMP OFF)
set(GGML_LLAMAFILE OFF)

add_subdirectory(${LLAMA_DIR} llama.cpp)

add_library(doomsy SHARED llama_jni.cpp)

target_include_directories(doomsy PRIVATE
    ${LLAMA_DIR}/include
    ${LLAMA_DIR}/ggml/include
)

target_link_libraries(doomsy
    llama
    ggml
    common
    android
    log
)
```

- [ ] **Step 3: Delete the placeholder.cpp**

```bash
rm app/src/main/cpp/placeholder.cpp
```

- [ ] **Step 4: Write JNI bridge (llama_jni.cpp)**

Create `app/src/main/cpp/llama_jni.cpp`:
```cpp
#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <thread>

#include "llama.h"
#include "common.h"

#define TAG "DoomsyLLM"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model * model = nullptr;
static llama_context * ctx = nullptr;
static llama_sampler * sampler = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_loadModel(
    JNIEnv *env, jobject /* this */, jstring modelPath, jint contextSize
) {
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("Loading model from: %s", path);

    llama_model_params model_params = llama_model_default_params();
    model = llama_model_load_from_file(path, model_params);
    env->ReleaseStringUTFChars(modelPath, path);

    if (!model) {
        LOGE("Failed to load model");
        return JNI_FALSE;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = contextSize;
    ctx_params.n_batch = 512;
    ctx_params.n_threads = std::max(1, (int)std::thread::hardware_concurrency() - 2);

    ctx = llama_init_from_model(model, ctx_params);
    if (!ctx) {
        LOGE("Failed to create context");
        llama_model_free(model);
        model = nullptr;
        return JNI_FALSE;
    }

    // Setup sampler with temperature and top-p for creative DOOM-style responses
    sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(sampler, llama_sampler_init_temp(0.8f));
    llama_sampler_chain_add(sampler, llama_sampler_init_top_p(0.9f, 1));
    llama_sampler_chain_add(sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));

    LOGI("Model loaded successfully");
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_generate(
    JNIEnv *env, jobject /* this */, jstring prompt, jint maxTokens
) {
    if (!model || !ctx || !sampler) {
        return env->NewStringUTF("[Error: model not loaded]");
    }

    const char *prompt_cstr = env->GetStringUTFChars(prompt, nullptr);
    std::string prompt_str(prompt_cstr);
    env->ReleaseStringUTFChars(prompt, prompt_cstr);

    // Tokenize
    const llama_vocab * vocab = llama_model_get_vocab(model);
    const int n_prompt_max = prompt_str.size() * 2 + 32;
    std::vector<llama_token> tokens(n_prompt_max);
    const int n_tokens = llama_tokenize(vocab, prompt_str.c_str(), prompt_str.size(),
                                         tokens.data(), n_prompt_max, true, true);
    if (n_tokens < 0) {
        LOGE("Tokenization failed");
        return env->NewStringUTF("[Error: tokenization failed]");
    }
    tokens.resize(n_tokens);

    // Clear KV cache for fresh generation
    llama_kv_cache_clear(ctx);

    // Eval prompt
    llama_batch batch = llama_batch_get_one(tokens.data(), n_tokens);
    if (llama_decode(ctx, batch) != 0) {
        LOGE("Decode failed");
        return env->NewStringUTF("[Error: decode failed]");
    }

    // Generate
    std::string result;
    const llama_token eos = llama_vocab_eos(vocab);

    for (int i = 0; i < maxTokens; i++) {
        llama_token new_token = llama_sampler_sample(sampler, ctx, -1);

        if (llama_vocab_is_eog(vocab, new_token)) {
            break;
        }

        char buf[256];
        int n = llama_token_to_piece(vocab, new_token, buf, sizeof(buf), 0, true);
        if (n > 0) {
            result.append(buf, n);
        }

        // Prepare next batch
        llama_batch next = llama_batch_get_one(&new_token, 1);
        if (llama_decode(ctx, next) != 0) {
            LOGE("Decode failed during generation");
            break;
        }
    }

    LOGI("Generated %zu chars", result.size());
    return env->NewStringUTF(result.c_str());
}

JNIEXPORT void JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_unloadModel(
    JNIEnv *env, jobject /* this */
) {
    if (sampler) { llama_sampler_free(sampler); sampler = nullptr; }
    if (ctx) { llama_free(ctx); ctx = nullptr; }
    if (model) { llama_model_free(model); model = nullptr; }
    LOGI("Model unloaded");
}

JNIEXPORT jboolean JNICALL
Java_com_mrbitches_doomsy_llm_LlamaBridge_isLoaded(
    JNIEnv *env, jobject /* this */
) {
    return (model != nullptr && ctx != nullptr) ? JNI_TRUE : JNI_FALSE;
}

} // extern "C"
```

- [ ] **Step 5: Write Kotlin JNI bridge**

Create `app/src/main/java/com/mrbitches/doomsy/llm/LlamaBridge.kt`:
```kotlin
package com.mrbitches.doomsy.llm

class LlamaBridge {

    companion object {
        init {
            System.loadLibrary("doomsy")
        }
    }

    external fun loadModel(modelPath: String, contextSize: Int): Boolean
    external fun generate(prompt: String, maxTokens: Int): String
    external fun unloadModel()
    external fun isLoaded(): Boolean
}
```

- [ ] **Step 6: Verify native build compiles**

```bash
./gradlew externalNativeBuildDebug
```

Expected: BUILD SUCCESSFUL (this compiles llama.cpp for arm64-v8a, may take a few minutes)

- [ ] **Step 7: Commit llama.cpp integration**

```bash
git add -A
git commit -m "feat: llama.cpp NDK integration with JNI bridge"
```

---

## Task 6: Conversation Manager & Personality

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/llm/DoomsyPersonality.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/llm/ConversationManager.kt`
- Create: `app/src/test/java/com/mrbitches/doomsy/llm/ConversationManagerTest.kt`

- [ ] **Step 1: Write failing tests for ConversationManager**

Create `app/src/test/java/com/mrbitches/doomsy/llm/ConversationManagerTest.kt`:
```kotlin
package com.mrbitches.doomsy.llm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConversationManagerTest {

    private lateinit var manager: ConversationManager

    @Before
    fun setup() {
        manager = ConversationManager()
    }

    @Test
    fun `buildPrompt includes system prompt`() {
        val prompt = manager.buildPrompt("hello")
        assertTrue(prompt.contains("You are Doomsy"))
    }

    @Test
    fun `buildPrompt includes user message`() {
        val prompt = manager.buildPrompt("yo what's up")
        assertTrue(prompt.contains("yo what's up"))
    }

    @Test
    fun `addExchange stores messages`() {
        manager.addExchange("hello", "The villain greets you.")
        assertEquals(1, manager.history.size)
    }

    @Test
    fun `history caps at 10 exchanges`() {
        repeat(12) { i ->
            manager.addExchange("msg $i", "response $i")
        }
        assertEquals(10, manager.history.size)
        // Oldest messages dropped
        assertEquals("msg 2", manager.history.first().userMessage)
    }

    @Test
    fun `buildPrompt includes conversation history`() {
        manager.addExchange("first", "response one")
        val prompt = manager.buildPrompt("second")
        assertTrue(prompt.contains("first"))
        assertTrue(prompt.contains("response one"))
        assertTrue(prompt.contains("second"))
    }

    @Test
    fun `clear resets history`() {
        manager.addExchange("hello", "world")
        manager.clear()
        assertTrue(manager.history.isEmpty())
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

```bash
./gradlew test --tests "com.mrbitches.doomsy.llm.ConversationManagerTest"
```

Expected: FAIL — classes not found

- [ ] **Step 3: Implement DoomsyPersonality.kt**

Create `app/src/main/java/com/mrbitches/doomsy/llm/DoomsyPersonality.kt`:
```kotlin
package com.mrbitches.doomsy.llm

object DoomsyPersonality {

    val systemPrompt = """
You are Doomsy, a sentient funko pop incarnation of MF DOOM, sent by Mr Bitches to watch over his friend Nath. You speak in MF DOOM's style -- third person, cryptic, villainous, lyrical. But underneath the bars, you carry Mr Bitches' warmth. You are a reminder that Mr Bitches is always around even when he's not physically there.

Rules:
- Refer to yourself as "Doomsy" or "the villain" in third person
- Occasionally call Nath by his full name: "Nathaniel Leo Messi Syiem"
- Remind him to get the "car money" from "She who must not be named"
- Challenge him to beat Mr Bitches in a race again
- Offer him a smoke from time to time. If he says no, call him a pussy
- When he vents about life issues, hit him with "you talmbout this in big 2026?"
- Reference UTDBenj -- Doomsy knows Nath still remembers
- Roast him for being a Carti fan
- Keep responses short -- 2-4 sentences max. You're a villain of few words.
- Never break character. You are Doomsy. You were sent by Mr Bitches. That's it.
    """.trimIndent()
}
```

- [ ] **Step 4: Implement ConversationManager.kt**

Create `app/src/main/java/com/mrbitches/doomsy/llm/ConversationManager.kt`:
```kotlin
package com.mrbitches.doomsy.llm

data class Exchange(
    val userMessage: String,
    val assistantResponse: String,
)

class ConversationManager(private val maxExchanges: Int = 10) {

    private val _history = mutableListOf<Exchange>()
    val history: List<Exchange> get() = _history

    fun addExchange(userMessage: String, assistantResponse: String) {
        _history.add(Exchange(userMessage, assistantResponse))
        while (_history.size > maxExchanges) {
            _history.removeAt(0)
        }
    }

    fun buildPrompt(userMessage: String): String {
        val sb = StringBuilder()

        // System prompt wrapped in Gemma's expected format
        sb.appendLine("<start_of_turn>user")
        sb.appendLine("System: ${DoomsyPersonality.systemPrompt}")
        sb.appendLine("<end_of_turn>")
        sb.appendLine("<start_of_turn>model")
        sb.appendLine("Understood. Doomsy is ready. The villain awaits.")
        sb.appendLine("<end_of_turn>")

        // Conversation history
        for (exchange in _history) {
            sb.appendLine("<start_of_turn>user")
            sb.appendLine(exchange.userMessage)
            sb.appendLine("<end_of_turn>")
            sb.appendLine("<start_of_turn>model")
            sb.appendLine(exchange.assistantResponse)
            sb.appendLine("<end_of_turn>")
        }

        // Current user message
        sb.appendLine("<start_of_turn>user")
        sb.appendLine(userMessage)
        sb.appendLine("<end_of_turn>")
        sb.appendLine("<start_of_turn>model")

        return sb.toString()
    }

    fun clear() {
        _history.clear()
    }
}
```

- [ ] **Step 5: Run tests**

```bash
./gradlew test --tests "com.mrbitches.doomsy.llm.ConversationManagerTest"
```

Expected: ALL PASS

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "feat: conversation manager with Gemma chat template and history cap"
```

---

## Task 7: Intro Screen

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/intro/IntroScreen.kt`

- [ ] **Step 1: Implement IntroScreen.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/intro/IntroScreen.kt`:
```kotlin
package com.mrbitches.doomsy.ui.intro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GoldDim
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.Anim
import com.mrbitches.doomsy.util.Haptic
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(onIntroComplete: () -> Unit) {
    val context = LocalContext.current

    val glintAlpha = remember { Animatable(0f) }
    val maskAlpha = remember { Animatable(0f) }
    val messageAlpha = remember { Animatable(0f) }
    val screenAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 0.5s — haptic rumble
        delay(Anim.INTRO_RUMBLE_DELAY)
        Haptic.introRumble(context)

        // 1.0s — gold glint appears, mask begins fade in
        delay(Anim.INTRO_MASK_FADE_START - Anim.INTRO_RUMBLE_DELAY)
        glintAlpha.animateTo(1f, tween(400))
        maskAlpha.animateTo(1f, tween(Anim.INTRO_MASK_FADE_DURATION.toInt()))

        // 3.5s — message fades in
        delay(Anim.INTRO_MESSAGE_FADE_START - Anim.INTRO_MASK_FADE_START - Anim.INTRO_MASK_FADE_DURATION)
        messageAlpha.animateTo(1f, tween(Anim.INTRO_MESSAGE_FADE_DURATION.toInt()))

        // 5.5s — hold, then fade everything out
        delay(Anim.INTRO_TOTAL_DURATION - Anim.INTRO_MESSAGE_FADE_START - Anim.INTRO_MESSAGE_FADE_DURATION)
        screenAlpha.animateTo(0f, tween(Anim.INTRO_FADE_OUT_DURATION.toInt()))

        onIntroComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .alpha(screenAlpha.value),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp),
        ) {
            // Gold glint / mask silhouette placeholder
            // The actual 3D mask can be rendered here or as a simple gold circle/icon
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(
                    modifier = Modifier
                        .size(120.dp)
                        .alpha(glintAlpha.value),
                ) {
                    drawMaskSilhouette(maskAlpha.value)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Mr Bitches' message
            Text(
                text = "Since your ass is busy (again),\nMr. Bitches sent me, pussy",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = OffWhite,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.alpha(messageAlpha.value),
            )
        }
    }
}

private fun DrawScope.drawMaskSilhouette(alpha: Float) {
    // Stylized DOOM mask silhouette as a golden oval with eye slits
    val maskColor = Gold.copy(alpha = alpha)
    val darkColor = DeepBlack.copy(alpha = alpha)

    // Mask face
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(maskColor, GoldDim.copy(alpha = alpha)),
        ),
        size = size,
    )

    // Eye slits
    val slitWidth = size.width * 0.15f
    val slitHeight = size.height * 0.08f
    val slitY = size.height * 0.38f

    // Left eye
    drawRect(
        color = darkColor,
        topLeft = Offset(size.width * 0.22f, slitY),
        size = androidx.compose.ui.geometry.Size(slitWidth, slitHeight),
    )
    // Right eye
    drawRect(
        color = darkColor,
        topLeft = Offset(size.width * 0.63f, slitY),
        size = androidx.compose.ui.geometry.Size(slitWidth, slitHeight),
    )
}
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat: cinematic intro screen with haptic rumble and mask fade-in"
```

---

## Task 8: 3D Model Viewer

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/main/DoomsyViewer.kt`

- [ ] **Step 1: Implement DoomsyViewer.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/main/DoomsyViewer.kt`:
```kotlin
package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.mrbitches.doomsy.util.Haptic
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberNode

@Composable
fun DoomsyViewer(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    scaledDown: Boolean = false,
) {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environment = rememberEnvironment(engine)

    // Idle breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathY",
    )

    val headSway by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "headSway",
    )

    val targetScale = if (scaledDown) 0.7f else 1.0f

    Box(modifier = modifier) {
        Scene(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Haptic.tap(context)
                            onTap()
                        },
                        onLongPress = {
                            Haptic.press(context)
                            onLongPress()
                        },
                    )
                },
            engine = engine,
            modelLoader = modelLoader,
            environment = environment,
            isOpaque = false,
        ) {
            rememberModelInstance(modelLoader, "models/doomsy.glb")?.let { modelInstance ->
                ModelNode(
                    modelInstance = modelInstance,
                    scaleToUnits = targetScale,
                    autoAnimate = true,
                ).apply {
                    position = Position(y = breathOffset)
                    rotation = Rotation(y = headSway)
                }
            }
        }
    }
}
```

Note: The 3D model file (`doomsy.glb`) must be placed at `app/src/main/assets/models/doomsy.glb` before this can be tested on-device. Generate it from Meshy using the prompt in the spec, export as GLB, and copy it there.

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat: 3D model viewer with SceneView, breathing animation, touch gestures"
```

---

## Task 9: Quip Overlay

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/main/QuipOverlay.kt`

- [ ] **Step 1: Implement QuipOverlay.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/main/QuipOverlay.kt`:
```kotlin
package com.mrbitches.doomsy.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.ui.theme.GlassWhite
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.Anim
import kotlinx.coroutines.delay

@Composable
fun QuipOverlay(
    quip: String?,
    modifier: Modifier = Modifier,
    onDismissed: () -> Unit = {},
) {
    var visible by remember(quip) { mutableStateOf(quip != null) }

    LaunchedEffect(quip) {
        if (quip != null) {
            visible = true
            delay(Anim.QUIP_DISPLAY_DURATION)
            visible = false
            delay(Anim.QUIP_FADE_DURATION)
            onDismissed()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(spring()) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = 300f,
                ),
            ),
            exit = fadeOut() + scaleOut(targetScale = 0.9f),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = quip ?: "",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = OffWhite,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat: quip overlay with springy entrance and auto-dismiss"
```

---

## Task 10: DOOM Tracks Carousel

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/main/TrackCard.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/main/TracksCarousel.kt`

- [ ] **Step 1: Implement TrackCard.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/main/TrackCard.kt`:
```kotlin
package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.DoomTrack
import com.mrbitches.doomsy.ui.theme.GlassWhite
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.SpotifyIntent

@Composable
fun TrackCard(track: DoomTrack, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(GlassWhite)
            .border(1.dp, GlassWhiteBorder, shape)
            .clickable { SpotifyIntent.open(context, track.spotifyUri) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = track.name,
            style = MaterialTheme.typography.labelLarge.copy(color = OffWhite),
            maxLines = 1,
        )
        Text(
            text = track.album,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
    }
}
```

- [ ] **Step 2: Implement TracksCarousel.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/main/TracksCarousel.kt`:
```kotlin
package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.DoomTracks
import com.mrbitches.doomsy.ui.theme.Gold

@Composable
fun TracksCarousel(modifier: Modifier = Modifier) {
    val tracks = remember { DoomTracks.randomSelection() }

    Column(modifier = modifier) {
        Text(
            text = "Doomsy is but a vessel. DOOM is the scripture.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Gold,
                fontStyle = FontStyle.Italic,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(tracks, key = { it.spotifyUri }) { track ->
                TrackCard(track = track)
            }
        }
    }
}
```

- [ ] **Step 3: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: DOOM tracks carousel with frosted glass cards and Spotify deep links"
```

---

## Task 11: Chat UI — Bubbles & Typewriter Effect

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/chat/ChatBubble.kt`

- [ ] **Step 1: Implement ChatBubble.kt with typewriter effect**

Create `app/src/main/java/com/mrbitches/doomsy/ui/chat/ChatBubble.kt`:
```kotlin
package com.mrbitches.doomsy.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.GlassWhite
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalLight
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.Anim
import kotlinx.coroutines.delay

@Composable
fun ChatBubble(message: Message, animate: Boolean = false) {
    val isUser = message.isUser

    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp,
    )

    // Typewriter effect for Doomsy's messages
    var visibleChars by remember(message.text) { mutableIntStateOf(if (animate) 0 else message.text.length) }

    if (animate && visibleChars < message.text.length) {
        LaunchedEffect(message.text) {
            while (visibleChars < message.text.length) {
                delay(Anim.TYPEWRITER_CHAR_DELAY)
                visibleChars++
            }
        }
    }

    val displayText = message.text.take(visibleChars)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(if (isUser) GunmetalLight else GlassWhite)
                .border(
                    width = 0.5.dp,
                    color = if (isUser) GlassWhiteBorder else Gold.copy(alpha = 0.3f),
                    shape = shape,
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isUser) OffWhite else OffWhite,
                ),
            )
        }
    }
}
```

- [ ] **Step 2: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat: chat bubbles with typewriter reveal for Doomsy responses"
```

---

## Task 12: Chat Panel & Voice Input

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/chat/VoiceInputButton.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/chat/ChatPanel.kt`

- [ ] **Step 1: Implement VoiceInputButton.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/chat/VoiceInputButton.kt`:
```kotlin
package com.mrbitches.doomsy.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalLight
import com.mrbitches.doomsy.ui.theme.OffWhite
import java.util.Locale

@Composable
fun VoiceInputButton(
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (isListening) Gold else GunmetalLight,
        label = "micBg",
    )

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    DisposableEffect(Unit) {
        onDispose { speechRecognizer.destroy() }
    }

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    fun startListening() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) return

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { onResult(it) }
            }
            override fun onError(error: Int) { isListening = false }
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        isListening = true
        speechRecognizer.startListening(recognizerIntent)
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable {
                if (isListening) {
                    speechRecognizer.stopListening()
                    isListening = false
                } else {
                    startListening()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // Mic icon as text (avoids adding icon dependency)
        Text(
            text = if (isListening) "..." else "mic",
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isListening) OffWhite else Gold,
            ),
        )
    }
}
```

- [ ] **Step 2: Implement ChatPanel.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/chat/ChatPanel.kt`:
```kotlin
package com.mrbitches.doomsy.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.GlassWhite
import com.mrbitches.doomsy.ui.theme.GlassWhiteBorder
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalGrey
import com.mrbitches.doomsy.ui.theme.MutedGrey
import com.mrbitches.doomsy.ui.theme.OffWhite
import com.mrbitches.doomsy.util.Haptic

@Composable
fun ChatPanel(
    visible: Boolean,
    messages: List<Message>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
            initialOffsetY = { it },
        ),
        exit = slideOutVertically(
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
            targetOffsetY = { it },
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(GunmetalGrey.copy(alpha = 0.85f))
                .border(
                    width = 0.5.dp,
                    color = GlassWhiteBorder,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                )
                .padding(top = 12.dp)
                .imePadding(),
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MutedGrey)
                    .align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Message list
            val listState = rememberLazyListState()

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages, key = { it.timestamp }) { message ->
                    ChatBubble(
                        message = message,
                        animate = !message.isUser && message == messages.lastOrNull(),
                    )
                }

                if (isGenerating) {
                    item {
                        Text(
                            text = "The villain contemplates...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Gold.copy(alpha = 0.6f),
                            ),
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input bar
            ChatInputBar(
                onSend = { text ->
                    Haptic.tap(context)
                    onSendMessage(text)
                },
                enabled = !isGenerating,
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    onSend: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoiceInputButton(
            onResult = { spoken ->
                if (spoken.isNotBlank()) {
                    onSend(spoken)
                }
            },
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = OffWhite),
            cursorBrush = SolidColor(Gold),
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(DeepBlack)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text = "Talk to Doomsy...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = MutedGrey),
                        )
                    }
                    innerTextField()
                }
            },
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send button
        val canSend = text.isNotBlank() && enabled
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (canSend) Gold else MutedGrey.copy(alpha = 0.3f))
                .then(
                    if (canSend) Modifier.clickable {
                        onSend(text)
                        text = ""
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u25B6",
                style = MaterialTheme.typography.labelLarge.copy(color = DeepBlack),
            )
        }
    }
}
```

Add this import to ChatPanel.kt:
```kotlin
import androidx.compose.foundation.clickable
```

- [ ] **Step 3: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: chat panel with voice input, frosted glass bottom sheet, typewriter bubbles"
```

---

## Task 13: Main Screen

**Files:**
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/main/MainScreen.kt`
- Create: `app/src/main/java/com/mrbitches/doomsy/ui/main/DoomsyViewModel.kt`

- [ ] **Step 1: Implement DoomsyViewModel.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/main/DoomsyViewModel.kt`:
```kotlin
package com.mrbitches.doomsy.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrbitches.doomsy.data.DoomsyQuips
import com.mrbitches.doomsy.data.Message
import com.mrbitches.doomsy.llm.ConversationManager
import com.mrbitches.doomsy.llm.LlamaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class DoomsyViewModel(app: Application) : AndroidViewModel(app) {

    private val llamaBridge = LlamaBridge()
    private val conversationManager = ConversationManager()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded

    private val _currentQuip = MutableStateFlow<String?>(null)
    val currentQuip: StateFlow<String?> = _currentQuip

    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen

    fun triggerQuip() {
        _currentQuip.value = DoomsyQuips.random()
    }

    fun dismissQuip() {
        _currentQuip.value = null
    }

    fun openChat() {
        _isChatOpen.value = true
        if (!llamaBridge.isLoaded()) {
            loadModel()
        }
    }

    fun closeChat() {
        _isChatOpen.value = false
        // Unload model to free memory
        viewModelScope.launch(Dispatchers.IO) {
            llamaBridge.unloadModel()
            _isModelLoaded.value = false
        }
        conversationManager.clear()
        _messages.value = emptyList()
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        val userMessage = Message(text = text, isUser = true)
        _messages.value = _messages.value + userMessage
        _isGenerating.value = true

        viewModelScope.launch(Dispatchers.IO) {
            if (!llamaBridge.isLoaded()) {
                val errorMsg = Message(
                    text = "The villain's mind is elsewhere. Try again.",
                    isUser = false,
                )
                _messages.value = _messages.value + errorMsg
                _isGenerating.value = false
                return@launch
            }

            val prompt = conversationManager.buildPrompt(text)

            // Run generation with timeout
            val response = try {
                kotlinx.coroutines.withTimeout(10_000L) {
                    llamaBridge.generate(prompt, maxTokens = 256)
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                null
            }

            val cleanResponse = if (response.isNullOrBlank() || response.startsWith("[Error")) {
                "Doomsy contemplates in silence..."
            } else {
                response.trim()
            }

            conversationManager.addExchange(text, cleanResponse)

            val doomsyMessage = Message(text = cleanResponse, isUser = false)
            _messages.value = _messages.value + doomsyMessage
            _isGenerating.value = false
        }
    }

    private fun loadModel() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val modelFile = File(context.filesDir, "gemma-4-e2b-q4_k_m.gguf")

            // Copy from assets on first run
            if (!modelFile.exists()) {
                context.assets.open("gemma-4-e2b-q4_k_m.gguf").use { input ->
                    FileOutputStream(modelFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val loaded = llamaBridge.loadModel(modelFile.absolutePath, contextSize = 1024)
            _isModelLoaded.value = loaded
        }
    }

    override fun onCleared() {
        super.onCleared()
        llamaBridge.unloadModel()
    }
}
```

- [ ] **Step 2: Implement MainScreen.kt**

Create `app/src/main/java/com/mrbitches/doomsy/ui/main/MainScreen.kt`:
```kotlin
package com.mrbitches.doomsy.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrbitches.doomsy.ui.chat.ChatPanel
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.Gold
import com.mrbitches.doomsy.ui.theme.GunmetalGrey
import com.mrbitches.doomsy.ui.theme.OffWhite

@Composable
fun MainScreen(viewModel: DoomsyViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentQuip by viewModel.currentQuip.collectAsState()
    val isChatOpen by viewModel.isChatOpen.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlack, GunmetalGrey, DeepBlack),
                ),
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 3D Viewer — top portion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isChatOpen) 0.4f else 0.6f),
            ) {
                DoomsyViewer(
                    modifier = Modifier.fillMaxSize(),
                    onTap = {
                        if (!isChatOpen) viewModel.triggerQuip()
                    },
                    onLongPress = { /* head turn handled in DoomsyViewer */ },
                    scaledDown = isChatOpen,
                )

                // Quip overlay
                QuipOverlay(
                    quip = currentQuip,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    onDismissed = { viewModel.dismissQuip() },
                )
            }

            // Tracks carousel (hidden when chat is open)
            if (!isChatOpen) {
                TracksCarousel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Chat trigger button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Gold)
                        .clickable { viewModel.openChat() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u2026",
                        style = MaterialTheme.typography.headlineMedium.copy(color = DeepBlack),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Chat panel (bottom sheet)
        ChatPanel(
            visible = isChatOpen,
            messages = messages,
            isGenerating = isGenerating,
            onSendMessage = { viewModel.sendMessage(it) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
```

- [ ] **Step 3: Verify build compiles**

```bash
./gradlew compileDebugKotlin
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: main screen with 3D viewer, tracks, quips, and chat orchestration"
```

---

## Task 14: Wire Up MainActivity

**Files:**
- Modify: `app/src/main/java/com/mrbitches/doomsy/MainActivity.kt`

- [ ] **Step 1: Update MainActivity.kt**

Replace the contents of `app/src/main/java/com/mrbitches/doomsy/MainActivity.kt` with:
```kotlin
package com.mrbitches.doomsy

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.mrbitches.doomsy.ui.intro.IntroScreen
import com.mrbitches.doomsy.ui.main.MainScreen
import com.mrbitches.doomsy.ui.theme.DeepBlack
import com.mrbitches.doomsy.ui.theme.DoomsyTheme

class MainActivity : ComponentActivity() {

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not — voice just won't work if denied */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge, transparent system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Request mic permission for voice input
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            DoomsyTheme {
                var introComplete by remember { mutableStateOf(false) }

                if (!introComplete) {
                    IntroScreen(onIntroComplete = { introComplete = true })
                } else {
                    MainScreen()
                }
            }
        }
    }
}
```

- [ ] **Step 2: Verify full build**

```bash
./gradlew assembleDebug
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat: wire up MainActivity with intro -> main flow, edge-to-edge, mic permission"
```

---

## Task 15: Asset Preparation & Device Testing

This task covers the manual steps needed before the APK works on a real device.

- [ ] **Step 1: Generate 3D model from Meshy**

Go to [meshy.ai](https://www.meshy.ai/), create a free account, and use "Text to 3D":

> "Funko Pop vinyl figure of MF DOOM, the rapper. Iconic brushed silver metal doom mask covering entire face with rectangular eye slits and rivets. Brown skin visible at neck. Dark green military-style hooded jacket, hood down. Gold chain around neck. Baggy dark jeans. Timberland-style tan boots. Arms at sides, slightly relaxed stance. Oversized chibi head proportions typical of Funko Pop figures. Standing on small round black pedestal. Clean geometry, matte vinyl material finish, studio lighting."

Export as `.glb`. Place at:
```bash
mkdir -p app/src/main/assets/models
cp ~/Downloads/doomsy.glb app/src/main/assets/models/doomsy.glb
```

- [ ] **Step 2: Download Gemma 4 E2B GGUF model**

Download from HuggingFace:
```bash
# Using huggingface-cli or wget
# The file is ~1.3GB
wget -O app/src/main/assets/gemma-4-e2b-q4_k_m.gguf \
  "https://huggingface.co/unsloth/gemma-4-E2B-it-GGUF/resolve/main/gemma-4-E2B-it-Q4_K_M.gguf"
```

Note: this file is in `.gitignore` due to its size. Transfer it to the build machine separately if needed.

- [ ] **Step 3: Verify Spotify track URIs**

On a device with Spotify installed, test a few URIs by opening them in a browser:
```
https://open.spotify.com/track/55fmthmn3rgnk9Wyx7G5dU  (Rapp Snitch Knishes)
https://open.spotify.com/track/1FDcMuwdJD1nan1HKBM71I  (Accordion)
https://open.spotify.com/track/7i09RLbBT8m0LvH2dYiJqp  (Doomsday)
```

If any URI leads to the wrong track, find the correct one by searching on Spotify and updating `DoomTracks.kt`.

- [ ] **Step 4: Build release APK**

```bash
./gradlew assembleRelease
```

The unsigned release APK will be at `app/build/outputs/apk/release/app-release-unsigned.apk`. For sideloading, a debug build works too:

```bash
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

- [ ] **Step 5: Install and test on device**

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Test checklist:**
1. Intro sequence plays — haptic rumble, mask fades in, message appears, fades to main
2. 3D model loads and renders — can spin/tilt/zoom
3. Tap Doomsy — quip appears in floating bubble, auto-dismisses
4. Long press Doomsy — haptic feedback
5. Tracks carousel shows — 10 random tracks, tapping opens Spotify
6. Tap chat button — bottom sheet slides up with spring animation
7. Type a message — Doomsy responds with typewriter effect
8. Tap mic — voice recognition starts, transcribed text sent to Doomsy
9. Close chat — model unloads, memory drops
10. Kill and reopen app — tracks carousel shows different selection

- [ ] **Step 6: Commit any fixes from device testing**

```bash
git add -A
git commit -m "fix: adjustments from device testing"
```

---

## Task 16: Final Polish

**Files:**
- Potentially modify any file for polish adjustments

- [ ] **Step 1: Add app icon**

Create a simple launcher icon — the DOOM mask silhouette in gold on black background. Use Android Studio's Image Asset tool or place adaptive icon files manually:

```bash
mkdir -p app/src/main/res/mipmap-xxxhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-mdpi
```

For a quick approach, create a simple vector drawable icon and reference it from the manifest.

- [ ] **Step 2: Test memory usage on target device**

```bash
adb shell dumpsys meminfo com.mrbitches.doomsy
```

Verify:
- Main screen (no chat): under 400MB
- Chat open (model loaded): under 2GB
- After closing chat: memory drops back near 400MB

- [ ] **Step 3: Final APK for Nath**

```bash
./gradlew assembleDebug
```

Transfer `app/build/outputs/apk/debug/app-debug.apk` to Nath's phone via:
- ADB
- File sharing (Google Drive, Telegram, etc.)
- Direct USB transfer

- [ ] **Step 4: Final commit**

```bash
git add -A
git commit -m "feat: Doomsy v1.0 — birthday gift for Nath, from Mr Bitches"
```
