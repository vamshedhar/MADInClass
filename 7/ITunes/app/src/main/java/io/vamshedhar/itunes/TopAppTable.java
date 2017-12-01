package io.vamshedhar.itunes;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/23/17 7:54 PM.
 * vchinta1@uncc.edu
 */

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TopAppTable {
    static final String TABLENAME="appstable";
    static final String COLUMN_ID="_id";
    static final String COLUMN_NAME="name";
    static final String COLUMN_PRICE="price";
    static final String COLUMN_URL="imageurl";

    static public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + TABLENAME + "(");
        sb.append(COLUMN_ID + " integer primary key autoincrement, ");

        sb.append(COLUMN_NAME + " text not null,  ");
        sb.append(COLUMN_PRICE + " text not null, ");
        sb.append(COLUMN_URL + " text not null); ");

        try {
            db.execSQL(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+TABLENAME);

        TopAppTable.onCreate(db);
    }

}