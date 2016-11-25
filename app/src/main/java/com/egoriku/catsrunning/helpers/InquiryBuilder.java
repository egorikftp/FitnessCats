package com.egoriku.catsrunning.helpers;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;

import com.egoriku.catsrunning.App;

import java.util.ArrayList;
import java.util.Collections;


public class InquiryBuilder implements Cursor {
    private static final String SELECT = "SELECT";
    private static final String SELECT_FROM = "SELECT FROM";
    private static final String DELETE = "DELETE FROM";
    private static final String WHERE = "WHERE";
    private static final String FROM = "FROM";
    private static final String JOIN = "JOIN";
    private static final String ON = "ON";
    private static final String AND = "AND";
    private static final String CREATE = "CREATE TABLE";
    private static final String CREATE_INDEX = "CREATE INDEX";
    private static final String DROP_INDEX = "DROP INDEX";
    private static final String DROP = "DROP TABLE";
    private static final String ALTER = "ALTER TABLE";
    private static final String RENAME = "RENAME TO";
    private static final String INSERT = "INSERT INTO";
    private static final String UPDATE = "UPDATE";
    private static final String SET = "SET";
    private static final String ORDER_BY = "ORDER BY";
    private static final String DESC = "DESC";
    private static final String GROUP_BY = "GROUP BY";
    private static final String VALUES = "VALUES";
    private static final String PRIMARY_KEY = "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT";
    public static final String EQ_QUESTION = "=?";
    //TODO List<String>
    private ArrayList<String> params = new ArrayList<>();
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<String> insertFields = new ArrayList<>();
    private ArrayList<String> insertValues = new ArrayList<>();
    private StringBuilder query = new StringBuilder();

    public InquiryBuilder from(String table) {
        query.append(FROM + " ").append(table).append(" ");
        return this;
    }


    public InquiryBuilder get(String... columns) {
        for (int i = 0, len = columns.length; i < len; i++) {
            query.append(" ").append(columns[i]);
            if (i + 1 < len) {
                query.append(",");
            } else {
                query.append(" ");
            }
        }
        return this;
    }

    public InquiryBuilder join(String table, String condition, String param) {
        if (param != null) {
            query.append(JOIN + " ").append(table).append(" ").append(ON).append(" ").append(condition).append("? ");
            params.add(param);
        } else {
            query.append(JOIN + " ").append(table).append(" ").append(ON).append(" ").append(condition).append(" ");
        }
        return this;
    }


    public InquiryBuilder where(boolean multi, String condition, String... params) {
        if (!params.equals(null)) {
            if (multi) {
                query.append(WHERE + " ").append(condition).append(" ");
                Collections.addAll(this.params, params);
            } else {
                query.append(WHERE + " ").append(condition).append("?");
                Collections.addAll(this.params, params);
            }
        } else {
            query.append(WHERE + " ").append(condition);
        }
        return this;
    }


    public InquiryBuilder updateWhere(String condition, String... params) {
        for (int i = 0, len = insertFields.size(); i < len; i++) {
            query.append(insertFields.get(i)).append(EQ_QUESTION);
            if (i + 1 < len) {
                query.append(", ");
            } else {
                query.append(" ");
            }
        }

        if (!params.equals(null)) {
            query.append(WHERE + " ").append(condition).append("?");
            Collections.addAll(insertValues, params);
        } else {
            query.append(WHERE + " ").append(condition);
        }
        return this;
    }


    public Cursor select() {
        return getQuery(SELECT, getParams());
    }


    public String selectFrom(String table, String... columns) {
        query.append(" " + SELECT + " ");
        for (int i = 0, len = columns.length; i < len; i++) {
            query.append(" ").append(columns[i]);
            if (i + 1 < len) {
                query.append(",");
            } else {
                query.append(" " + FROM + " ").append(table);
            }
        }
        return query.toString();
    }


    public InquiryBuilder table(String table) {
        query.append(" ").append(table).append(" (");
        return this;
    }

    public InquiryBuilder updateTable(String table) {
        query.append(" ").append(table).append(" ").append(SET).append(" ");
        return this;
    }

    public InquiryBuilder pkField(String field) {
        query.append(field).append(" ").append(PRIMARY_KEY).append(",");
        return this;
    }


    public InquiryBuilder field(String field, String params) {
        fields.add(field + " " + params);
        return this;
    }

    public InquiryBuilder set(String field, Object value) {
        insertFields.add(field);
        insertValues.add(String.valueOf(value));
        return this;
    }

    public String create() {
        for (int i = 0, len = fields.size(); i < len; i++) {
            query.append(fields.get(i));
            if (i + 1 < len) {
                query.append(",");
            } else {
                query.append(")");
            }
        }
        return CREATE + query.toString();
    }

    public String drop(String table) {
        return DROP + " " + table;
    }

