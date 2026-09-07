package com.akahwaj.streamversesetup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class UpdateChecker {
    private static final String RELEASE_API = "https://api.github.com/repos/Akahwaj/StreamVerse-Setup/releases/latest";
    private static final String ASSET_PREFIX = "StreamVerse-Setup-";
    private final Activity activity;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    UpdateChecker(Activity activity) { this.activity = activity; }

    void check(boolean userRequested) {
        executor.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(RELEASE_API).openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                connection.setRequestProperty("User-Agent", "StreamVerse-Setup/0.1.0");
                if (connection.getResponseCode() != 200) throw new IllegalStateException("Release channel is not ready");
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line; while ((line = reader.readLine()) != null) body.append(line);
                }
                JSONObject release = new JSONObject(body.toString());
                String tag = release.optString("tag_name", "").replaceFirst("^v", "");
                String download = findApk(release.optJSONArray("assets"));
                if (download != null && isNewer(tag, "0.1.0")) {
                    activity.runOnUiThread(() -> showUpdate(tag, download));
                } else if (userRequested) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "StreamVerse Setup is up to date.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception error) {
                if (userRequested) activity.runOnUiThread(() -> Toast.makeText(activity, "Update check is unavailable right now.", Toast.LENGTH_LONG).show());
            } finally {
                executor.shutdown();
            }
        });
    }

    private String findApk(JSONArray assets) {
        if (assets == null) return null;
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.optJSONObject(i);
            if (asset == null) continue;
            String name = asset.optString("name", "");
            if (name.startsWith(ASSET_PREFIX) && name.endsWith(".apk")) return asset.optString("browser_download_url", null);
        }
        return null;
    }

    private boolean isNewer(String remote, String local) {
        String[] a = remote.split("\\."); String[] b = local.split("\\.");
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int x = i < a.length ? number(a[i]) : 0; int y = i < b.length ? number(b[i]) : 0;
            if (x != y) return x > y;
        }
        return false;
    }

    private int number(String value) {
        try { return Integer.parseInt(value.replaceAll("[^0-9].*$", "")); } catch (Exception ignored) { return 0; }
    }

    private void showUpdate(String version, String url) {
        new AlertDialog.Builder(activity)
            .setTitle("StreamVerse Setup update")
            .setMessage("Version " + version + " is available.")
            .setNegativeButton("Later", null)
            .setPositiveButton("Download", (dialog, which) -> download(version, url))
            .show();
    }

    private void download(String version, String url) {
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
            .setTitle("StreamVerse Setup " + version)
            .setDescription("Downloading verified app update")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS, "StreamVerse-Setup-" + version + ".apk");
        long id = manager.enqueue(request);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) != id) return;
                try { activity.unregisterReceiver(this); } catch (Exception ignored) {}
                install(manager, id);
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= 33) activity.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else activity.registerReceiver(receiver, filter);
    }

    private void install(DownloadManager manager, long id) {
        if (Build.VERSION.SDK_INT >= 26 && !activity.getPackageManager().canRequestPackageInstalls()) {
            activity.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getPackageName())));
            Toast.makeText(activity, "Allow installs, then run Check for updates again.", Toast.LENGTH_LONG).show();
            return;
        }
        Uri apk = manager.getUriForDownloadedFile(id);
        if (apk == null) { Toast.makeText(activity, "The update download could not be opened.", Toast.LENGTH_LONG).show(); return; }
        Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(apk, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(install);
    }
}
