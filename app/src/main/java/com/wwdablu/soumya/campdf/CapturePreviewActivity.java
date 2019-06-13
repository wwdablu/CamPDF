package com.wwdablu.soumya.campdf;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.wwdablu.soumya.campdf.util.ShareBox;

public class CapturePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_preview);

        Bitmap capturedBitmap = (Bitmap) ShareBox.getInstance().access(getIntent().getStringExtra("shareBoxKey"));
        ((ImageView)findViewById(R.id.iv_capture_preview)).setImageBitmap(capturedBitmap);

        findViewById(R.id.btn_accept_capture).setOnClickListener(view -> {
            setResult(Activity.RESULT_OK);
            finish();
        });

        findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }
}
