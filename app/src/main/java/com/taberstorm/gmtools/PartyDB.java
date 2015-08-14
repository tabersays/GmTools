package com.taberstorm.gmtools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;

/**
 * Created by taber on 8/2/2015.
 */

public class PartyDB {

    private PartyDBHelper dbHelper;

    private SQLiteDatabase database;

    public final static String PARTY_TABLE = "Parties";
    public final static String PARTY_NAME = "name";

    public final static String CHARACTER_TABLE = "Characters";
    public final static String CHARACTER_NAME = "name";
    public final static String CHARACTER_PARTY = "party";
    public final static String CHARACTER_INITIATIVE = "initiative";


    /**
     * @param context
     */
    public PartyDB(Context context) {
        dbHelper = new PartyDBHelper(context);
        database = dbHelper.getWritableDatabase();
    }


    public long createPartyRecord(String name) {
        ContentValues values = new ContentValues();
        values.put(PARTY_NAME, name);
        return database.insert(PARTY_TABLE, null, values);
    }

    public long createCharacterRecord(String name, String party, Integer initiative) {
        ContentValues values = new ContentValues();
        values.put(CHARACTER_NAME, name);
        values.put(CHARACTER_PARTY, party);
        values.put(CHARACTER_INITIATIVE, initiative);
        return database.insert(CHARACTER_TABLE, null, values);
    }

    public Cursor selectPartyRecord() {
        String[] cols = new String[]{PARTY_NAME};
        Cursor mCursor = database.query(true, PARTY_TABLE, cols, null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor selectCharacterRecord(String party) {
        String[] columns = new String[]{CHARACTER_NAME};
        String selection = CHARACTER_PARTY + "=?";
        String[] choice = new String[]{party};
        Cursor mCursor = database.query(true, CHARACTER_TABLE, columns, selection, choice, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long deletePartyRecord(String party) {
        String[] columns = new String[] {party};
        return database.delete(PARTY_TABLE, PARTY_NAME + "=?", columns);
    }
}
