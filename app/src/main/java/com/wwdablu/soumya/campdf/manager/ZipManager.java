package com.wwdablu.soumya.campdf.manager;

import androidx.annotation.NonNull;

import com.wwdablu.soumya.wzip.WZip;
import com.wwdablu.soumya.wzip.WZipCallback;

import java.io.File;
import java.util.LinkedList;

public class ZipManager {

    public static void generateZip(@NonNull String fileName,
                                   @NonNull File storagePath,
                                   @NonNull WZipCallback callback) {

        File[] files = storagePath.listFiles();
        LinkedList<File> fileLinkedList = new LinkedList<>();
        for(File file : files) {

            if(file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".png")) {
                fileLinkedList.add(file);
            }
        }

        WZip wZip = new WZip();
        wZip.zip(fileLinkedList, new File(storagePath.getAbsolutePath()
            + File.separator + fileName + ".zip"), "zipper", callback);
    }
}
