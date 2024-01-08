package com.elazzabimohamed.pdfreader;
// this project created with love by mohamedElazzabi



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "list.db";
    public static final String TABLE_NAME = "bookmark";
    public static final String COL1 = "ID";
    public static final String COL2 = "name";
    public static final String COL3 = "uri";
    public static final String COL4 = "size";
    public static final String COL5 = "date";
    //////////////////////////////////
    public static final String TABLE_NAME2 = "kickCounter";
    public static final String KICK_ID = "_ID";
    public static final String KICK_WEEK = "_week";
    public static final String KICK_DATE = "_date";
    public static final String KICK_QTY = "_quantite";
    public static final String KICK_DURATION = "_duration";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT, " + " uri TEXT," + " size TEXT, " + " date TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS  " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS  " + TABLE_NAME2);
        onCreate(db);
    }


    public boolean addData(String name, String uri,String size, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, name);
        contentValues.put(COL3, uri);
        contentValues.put(COL4, size);
        contentValues.put(COL5, date);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;

        }
    }

    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC",null);
        return data;
    }
    public Cursor getList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME ,null);
        return data;
    }
    public void delete() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting rows
        sqLiteDatabase.delete(TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }
    public void removeSingleRow(int position) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + "= '" + position + "'");

        //Close the database
        database.close();
    }
    public void UpdateSingleRow(int position,String name) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL2, name);
        database.update(TABLE_NAME, values, COL1+"="+position, null);
        database.close();
    }
    public int searchTableForId( String col3Value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { "ID" };
        String selection = "uri=?";
        String[] selectionArgs = { col3Value };
        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                projection,  // The columns to return
                selection,   // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,    // don't group the rows
                null,    // don't filter by row groups
                null    // don't sort the order
        );
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex("ID"));
        }
        cursor.close();
        db.close();
        return id;
    }



    ////////////////////////table kickcounter
    public boolean addKick(int week, String date,int quantity,String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KICK_WEEK, week);
        contentValues.put(KICK_DATE, String.valueOf(date));
        contentValues.put(KICK_QTY, quantity);
        contentValues.put(KICK_DURATION, duration);
        long result = db.insert(TABLE_NAME2, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;

        }
    }
    public Cursor getListKicks() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME2 + " ORDER BY " + KICK_ID + " DESC",null);
        return data;
    }
    public void deleteKicks() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting rows
        sqLiteDatabase.delete(TABLE_NAME2, null, null);
        sqLiteDatabase.close();
    }
    public void removeKickSingleRow(int position) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_NAME2 + " WHERE " + KICK_ID + "= '" + position + "'");

        //Close the database
        database.close();
    }



}