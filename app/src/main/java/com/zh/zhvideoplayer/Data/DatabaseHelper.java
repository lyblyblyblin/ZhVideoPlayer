package com.zh.zhvideoplayer.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lybly on 2018/3/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mcontext;
    private final String CREATE_DIRINFO="create table dirinfo("+ // dir部分
            "parentdir Text primary key,"+      ///storage/sdcard1/Download
            "currentdir Text"+                   //Download
            ")";

    private final String CREATE_VIDEOINFO="create table videoinfo("+  //video部分
            "filepath Text primary key,"+// /storage/sdcard1/Download/中国山寨机无可超越的史诗传说！无法匹敌的国产神话！.flv
            "filename Text,"+             //中国山寨机无可超越的史诗传说！无法匹敌的国产神话！.flv
            "parentdir Text,"+           ///storage/sdcard1/Download
            "foreign key(parentdir) references dirinfo(parentdir)"+  //外键
            ")";
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mcontext=context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIRINFO);
        db.execSQL(CREATE_VIDEOINFO);
        //Toast.makeText(mcontext,"suc", Toast.LENGTH_LONG).show();
    }

}
