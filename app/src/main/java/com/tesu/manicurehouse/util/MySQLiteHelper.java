package com.tesu.manicurehouse.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String CREATE_UPLOAD_MESSAGE = "create table videomessage ("
            + "id integer primary key autoincrement, "
            + "user_id text, "
            + "create_time integer, "
            + "is_liked integer,"
            + "is_collection integer,"
            + "liked_cnt integer,"
            + "forward_cnt integer,"
            + "collection_cnt integer,"
            + "play_cnt integer,"
            + "fee text,"
            + "is_fee integer,"
            + "title text,"
            + "pics text,"
            + "subtitle_file_url text,"
            + "video_url text,"
            + "type integer,"
            + "avatar text,"
            + "alias text,"
            + "id_value integer,"
            + "converImageUrl text,"
            + "video_id integer)";

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_UPLOAD_MESSAGE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.execSQL("drop table videomessage");
            db.execSQL(CREATE_UPLOAD_MESSAGE);
        }

    }
}
