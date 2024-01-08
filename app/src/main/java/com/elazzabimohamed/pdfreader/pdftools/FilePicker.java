package com.elazzabimohamed.pdfreader.pdftools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class FilePicker {

    public static final int PICK_PDF_REQUEST_CODE = 111;

    public static void openPDFPicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        activity.startActivityForResult(intent, PICK_PDF_REQUEST_CODE);
    }

    public static List<String> getSelectedPDFPaths(Activity activity, int requestCode, int resultCode, Intent data) {
        List<String> pdfPaths = new ArrayList<>();

        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {


                // Multiple PDF files
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri pdfUri = data.getClipData().getItemAt(i).getUri();
                        String pdfPath = PDFUtils.getPath(activity, pdfUri);
                        pdfPaths.add(pdfPath);
                    }
                }
            }
        }

        return pdfPaths;
    }
}
