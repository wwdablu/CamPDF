package com.wwdablu.soumya.campdf;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wwdablu.soumya.cam2lib.Cam2Lib;
import com.wwdablu.soumya.cam2lib.Cam2LibCallback;

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
        cam2Lib.close();
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        finish();
    }
}
