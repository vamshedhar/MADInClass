package io.vamshedhar.itunes;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/23/17 7:53 PM.
 * vchinta1@uncc.edu
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;



public class DatabaseDataManager {
    private Context mContext;
    private DatabaseOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private TopAppDAO topAppDAO;

    public DatabaseDataManager(Context mContext) {
        this.mContext = mContext;
        dbOpenHelper = new DatabaseOpenHelper(this.mContext);
        db = dbOpenHelper.getWritableDatabase();
        topAppDAO = new TopAppDAO(db);
    }

    public void close() {
        if (db != null)
            db.close();
    }

    public TopAppDAO getAppDAO() {
        return this.topAppDAO;
    }

    public long saveApp(TopApp topApp) {
        return this.topAppDAO.save(topApp);
    }

    public boolean deleteApp(String id) {
        return this.topAppDAO.delete(id);
    }

    public TopApp getApp(long id) {
        return this.topAppDAO.get(id);
    }
    public List<TopApp> getAll() {
        return this.topAppDAO.getAll();
    }
}