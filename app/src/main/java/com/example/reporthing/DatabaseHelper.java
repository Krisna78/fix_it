package com.example.reporthing;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.reporthing.Auth.AuthActivity;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "db_master.db";
    public static final Integer versions = 1;
    public DatabaseHelper(@Nullable Context context) {
        super(context,databaseName,null,versions);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table student(id TEXT primary key,password TEXT," +
                "username TEXT,namaSiswa TEXT)");
        db.execSQL("Create table teacher(id TEXT primary key,password TEXT," +
                "username TEXT,namaGuru TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists student");
        db.execSQL("drop table if exists teacher");
    }
    public Boolean insertData(String ids,String pass,String username, String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",ids);
        contentValues.put("password",pass);
        contentValues.put("username",username);
        contentValues.put("namaGuru",name);
        long result = sqLiteDatabase.insert("teacher",null,contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public String checkUsername(String username, String password) {
        String sendData;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorStudent = sqLiteDatabase.rawQuery("select * from student where username = ? and password = ? ",new String[]{username,password});
        Cursor cursorTeacher = sqLiteDatabase.rawQuery("select * from teacher where username = ? and password = ? ",new String[]{username,password});
        if (cursorStudent.getCount() > 0) {
            return "student";
        } else if(cursorTeacher.getCount() > 0) {
            return "teacher";
        } else {
            return "salah";
        }
//        if(cursor.getCount() > 0 ) {
//            return true;
//        } else {
//            return false;
//        }
    }
}