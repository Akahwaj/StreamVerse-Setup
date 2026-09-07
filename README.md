# StreamVerse Setup

A separate Fire TV / Google TV companion for StreamVerse. It keeps a curated list of 15 free community add-ons, opens official configuration pages, installs secure `manifest.json` links through the standard `stremio://` handoff, and stores Live TV M3U/XMLTV URLs locally.

This project does **not** replace or modify the original StreamVerse APK. Its package is `com.akahwaj.streamversesetup`.

## Safety model

- No account login, password, debrid token, or private manifest is bundled.
- A separate Android app cannot silently write another app's private add-on database.
- Add-on installation therefore uses StreamVerse/Nuvio's normal confirmation flow.
- Community services can change or disappear; links are clearly marked as third-party.
- Live TV accepts user-supplied HTTPS URLs. Users are responsible for having permission to use their playlist.
- Automatic updates check the public `Akahwaj/StreamVerse-Setup` GitHub release channel and always require Android's install confirmation.

## Build

```bash
gradle :app:assembleRelease
```

GitHub Actions builds and validates the APK. A production update channel should use a persistent signing key stored as repository secrets.
