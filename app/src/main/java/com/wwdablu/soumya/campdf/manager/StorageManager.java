package com.wwdablu.soumya.campdf.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wwdablu.soumya.campdf.R;

import java.io.File;
import java.util.LinkedList;

public final class StorageManager {

    public static class EntryInfo {
        private String fileName;
        private boolean hasZip;
        private boolean hasImages;
        private boolean hasPdf;
        private int imageCount;
        private File pdfFile;
        private File zipFile;
        private File captureDirectory;

        EntryInfo() {
            //
        }

        public String getFileName() {
            return fileName;
        }

        public boolean hasZip() {
            return hasZip;
        }

        public boolean hasImages() {
            return hasImages;
        }

        public boolean hasPdf() {
            return hasPdf;
        }

        public int getImageCount() {
            return imageCount;
        }

        public File getPdfFile() {
            return pdfFile;
        }

        public File getZipFile() {
            return zipFile;
        }

        public File getCaptureDirectory() {
            return captureDirectory;
        }
    }

    @Nullable
    public static File createFolder(@NonNull Context context,
                                    @NonNull String folderName) {

        File extDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        return createDirIfNot(createDirIfNot(extDir, context.getString(R.string.app_name)), folderName);
    }

    public static void removeFolder(EntryInfo entryInfo) {

        File[] files = entryInfo.captureDirectory.listFiles();
        for(File file : files) {
            if(!file.delete()) {
                Log.d(StorageManager.class.getName(), "Could not delete, " + file.getName());
            }
        }

        entryInfo.captureDirectory.delete();
    }

    public static LinkedList<EntryInfo> getCapturedSessions(@NonNull Context context) {

        File camDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + context.getString(R.string.app_name));

        if(!camDir.exists() || !camDir.isDirectory() || !camDir.canRead()) {
            return new LinkedList<>();
        }

        File[] camDirectories = camDir.listFiles();
        LinkedList<EntryInfo> list = new LinkedList<>();

        for(File dir : camDirectories) {

            File[] files = dir.listFiles();

            EntryInfo entryInfo = new EntryInfo();
            entryInfo.captureDirectory = dir;

            for(File file : files) {

                if(file.getName().toLowerCase().endsWith(".pdf")) {
                    entryInfo.hasPdf = true;
                    entryInfo.pdfFile = file;
                    entryInfo.fileName = file.getName();
                }

                else if(file.getName().toLowerCase().endsWith(".zip")) {
                    entryInfo.hasZip = true;
                    entryInfo.zipFile = file;
                }

                else if(file.getName().toLowerCase().endsWith(".jpg")) {
                    entryInfo.hasImages = true;
                    entryInfo.imageCount++;
                }
            }

            list.add(entryInfo);
        }

        return list;
    }

    private static File createDirIfNot(File base, String dir) {
        File file = new File(base.getAbsoluteFile() + File.separator + dir);
        if(file.exists()) {
            return file;
        }

        if(file.mkdir()) {
            return file;
        }

        return base;
    }

    public static boolean cleanImages(@NonNull File storageDirectory) {

        File[] files = storageDirectory.listFiles();
        for(File file : files) {
            if(!file.getName().toLowerCase().endsWith(".pdf") &&
                    !file.getName().toLowerCase().endsWith(".zip") && !file.delete()) {
                Log.d(StorageManager.class.getName(), "Could not delete, " + file.getName());
            }
        }

        return storageDirectory.delete();
    }
}
