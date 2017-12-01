package io.vamshedhar.itunes;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/23/17 7:53 PM.
 * vchinta1@uncc.edu
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseOpenHelper extends SQLiteOpenHelper {
    static final String DB_NAME="filteredApps.db";
    static final int DB_VERSION=1;

    public DatabaseOpenHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        TopAppTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TopAppTable.onUpgrade(db,oldVersion,newVersion);
    }
}
