package com.elazzabimohamed.pdfreader;


import static android.graphics.Color.BLACK;
import static android.view.Gravity.CENTER;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> implements Filterable {
    private List<PdfFile> pdfList;
    private Context context;
    Uri uriData;
    String nameData;
    Long fileid;
    String getFileSizeInMB;
    String dateData;
    DatabaseHelper db;
    int pos;
    View toastView;
    int adscount;
    private MyFilter mFilter;
    private Activity mActivity;
   // List<PdfFile>Backuplist= new ArrayList<>();
    private List<PdfFile> mFilteredData;

    public PdfAdapter(List<PdfFile> pdfList, Context context,Activity mActivity) {
        this.pdfList = pdfList;
        this.context = context;
         this.mActivity =mActivity;
        mFilter = new MyFilter();
        mFilteredData = pdfList;

    }

    @Override
    public PdfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
         toastView = LayoutInflater.from(context).inflate(R.layout.toast_layout, parent, false);

        return new PdfViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PdfViewHolder holder, int position) {
        PdfFile pdf = pdfList.get(position);
        uriData = pdf.getFileUri();
        fileid = pdf.getFileid();
       // Backuplist=pdfList;


        getFileSizeInMB = pdf.getFileSizeInMB();
        int maxWidthPixels = 500; // Replace with the maximum width of your TextView in pixels
        holder.name.setText(TextUtils.ellipsize(pdf.getDisplayName(), holder.name.getPaint(), maxWidthPixels, TextUtils.TruncateAt.END));


       // holder.name.setText(pdf.getDisplayName());
        holder.image.setBackgroundResource(R.drawable.ic_pdf);
        holder.pdf_size.setText(String.valueOf(pdf.getFileSizeInMB())+" Mb"+" • "+pdf.getFileDate() );


        holder.linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // readadscount();


                Intent intent = new Intent(context, PDFActivity.class);
                String textdata = pdf.getDisplayName();
                uriData = pdf.getFileUri();
                intent.putExtra("text", textdata);
                intent.putExtra("uri", uriData);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);



            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriData = pdf.getFileUri();
                nameData = pdf.getDisplayName();
                getFileSizeInMB= pdf.getFileSizeInMB();
                dateData= pdf.getFileDate();
                pos = position;

                showCustomPopup(v);

            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }
    public Filter getFilter() {
        return mFilter;
    }

    public class PdfViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public TextView menu;
        public LinearLayout linear1;
        public  TextView pdf_size;



        public PdfViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pdf_name);
            image = itemView.findViewById(R.id.pdf_image);
            menu = itemView.findViewById(R.id.custom_popup_button_trigger);
            linear1 = itemView.findViewById(R.id.linear1);
            pdf_size = itemView.findViewById(R.id.pdf_size);


        }
    }


    public void showCustomPopup(View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.action_popup_menu, null);


        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);


        // Set the PopupWindow background color to transparent

        // Set the PopupWindow animation style
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupView.setElevation(100);


        // Show the PopupWindow
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        LinearLayout RL = (LinearLayout) popupView.findViewById(R.id.bac_dim_layout);
        RL.setVisibility(0);

        TextView title = (TextView) popupView.findViewById(R.id.title_menu);
        TextView DateAndSize =(TextView) popupView.findViewById(R.id.DateAndSize);
        DateAndSize.setText(   String.valueOf(getFileSizeInMB)+" Mb"+" • "+dateData );

        int maxWidthPixels = 500;
        title.setText(TextUtils.ellipsize(nameData, title.getPaint(), maxWidthPixels, TextUtils.TruncateAt.END));
        db = new DatabaseHelper(context);

        ImageView bookmark_con = (ImageView) popupView.findViewById(R.id.bookmark_con);
        if((db.searchTableForId(uriData.toString())!=-1)) {
            //db.addData(nameData, String.valueOf(uriData), getFileSizeInMB, dateData);
            bookmark_con.setImageResource(0);
            bookmark_con.setImageResource(R.drawable.fav_red);
        }
        else {
            bookmark_con.setImageResource(0);
            bookmark_con.setImageResource(R.drawable.favorite_grey);;
        }



        RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        LinearLayout rename_con = (LinearLayout) popupView.findViewById(R.id.rename_con);
        rename_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(uriData, nameData);

                popupWindow.dismiss();
            }
        });
        LinearLayout share_con = (LinearLayout) popupView.findViewById(R.id.share_con);
        share_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePdf(uriData);
                popupWindow.dismiss();
            }
        });


        LinearLayout delete_con = (LinearLayout) popupView.findViewById(R.id.delete_con);
        delete_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string._delete_warning)
                        .setMessage(R.string._delete_msg)
                        .setPositiveButton(R.string._yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Code to be executed when Yes button is clicked
                                DeletePdf(context, uriData);
                                db = new DatabaseHelper(context);
                                if((db.searchTableForId(uriData.toString())!=-1)) {
                                    db.removeSingleRow(db.searchTableForId(uriData.toString()));
                                }
                                pdfList.remove(pos);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos,pdfList.size());
                                notifyDataSetChanged();
                                popupWindow.dismiss();
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
        });

         bookmark_con = (ImageView) popupView.findViewById(R.id.bookmark_con);
        ImageView finalBookmark_con = bookmark_con;
        bookmark_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DatabaseHelper(context);
                if((db.searchTableForId(uriData.toString())==-1)) {
                    db.addData(nameData, String.valueOf(uriData), getFileSizeInMB, dateData);
                    finalBookmark_con.setImageResource(0);
                    finalBookmark_con.setImageResource(R.drawable.fav_red);
                }
                else{
                    db = new DatabaseHelper(context);
                    db.removeSingleRow(db.searchTableForId(uriData.toString()));
                    finalBookmark_con.setImageResource(0);
                    finalBookmark_con.setImageResource(R.drawable.favorite_grey);
                    notifyItemRemoved(pos);
                    notifyItemChanged(pos);
                }



            }
        });

      LinearLayout details_con = (LinearLayout) popupView.findViewById(R.id.details_con);
      details_con.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              showDetailsDialog(context);
          }
      });



    }

    private void showRenameDialog(Uri fileUri, String Name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setTitle(R.string._rename_pdf);


        // Create an edit text to allow the user to enter the new file name
        final EditText editText = new EditText(context);
        editText.setText(Name); // Set the initial text to the current file name
        editText.setSelection(editText.getText().length()); // Set the cursor to the end of the text
        builder.setView(editText);

        // Add the "Rename" button
        builder.setPositiveButton(R.string._rename, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFileName = editText.getText().toString();
                renameFile(fileUri, newFileName);
                db = new DatabaseHelper(context);
                if((db.searchTableForId(uriData.toString())!=-1)) {
                    db.UpdateSingleRow(db.searchTableForId(uriData.toString()), newFileName);
                    notifyDataSetChanged();
                    notifyItemInserted(pos);
                    notifyItemChanged(pos);


                }

            }
        });

        // Add the "Cancel" button
        builder.setNegativeButton(R.string._cancel, null);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public static String getFilePathFromUri(Context context, Uri uri) {
        String filePath = "";
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
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


    public void showDetailsDialog(final Context context) {
        final TextView filename = new TextView(context);
        filename.setText("File name: "+nameData);
        filename.setTextSize(16);
        filename.setTextColor(BLACK);
        filename.setPadding(10,10,10,10);
        filename.setGravity(CENTER);



        final TextView filepath = new TextView(context);
        filepath.setText("File Path: "+getFilePathFromUri(context,uriData));
        filepath.setTextSize(16);
        filepath.setTextColor(BLACK);
        filepath.setPadding(10,10,10,10);
        filepath.setGravity(CENTER);


        final TextView filesize = new TextView(context);
        filesize.setText("File size: "+getFileSizeInMB);
        filesize.setTextSize(16);
        filesize.setTextColor(BLACK);
        filesize.setPadding(10,10,10,10);
        filesize.setGravity(CENTER);



        final TextView filefdate = new TextView(context);
        filefdate.setText("File date: "+dateData);
        filefdate.setTextSize(16);
        filefdate.setTextColor(BLACK);
        filefdate.setTextAppearance(context, R.style.MyAlertDialogStyle);
        filefdate.setPadding(10,10,10,10);
        filefdate.setGravity(CENTER);


        LinearLayout layout = new LinearLayout(context);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(CENTER);
        layout.setVerticalGravity(CENTER);

        layout.addView(filename);
        layout.addView(filepath);
        layout.addView(filesize);
        layout.addView(filefdate);



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("PDF file details");


        builder.setView(layout);
        builder.create().show();
    }





    // Helper method to extract the file name from a URI
    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }



    // Helper method to rename a file
    private void renameFile(Uri fileUri, String newFileName) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int updatedRow = context.getContentResolver().update(fileUri, contentValues, null);

            if (updatedRow > 0) {
                // File renamed successfull
                costumMsg(context.getString(R.string._file_re_success),R.drawable.check);

            } else {
                // File rename failed
                costumMsg(context.getString(R.string._file_re_failed),R.drawable.failed);
                //Toast.makeText(context, "File rename failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            File originalFile = new File(fileUri.getPath());
            File newFile = new File(originalFile.getParent(), newFileName);
            if (originalFile.renameTo(newFile)) {
                // File renamed successfully
                costumMsg(context.getString(R.string._file_re_success),R.drawable.check);

            } else {
                // File rename failed
                costumMsg(context.getString(R.string._file_re_failed),R.drawable.failed);
              //  Toast.makeText(context, "File rename failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sharePdf(Uri uri) {
        // Create an intent to share the PDF file
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string._shae)));
    }

    private void costumMsg(String msg,int icon){

        TextView toastText = toastView.findViewById(R.id.toast_text);
        toastText.setText(msg);

        ImageView toastIcon = toastView.findViewById(R.id.toast_icon);
        toastIcon.setImageResource(icon);

// Create and show the Toast message with the custom layout
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.show();

    }

    public void DeletePdf(Context context, Uri uri) {
        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        int result = contentResolver.delete(uri, null, null);
        if (result > 0) {
            // Document deleted successfully

            costumMsg(context.getString(R.string._file_del_success),R.drawable.check);


        } else {
            // Document delete failed
            costumMsg(context.getString(R.string._file_del_failed),R.drawable.failed);
            //Toast.makeText(context, "File deleted failed", Toast.LENGTH_SHORT).show();

        }
    }






    private void saveadscount(){
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("adscount", adscount);
        myEdit.apply();
    }
    private int readadscount(){

        SharedPreferences sh = mActivity.getSharedPreferences("MySharedPref", context.MODE_PRIVATE);
        adscount = sh.getInt("adscount",4);
        return adscount;
    }


    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                // if the search query is empty, return the original data
                results.values = mFilteredData;
                results.count = mFilteredData.size();
            } else {
                // perform the filtering based on the search query
                List<PdfFile> filteredList = new ArrayList<>();
                String query = constraint.toString().toLowerCase().trim();
                for (PdfFile data : mFilteredData) {
                    if (data.getDisplayName().toLowerCase().contains(query)
                            ) {
                        filteredList.add(data);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // update the filtered data and notify the adapter of the changes
            pdfList = (List<PdfFile>) results.values;
            notifyDataSetChanged();

        }
    }


}

