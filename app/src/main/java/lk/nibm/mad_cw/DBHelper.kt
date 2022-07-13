package lk.nibm.mad_cw

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context?) : SQLiteOpenHelper(context, "GymExercise.db", null, 1) {


    override fun onCreate(DB: SQLiteDatabase) {
        DB.execSQL("CREATE TABLE IF NOT EXISTS Exercise(UID TEXT , Date TEXT, Week TEXT,Name TEXT, Weight REAL, primary key(UID, Name, Week))")
    }

    override fun onUpgrade(DB: SQLiteDatabase, i: Int, ii: Int) {
        DB.execSQL("drop Table if exists Exercise")
    }

    fun insertData(UID:String, Date:String, Week:String, Name:String, Weight: Double) : Boolean {

        val dB = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("UID", UID)
        contentValues.put("Date", Date)
        contentValues.put("Week", Week)
        contentValues.put("Name", Name)
        contentValues.put("Weight", Weight)

        val result = dB.insert("Exercise", null, contentValues)

        return result != -1L
    }

    fun deleteData(name: String): Boolean? {
        val dB = this.writableDatabase
        val cursor: Cursor = dB.rawQuery("Select * from Exercise where name = ?", arrayOf(name))
        return if (cursor.count > 0) {
            val result = dB.delete("Exercise", "name=?", arrayOf(name)).toLong()
            result != -1L
        } else {
            false
        }
    }

    fun getData(uid: String): Cursor? {
        var uid = uid
        val dB = this.writableDatabase
        return dB.rawQuery("Select DISTINCT " +
                "Name, Date, Week, Weight " +
                "from Exercise  " +
                "ORDER BY WEEK DESC LIMIT (SELECT COUNT(DISTINCT Name) FROM Exercise) ", null)
    }

    fun getDataGraph(week: String, name: String): Cursor?{
        val dB = this.writableDatabase

        return dB.rawQuery("SELECT Week, Weight " +
                "FROM Exercise WHERE Week LIKE '"+week+"' AND NAME LIKE '"+name+"'", null)
    }

    fun getWeekCount(): Cursor?{
        val dB = this.writableDatabase

        return dB.rawQuery("SELECT COUNT(DISTINCT Week) FROM Exercise ", null)
    }

    fun getName(): Cursor?{
        val dB = this.writableDatabase

        return dB.rawQuery("SELECT DISTINCT Name FROM Exercise", null)
    }

    fun getRowCount(): Long {
        val db = this.readableDatabase
        val count = DatabaseUtils.queryNumEntries(db, "Exercise")
        db.close()
        return count
    }

    fun deleteAllData(){
        val dB = this.writableDatabase
    dB.execSQL("DELETE FROM Exercise")
    }
}