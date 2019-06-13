package com.wwdablu.soumya.campdf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wwdablu.soumya.cam2lib.Cam2Lib;
import com.wwdablu.soumya.cam2lib.Cam2LibCallback;
import com.wwdablu.soumya.cam2lib.Cam2LibConverter;
import com.wwdablu.soumya.campdf.util.PdfManager;
import com.wwdablu.soumya.campdf.util.ShareBox;

import java.io.IOException;

public class CameraCaptureActivity extends AppCompatActivity implements Cam2LibCallback {

    private Cam2Lib cam2Lib;
    private TextureView textureView;
    private Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        textureView = findViewById(R.id.texv_camera);
        captureButton = findViewById(R.id.btn_capture);

        captureButton.setOnClickListener(view -> cam2Lib.getImage());

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

        //TODO - Call a worker to save the bitmap in the PDF
        PdfManager pdfManager = new PdfManager(this, "/Hello.pdf");
        pdfManager.write(capturedBitmap);
        try {
            pdfManager.publish();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("App", "", e);
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
}
