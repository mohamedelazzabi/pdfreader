// this project created with love by mohamedElazzabi
package com.elazzabimohamed.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class bookmark extends AppCompatActivity {
    DatabaseHelper myDB;
    private List<PdfFile> pdfList;
    ArrayList mylist ;
    PdfAdapter pa;
    RecyclerView recyclerView;
    LinearLayout noContent;
    Button allowReq;
    private PdfAdapter pdfAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        noContent = findViewById(R.id.noContent);
        setTitle(R.string.Bookmark);

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_view);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.dashboard:
                        return true;
                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(),setting.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        mylist = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        myDB = new DatabaseHelper(this);
        showList();



    }


    public void  showList(){


        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {

            noContent.setVisibility(View.VISIBLE);
           // Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {


            mylist.clear(); // clear list
            if (data.moveToFirst()) {
                do {
                    int id = data.getInt(0);
                    String name = data.getString(1);
                    String uri = data.getString(2);
                    String size = data.getString(3);
                    String date = data.getString(4);

                    PdfFile aw = new PdfFile(name, Uri.parse(uri),id,size,date);
                    mylist.add(aw);
                } while (data.moveToNext());
            }

        }

        recyclerView.removeAllViews();
        pa = new PdfAdapter( mylist,this,bookmark.this);
        recyclerView.setAdapter(pa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }




}