# StreamVerse Setup Web

StreamVerse Setup Web is the browser companion for configuring StreamVerse from a phone, tablet, or desktop browser. It supports community add-on discovery, provider configuration, custom manifest handoff, QR-assisted setup, clipboard workflows, and Live TV URL management.

## Current role

The web companion is intended to make StreamVerse setup easier without placing personal configuration inside the public Android APK.

It supports:

- community add-on discovery
- provider configuration pages
- StreamVerse-compatible manifest/deep-link handoff
- QR-assisted phone-to-TV setup
- clipboard/copy support
- Live TV M3U/XMLTV configuration
- installable PWA-style use where supported

## QR setup expectations

A QR code is only the beginning of the setup flow.

For configurable add-ons, tracking services, and providers, the expected flow is:

1. open the correct provider setup or authentication page
2. let the user complete the required configuration
3. receive the configured manifest, deep link, or other supported result
4. hand the result back to StreamVerse
5. let StreamVerse validate and persist the final configuration

If the phone/browser side completes but StreamVerse never receives the result, the setup is incomplete.

## Privacy

- no required account sign-in
- no personal manifests or private bootstrap data are bundled
- no debrid, provider, or tracking credentials are committed
- local playlist/setup values should stay on the user's device unless the user explicitly sends them through a supported handoff
- QR values should be generated locally where practical

## Third-party streaming and piracy disclaimer

StreamVerse Setup Web does not host, upload, index, sell, or provide third-party streams or copyrighted media. It only helps users configure add-ons, manifests, playlists, providers, APIs, and services they choose to use with StreamVerse.

StreamVerse, StreamVerse Setup, and their maintainers are not responsible for piracy, unauthorized streaming, copyright infringement, or other misuse performed through third-party add-ons, manifests, playlists, providers, links, or user-supplied configuration.

Third-party services are independently operated and may change, disappear, restrict access, or impose their own terms. Listing or linking a service does not imply ownership, endorsement, affiliation, or authorization by StreamVerse.

Users are responsible for complying with applicable law and third-party terms and for ensuring they have permission to access or play the content they configure.

## Hosting

Serve the static web companion over HTTPS so browser clipboard, PWA, QR, and related setup features work consistently on supported devices.
