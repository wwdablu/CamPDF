package com.wwdablu.soumya.campdf.workers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wwdablu.soumya.campdf.manager.PdfManager;

import java.io.File;
import java.io.IOException;

public final class PDFBitmapWorker extends Thread {

    public interface Callback {
        void onStarted();
        void onCompleted();
        void onError();
    }

    private File mStorageDir;
    private PdfManager mPdfManager;
    private Callback mCallback;

    public PDFBitmapWorker(@NonNull File storageDir, @NonNull PdfManager pdfManager, @NonNull Callback callback) {
        mStorageDir = storageDir;
        mPdfManager = pdfManager;
        mCallback = callback;
    }

    @Override
    public void run() {
        super.run();
        mCallback.onStarted();

        File[] allFiles = mStorageDir.listFiles();
        for(File file : allFiles) {

            if (isSupportedImageFile(file)) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                mPdfManager.write(bitmap);
                bitmap.recycle();
                System.gc();
            }
        }

        try {
            mPdfManager.publish();
        } catch (IOException e) {
            mCallback.onError();
            Log.e(PDFBitmapWorker.class.getName(), "Could not create PDF.", e);
        }

        mCallback.onCompleted();
    }

    private boolean isSupportedImageFile(File file) {
        return file != null && file.canRead() && (file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png"));
    }
}
