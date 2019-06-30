package com.wwdablu.soumya.campdf.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wwdablu.soumya.campdf.R;
import com.wwdablu.soumya.campdf.firebase.Analytics;
import com.wwdablu.soumya.campdf.util.ShareBox;

public class CapturePreviewActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Bitmap mCapturedBitmap;

    private String mShareBoxKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_preview);

        mShareBoxKey = getIntent().getStringExtra("shareBoxKey");
        mCapturedBitmap = (Bitmap) ShareBox.getInstance().access(mShareBoxKey);
        if(mCapturedBitmap == null) {
            Toast.makeText(this, getString(R.string.could_not_retrieve_captured_image), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Set the image
        mImageView = findViewById(R.id.iv_capture_preview);
        mImageView.setImageBitmap(mCapturedBitmap);

        findViewById(R.id.btn_accept_capture).setOnClickListener(view -> {
            setResult(Activity.RESULT_OK);
            Analytics.getInstance().logConfirmCapture();
            finish();
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

            Bitmap rotatedBitmap;
            Matrix matrix = new Matrix();

            switch (view.getId()) {
                case R.id.btn_rotate_left:
                    matrix.postRotate(-90F);
                    rotatedBitmap = Bitmap.createBitmap(mCapturedBitmap, 0, 0,
                        mCapturedBitmap.getWidth(), mCapturedBitmap.getHeight(), matrix, true);
                    break;

                case R.id.btn_rotate_right:
                default:
                    matrix.postRotate(90F);
                    rotatedBitmap = Bitmap.createBitmap(mCapturedBitmap, 0, 0,
                        mCapturedBitmap.getWidth(), mCapturedBitmap.getHeight(), matrix, true);
                    break;
            }

            mImageView.setImageBitmap(rotatedBitmap);

            mCapturedBitmap.recycle();
            mCapturedBitmap = rotatedBitmap;

            //Put the new bitmap in the sharebox
            ShareBox.getInstance().put(mShareBoxKey, mCapturedBitmap);
        }
    };
}
