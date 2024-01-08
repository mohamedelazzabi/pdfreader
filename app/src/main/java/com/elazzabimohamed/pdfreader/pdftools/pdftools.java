package com.elazzabimohamed.pdfreader.pdftools;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.elazzabimohamed.pdfreader.DatabaseHelper;
import com.elazzabimohamed.pdfreader.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class pdftools extends AppCompatActivity {

    Button mergerBtn;
    ArrayList filePaths;
    DatabaseHelper myDB;
    private static  int PICK_PDF_REQUEST = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdftools);
        mergerBtn =findViewById(R.id.merge);

       myDB = new DatabaseHelper(this);
        filePaths = new ArrayList<>();

        mergerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(intent, PICK_PDF_REQUEST);


            }
        });


    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = getFilePathFromUri(uri);
                if (filePath != null && new File(filePath).exists()) {
                    List<String> filePaths = new ArrayList<>();
                    filePaths.add(filePath);
                    String mergedFilePath = ceateDestinationPath();

                    try {
                        PDFUtils.mergePdfFiles(filePaths, mergedFilePath);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve valid file path from URI or file does not exist.");
                }
            } else {
                Log.e(TAG, "URI is null.");
            }
        }
    }


    String ceateDestinationPath(){
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String filePath = null;
// Create a file in the Downloads directory with a unique name
        File file = new File(downloadsDir, "ecbfile.pdf");
        int count = 0;
        while (file.exists()) {
            count++;
            file = new File(downloadsDir, "merge" + count + ".pdf");
             filePath = file.getAbsolutePath();
        }
        return filePath;
    }



    public void  getpath(){
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {


            // Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {

            filePaths.clear(); // clear list
            if (data.moveToFirst()) {
                do {
                    String uri = data.getString(2);

                    filePaths.add(getFilePathFromUri(Uri.parse(uri)));
                } while (data.moveToNext());
            }

        }
    }

    public  String getFilePathFromUri( Uri uri) {
        String filePath = "";
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath;
    }


}