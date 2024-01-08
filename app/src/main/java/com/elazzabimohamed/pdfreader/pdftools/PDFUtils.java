package com.elazzabimohamed.pdfreader.pdftools;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

public class PDFUtils {
    private static Context context;


    public static void mergePdfFiles(List<String> filePaths, String mergedFilePath) throws FileNotFoundException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(mergedFilePath);

        for (String filePath : filePaths) {
            merger.addSource(filePath);
        }

        try {
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            Log.d(TAG, "PDF files merged successfully.");
            Toast.makeText(context, "PDF files merged successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error merging PDF files", Toast.LENGTH_SHORT).show();

            Log.e(TAG, "Error merging PDF files: " + e.getMessage());
        }
    }

    public static String getPath(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }

}
