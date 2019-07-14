package com.wwdablu.soumya.campdf.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wwdablu.soumya.cam2lib.Cam2Lib;
import com.wwdablu.soumya.cam2lib.Cam2LibCallback;
import com.wwdablu.soumya.cam2lib.Cam2LibConverter;
import com.wwdablu.soumya.campdf.R;
import com.wwdablu.soumya.campdf.firebase.Analytics;
import com.wwdablu.soumya.campdf.manager.ImageManager;
import com.wwdablu.soumya.campdf.manager.PdfManager;
import com.wwdablu.soumya.campdf.manager.StorageManager;
import com.wwdablu.soumya.campdf.manager.ZipManager;
import com.wwdablu.soumya.campdf.util.ShareBox;
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
    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        TextureView textureView = findViewById(R.id.texv_camera);

        findViewById(R.id.btn_capture).setOnClickListener(view -> cam2Lib.getImage());
        findViewById(R.id.btn_complete).setOnClickListener(view -> saveSessionData());
        findViewById(R.id.btn_cancel).setOnClickListener(view -> cancelSessionData());

        //Create the session ID
        createSessionId();

        mStoragePath = StorageManager.createFolder(this, mSessionId);

        mImageManager = new ImageManager();

        cam2Lib = new Cam2Lib(this, this);
        cam2Lib.open(textureView, CameraDevice.TEMPLATE_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode != 1000) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if(resultCode == Activity.RESULT_OK) {
            Bitmap capturedBitmap = (Bitmap) ShareBox.getInstance().access("captureBitmap");
            ShareBox.getInstance().remove("captureBitmap");

            if(capturedBitmap != null) {
                mImageManager.save(capturedBitmap, mStoragePath, Bitmap.CompressFormat.JPEG,
                        100, true);
            }
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
            .setTitle(getString(R.string.complete_session))
            .setMessage(getString(R.string.ask_complete_session))
            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> saveSessionData())
            .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> cancelSessionData())
            .setNeutralButton(getString(R.string.cancel), (dialogInterface, i) -> cam2Lib.startPreview())
            .show();
    }

    private void cancelSessionData() {
        setResult(Activity.RESULT_CANCELED);
        StorageManager.cleanImages(mStoragePath);
        finish();
    }

    private void saveSessionData() {

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_capture, null, false);

        new AlertDialog.Builder(this)
            .setTitle("File Name")
            .setMessage("Provide name of the file")
            .setView(dialogView)
            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {

                mFileName = ((EditText) dialogView.findViewById(R.id.et_file_name)).getText().toString();
                mPdfManager = new PdfManager(this, new File(mStoragePath.getAbsoluteFile() + File.separator + mFileName + ".pdf"));

                new PDFBitmapWorker(mStoragePath, mPdfManager, new PDFBitmapWorker.Callback() {
                    @Override
                    public void onStarted() {
                        //
                    }

                    @Override
                    public void onCompleted() {
                        Analytics.getInstance().logPdfCreation();

                        CheckBox saveAsZip = dialogView.findViewById(R.id.cb_save_image_zip);
                        CheckBox saveImages = dialogView.findViewById(R.id.cb_save_as_images);

                        if(saveAsZip.isChecked()) {
                            generateZipArchive(saveImages.isChecked());
                        } else {
                            setResult(Activity.RESULT_OK);
                            StorageManager.cleanImages(mStoragePath);
                            finish();
                        }
                    }

                    @Override
                    public void onError() {
                        //
                    }
                }).start();
            })
            .setNegativeButton(getString(R.string.cancel), null)
            .show();
    }

    private void generateZipArchive(final boolean saveImages) {

        ZipManager.generateZip(mFileName, mStoragePath, new WZipCallback() {
            @Override
            public void onStarted(String identifier) {
                //
            }

            @Override
            public void onZipCompleted(File zipFile, String identifier) {

                if(saveImages) {
                    Analytics.getInstance().logImageCreation();
                } else {
                    StorageManager.cleanImages(mStoragePath);
                }

                Analytics.getInstance().logZipCreation();
                runOnUiThread(() -> {
                    setResult(Activity.RESULT_OK);
                    Toast.makeText(CameraCaptureActivity.this, getString(R.string.completed), Toast.LENGTH_SHORT).show();
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
