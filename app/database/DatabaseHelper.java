package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mi_base.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
        db.execSQL("CREATE TABLE ejemplo (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        db.execSQL("DROP TABLE IF EXISTS ejemplo");
        onCreate(db);
    }
}