package com.wwdablu.soumya.campdf.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public final class ShareBox {

    private static ShareBox sInstance;
    private HashMap<String, Object> mSharedItems;

    private ShareBox() {
        mSharedItems = new HashMap<>();
    }

    @NonNull
    public static ShareBox getInstance() {

        if(sInstance == null) {
            sInstance = new ShareBox();
        }

        return sInstance;
    }

    public boolean contains(@NonNull String key) {
        return mSharedItems.containsKey(key);
    }

    public boolean put(@NonNull String key, @NonNull Object object) {

        mSharedItems.put(key, object);
        return true;
    }

    @Nullable
    public Object access(@NonNull String key) {
        return mSharedItems.get(key);
    }

    public boolean remove(@NonNull String key) {
        return mSharedItems.remove(key) != null;
    }
}
