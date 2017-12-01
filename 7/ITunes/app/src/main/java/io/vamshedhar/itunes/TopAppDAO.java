package io.vamshedhar.itunes;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/23/17 7:54 PM.
 * vchinta1@uncc.edu
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


public class TopAppDAO {
    private SQLiteDatabase db;

    public TopAppDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long save(TopApp topApp) {
        ContentValues values = new ContentValues();

        values.put(TopAppTable.COLUMN_ID, topApp.getId());
        values.put(TopAppTable.COLUMN_NAME, topApp.getName());
        values.put(TopAppTable.COLUMN_PRICE, topApp.getPrice());
        values.put(TopAppTable.COLUMN_URL, topApp.imageUrl);

        return db.insert(TopAppTable.TABLENAME, null, values);

    }


    public boolean delete(String id) {
        return db.delete(TopAppTable.TABLENAME, TopAppTable.COLUMN_ID + "=?", new String[]{id}) > 0;

    }

    public TopApp get(Long id) {
        TopApp topApp = null;
        Cursor c = db.query(true, TopAppTable.TABLENAME,
                new String[]{TopAppTable.COLUMN_ID, TopAppTable.COLUMN_NAME
                        , TopAppTable.COLUMN_PRICE, TopAppTable.COLUMN_URL}

                , TopAppTable.COLUMN_ID + "=?", new String[]{id + ""}
                , null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            topApp = buildNoteFromCursor(c);
            if (!c.isClosed())
                c.close();

        }


        return topApp;
    }

    public List<TopApp> getAll() {

        List<TopApp> topApps = new ArrayList<>();
        Cursor c = db.query(TopAppTable.TABLENAME,
                new String[]{TopAppTable.COLUMN_ID, TopAppTable.COLUMN_NAME
                        , TopAppTable.COLUMN_PRICE, TopAppTable.COLUMN_URL}
                , null, null, null, null, null);

        if (c != null && c.moveToFirst()) {
            do {
                TopApp topApp = buildNoteFromCursor(c);
                if (topApp != null)
                    topApps.add(topApp);
            } while (c.moveToNext());
            if (!c.isClosed())
                c.close();
        }
        return topApps;
    }


    private TopApp buildNoteFromCursor(Cursor c) {
        TopApp topApp = null;
        if (c != null) {
            boolean b;


            topApp = new TopApp(c.getString(0), c.getString(1), c.getString(2), c.getString(3), "");
        }

        return topApp;
    }

}