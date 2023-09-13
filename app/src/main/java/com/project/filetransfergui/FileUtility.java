package com.project.filetransfergui;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.util.List;
import java.util.Locale;

public class FileUtility {

    private static List<Uri> uris;
    private static String currentFileName;

    public static List<Uri> getUris() {
        return uris;
    }

    public static void setUris(List<Uri> uris) {
        FileUtility.uris = uris;
    }

    public static String getCurrentFileName() {
        return currentFileName;
    }

    public static void setCurrentFileName(String currentFileName) {
        FileUtility.currentFileName = currentFileName;
    }

    public static void setCurrentFileNameFromUri(Uri uri, ContentResolver resolver) {
        FileUtility.currentFileName = getFileNameFromUri(uri, resolver);
    }

    public static String formatFileSize(float bytes) {
        String[] suffixes = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

        int i;
        for (i = 0; i < suffixes.length; i++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }

        return "\n(" + String.format(Locale.US, "%." + 3 + "f", bytes) + " " + suffixes[i] + ")";
    }

    @SuppressLint("Range")
    public static String getFileNameFromUri(Uri uri, ContentResolver resolver) {
        String result = null;
        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (uri.getScheme().equals("content")) {
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static void renameFile() {
        int i;
        if (currentFileName.contains(".")) {
            i = currentFileName.lastIndexOf('.');
        } else {
            i = currentFileName.length();
        }
        currentFileName = currentFileName.substring(0, i) + "v2" + currentFileName.substring(i);
    }

}
