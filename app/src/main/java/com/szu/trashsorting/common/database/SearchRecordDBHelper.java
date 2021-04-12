package com.szu.trashsorting.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SearchRecordDBHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER = "create table SearchRecord ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "record1 text, "
            + "record2 text, "
            + "record3 text, "
            + "record4 text)";

    private final Context mContext;

    public SearchRecordDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        Toast.makeText(mContext, "Record Create Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists SearchRecord");
        onCreate(db);
    }

}
