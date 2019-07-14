package com.wwdablu.soumya.campdf.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.wwdablu.soumya.campdf.R;
import com.wwdablu.soumya.campdf.adapter.CaptureSessionView;
import com.wwdablu.soumya.campdf.manager.StorageManager;
import com.wwdablu.soumya.campdf.util.CameraHelper;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private CaptureSessionView mCaptureSessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if(!CameraHelper.getInstance(view.getContext()).hasCameraHardware()) {
                Toast.makeText(MainActivity.this, getString(R.string.no_hardware_camera), Toast.LENGTH_SHORT).show();
                return;
            }

            if(CameraHelper.getInstance(view.getContext()).hasCameraPermission()) {
                launchCamera();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView sessionRecyclerView = findViewById(R.id.rv_captured_docs_list);
        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCaptureSessionList = new CaptureSessionView(StorageManager.getCapturedSessions(this));
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
            findViewById(R.id.incl_app_bar_main).findViewById(R.id.incl_content_main)
                .findViewById(R.id.tv_no_captured_content).setVisibility(View.VISIBLE);
            findViewById(R.id.incl_app_bar_main).findViewById(R.id.incl_content_main)
                    .findViewById(R.id.rv_captured_docs_list).setVisibility(View.GONE);
        } else {

            findViewById(R.id.incl_app_bar_main).findViewById(R.id.incl_content_main)
                    .findViewById(R.id.tv_no_captured_content).setVisibility(View.GONE);
            findViewById(R.id.incl_app_bar_main).findViewById(R.id.incl_content_main)
                    .findViewById(R.id.rv_captured_docs_list).setVisibility(View.VISIBLE);
            mCaptureSessionList.setData(list);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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