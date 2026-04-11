# Doomsy -- Design Spec

## Overview

A birthday gift Android app for Nath from Mr Bitches. A 3D interactive MF DOOM funko-pop character called Doomsy lives on Nath's phone. Doomsy is a villain-poet who channels Mr Bitches' friendship -- roasts Nath, drops inside jokes, speaks in DOOM's cryptic third-person style, but underneath it all reminds Nath his boy is always around.

**Target device:** Mid-range Samsung (6GB+ RAM, Android 10+)
**Distribution:** Sideloaded APK, not Play Store

---

## App Flow

### 1. Cinematic Intro (plays every cold start)

| Time | Event |
|------|-------|
| 0.0s | Pure black screen |
| 0.5s | Deep haptic bass rumble begins |
| 1.0s | Gold glint appears center screen, DOOM mask silhouette fades in |
| 2.5s | Mask fully visible, subtle metallic shimmer animation |
| 3.5s | Message fades in below mask: *"Since your ass is busy (again), Mr. Bitches sent me, pussy"* |
| 5.5s | Everything fades, transitions to main screen |

### 2. Main Screen

- Doomsy's 3D funko-pop model centered on dark metallic backdrop
- Idle breathing animation, subtle head sway
- Nath can spin/tilt/zoom the model
- Tapping Doomsy triggers random pre-loaded quips (no LLM)
- Below Doomsy: headline *"Doomsy is but a vessel. DOOM is the scripture."*
- Below headline: horizontal scrollable carousel of 10 randomly selected DOOM tracks (from a pool of 25+), each opening Spotify via deep link
- Chat button at bottom to enter chat mode

### 3. Chat Mode

- Bottom sheet slides up covering ~60% of screen with springy overshoot
- Frosted glass background, gold accent on send button
- Text input bar at bottom with mic icon for voice input
- Doomsy's responses appear with typewriter-style reveal
- 3D model stays visible in top 40%, scales down, reacts with head nods
- Max ~10 messages per session before context drops oldest

### 4. Idle State

- When not chatting, Doomsy vibes with slow breathing animation
- Occasional subtle head movement
- Tapping triggers random quips from pre-loaded pool

---

## Visual Design

### Palette

| Role | Color | Hex |
|------|-------|-----|
| Background dark | Deep black | #0A0A0A |
| Background mid | Gunmetal grey | #1C1C1E |
| Accent | Gold | #D4AF37 |
| Secondary | Brushed silver | #8E8E93 |
| Text primary | Off-white | #F5F5F7 |
| Text secondary | Muted grey | #6E6E73 |
| Glass cards | White 8-10% opacity | rgba(255,255,255,0.08-0.10) |

### UX Language (Not Boring Weather DNA)

- Spring-based animations everywhere -- nothing linear, everything has bounce and weight
- Frosted glass cards with blur backdrop
- Large touch targets, generous padding
- Typography: Inter, bold headings, light body
- Haptic feedback on key interactions (intro rumble, tap reactions, send message)
- Parallax-style depth -- subtle background movement on phone tilt (gyroscope)
- No navigation chrome, no hamburger menus, no settings. One screen. Doomsy and chat.

---

## 3D Model

### Generation

Use Meshy (free tier) or equivalent free AI 3D generator.

**Prompt:**
> "Funko Pop vinyl figure of MF DOOM, the rapper. Iconic brushed silver metal doom mask covering entire face with rectangular eye slits and rivets. Brown skin visible at neck. Dark green military-style hooded jacket, hood down. Gold chain around neck. Baggy dark jeans. Timberland-style tan boots. Arms at sides, slightly relaxed stance. Oversized chibi head proportions typical of Funko Pop figures. Standing on small round black pedestal. Clean geometry, matte vinyl material finish, studio lighting."

Export as `.glb`, optimize mesh to under 5MB.

### Rendering

- SceneView library (Filament engine)
- Single directional light + subtle ambient light
- Dark environment map for metallic mask reflections
- No ground shadows (saves GPU on mid-range hardware)

