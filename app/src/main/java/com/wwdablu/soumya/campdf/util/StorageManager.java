package com.wwdablu.soumya.campdf.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wwdablu.soumya.campdf.R;

import java.io.File;

public final class StorageManager {

    @Nullable
    public static File createFolder(@NonNull Context context,
                                    @NonNull String folderName) {

        File extDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        return createDirIfNot(createDirIfNot(extDir, context.getString(R.string.app_name)), folderName);
    }

    private static File createDirIfNot(File base, String dir) {
        File file = new File(base.getAbsoluteFile() + File.separator + dir);
        if(file.exists()) {
            return file;
        }

        if(file.mkdir()) {
            return file;
        }

        return base;
    }

    public static boolean clean(@NonNull File storageDirectory) {

        File[] files = storageDirectory.listFiles();
        for(File file : files) {
            if(!file.delete()) {
                Log.d(StorageManager.class.getName(), "Could not delete, " + file.getName());
            }
        }

        return storageDirectory.delete();
    }
}
