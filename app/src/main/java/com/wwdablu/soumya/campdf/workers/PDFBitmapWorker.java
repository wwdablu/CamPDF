package com.wwdablu.soumya.campdf.workers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wwdablu.soumya.campdf.util.PdfManager;

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
                Bitmap scaledBitmap = scaleBitmap(bitmap);
                mPdfManager.write(scaledBitmap);
                bitmap.recycle();
                scaledBitmap.recycle();
                System.gc();
            }
        }

        mCallback.onCompleted();

        try {
            mPdfManager.publish();
        } catch (IOException e) {
            mCallback.onError();
            Log.e(PDFBitmapWorker.class.getName(), "Could not create PDF.", e);
        }
    }

    private boolean isSupportedImageFile(File file) {
        return file != null && file.canRead() && (file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png"));
    }

    private Bitmap scaleBitmap(Bitmap bm) {

        float width = bm.getWidth();
        float height = bm.getHeight();

        float maxWidth = mPdfManager.getmSimplyPdfDocument().getUsablePageWidth();
        float maxHeight = mPdfManager.getmSimplyPdfDocument().getPageContentHeight();

        if (width > height) {
            // landscape
            float ratio = width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        return Bitmap.createScaledBitmap(bm, (int) width, (int) height, true);
    }
}