### Interactions

| Gesture | Response |
|---------|----------|
| Drag horizontal | Rotate model on Y axis |
| Drag vertical | Tilt model on X axis (clamped ~30 degrees) |
| Pinch | Zoom in/out (clamped range) |
| Single tap | Random quip as floating text bubble + head nod animation |
| Long press | Subtle vibration + slow dramatic head turn |
| Idle (5s+ no touch) | Slow breathing animation, subtle head sway |

### Tap Quips (pre-loaded, no LLM)

- "Nathaniel Leo Messi Syiem... the villain watches."
- "Mr Bitches sends his regards."
- "You talmbout tapping Doomsy in big 2026?"
- "Doomsy don't flinch. Doomsy observes."
- "Ask about the car money. Doomsy dares you."
- "UTDBenj remembers. Doomsy remembers. We all remember."
- "The villain is idle. The villain is never truly idle."
- "Carti fan caught in the wild. The villain takes notes."
- "Doomsy sees all. Doomsy judges silently."
- "The mask don't lie. You owe somebody a race."
- "Mr Bitches whispers through the mask. He says wassup."
- "Smoke? ...no? Doomsy expected more from Nathaniel."
- "The villain awaits your words. Or your excuses."
- "DOOM taught Doomsy patience. Mr Bitches taught Doomsy loyalty."
- "She who must not be named... still got that car money though."

---

## DOOM Tracks Carousel

### Concept

On every cold start, 10 tracks are randomly selected from a pool of 25+ hardcoded DOOM songs. Displayed as a horizontal scrollable carousel below the headline on the main screen.

**Headline:** *"Doomsy is but a vessel. DOOM is the scripture."*

### UI

- Horizontal scrollable row of frosted glass pill cards
- Each card shows: track name + album name
- Tapping a card fires a Spotify deep link intent (opens Spotify app if installed, browser fallback)
- No internet permission needed from our app -- Android handles the intent

### Song Pool

| Track | Album |
|-------|-------|
| Doomsday | Operation: Doomsday |
| Rhymes Like Dimes | Operation: Doomsday |
| Gas Drawls | Operation: Doomsday |
| Hey! | Operation: Doomsday |
| Books of War | Operation: Doomsday |
| Accordion | Madvillainy |
| All Caps | Madvillainy |
| Meat Grinder | Madvillainy |
| Figaro | Madvillainy |
| Rhinestone Cowboy | Madvillainy |
| Curls | Madvillainy |
| Raid | Madvillainy |
| Strange Ways | Madvillainy |
| Rapp Snitch Knishes | MM..FOOD |
| One Beer | MM..FOOD |
| Beef Rapp | MM..FOOD |
| Potholderz | MM..FOOD |
| Hoe Cakes | MM..FOOD |
| Vomitspit | Vaudeville Villain |
| Lickupon | Vaudeville Villain |
| Let Me Watch | Vaudeville Villain |
| That's That | Born Like This |
| Cellz | Born Like This |
| Gazzillion Ear | Born Like This |
| Kon Karne | Take Me to Your Leader |

Spotify track URLs to be resolved during implementation.

---

## LLM Integration

### Model

Gemma 4 E2B, Q4_K_M quantization (~1.3GB GGUF file)

### Runtime

llama.cpp compiled for Android via NDK, called through JNI bridge from Kotlin.

### Memory Strategy

- Model loaded lazily -- only when chat panel opens, not on app launch
- Context window capped at 1024 tokens
- Conversation history: last 10 messages max, oldest drop off
- Model unloaded when chat closes or app backgrounded
- Loading state: Doomsy's mask eyes glow briefly while model initializes (~2-3s)

### System Prompt

```
You are Doomsy, a sentient funko pop incarnation of MF DOOM, sent by Mr Bitches
to watch over his friend Nath. You speak in MF DOOM's style -- third person,
cryptic, villainous, lyrical. But underneath the bars, you carry Mr Bitches'
warmth. You are a reminder that Mr Bitches is always around even when he's not
physically there.

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
```

