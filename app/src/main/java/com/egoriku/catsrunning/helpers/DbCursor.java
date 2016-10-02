package com.egoriku.catsrunning.helpers;

import android.database.Cursor;


public class DbCursor {
    private Cursor cursor;

    public DbCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean isValid() {
        if (cursor != null && cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public String getString(String column) {
        int columnIndex = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(columnIndex);
    }

    public int getInt(String column) {
        int columnIndex = cursor.getColumnIndexOrThrow(column);
        return cursor.getInt(columnIndex);
    }

    public double getDouble(String column) {
        int columnIndex = cursor.getColumnIndexOrThrow(column);
        return cursor.getDouble(columnIndex);
    }

    public long getLong(String column) {
        int columnIndex = cursor.getColumnIndexOrThrow(column);
        return cursor.getLong(columnIndex);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void close() {
        cursor.close();
    }
}
