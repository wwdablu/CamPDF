package com.wwdablu.soumya.campdf.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintAttributes;

import androidx.annotation.NonNull;

import com.wwdablu.soumya.simplypdf.DocumentInfo;
import com.wwdablu.soumya.simplypdf.SimplyPdf;
import com.wwdablu.soumya.simplypdf.SimplyPdfDocument;
import com.wwdablu.soumya.simplypdf.composers.Composer;
import com.wwdablu.soumya.simplypdf.composers.ImageComposer;
import com.wwdablu.soumya.simplypdf.composers.models.ImageProperties;

import java.io.File;
import java.io.IOException;

public final class PdfManager {

    private SimplyPdfDocument mSimplyPdfDocument;

    private ImageProperties mImageProperties;
    private ImageComposer mImageComposer;

    private boolean mIsFirstPage = true;

    public PdfManager(@NonNull Context context, @NonNull File file) {

        mSimplyPdfDocument = SimplyPdf.with(context, file)
                .colorMode(DocumentInfo.ColorMode.COLOR)
                .margin(DocumentInfo.Margins.DEFAULT)
                .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
                .paperSize(PrintAttributes.MediaSize.ISO_A4)
                .build();

        mImageProperties = new ImageProperties();
        mImageProperties.alignment = Composer.Alignment.CENTER;

        mImageComposer = new ImageComposer(mSimplyPdfDocument);
    }

    public void write(@NonNull Bitmap bitmap) {

        if(!mIsFirstPage) {
            mSimplyPdfDocument.newPage();
        }

        mImageComposer.drawBitmap(bitmap, mImageProperties);
        mIsFirstPage = false;
    }

    public void publish() throws IOException {
        mSimplyPdfDocument.finish();
    }
}