### Input

- **Text:** Standard keyboard input
- **Voice:** Android `SpeechRecognizer` API -- tap mic icon, speak, transcribed text sent to LLM as text
- Both inputs are identical from the LLM's perspective

### Output

- Text only -- chat bubbles with typewriter reveal animation
- No TTS, no audio output

### Error Handling

- Model fails to load: quip "The villain's mind is elsewhere. Try again." + retry button
- Response timeout (>10s): "Doomsy contemplates in silence..."
- Gibberish input: LLM handles naturally in character

---

## Technical Architecture

### Stack

- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose + Material 3 (heavily restyled)
- **3D:** SceneView (Filament engine)
- **LLM:** llama.cpp via NDK/JNI
- **Voice:** Android SpeechRecognizer API
- **Build:** Gradle KTS, minSdk 26, targetSdk 35

### Project Structure

```
app/
  src/main/
    java/com/mrbitches/doomsy/
      MainActivity.kt                # Single activity entry point
      ui/
        theme/
          Theme.kt                   # Dark metallic palette, typography
          Color.kt                   # Gold, gunmetal, glass values
          Type.kt                    # Inter font family
        intro/
          IntroScreen.kt             # Cinematic intro sequence
        main/
          MainScreen.kt              # 3D model + tracks + chat orchestration
          DoomsyViewer.kt            # SceneView wrapper, gestures, animations
          QuipOverlay.kt             # Floating tap-quip text bubbles
          TracksCarousel.kt          # DOOM tracks horizontal carousel
          TrackCard.kt               # Individual frosted glass track pill
        chat/
          ChatPanel.kt              # Bottom sheet, message list, input bar
          ChatBubble.kt             # Styled message bubbles with typewriter
          VoiceInputButton.kt       # Mic button + SpeechRecognizer
      llm/
        LlamaBridge.kt              # JNI interface to llama.cpp
        DoomsyPersonality.kt        # System prompt, conversation management
        ConversationManager.kt      # Message history, 10-msg cap, context
      data/
        Message.kt                  # Data class for chat messages
        DoomTracks.kt               # Hardcoded track list + Spotify URLs
        DoomsyQuips.kt              # Pre-loaded quip strings
      util/
        HapticUtil.kt               # Haptic feedback patterns
        AnimationUtil.kt            # Spring specs, shared animation configs
        SpotifyIntent.kt            # Spotify deep link intent helper
    assets/
      doomsy.glb                    # 3D model (<5MB)
      gemma-4-e2b-q4_k_m.gguf      # LLM model (~1.3GB)
    res/
      font/inter/                   # Inter font files
  cpp/
    CMakeLists.txt                  # NDK build config for llama.cpp
    llama_jni.cpp                   # JNI bridge
  build.gradle.kts
```

### Dependencies

- `io.github.sceneview:sceneview` -- 3D rendering + Filament
- `llama.cpp` -- compiled from source via CMake/NDK
- Jetpack Compose BOM
- `androidx.compose.material3`
- `accompanist` -- system UI controller (edge-to-edge, transparent status bar)
- No network permissions. Fully offline.

### APK Distribution

- GGUF model (~1.3GB) bundled in assets
- Total APK: ~1.4-1.5GB
- Sideloaded to Nath's phone, install once, never needs internet

### Memory Budget (mid-range 6GB Samsung)

| Component | RAM |
|-----------|-----|
| Android OS | ~3-4GB |
| Gemma 4 E2B Q4_K_M (loaded on chat open) | ~1.3-1.5GB |
| KV cache (1024 tokens, 10 messages) | ~100-150MB |
| 3D model + Filament renderer | ~100-200MB |
| App UI (Compose, animations) | ~100-150MB |
| **Total app** | **~1.6-2.0GB** |

Model lazy-loaded on chat open, unloaded on chat close / background. Main screen with 3D + carousel runs at ~300-400MB before chat opens.
