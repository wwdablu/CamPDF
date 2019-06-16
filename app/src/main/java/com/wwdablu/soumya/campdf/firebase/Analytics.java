package com.wwdablu.soumya.campdf.firebase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wwdablu.soumya.campdf.BuildConfig;

public final class Analytics {

    private static Analytics sAnalytics;
    private Context mContext;
    private String mId;

    private FirebaseAnalytics mFirebaseAnalytics;

    private Analytics(@NonNull Context context) {
        mContext = context;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
        mId = BuildConfig.DEBUG ? "debug__" : "";
    }

    public static Analytics getInstance(@NonNull Context context) {

        if(sAnalytics == null) {
            sAnalytics = new Analytics(context);
        }

        return sAnalytics;
    }

    public static Analytics getInstance() {
        return sAnalytics;
    }

    public void logConfirmCapture() {
        mFirebaseAnalytics.logEvent(mId + "positiveCapture", null);
    }

    public void logNegativeCapture() {
        mFirebaseAnalytics.logEvent(mId + "negativeCapture", null);
    }

    public void logPdfCreation() {
        mFirebaseAnalytics.logEvent(mId + "pdfCreated", null);
    }

    public void logZipCreation() {
        mFirebaseAnalytics.logEvent(mId + "zipCreated", null);
    }

    public void logImageCreation() {
        mFirebaseAnalytics.logEvent(mId + "imageCreated", null);
    }
}
