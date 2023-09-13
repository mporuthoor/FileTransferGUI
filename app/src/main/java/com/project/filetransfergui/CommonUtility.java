package com.project.filetransfergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.net.Socket;

public class CommonUtility {
    private static Socket socket;

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        CommonUtility.socket = socket;
    }

    @SuppressWarnings("deprecation")
    public static String getDownloadPath(StorageManager storageManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);
            return storageVolume.getDirectory().getPath() + "/Download/";
        } else {
            return Environment.getExternalStorageDirectory().getPath() + "/Download/";
        }
    }

    public static void returnHomeFromMainThread(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        });
    }
}
