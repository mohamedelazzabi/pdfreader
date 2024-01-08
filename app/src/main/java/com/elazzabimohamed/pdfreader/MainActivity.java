package com.elazzabimohamed.pdfreader;
// this project created with love by mohamedElazzabi

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private List<PdfFile> pdfList;
    private RecyclerView recyclerView;
    private PdfAdapter pdfAdapter;
    private static final int REQUEST_CODE_PERMISSION = 1100;

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Handle search query submission
        //  pdfAdapter.filter(query);

        if(pdfAdapter!=null) {
                    }
        return true;

    }


    @Override
    public boolean onQueryTextChange(String newText) {
        if(pdfAdapter!=null) {
            pdfAdapter.getFilter().filter(newText);        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_search, menu);

        // Get the SearchView and set up listeners
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu2) {
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean sortdesc;
    boolean sortAsc;
    boolean sortByName;
    boolean sortByDate;
    boolean sortBySize;

    LinearLayout permissionReq;
    Button allowReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Remove the default ActionBar title

        permissionReq = findViewById(R.id.permissionReq);
        allowReq= findViewById(R.id.allowReq);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), bookmark.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), setting.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(checkpermR()){
                pdfList = new ArrayList<>();
                recyclerView = findViewById(R.id.recyclerView);

                readSorting();
                pdfAdapter = new PdfAdapter(getExternalPDFFileList(sortAsc, sortdesc, sortByName, sortBySize, sortByDate), this,MainActivity.this);
                recyclerView.setAdapter(pdfAdapter);
                pdfAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            }
            else{
                permissionReq.setVisibility(View.VISIBLE);
                allowReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermission();
                    }
                });

            }
        }
        else {
            if(checkPermission()){
                pdfList = new ArrayList<>();
                recyclerView = findViewById(R.id.recyclerView);

                readSorting();
                pdfAdapter = new PdfAdapter(getExternalPDFFileList(sortAsc, sortdesc, sortByName, sortBySize, sortByDate), this,MainActivity.this);
                recyclerView.setAdapter(pdfAdapter);
                pdfAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            }
            else{
                permissionReq.setVisibility(View.VISIBLE);
                allowReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermission();
                    }
                });

            }
        }











    }

    private Bitmap getThumbnail(String filePath) {
        Bitmap thumbnail = null;
        try {
            // Load the PDF file using PdfiumCore
            PdfiumCore pdfiumCore = new PdfiumCore(this);
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);

            // Render the first page of the PDF file
            pdfiumCore.openPage(pdfDocument, 0);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, 0);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, 0);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, 0, 0, 0, width, height);

            thumbnail = bmp;

            // Close the document
            pdfiumCore.closeDocument(pdfDocument);
            fd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumbnail;
    }







    private boolean checkPermission() {

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }


    private boolean checkpermR(){
        boolean check = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permission is granted
               check= true;
            } else {
                // Permission is not granted
                check=false;
            }
        }
        return  check;


    }
    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permission is already granted
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_CODE_PERMISSION);
            }
        }else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now use the camera
            } else {
                // Permission denied, inform the user or take other actions
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==REQUEST_CODE_PERMISSION ) {
            if(checkpermR()){
                permissionReq.setVisibility(View.GONE);
                pdfList = new ArrayList<>();
                recyclerView = findViewById(R.id.recyclerView);

                readSorting();
                pdfAdapter = new PdfAdapter(getExternalPDFFileList(sortAsc, sortdesc, sortByName, sortBySize, sortByDate), this,MainActivity.this);
                recyclerView.setAdapter(pdfAdapter);
                pdfAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            }

        } else {
            // Permission denied, display a message to the user
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveSorting(boolean sortAsc,boolean sortdesc,boolean sortByName,boolean sortBySize,boolean sortByDate){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putBoolean("sortAsc", sortAsc);
        myEdit.putBoolean("sortdesc", sortdesc);
        myEdit.putBoolean("sortByName", sortByName);
        myEdit.putBoolean("sortBySize", sortBySize);
        myEdit.putBoolean("sortByDate", sortByDate);
        myEdit.apply();
    }
    void readSorting(){
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sortAsc = sh.getBoolean("sortAsc",false);
        sortdesc = sh.getBoolean("sortdesc",true);
        sortByName = sh.getBoolean("sortByName",false);
        sortBySize = sh.getBoolean("sortBySize",false);
        sortByDate = sh.getBoolean("sortByDate",true);

    }


    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by");

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sort, null);
        builder.setView(view);

        // Get references to the TextViews in the dialog
        TextView tvSortByName = view.findViewById(R.id.tv_sort_by_name);
        TextView tvSortByDateModified = view.findViewById(R.id.tv_sort_by_date_modified);
        TextView tvSortByFileSize = view.findViewById(R.id.tv_sort_by_file_size);
        TextView tvSortBydesc = view.findViewById(R.id.tv_sort_by_desc);
        TextView tvSortByasc = view.findViewById(R.id.tv_sort_by_asc);

        readSorting();
        if(sortAsc){
            tvSortByasc.setTextColor(Color.RED);
            tvSortBydesc.setTextColor(Color.BLACK);
        }
        else
        if(sortdesc){
            tvSortBydesc.setTextColor(Color.RED);
            tvSortByasc.setTextColor(Color.BLACK);
        }

        if(sortByName){
            tvSortByName.setTextColor(Color.RED);
            tvSortByDateModified.setTextColor(Color.BLACK);
            tvSortByFileSize.setTextColor(Color.BLACK);
        }
        else
        if(sortByDate){
            tvSortByDateModified.setTextColor(Color.RED);
            tvSortByName.setTextColor(Color.BLACK);
            tvSortByFileSize.setTextColor(Color.BLACK);
        }
        else
        if(sortBySize){
            tvSortByFileSize.setTextColor(Color.RED);
            tvSortByDateModified.setTextColor(Color.BLACK);
            tvSortByName.setTextColor(Color.BLACK);
        }


            tvSortBydesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortBydesc.setTextColor(Color.RED);
                tvSortByasc.setTextColor(Color.BLACK);

                // TODO: Implement sorting by name

                saveSorting(false,true,true,false,false);

            }
        });
        tvSortByasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByasc.setTextColor(Color.RED);
                tvSortBydesc.setTextColor(Color.BLACK);

                // TODO: Implement sorting by name

                saveSorting(true,false,true,false,false);

            }
        });

        // Set the click listeners for the TextViews to change color when clicked
        tvSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByName.setTextColor(Color.RED);
                tvSortByDateModified.setTextColor(Color.BLACK);
                tvSortByFileSize.setTextColor(Color.BLACK);
                // TODO: Implement sorting by name

                saveSorting(sortAsc,sortdesc,true,false,false);

            }
        });
        tvSortByDateModified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByDateModified.setTextColor(Color.RED);
                tvSortByName.setTextColor(Color.BLACK);
                tvSortByFileSize.setTextColor(Color.BLACK);

                saveSorting(sortAsc,sortdesc,false,false,true);

                // TODO: Implement sorting by date modified
            }
        });
        tvSortByFileSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSortByFileSize.setTextColor(Color.RED);
                tvSortByDateModified.setTextColor(Color.BLACK);
                tvSortByName.setTextColor(Color.BLACK);
                // TODO: Implement sorting by file size
                saveSorting(sortAsc,sortdesc,false,true,false);

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Implement sorting based on the selected TextView
                readSorting();
                pdfList = new ArrayList<>();
                recyclerView = findViewById(R.id.recyclerView);


                    pdfAdapter = new PdfAdapter(getExternalPDFFileList(sortAsc,sortdesc,sortByName,sortBySize,sortByDate),getApplicationContext(),MainActivity.this);
                    recyclerView.setAdapter(pdfAdapter);
                    pdfAdapter.notifyDataSetChanged();
                    recyclerView.invalidate();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                }



        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }


    private ArrayList<PdfFile> getExternalPDFFileList(boolean sortAsc,boolean sortdesc,boolean sortByName,boolean sortBySize,boolean sortByDate) {

        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.SIZE, MediaStore.Images.Media.DATE_MODIFIED};
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE;
        String[] selectionArgs = null;
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};

        //  String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " ASC"; // for ascending order

        String sortOrder = null;
            if(sortdesc&&sortByDate) {
                sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"; // for descending order
            }else
            if(sortAsc&&sortByDate)
            {
                sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " ASC";
            }

        if(sortdesc&&sortByName) {
            sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC"; // for descending order
        }else
        if(sortAsc&&sortByName)
        {
            sortOrder = MediaStore.Files.FileColumns.DISPLAY_NAME + " ASC";
        }
        if(sortdesc&&sortBySize) {
            sortOrder = MediaStore.Files.FileColumns.SIZE + " DESC"; // for descending order
        }else
        if(sortAsc&&sortBySize)
        {
            sortOrder = MediaStore.Files.FileColumns.SIZE + " ASC";
        }




        Cursor cursor = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);

        assert cursor != null;

        ArrayList<PdfFile> uriList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            long fileId = cursor.getLong(columnIndex);
            Uri fileUri = Uri.parse(uri.toString() + "/" + fileId);
            String displayName = cursor.getString(cursor.getColumnIndex(projection[1]));

            int sizeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
            long fileSizeInBytes = cursor.getLong(sizeIndex);
           // long fileSizeInKB = fileSizeInBytes / 1024; // Convert bytes to KB
          //  long fileSizeInMB = fileSizeInKB / 1024; // Convert KB to MB

            double fileSizeInMB = (double) fileSizeInBytes / (1024 * 1024);
            String mb =String.format("%.3f", fileSizeInMB);



            long dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED));
            Date creationDate = new Date(dateAdded * 1000);
            // You can format the date using a SimpleDateFormat object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(creationDate);
            uriList.add(new PdfFile(displayName, fileUri,fileId,mb,formattedDate));
        }

        cursor.close();
        return uriList;
    }




}
