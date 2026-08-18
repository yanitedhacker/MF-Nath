# AGENTS.md

## Cursor Cloud specific instructions

This repo has two independent components (see `README.md` for full docs):

- `app/` — the **Doomsy** Android app (Kotlin, Jetpack Compose, Gradle). Namespace `com.mrbitches.doomsy`, `compileSdk 35`, `minSdk 26`.
- `worker/` — an optional **Cloudflare Worker** (Node + Wrangler) that backs the in-app chat via Workers AI.

### Toolchain (already provisioned in the VM snapshot)

- **JDK 17** at `/usr/lib/jvm/java-17-openjdk-amd64` (Gradle must run on 17, not the system JDK 21). `JAVA_HOME`, `ANDROID_HOME`/`ANDROID_SDK_ROOT` (`~/android-sdk`), and `PATH` are exported in `~/.bashrc`, so a normal login shell already has them. Non-login shells may need `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64`.
- **Android SDK** at `~/android-sdk` with `platforms;android-35`, `build-tools;35.0.0`, `platform-tools` and licenses accepted. Gradle finds it via `ANDROID_HOME` (no `local.properties` needed).
- **Node 22 / npm 10** for the Worker.

### Android (`app/`) — standard commands (from repo root)

- Unit tests: `./gradlew :app:testDebugUnitTest`
- Lint: `./gradlew :app:lintDebug` (HTML report at `app/build/reports/lint-results-debug.html`)
- Build debug APK: `./gradlew :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`

Non-obvious notes:
- The hero 3D model `app/src/main/assets/models/doomsy.glb` is **gitignored and absent**. It is only loaded at runtime by SceneView; it is **not** required to build the APK or run unit tests. A running app without it just won't render the 3D bust.
- Point the app at a Worker via `-PdoomsyApiBaseUrl=<url>` or `local.properties` (`doomsyApiBaseUrl=...`); empty URL = offline fallback chat (no network LLM).
- Running the GUI app requires an Android emulator/physical device (KVM-accelerated), which is generally not available in this headless VM. Validate Android changes via unit tests + lint + `assembleDebug`.

### Worker (`worker/`) — standard commands (from `worker/`)

- Install: `npm install`
- Syntax check (this is the CI "lint" for the worker): `npm run check`
- Local dev server: `npm run dev` (wrangler dev)

Non-obvious notes:
- `wrangler.jsonc` declares the Workers AI binding as `ai.remote: true`. Because of this, `wrangler dev` **refuses to start non-interactively without `CLOUDFLARE_API_TOKEN`**. To smoke-test routing/`/health` locally without a Cloudflare account, run wrangler against a temporary config that omits the `ai` block (do not commit that change). With no real `env.AI`, `POST /chat` correctly reaches the AI call and returns `502 workers_ai_failed`; `/health`, validation (400s), and 404 routing all work.
- Real `/chat` AI replies and `npm run deploy` require a Cloudflare account (`CLOUDFLARE_API_TOKEN`) and Workers AI access.

### CI

`.github/workflows/ci.yml` runs `:app:testDebugUnitTest` (JDK 17) and the Worker `npm run check`.
