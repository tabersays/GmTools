package com.taberstorm.gmtools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by taber on 8/2/2015.
 */
public class PartyDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PartyDB";

    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE_PARTIES = "CREATE TABLE Parties (name TEXT PRIMARY KEY)";
    private static final String DATABASE_CREATE_TABLE_CHARACTERS = "CREATE TABLE Characters (name TEXT PRIMARY KEY , party TEXT , initiative INTEGER)";

    public PartyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_PARTIES);
        database.execSQL(DATABASE_CREATE_TABLE_CHARACTERS);

    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(PartyDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS Parties");
        database.execSQL("DROP TABLE IF EXISTS Characters");
        onCreate(database);
    }
}

