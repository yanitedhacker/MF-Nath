# Doomsy

A small, opinionated **Android** companion app built as a fun **open-source birthday project**: a stylized chat experience around a masked “villain” hero, a **3D GLB** bust, a **music discovery** carousel with **Spotify** deep links, and optional **Cloudflare Workers AI** for replies so the system prompt and model calls stay off the device.

This repository is meant to be forked, themed, and self-hosted—swap assets, prompts, and API endpoints without touching the core UI flow.

---

## Features

| Area | What it does |
|------|----------------|
| **UI** | Jetpack Compose + Material 3, custom palette (high-contrast light base, orange accent), intro → main flow (tap to skip; remembered after first run), edge-to-edge layout. Tablets and landscape use a **two-pane** mask + chat layout. |
| **3D** | [SceneView](https://github.com/SceneView/sceneview-android) loads `assets/models/doomsy.glb` with idle motion, tap/long-press quips, and gesture-friendly scaling. Low-RAM phones **skip** the GLB (2D mask fallback); mid-range heaps **downscale** it. |
| **Chat** | Scrollable message stack, composer with keyboard Send, optional voice input (`RECORD_AUDIO`). Cloud replies **stream tokens** into the assistant bubble. Last messages persist across launches; **clear** and **export** are in the toolbar. |
| **Voice** | Assistant replies can be spoken with Android TTS. **Mute** is remembered on device. |
| **Tracks** | Curated pool of tracks (multiple artists); horizontal carousel; each card opens `spotify:track:…` via `Intent`. **Shuffle refreshes** each time the tracks panel is opened. |
| **Cloud (optional)** | HTTP `POST` to a Cloudflare Worker at `/chat`; Worker runs **Workers AI** with a server-side system prompt. App probes `GET /health` on launch. If no base URL is configured, the app uses **local fallback** copy (no network LLM). Optional `DOOMSY_API_KEY` / `X-Doomsy-Key` auth; `/chat` stays public when the secret is unset. |

---

## Architecture (high level)

```
┌─────────────────────────────────────────────────────────────┐
│  Android app (Compose)                                       │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────────┐ │
│  │ MainScreen  │  │ DoomsyHero   │  │ Chat + ViewModel    │ │
│  │ (two-pane)  │  │ (SceneView)  │  │ → ConversationMgr   │ │
│  └─────────────┘  └──────────────┘  └──────────┬──────────┘ │
│                                                 │            │
│                     ┌────────────────────────────▼────────┐  │
│                     │ DoomsyCloudClient (if BuildConfig   │  │
│                     │ DOOMSY_API_BASE_URL non-empty)      │  │
│                     │ Accept: text/event-stream           │  │
│                     │ optional X-Doomsy-Key               │  │
│                     └────────────────────────────┬────────┘  │
└──────────────────────────────────────────────────┼──────────┘
                                                   │ HTTPS POST /chat
                                                   ▼
┌──────────────────────────────────────────────────────────────┐
│  Cloudflare Worker (`worker/`)                             │
│  • POST /chat  JSON: { message, history }                  │
│  • SSE when Accept includes text/event-stream              │
│  • Optional DOOMSY_API_KEY (X-Doomsy-Key)                  │
│  • CORS enabled for mobile clients                          │
│  • Workers AI: env.AI.run(model, { messages, … })           │
│  • System prompt lives in worker/src/prompt.js (not in APK) │
└──────────────────────────────────────────────────────────────┘
```

- **Build-time URL**: `DOOMSY_API_BASE_URL` is injected via `BuildConfig` from Gradle / `local.properties` (see below).
- **Health**: On launch, the app `GET`s `/health` so the status chip can show live vs. fallback before the first message.
- **History**: Last few exchanges are stored on device and sent to the Worker for short conversational context (see `DoomsyCloudClient` for limits).
- **Streaming**: The assistant bubble grows as SSE `data:` tokens arrive. Offline fallback replies still appear in one shot.

---

## Requirements

| Tool | Notes |
|------|--------|
| **JDK 17** | Required by Android Gradle Plugin / Kotlin toolchain. |
| **Android Studio** | Koala+ recommended; **compileSdk 36**, **targetSdk 36**, **minSdk 26**. |
| **Node.js 18+** | Only for the Cloudflare Worker (`worker/`). |
| **Cloudflare account** | If you deploy the Worker and use Workers AI. |

---

## Repository layout

| Path | Purpose |
|------|---------|
| `app/` | Android application module (Kotlin, Compose, assets). |
| `app/src/main/assets/models/doomsy.glb` | Hero 3D model (GLB). **Not stored in this repo** (see [Customization](#customization-checklist)); add the file locally after clone. |
| `app/src/main/java/.../data/DoomTracks.kt` | Track metadata + Spotify URIs + shuffle helper. |
| `worker/` | Cloudflare Worker: `npm install`, `npm run deploy` via Wrangler. |
| `worker/src/prompt.js` | Server-side system prompt for the chat model. |
| `worker/wrangler.jsonc` | Worker name, AI binding, `DOOMSY_MODEL` var (e.g. Llama instruct on Workers AI). |

---

## Build & run (Android)

From the repo root:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Open the project in Android Studio and run on an emulator or device (API 26+).

### Release candidate (signed APK + bundle)

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

Outputs:

- **APK (sideload):** `app/build/outputs/apk/release/app-release.apk`
- **AAB (Play-style):** `app/build/outputs/bundle/release/app-release.aab`

Release builds are configured to sign with the **debug keystore** so you can install the RC immediately (`adb install -r app/build/outputs/apk/release/app-release.apk`). For Google Play, add a proper `signingConfigs { release { ... } }` with your upload keystore and remove the debug `signingConfig` line from the `release` build type.

### Point the app at your Worker (optional)

**Option A — `local.properties`** (gitignored; good for local dev):

```properties
doomsyApiBaseUrl=https://your-worker.your-subdomain.workers.dev
doomsyApiKey=your-shared-secret
```

**Option B — Gradle property** (CI or one-off build):

```bash
./gradlew :app:assembleDebug \
  -PdoomsyApiBaseUrl=https://your-worker.your-subdomain.workers.dev \
  -PdoomsyApiKey=your-shared-secret
```

If the URL is **empty**, the app does **not** call the cloud endpoint; chat uses built-in offline responses instead.

The client appends `/chat` if the base URL does not already end with `/chat`.

If `doomsyApiKey` is set, the app sends it as `X-Doomsy-Key`. Leave both the Gradle property and the Worker secret unset to keep `/chat` public (per-IP rate limiting still applies).

---

## Deploy the Cloudflare Worker

```bash
cd worker
npm install
npx wrangler login   # if needed
npm run deploy
```

Configure **Workers AI** and the model string in `wrangler.jsonc` (`vars.DOOMSY_MODEL`, `ai.binding`). After deploy, use the printed `*.workers.dev` URL as `doomsyApiBaseUrl` when building the Android app.

**Optional auth:**

```bash
npx wrangler secret put DOOMSY_API_KEY
```

When that secret is present, `POST /chat` requires header `X-Doomsy-Key` with the same value. `GET /health` stays unauthenticated so the app can still probe reachability. `/chat` remains public if the secret is not set.

**Free-tier note (Workers):** Cloudflare’s Workers Free plan includes a **daily request quota** (see [Workers limits](https://developers.cloudflare.com/workers/platform/limits/)); each chat message that hits your Worker counts toward that quota. Workers AI has separate billing/limits—check current Cloudflare docs for your account.

---

## Tests

```bash
./gradlew :app:testDebugUnitTest
cd worker && npm run check
```

Unit tests cover data helpers (tracks, intents, conversation pieces, session codec, cloud URLs, SSE tokens, layout breakpoints, etc.). A GitHub Actions workflow runs the same Android unit tests plus a Worker syntax/auth check on push/PR.

Instrumented UI tests (intro skip, composer send, Spotify URIs) run on a device or emulator:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Adjust package names and URIs if you fork the project.

---

## Customization checklist

- Add `app/src/main/assets/models/doomsy.glb` after clone (it is **gitignored** to keep the repository small). Use any reasonable-size **GLB** for the hero bust (e.g. export from Meshy or another tool). Low-RAM devices skip this asset automatically.
- **Launcher icon:** adaptive icons live under `app/src/main/res/mipmap-*` and `mipmap-anydpi-v26/`; background color is `ic_launcher_background` in `values/ic_launcher_background.xml`. Replace the generated bitmaps if you want a different mark.
- Edit **`DoomTracks.kt`** for artists, albums, and `spotify:track:` IDs (verify on device).
- Edit **`worker/src/prompt.js`** for persona and safety boundaries.
- Tune **`worker/wrangler.jsonc`** model id and token limits in `worker/src/index.js` if your use case needs longer replies.
- The Worker also exposes **`GET /health`** (and `GET /`) for reachability checks, with a simple per-IP rate limit on `/chat`, optional `DOOMSY_API_KEY`, and SSE when the client asks for `text/event-stream`.
- Theme colors live under `app/.../ui/theme/`.

---

## Tech stack (versions are defined in Gradle)

- **Kotlin**, **Jetpack Compose** (BOM-managed), **Material 3**
- **SceneView** for Filament-based GLB rendering
- **Cloudflare Workers** + **Workers AI** for the optional chat backend
- **Wrangler** for Worker deployment

---

## Contributing

Issues and PRs are welcome: documentation improvements, dependency bumps, accessibility, and clearer separation of theme vs. content are especially helpful for a small “gift app” codebase.

---

## Disclaimer

This project is **unofficial fan work**. It is not affiliated with any artist, label, or streaming service. Trademarks belong to their respective owners. Use responsibly and respect platform terms of service (including Spotify and Cloudflare).

## License

Licensed under the **Apache License, Version 2.0**. See [`LICENSE`](LICENSE) for the full text.