    public InquiryBuilder alter(String table) {
        query.append(ALTER + " ").append(table);
        return this;
    }

    public String rename(String table) {
        return query.toString() + " " + RENAME + " " + table;
    }


    public Void insert(SQLiteDatabase db) {
        for (int i = 0, len = insertFields.size(); i < len; i++) {
            query.append(insertFields.get(i));
            if (i + 1 < len) {
                query.append(", ");
            } else {
                query.append(") " + VALUES + " (");
            }
        }

        for (int i = 0, len = insertValues.size(); i < len; i++) {
            query.append("?");
            if (i + 1 < len) {
                query.append(", ");
            } else {
                query.append(")");
            }
        }

        SQLiteStatement statement = db.compileStatement(INSERT + query.toString());
        statement.bindAllArgsAsStrings(insertValues.toArray(new String[insertValues.size()]));

        try {
            statement.executeInsert();
        } finally {
            statement.close();
        }
        return null;
    }


    public long insertForId(SQLiteDatabase db) {
        long id;
        for (int i = 0, len = insertFields.size(); i < len; i++) {
            query.append(insertFields.get(i));
            if (i + 1 < len) {
                query.append(", ");
            } else {
                query.append(") " + VALUES + " (");
            }
        }

        for (int i = 0, len = insertValues.size(); i < len; i++) {
            query.append("?");
            if (i + 1 < len) {
                query.append(", ");
            } else {
                query.append(")");
            }
        }

        SQLiteStatement statement = db.compileStatement(INSERT + query.toString());
        statement.bindAllArgsAsStrings(insertValues.toArray(new String[insertValues.size()]));

        try {
            id = statement.executeInsert();
        } finally {
            statement.close();
        }
        return id;
    }


    public InquiryBuilder insertInto(String table, String... columns) {
        query.append(INSERT + " ").append(table).append("(");
        for (int i = 0, len = columns.length; i < len; i++) {
            query.append(" ").append(columns[i]);
            if (i + 1 < len) {
                query.append(",");
            } else {
                query.append(")");
            }
        }
        return this;
    }


    public Void update() {
        SQLiteStatement statement = App.getInstance().getDb().compileStatement(UPDATE + query.toString());
        statement.bindAllArgsAsStrings(insertValues.toArray(new String[insertValues.size()]));

        try {
            statement.execute();
        } finally {
            statement.close();
        }

        return null;
    }


    private String[] getParams() {
        if (!params.isEmpty()) {
            String[] resultParams = new String[params.size()];
            for (int i = 0, len = params.size(); i < len; i++) {
                resultParams[i] = params.get(i);
            }
            return resultParams;
        } else {
            return null;
        }
    }

    private Cursor getQuery(String queryType, String[] params) {
        return App.getInstance().getDb().rawQuery(queryType + query.toString(), params);

    }

    public InquiryBuilder orderBy(String column) {
        query.append(" " + ORDER_BY + " ").append(column);
        return this;
    }

    public InquiryBuilder groupBy(String column) {
        query.append(" " + GROUP_BY + " ").append(column);
        return this;
    }

    public InquiryBuilder desc() {
        query.append(" " + DESC + " ");
        return this;
    }

    public String createIndex(String indexName, String table, String column) {
        return CREATE_INDEX + " " + indexName + " " + ON + " " + table + " (" + column + ")";
    }

    public String dropIndex(String indexName) {
        return DROP_INDEX + " " + indexName;
    }

    public Void cleanTable(String table) {
        App.getInstance().getDb().execSQL(DELETE + " " + table);
        return null;
    }

    public InquiryBuilder tableDelete(String table) {
        query.append(" ").append(table).append(" ");
        return this;
    }

    public Void delete() {
        App.getInstance().getDb().execSQL(DELETE + query.toString(), getParams());
        return null;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public boolean move(int offset) {
        return false;
    }

    @Override
    public boolean moveToPosition(int position) {
        return false;
    }

    @Override
    public boolean moveToFirst() {
        return false;
    }

    @Override
    public boolean moveToLast() {
        return false;
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    @Override
    public boolean moveToPrevious() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public int getColumnIndex(String columnName) {
        return 0;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return null;
    }

    @Override
    public String[] getColumnNames() {
        return new String[0];
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return new byte[0];
    }

    @Override
    public String getString(int columnIndex) {
        return null;
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

    }

    @Override
    public short getShort(int columnIndex) {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) {
        return 0;
    }

    @Override
    public long getLong(int columnIndex) {
        return 0;
    }

    @Override
    public float getFloat(int columnIndex) {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex) {
        return 0;
    }

    @Override
    public int getType(int columnIndex) {
        return 0;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return false;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public void setExtras(Bundle extras) {

    }

    @Override
    public Bundle respond(Bundle extras) {
        return null;
    }
}

