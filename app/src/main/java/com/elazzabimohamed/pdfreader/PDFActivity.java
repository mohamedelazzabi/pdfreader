package com.elazzabimohamed.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.barteksc.pdfviewer.PDFView;

public class PDFActivity extends AppCompatActivity  {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.horizontalview);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.menu1) {

            try {

                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                String jobName = getString(R.string.app_name) + " Document";

                PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(this, datauri);

                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (id == R.id.horizontalview) {

            if(checkView==true) {
                pdfView.fromUri(datauri)
                        .swipeHorizontal(true) // set swipe horizontal
                        .load();
                checkView=false;
                item.setIcon(R.drawable.flip_vert);
            }
            else if(checkView==false){
                pdfView.fromUri(datauri)
                        .load();
                checkView=true;
                item.setIcon(R.drawable.flip);
            }
        }
        if (id == R.id.menu2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string._delete_warning)
                    .setMessage(R.string._delete_msg)
                    .setPositiveButton(R.string._yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Code to be executed when Yes button is clicked
                             DeletePdf(getApplicationContext(), datauri);
                            db = new DatabaseHelper(getApplicationContext());
                            if((db.searchTableForId(datauri.toString())!=-1)) {
                                db.removeSingleRow(db.searchTableForId(datauri.toString()));
                            }
                        }
                    })
                    .setNegativeButton(R.string._no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Code to be executed when No button is clicked
                        }
                    })
                    .show();
        }


        return super.onOptionsItemSelected(item);
    }
    boolean checkView=false;
    PDFView pdfView;
    String datatext;
    Uri datauri;
    PdfAdapter pdfAdapter;
    DatabaseHelper db;
    View toastView;
    Intent intent;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfactivity);


        intent = getIntent();
        datatext = intent.getStringExtra("text");
        datauri = getIntent().getParcelableExtra("uri");
        if(datauri!=null) {
            int maxWidthPixels = 500; // Replace with the maximum width of your TextView in pixels
            setTitle(TextUtils.ellipsize(datatext, new TextPaint(), maxWidthPixels, TextUtils.TruncateAt.END));
            if (checkView == false) {
                pdfView = findViewById(R.id.pdfView);
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                boolean isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

                if (isDarkMode) {
                    // Dark mode is activated
                    // Perform your desired actions for dark mode
                    pdfView.setNightMode(isDarkMode);
                    pdfView.fromUri(datauri).load();
                }
                else{
                    pdfView.fromUri(datauri).load();

                }

                checkView = true;

            }
        }else {
            Intent i = getIntent();
            String action = i.getAction();
            String type = i.getType();
            if (i.ACTION_VIEW.equals(action) && type != null && type.equals("application/pdf")) {
                // Get the URI of the PDF file from the intent
                 datauri = intent.getData();
                if (datauri != null) {
                    if (checkView == false) {
                        pdfView = findViewById(R.id.pdfView);
                        pdfView.fromUri(datauri).load();
                        checkView = true;
                    }
                }
            }
        }










    }

    public void DeletePdf(Context context, Uri uri) {
        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        int result = contentResolver.delete(uri, null, null);
        if (result > 0) {
            // Document deleted successfully

            costumMsg(context.getString(R.string._file_del_success),R.drawable.check);

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
          //  finish();


        } else {

            // Document delete failed
            costumMsg(context.getString(R.string._file_del_failed),R.drawable.failed);
            //Toast.makeText(context, "File deleted failed", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void costumMsg(String msg,int icon){
        toastView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_layout,null, false);
        TextView toastText = toastView.findViewById(R.id.toast_text);
        toastText.setText(msg);

        ImageView toastIcon = toastView.findViewById(R.id.toast_icon);
        toastIcon.setImageResource(icon);

// Create and show the Toast message with the custom layout
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.show();

    }



}