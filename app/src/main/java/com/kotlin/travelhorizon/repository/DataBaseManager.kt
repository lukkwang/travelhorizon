package com.kotlin.travelhorizon.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.kotlin.travelhorizon.dto.Dto

class DataBaseManager(context: Context)  {
    private val db: SQLiteDatabase

    init {
        val helper = DataBaseSQLiteOpenHelper(context)

        db = helper.writableDatabase
    }

    companion object {
        private const val DB_NAME = "travel_horizon"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "record"

        private const val TABLE_ROW_ID = "id"  // millisecond
        private const val TABLE_ROW_DATE = "date"  // visit date (YYYY-MM-DD)
        private const val TABLE_ROW_YEAR = "year"  // YYYY
        private const val TABLE_ROW_HOUR = "hour"  // visit time (hour)
        private const val TABLE_ROW_MIN = "min"  // visit time (min)
        private const val TABLE_ROW_LOCATION_LATITUDE = "latitude"
        private const val TABLE_ROW_LOCATION_LONGITUDE = "longitude"
        private const val TABLE_ROW_SUBJECT = "subject"
        private const val TABLE_ROW_REVISIT = "revisit_flag"
        private const val TABLE_ROW_CONTENT = "content"
    }

    fun insert(dto: Dto) {

        val query = """INSERT INTO $TABLE_NAME
                        (
                            $TABLE_ROW_ID,
                            $TABLE_ROW_DATE,
                            $TABLE_ROW_YEAR,
                            $TABLE_ROW_HOUR,
                            $TABLE_ROW_MIN,
                            $TABLE_ROW_LOCATION_LATITUDE,
                            $TABLE_ROW_LOCATION_LONGITUDE,
                            $TABLE_ROW_SUBJECT,
                            $TABLE_ROW_REVISIT,
                            $TABLE_ROW_CONTENT
                        )
                        VALUES
                        (
                            '${dto.id}',
                            '${dto.date}',
                             ${dto.year},
                             ${dto.hour},
                            '${dto.min}',
                            '${dto.latitude}',
                            '${dto.longitude}',
                            '${dto.subject}',
                             ${dto.revisitFlag},
                            '${dto.content}'
                        )"""

        //Log.i("insert : ", query)

        db.execSQL(query)
    }

    /**
     * retrieve tab data list in a list page
     */
    fun selectGroupbyYearList(): List<String> {

        val yearList: MutableList<String> = mutableListOf()

        val query = """SELECT $TABLE_ROW_YEAR FROM $TABLE_NAME 
                        GROUP BY $TABLE_ROW_YEAR
                        ORDER BY $TABLE_ROW_YEAR DESC"""

        //Log.i("select group by year list : ", query)

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext())
            yearList.add(cursor.getString(0))

        cursor.close()

        return yearList.toList()
    }

    /**
     * retrieve record list in a tab
     */
    fun selectList(year: String): List<Dto> {

        val recordList: MutableList<Dto> = mutableListOf()

        val query = """SELECT
                                $TABLE_ROW_ID,
                                $TABLE_ROW_DATE,
                                $TABLE_ROW_HOUR,
                                $TABLE_ROW_MIN,
                                $TABLE_ROW_REVISIT,
                                $TABLE_ROW_SUBJECT
                        FROM
                                $TABLE_NAME
                        WHERE
                                $TABLE_ROW_YEAR = ?
                        ORDER BY
                                $TABLE_ROW_DATE DESC,
                                $TABLE_ROW_HOUR DESC,
                                $TABLE_ROW_MIN DESC,
                                $TABLE_ROW_ID DESC"""

        //Log.i("select list : ", query + " : " + year)

        val cursor = db.rawQuery(query, arrayOf(year))

        while (cursor.moveToNext()) {
            val dto: Dto = Dto(
                id = cursor.getLong(0),
                date = cursor.getString(1),
                hour = cursor.getInt(2),
                min = cursor.getInt(3),
                revisitFlag = if (cursor.getInt(4) == 1) true else false,
                subject = cursor.getString(5)
            )

            recordList.add(dto)
        }

        cursor.close()

        return recordList.toList()
    }

    /**
     * retrieve a record
     */
    fun select(id: String): Dto {

        var dto: Dto? = null

        val query = """SELECT
                                $TABLE_ROW_ID,
                                $TABLE_ROW_DATE,
                                $TABLE_ROW_HOUR,
                                $TABLE_ROW_MIN,
                                $TABLE_ROW_LOCATION_LATITUDE, 
                                $TABLE_ROW_LOCATION_LONGITUDE,
                                $TABLE_ROW_SUBJECT,
                                $TABLE_ROW_REVISIT,
                                $TABLE_ROW_CONTENT
                        FROM
                                $TABLE_NAME
                        WHERE
                                $TABLE_ROW_ID = CAST(? AS integer)"""

        //Log.i("select one : ", query)

        val cursor = db.rawQuery(query, arrayOf(id))

        if(cursor.moveToFirst()) {
            dto = Dto(
                id = cursor.getLong(0),
                date = cursor.getString(1),
                year = cursor.getString(1).split("-")[0],
                hour = cursor.getInt(2),
                min = cursor.getInt(3),
                latitude = cursor.getString(4),
                longitude = cursor.getString(5),
                subject = cursor.getString(6),
                revisitFlag = (cursor.getInt(7) == 1),
                content = cursor.getString(8)
            )
        } else
            dto = Dto(0)

        cursor.close()

        return dto
    }

    fun update(dto: Dto) {

        val query = """UPDATE  $TABLE_NAME
                        SET
                                $TABLE_ROW_DATE = '${dto.date}',
                                $TABLE_ROW_YEAR = ${dto.year},
                                $TABLE_ROW_HOUR = ${dto.hour},
                                $TABLE_ROW_MIN = '${dto.min}',
                                $TABLE_ROW_LOCATION_LATITUDE = '${dto.latitude}', 
                                $TABLE_ROW_LOCATION_LONGITUDE = '${dto.longitude}',
                                $TABLE_ROW_SUBJECT = '${dto.subject}',
                                $TABLE_ROW_REVISIT = ${dto.revisitFlag},
                                $TABLE_ROW_CONTENT = '${dto.content}'
                        WHERE
                                $TABLE_ROW_ID = ${dto.id}"""

        //Log.i("select one : ", query)

        db.execSQL(query)
    }

    fun delete(id: String) {

        val query = """DELETE FROM $TABLE_NAME
                        WHERE $TABLE_ROW_ID = ${id}"""

        //Log.i("delete : ", query)

        db.execSQL(query)
    }

    fun close(){
        db.close()
    }

    private inner class DataBaseSQLiteOpenHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {

            val newTableQueryString = """CREATE TABLE $TABLE_NAME (
                                                        $TABLE_ROW_ID integer primary key autoincrement not null,
                                                        $TABLE_ROW_DATE text not null,
                                                        $TABLE_ROW_YEAR text not null,
                                                        $TABLE_ROW_HOUR integer not null,
                                                        $TABLE_ROW_MIN integer not null,
                                                        $TABLE_ROW_LOCATION_LATITUDE text,
                                                        $TABLE_ROW_LOCATION_LONGITUDE text,
                                                        $TABLE_ROW_SUBJECT text not null,
                                                        $TABLE_ROW_REVISIT boolean,
                                                        $TABLE_ROW_CONTENT text
                                                        )"""

            db.execSQL(newTableQueryString)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        }
    }
}