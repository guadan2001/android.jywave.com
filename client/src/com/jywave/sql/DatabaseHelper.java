package com.jywave.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "SQLiteOpenHelper";
 
    private static final String DB_NAME = "jywave.db";
    private static final int VERSION = 1;
     
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS `eps` ( " +
        		"`id` INTEGER PRIMARY KEY NOT NULL,  " +
        		"`title` TEXT NOT NULL,  " +
        		"`category` TEXT NOT NULL,  " +
        		"`sn` INTEGER NOT NULL,  " +
        		"`duration` INTEGER NOT NULL,  " +
        		"`description` TEXT NOT NULL,  " +
        		"`publishDate` INTEGER NOT NULL, " +
        		"`star` INTEGER NOT NULL,  " +
        		"`rating` INTEGER NOT NULL,  " +
        		"`url` TEXT NOT NULL,  " +
        		"`coverUrl` TEXT NOT NULL,  " +
        		"`coverThumbnailUrl` TEXT NOT NULL,  " +
        		"`isNew` INTEGER NOT NULL default '0');";
        db.execSQL(sql);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
 
    }
}