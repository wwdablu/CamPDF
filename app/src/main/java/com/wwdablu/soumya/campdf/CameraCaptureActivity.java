package com.wwdablu.soumya.campdf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wwdablu.soumya.cam2lib.Cam2Lib;
import com.wwdablu.soumya.cam2lib.Cam2LibCallback;
import com.wwdablu.soumya.cam2lib.Cam2LibConverter;
import com.wwdablu.soumya.campdf.firebase.Analytics;
import com.wwdablu.soumya.campdf.util.ImageManager;
import com.wwdablu.soumya.campdf.util.PdfManager;
import com.wwdablu.soumya.campdf.util.ShareBox;
import com.wwdablu.soumya.campdf.util.StorageManager;
import com.wwdablu.soumya.campdf.util.ZipManager;
import com.wwdablu.soumya.campdf.workers.PDFBitmapWorker;
import com.wwdablu.soumya.wzip.WZipCallback;

import java.io.File;
import java.util.Calendar;

public class CameraCaptureActivity extends AppCompatActivity implements Cam2LibCallback {

    private Cam2Lib cam2Lib;

    private ImageManager mImageManager;
    private PdfManager mPdfManager;
    private File mStoragePath;

    private String mSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        TextureView textureView = findViewById(R.id.texv_camera);
        Button captureButton = findViewById(R.id.btn_capture);

        captureButton.setOnClickListener(view -> cam2Lib.getImage());

        //Create the session ID
        createSessionId();

        mStoragePath = StorageManager.createFolder(this, mSessionId);

        mImageManager = new ImageManager();
        mPdfManager = new PdfManager(this, new File(mStoragePath.getAbsoluteFile() + File.separator + "file.pdf"));

        cam2Lib = new Cam2Lib(this, this);
        cam2Lib.open(textureView, CameraDevice.TEMPLATE_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode != 1000) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Bitmap capturedBitmap = null;
        if(resultCode == Activity.RESULT_OK) {
            capturedBitmap = (Bitmap) ShareBox.getInstance().access("captureBitmap");
            ShareBox.getInstance().remove("captureBitmap");
        }

        if(capturedBitmap != null) {
            mImageManager.save(capturedBitmap, mStoragePath, Bitmap.CompressFormat.JPEG, 100);
            capturedBitmap.recycle();
        }

        cam2Lib.startPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cam2Lib.stopPreview();
        cam2Lib.close();
    }

    @Override
    public void onBackPressed() {
        confirmSessionEnd();
    }

    @Override
    public void onReady() {
        cam2Lib.startPreview();
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    public void onImage(Image image) {
        cam2Lib.stopPreview();
        ShareBox.getInstance().put("captureBitmap", Cam2LibConverter.toBitmap(image));

        Intent previewIntent = new Intent(this, CapturePreviewActivity.class);
        previewIntent.putExtra("shareBoxKey", "captureBitmap");
        startActivityForResult(previewIntent, 1000);
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmSessionEnd() {

        cam2Lib.stopPreview();

        new AlertDialog.Builder(this)
            .setTitle("Complete Session")
            .setMessage("Do you want to complete the session and generate the PDF document")
            .setPositiveButton("Yes", (dialogInterface, i) -> saveSessionData())
            .setNegativeButton("No", (dialogInterface, i) -> {
                StorageManager.cleanImages(mStoragePath);
                finish();
            })
            .setNeutralButton("Cancel", (dialogInterface, i) -> cam2Lib.startPreview())
            .show();
    }

    private void saveSessionData() {

        new PDFBitmapWorker(mStoragePath, mPdfManager, new PDFBitmapWorker.Callback() {
            @Override
            public void onStarted() {
                //
            }

            @Override
            public void onCompleted() {
                Analytics.getInstance().logPdfCreation();
                //TODO - Generate zip if required
                generateZipArchive();
            }

            @Override
            public void onError() {
                //
            }

        }).start();
    }

    private void generateZipArchive() {

        ZipManager.generateZip(mSessionId, mStoragePath, new WZipCallback() {
            @Override
            public void onStarted(String identifier) {
                //
            }

            @Override
            public void onZipCompleted(File zipFile, String identifier) {

                Analytics.getInstance().logZipCreation();
                runOnUiThread(() -> {
                    Toast.makeText(CameraCaptureActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onUnzipCompleted(String identifier) {
                //
            }

            @Override
            public void onError(Throwable throwable, String identifier) {
                finish();
            }
        });
    }

    private void createSessionId() {

        if(!TextUtils.isEmpty(mSessionId)) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        mSessionId = calendar.get(Calendar.YEAR) + "_" +
                calendar.get(Calendar.MONTH) + "_" +
                calendar.get(Calendar.DATE) + "_" +
                calendar.getTimeInMillis();
    }
}
