# Doomsy -- Development Handoff

## Project Status

All application code is written and committed (14 commits on `main`). The project needs assets, build verification, and device testing on a machine with Android Studio.

## Commit History

```
322b6f7 feat: wire up MainActivity with intro -> main flow, edge-to-edge, mic permission
fe92ac2 feat: main screen with 3D viewer, tracks, quips, and chat orchestration
98839fd feat: chat panel with voice input, frosted glass bottom sheet, typewriter bubbles
88e6efb feat: chat bubbles with typewriter reveal for Doomsy responses
599bc3c feat: DOOM tracks carousel with frosted glass cards and Spotify deep links
d4869fe feat: quip overlay with springy entrance and auto-dismiss
77cdf4e feat: 3D model viewer with SceneView, breathing animation, touch gestures
3024cfd feat: cinematic intro screen with haptic rumble and mask fade-in
42d53ce feat: conversation manager with Gemma chat template and history cap
c973ebe feat: llama.cpp NDK integration with JNI bridge
736ec59 feat: Spotify deep link intent helper with tests
5bc157f feat: data layer — Message, DoomTracks, DoomsyQuips with tests
abcd563 feat: dark metallic theme, spring animations, haptic patterns
8c5f370 feat: project scaffold with Gradle, NDK, Compose, SceneView deps
```

## Remaining Tasks

### Task 15: Asset Preparation & Build

- [ ] **15a: Download Inter font**
  - Get Inter font family from https://fonts.google.com/specimen/Inter
  - Place these files in `app/src/main/res/font/`:
    - `inter_regular.ttf`
    - `inter_medium.ttf`
    - `inter_bold.ttf`
    - `inter_light.ttf`

- [ ] **15b: Generate 3D model from Meshy**
  - Go to https://www.meshy.ai/ (free tier)
  - Use "Text to 3D" with this prompt:
    > "Funko Pop vinyl figure of MF DOOM, the rapper. Iconic brushed silver metal doom mask covering entire face with rectangular eye slits and rivets. Brown skin visible at neck. Dark green military-style hooded jacket, hood down. Gold chain around neck. Baggy dark jeans. Timberland-style tan boots. Arms at sides, slightly relaxed stance. Oversized chibi head proportions typical of Funko Pop figures. Standing on small round black pedestal. Clean geometry, matte vinyl material finish, studio lighting."
  - Export as `.glb`
  - Place at `app/src/main/assets/models/doomsy.glb`
  - Keep under 5MB if possible

- [ ] **15c: Download Gemma 4 E2B GGUF model**
  - Download from HuggingFace (~1.3GB):
    ```bash
    wget -O app/src/main/assets/gemma-4-e2b-q4_k_m.gguf \
      "https://huggingface.co/unsloth/gemma-4-E2B-it-GGUF/resolve/main/gemma-4-E2B-it-Q4_K_M.gguf"
    ```
  - This file is in `.gitignore` due to size -- don't commit it

- [ ] **15d: Open project in Android Studio**
  - Android Studio will auto-download Gradle wrapper, SDK, and NDK
  - First build will take a while (llama.cpp compiles for arm64-v8a)
  - If CMake version issues: install CMake 3.22.1 via SDK Manager > SDK Tools > CMake

- [ ] **15e: Fix any build errors**
  - SceneView API may have changed -- check `DoomsyViewer.kt` against latest SceneView 3.6.2 docs
  - llama.cpp API may have drifted -- check `llama_jni.cpp` against current `llama.h` headers
  - If `common` library target not found in CMake, try removing it from `target_link_libraries` in `app/src/main/cpp/CMakeLists.txt`

- [ ] **15f: Verify Spotify track URIs**
  - On a device with Spotify, tap a few track cards
  - If any URI opens the wrong track, search on Spotify and update the track ID in `app/src/main/java/com/mrbitches/doomsy/data/DoomTracks.kt`
  - URI format: `spotify:track:{TRACK_ID}`

- [ ] **15g: Run unit tests**
  ```bash
  ./gradlew test
  ```
  - 4 test files: DoomTracksTest, DoomsyQuipsTest, ConversationManagerTest, SpotifyIntentTest

### Task 16: Device Testing & Polish

- [ ] **16a: Install on device and test intro**
  ```bash
  ./gradlew installDebug
  ```
  - Verify: haptic rumble at 0.5s, mask fades in, message appears, fades to main screen

- [ ] **16b: Test 3D model**
  - Model loads and renders
  - Drag to spin/tilt, pinch to zoom
  - Idle breathing animation visible
  - Tap triggers quip bubble
  - Long press triggers haptic

- [ ] **16c: Test tracks carousel**
  - 10 random tracks shown below headline
  - Tapping a card opens Spotify
  - Kill and reopen app -- different track selection

- [ ] **16d: Test chat**
  - Tap gold button -- chat panel slides up with spring animation
  - Type a message -- Doomsy responds with typewriter reveal
  - Tap mic -- voice recognition starts (requires RECORD_AUDIO permission)
  - Model loads on first chat open (~2-3 seconds)
  - Error states: if model fails, shows "The villain's mind is elsewhere"

- [ ] **16e: Test memory usage**
  ```bash
  adb shell dumpsys meminfo com.mrbitches.doomsy
  ```
  - Main screen (no chat): under 400MB
  - Chat open (model loaded): under 2GB
  - After closing chat: drops back near 400MB

- [ ] **16f: Add app icon**
  - Use Android Studio > New > Image Asset
  - Create a DOOM mask silhouette icon (gold on black)
  - Or use any simple icon for now

- [ ] **16g: Build final APK for Nath**
  ```bash
  ./gradlew assembleDebug
  ```
  - APK at: `app/build/outputs/apk/debug/app-debug.apk`
  - Transfer to Nath's phone via USB, Telegram, Google Drive, etc.
  - He needs to enable "Install from unknown sources" to sideload

## Key Files Reference

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | Dependencies, NDK config, SDK versions |
| `app/src/main/cpp/CMakeLists.txt` | llama.cpp build config |
| `app/src/main/cpp/llama_jni.cpp` | C++ JNI bridge to llama.cpp |
| `app/src/main/java/.../llm/LlamaBridge.kt` | Kotlin JNI interface |
| `app/src/main/java/.../llm/DoomsyPersonality.kt` | System prompt with all inside jokes |
| `app/src/main/java/.../llm/ConversationManager.kt` | Chat history + Gemma prompt template |
| `app/src/main/java/.../data/DoomTracks.kt` | 25 DOOM tracks with Spotify URIs |
| `app/src/main/java/.../data/DoomsyQuips.kt` | 15 tap quips |
| `app/src/main/java/.../ui/main/DoomsyViewModel.kt` | All app state + LLM orchestration |
| `app/src/main/java/.../ui/main/MainScreen.kt` | Main screen layout |
| `app/src/main/java/.../ui/intro/IntroScreen.kt` | Cinematic intro sequence |

## Design Docs

- Spec: `docs/superpowers/specs/2026-04-11-doomsy-design.md`
- Implementation plan: `docs/superpowers/plans/2026-04-11-doomsy-implementation.md`
