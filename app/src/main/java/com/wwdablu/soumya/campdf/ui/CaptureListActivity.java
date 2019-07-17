package com.wwdablu.soumya.campdf.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wwdablu.soumya.campdf.R;
import com.wwdablu.soumya.campdf.adapter.CaptureListAdapter;
import com.wwdablu.soumya.campdf.manager.StorageManager;
import com.wwdablu.soumya.campdf.util.CameraHelper;

import java.util.LinkedList;

public class CaptureListActivity extends AppCompatActivity implements CaptureListAdapter.ActionCallback {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private CaptureListAdapter mCaptureSessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(!CameraHelper.getInstance(view.getContext()).hasCameraHardware()) {
                Toast.makeText(CaptureListActivity.this, getString(R.string.no_hardware_camera), Toast.LENGTH_SHORT).show();
                return;
            }

            if(CameraHelper.getInstance(view.getContext()).hasCameraPermission()) {
                launchCamera();
            } else {
                ActivityCompat.requestPermissions(CaptureListActivity.this, new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            }
        });

        RecyclerView sessionRecyclerView = findViewById(R.id.rv_captured_docs_list);
        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCaptureSessionList = new CaptureListAdapter(StorageManager.getCapturedSessions(this), this);
        sessionRecyclerView.setAdapter(mCaptureSessionList);
        updateList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode != 1000) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        updateList();
    }

    private void updateList() {

        LinkedList<StorageManager.EntryInfo> list = StorageManager.getCapturedSessions(this);
        if(list.size() == 0) {
            findViewById(R.id.rv_captured_docs_list).setVisibility(View.GONE);
        } else {
            findViewById(R.id.rv_captured_docs_list).setVisibility(View.VISIBLE);
            mCaptureSessionList.setData(list);
        }
    }

    @Override
    public void onDelete(StorageManager.EntryInfo entryInfo, int position) {
        StorageManager.removeFolder(entryInfo);
        updateList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            Toast.makeText(this, getString(R.string.needs_camera_permission), Toast.LENGTH_SHORT).show();
        }
    }

    private void launchCamera() {
        startActivityForResult(new Intent(this, CameraCaptureActivity.class), 1000);
    }
}
