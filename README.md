# StreamVerse Setup

StreamVerse Setup is the public companion app for configuring StreamVerse on Fire TV, Android TV, Google TV, and supported browser workflows. It is separate from the main StreamVerse APK and uses its own Android package:

```text
com.akahwaj.streamversesetup
```

The companion exists to make add-on, manifest, QR, and Live TV setup easier without embedding personal configuration inside the public StreamVerse build.

## Current role

StreamVerse Setup provides:

- a curated community add-on browser
- links to official/configuration pages for configurable providers
- custom manifest handoff into StreamVerse using supported deep-link flows
- QR-assisted setup for phone-to-TV configuration
- browser clipboard/copy support
- local Live TV M3U/XMLTV URL storage
- a lightweight installable web companion
- update checks for the Setup companion itself

It does not replace the main StreamVerse Android/Fire TV app and cannot silently write directly into another application's private Android storage.

## QR and provider setup

QR setup must be treated as a complete configuration flow, not just a barcode that points to a generic URL.

For configuration-required add-ons or services, the setup flow should:

1. open the correct provider configuration page
2. let the user finish provider-specific setup or authentication
3. obtain the configured manifest, deep link, or other supported result
4. hand that result back into StreamVerse
5. allow StreamVerse to validate and persist the configuration

This distinction is especially important for tracking providers and configurable add-on aggregators. A QR scan is not considered complete if the phone side succeeds but StreamVerse never receives or stores the resulting configuration.

## Privacy model

- no required account sign-in
- no personal manifest is bundled in this public repository
- no debrid token, provider credential, tracking credential, or private bootstrap data is committed
- Live TV URLs remain user-supplied configuration
- browser-side values should remain local unless the user explicitly sends them through a supported setup flow
- Android still requires normal user confirmation for app installation/update actions

## Third-party streaming and piracy disclaimer

StreamVerse Setup does not host, upload, index, sell, or provide third-party streams or copyrighted media. It only helps users configure links, manifests, playlists, add-ons, and services they choose to use with StreamVerse.

StreamVerse, StreamVerse Setup, and their maintainers are not responsible for piracy, unauthorized streaming, copyright infringement, or other misuse performed through third-party add-ons, manifests, playlists, providers, links, or user-supplied configuration.

Third-party providers are independent from StreamVerse and StreamVerse Setup. Their availability, behavior, content, APIs, authentication methods, and terms can change without notice. Listing or linking a provider, add-on, manifest, playlist format, or external service does not imply ownership, endorsement, affiliation, or authorization by StreamVerse.

Users are responsible for the services and sources they configure, for complying with applicable law and provider terms, and for ensuring they have permission to access or play the content involved.

## Build

```bash
gradle :app:assembleRelease
```

GitHub Actions builds and validates the Setup APK. Production distribution should use a persistent signing key stored securely as repository secrets so Android can recognize future updates as coming from the same application identity.
