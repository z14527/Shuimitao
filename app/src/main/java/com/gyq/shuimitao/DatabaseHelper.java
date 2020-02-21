package com.gyq.shuimitao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "KET.db"; //数据库名称
    private static final int version = 1; //数据库版本
    private final static String TABLE_NAME = "word";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);

    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }

    //增加操作
//    public long insert(String id1, String name1, String note1, String[] shan, String[] shui) {
//        SQLiteDatabase db = this.getWritableDatabase();
///* ContentValues */
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");// HH:mm:ss
//        ContentValues cv = new ContentValues();
//        Date date = new Date(System.currentTimeMillis());
//        cv.put("ct", simpleDateFormat.format(date));
//        long row = db.insert(TABLE_NAME, null, cv);
//        return row;
//    }

//    //删除操作
//    public void delete(String id1) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String where = "spell = ?";
//        String[] whereValue = {id1};
//        db.delete(TABLE_NAME, where, whereValue);
//    }

    //修改操作
    public void update(String id1, String fname, String fvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id = ?";
        String[] whereValue = {id1};

        ContentValues cv = new ContentValues();
        cv.put(fname, fvalue);
        db.update(TABLE_NAME, cv, where, whereValue);
    }
    public void update(String table,String id1, String fname, String fvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id = ?";
        String[] whereValue = {id1};

        ContentValues cv = new ContentValues();
        cv.put(fname, fvalue);
        db.update(table, cv, where, whereValue);
    }
    //修改操作
    public void insert(String table,String[] fname, String[] fvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for(int i=0;i<fname.length;i++)
            cv.put(fname[i], fvalue[i]);
        db.insert(table, null,cv);
    }

    public Cursor select(String id1, String[] fn) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = {id1};
        Cursor cursor = db.query(TABLE_NAME, fn, "id=?", whereValue, null, null, null);
        return cursor;
    }
    public Cursor tselect(String table1,String id1, String[] fn) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] whereValue = {id1};
        Cursor cursor = db.query(table1, fn, "id=?", whereValue, null, null, null);
        return cursor;
    }
    public Cursor select(String[] fn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, fn, null, null, null, null, null);
        return cursor;
    }

    public Cursor select(String selection,String[] where,String[] fn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, fn, selection, where, null, null, null);
        return cursor;
    }

    public Cursor lselect(String key, String[] fn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, fn, "name like ?", new String[]{"%" + key + "%"}, null, null, null);
//        Cursor cursor = db.rawQuery("select " + fn.toString() + " from " + TABLE_NAME + " where name like ? or ct like ?", new String[]{"%" + key + "%", "%" + key + "%"});
        return cursor;
    }
    public Cursor lselect(String key, String fd, String[] fn) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, fn, fd + " like ?", new String[]{"%" + key + "%"}, null, null, null);
//        Cursor cursor = db.rawQuery("select " + fn.toString() + " from " + TABLE_NAME + " where name like ? or ct like ?", new String[]{"%" + key + "%", "%" + key + "%"});
        return cursor;
    }
    public Cursor rselect(String selection,String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selection,args);
        return cursor;
    }

}