package com.example.river.uploadanddownload.upload;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.river.uploadanddownload.download.*;

import java.io.File;

/**
 * Created by Administrator on 2017/11/16.
 */
public class DBManager {
    private DBHelper dbHelper;
    public DBManager(DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public void save(FileInfo info){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("sourceId",info.getSourceId());
            cv.put("fileName",info.getFileName());
            cv.put("filePath",info.getUrl());
            cv.put("fileLen",info.getLen());
            cv.put("finished",info.getFinished());
            db.insert(DBHelper.TABLE_NAME,null,cv);
        }catch (Exception e){

        }

    }

    public String getBindId(FileInfo fileInfo){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select sourceid from uploadlog where filePath=?",new String[]{fileInfo.getUrl()});
        if(cursor.moveToFirst()){
            return cursor.getString(0);
        }
        return null;
    }

    public FileInfo queryData(String url){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(true, DBHelper.TABLE_NAME,null,"url=?",new String[]{url},null,null,null,null);
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
