package com.szu.trashsorting.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class UserDBHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER = "create table User ("
            + "id integer primary key autoincrement, "
            + "password text, "
            + "name text, "
            + "college text, "
            + "grade text, "
            + "stu_no text, "
            + "person_path text, "
            + "nickname text)";

    private Context mContext;

    public UserDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        Toast.makeText(mContext, "User Create Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        onCreate(db);
    }

}
