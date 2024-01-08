package com.elazzabimohamed.pdfreader;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.widget.Toast;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfDocumentAdapter extends PrintDocumentAdapter  {
    private Uri fileuri;
    private Context context;
    private int totalPages;



    public int retryAttempt;



    public PdfDocumentAdapter(Context context, Uri fileuri) {
        this.context = context;
        this.fileuri = fileuri;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle metadata) {
        // Load the PDF document and calculate the number of pages
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(fileuri, "r");
            PdfRenderer renderer = new PdfRenderer(parcelFileDescriptor);
            totalPages = renderer.getPageCount();
            renderer.close();
            parcelFileDescriptor.close();
            // Report the number of pages to the print framework
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("document.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages);
            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } catch (Exception e) {
            callback.onLayoutFailed(null);
        }
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Write the PDF document to the output stream
        try {
            InputStream input = context.getContentResolver().openInputStream(fileuri);
            OutputStream output = new FileOutputStream(destination.getFileDescriptor());
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            input.close();
            output.close();
        } catch (Exception e) {
            callback.onWriteFailed(null);
        }
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onFinish() {
        super.onFinish();

        Toast.makeText(context, "File", Toast.LENGTH_SHORT).show();




    }



}
