package com.wwdablu.soumya.campdf.util;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class ImageManager {

    public static boolean save(@NonNull Bitmap bitmap,
                             @NonNull File file,
                             Bitmap.CompressFormat compressFormat,
                             @IntRange(from = 1, to = 100) int compress) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(compressFormat, compress, fos);
            fos.flush();
        } catch (IOException iox) {
            Log.e(ImageManager.class.getName(), iox.getMessage(), iox);
            return false;
        } finally {

            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
