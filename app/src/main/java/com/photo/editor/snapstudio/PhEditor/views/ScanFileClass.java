package com.photo.editor.snapstudio.PhEditor.views;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class ScanFileClass {
    public void refreshGallery(Context context, String str, int i) {
        File file = new File(str);
        if (file.exists()) {
            if (i > 18) {
                MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new OnScanCompletedListener() {
                    public void onScanCompleted(String str, Uri uri) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Scanned ");
                        sb.append(str);
                        sb.append(":");
                        String sb2 = sb.toString();
                        String str2 = "ExternalStorage";
                        Log.i(str2, sb2);
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("-> uri=");
                        sb3.append(uri);
                        Log.i(str2, sb3.toString());
                    }
                });
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(Uri.fromFile(file));
                context.sendBroadcast(intent);
            }
            if (i <= 18) {
                StringBuilder sb = new StringBuilder();
                sb.append("file://");
                sb.append(Environment.getExternalStorageDirectory());
                context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse(sb.toString())));
            }
        }
    }
}
