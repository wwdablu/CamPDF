package com.wwdablu.soumya.campdf;

import android.app.Application;

import com.wwdablu.soumya.campdf.firebase.Analytics;

public class CamPdfApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Analytics.getInstance(this);
    }
}
