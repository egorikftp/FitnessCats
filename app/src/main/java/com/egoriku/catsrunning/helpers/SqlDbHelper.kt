package com.egoriku.catsrunning.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.egoriku.catsrunning.App
import org.jetbrains.anko.db.*

class SqlDbHelper(context: Context = App.instance) : ManagedSQLiteOpenHelper(context, SQL_CONSTANTS.DB_NAME, null, SQL_CONSTANTS.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(SQL_CONSTANTS.TABLE_REMINDER, true,
                Pair(SQL_CONSTANTS._ID, INTEGER + PRIMARY_KEY),
                Pair(SQL_CONSTANTS.TYPE_FIT, INTEGER),
                Pair(SQL_CONSTANTS.DATE_REMINDER, REAL),
                Pair(SQL_CONSTANTS.IS_RING, INTEGER))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(SQL_CONSTANTS.DB_NAME, true)
        onCreate(db)
    }

    companion object SQL_CONSTANTS {
        val DB_NAME = "FITNESS_CATS"
        val DB_VERSION = 1
        val instance by lazy { SqlDbHelper() }

        val TABLE_REMINDER = "Reminder"
        val _ID = "_id"
        val TYPE_FIT = "typeFit"
        val DATE_REMINDER = "dateReminder"
        val IS_RING = "isRing"
    }
}