package com.example.m21datarecovery;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.content.pm.PackageManager;
import android.widget.*;
import android.view.View;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    TextView status;
    LinearLayout list;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32,32,32,32);

        TextView title = new TextView(this);
        title.setText("M21 Data Recovery");
        title.setTextSize(26);
        root.addView(title);

        TextView note = new TextView(this);
        note.setText("\nScans files that are currently accessible on the phone. "
                + "It cannot bypass Android encryption or guarantee recovery of permanently deleted files.\n");
        note.setTextSize(16);
        root.addView(note);

        Button scan = new Button(this);
        scan.setText("Grant Access & Scan Storage");
        root.addView(scan);

        status = new TextView(this);
        status.setTextSize(15);
        root.addView(status);

        ScrollView sv = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        sv.addView(list);
        root.addView(sv, new LinearLayout.LayoutParams(-1,0,1));

        setContentView(root);

        scan.setOnClickListener(v -> requestAccess());
    }

    void requestAccess() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            }, 10);
        } else if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        } else scanFiles();
    }

    @Override public void onRequestPermissionsResult(int r, String[] p, int[] g) {
        super.onRequestPermissionsResult(r,p,g);
        scanFiles();
    }

    void scanFiles() {
        list.removeAllViews();
        status.setText("Scanning accessible storage...");
        File root = Environment.getExternalStorageDirectory();
        final int[] count = {0};
        scanDir(root, count);
        status.setText("Scan complete. Accessible files found: " + count[0]
                + "\nDeleted files that are no longer visible to Android cannot be recovered by this app.");
    }

    void scanDir(File dir, int[] count) {
        File[] fs = dir.listFiles();
        if (fs == null) return;
        for (File f : fs) {
            if (f.isDirectory() && !f.getName().equalsIgnoreCase("Android")) {
                scanDir(f, count);
            } else if (f.isFile()) {
                count[0]++;
                if (count[0] <= 500) {
                    TextView row = new TextView(this);
                    row.setText(f.getAbsolutePath() + "\n");
                    row.setTextSize(13);
                    row.setPadding(0,8,0,8);
                    list.addView(row);
                }
            }
        }
    }
}
