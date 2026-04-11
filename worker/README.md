# Doomsy Cloud Worker

This Worker keeps the real Doomsy system prompt off the APK and sends chat requests to Workers AI.

## Deploy

1. `cd /Users/archishmanpaul/Desktop/MF-Nath/worker`
2. `npm install`
3. `npx wrangler whoami`
4. `npm run deploy`

The deployed URL will look like:

`https://doomsy-chat.<your-subdomain>.workers.dev`

## Wire Android

Build the app with the Worker URL:

```bash
./gradlew :app:assembleDebug -PdoomsyApiBaseUrl=https://doomsy-chat.<your-subdomain>.workers.dev
```

If the property is omitted, the app falls back to offline Doomsy replies.
