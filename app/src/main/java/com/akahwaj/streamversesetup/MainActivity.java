package com.akahwaj.streamversesetup;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private WebView webView;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        webView = new WebView(this);
        webView.setBackgroundColor(0xff07111f);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.addJavascriptInterface(new SetupBridge(), "StreamVerseSetup");
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("https".equalsIgnoreCase(uri.getScheme())) {
                    return false;
                }
                openUri(uri);
                return true;
            }
        });
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
        new UpdateChecker(this).check(false);
    }

    private void openUri(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "No compatible app is installed.", Toast.LENGTH_LONG).show();
        }
    }

    private void openInsideApp(String value) {
        String clean = value == null ? "" : value.trim();
        Uri uri = Uri.parse(clean);
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            Toast.makeText(this, "Only secure HTTPS links are allowed.", Toast.LENGTH_LONG).show();
            return;
        }
        webView.loadUrl(clean);
    }

    private void openManifest(String value) {
        String clean = value == null ? "" : value.trim();
        if (!clean.startsWith("https://") || !clean.endsWith("manifest.json")) {
            Toast.makeText(this, "Enter a secure add-on manifest URL ending in manifest.json.", Toast.LENGTH_LONG).show();
            return;
        }
        Uri web = Uri.parse(clean);
        Uri install = web.buildUpon().scheme("stremio").build();
        openUri(install);
    }

    public final class SetupBridge {
        @JavascriptInterface public void installManifest(String url) { runOnUiThread(() -> openManifest(url)); }
        @JavascriptInterface public void openExternal(String url) { runOnUiThread(() -> openInsideApp(url)); }
        @JavascriptInterface public void openInsideApp(String url) { runOnUiThread(() -> openInsideApp(url)); }
        @JavascriptInterface public void goHome() { runOnUiThread(() -> webView.loadUrl("file:///android_asset/index.html")); }
        @JavascriptInterface public void checkForUpdates() {
            runOnUiThread(() -> new UpdateChecker(MainActivity.this).check(true));
        }
        @JavascriptInterface public void saveLiveTv(String manifest, String m3u, String xmltv) {
            getSharedPreferences("live_tv", MODE_PRIVATE).edit()
                .putString("manifest", safe(manifest)).putString("m3u", safe(m3u)).putString("xmltv", safe(xmltv)).apply();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Live TV URLs saved on this device.", Toast.LENGTH_SHORT).show());
        }
        @JavascriptInterface public String loadLiveTv() {
            SharedPreferences p = getSharedPreferences("live_tv", MODE_PRIVATE);
            return escape(p.getString("manifest", "")) + "\n" + escape(p.getString("m3u", "")) + "\n" + escape(p.getString("xmltv", ""));
        }
        private String safe(String value) { return value == null ? "" : value.trim(); }
        private String escape(String value) { return value == null ? "" : value.replace("\n", "").replace("\r", ""); }
    }

    @Override public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
}
