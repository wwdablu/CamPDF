package com.wwdablu.soumya.campdf.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wwdablu.soumya.campdf.R;
import com.wwdablu.soumya.campdf.firebase.Analytics;
import com.wwdablu.soumya.campdf.util.ShareBox;
import com.wwdablu.soumya.extimageview.BaseExtImageView;
import com.wwdablu.soumya.extimageview.Result;
import com.wwdablu.soumya.extimageview.rect.ExtRectImageView;

public class CapturePreviewActivity extends AppCompatActivity {

    private ExtRectImageView mImageView;

    private String mShareBoxKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_preview);

        mShareBoxKey = getIntent().getStringExtra("shareBoxKey");
        Bitmap bitmap = (Bitmap) ShareBox.getInstance().access(mShareBoxKey);
        if(bitmap == null) {
            Toast.makeText(this, getString(R.string.could_not_retrieve_captured_image), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Set the image
        mImageView = findViewById(R.id.iv_capture_preview);
        mImageView.setImageBitmap(bitmap);

        findViewById(R.id.btn_accept_capture).setOnClickListener(view -> {
            cropSelection();
        });

        findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            setResult(Activity.RESULT_CANCELED);
            Analytics.getInstance().logNegativeCapture();
            finish();
        });

        findViewById(R.id.btn_rotate_left).setOnClickListener(rotationListener);
        findViewById(R.id.btn_rotate_right).setOnClickListener(rotationListener);
    }

    private View.OnClickListener rotationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_rotate_left:
                    mImageView.rotate(BaseExtImageView.Rotate.CCW_90);
                    break;

                case R.id.btn_rotate_right:
                default:
                    mImageView.rotate(BaseExtImageView.Rotate.CW_90);
                    break;
            }
        }
    };

    private void cropSelection() {

        mImageView.crop(new Result<Void>() {
            @Override
            public void onComplete(Void data) {

                mImageView.getCroppedBitmap(new Result<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        runOnUiThread(() -> {

                            //We store the copy as the original bitmap will be cleared
                            ShareBox.getInstance().put(mShareBoxKey, data);
                            setResult(Activity.RESULT_OK);
                            Analytics.getInstance().logConfirmCapture();
                            finish();
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> Toast.makeText(CapturePreviewActivity.this,
                                "Could not crop the image.", Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> Toast.makeText(CapturePreviewActivity.this,
                        "Could not crop the image.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
