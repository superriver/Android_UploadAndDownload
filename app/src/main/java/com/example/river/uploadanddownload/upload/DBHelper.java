package com.example.river.uploadanddownload.upload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/10/19.
 */

public class DBHelper extends SQLiteOpenHelper{

    public static  String TABLE_NAME = "uploadlog";
    public DBHelper(Context context) {
        super(context, "download.db", null, 1);
        System.out.print("DBHelper");
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.print("onCreate");
        db.execSQL("create table uploadlog (_id integer primary key autoincrement ,sourceid varchar(10),fileName varchar,filePath varchar,fileLen integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.print("onUpgrade");
    }
    public void insert(SQLiteDatabase db,FileInfo info){
        try {
            ContentValues cv = new ContentValues();
            cv.put("fileName",info.getFileName());
            cv.put("url",info.getUrl());
            cv.put("fileLen",info.getLen());
            db.insert(TABLE_NAME,null,cv);
        }catch (Exception e){
        }

    }

    public FileInfo queryData(SQLiteDatabase db, String url){
        Cursor cursor = db.query(true,TABLE_NAME,null,"url=?",new String[]{url},null,null,null,null);
        FileInfo info = new FileInfo();
        if(cursor!=null){
            while (cursor.moveToNext()){
                info.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                info.setLen(cursor.getInt(cursor.getColumnIndex("length")));
                info.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
            }
            cursor.close();
        }
        return info;
    }
}
