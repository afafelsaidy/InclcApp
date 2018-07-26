package com.example.afaf.inclcapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.afaf.inclcapp.helper_database.appointment_Model;
import com.example.afaf.inclcapp.helper_database.debit_model;

import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by enterprise on 22/05/17.
 */

public class debit_helper extends SQLiteOpenHelper {


    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "Debits.db";
    private static final String table_Debits = "Debits";
    private static final String ID = "id";
    private static final String dDate = "dDate";
    private static final String dAmount = "dAmount";
    private static final String dAppointmentID = "dAppointmentID";
    private static final String dAppointmentName = "dAppointmentName";
    private static final String dProductID = "dProductID";
    private static final String dProductName = "dProductName";
    private static final String dNetPrice = "dNetPrice";
    private static final String dUnitNo = "dUnitNo";
    private static final String dId = "dId";


    private static final String[] COLUMNS = {ID, dDate, dAmount, dAppointmentID,
            dAppointmentName, dProductID, dProductName, dNetPrice, dUnitNo, dId};

    public debit_helper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create Event table
        String CREATE_Event_TABLE = "CREATE TABLE Debits ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "dDate TEXT," +
                " " + "dAmount TEXT, " + "dAppointmentID TEXT, " + "dAppointmentName TEXT, " + "dProductID TEXT, " + "dProductName TEXT," +
                " " + "dNetPrice TEXT, " + "dUnitNo TEXT, " + "dId TEXT)";
        db.execSQL(CREATE_Event_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table_Debits);
        this.onCreate(db);

    }

    public void createDebit(String dDate_, String dAmount_, String dAppointmentID_, String dAppointmentName_, String dProductID_
            , String dProductName_, String dNetPrice_, String dUnitNo_, String dId_) {
        // get reference of the EventDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(dDate, dDate_);
        values.put(dAmount, dAmount_);
        values.put(dAppointmentID, dAppointmentID_);
        values.put(dAppointmentName, dAppointmentName_);
        values.put(dProductID, dProductID_);
        values.put(dProductName, dProductName_);
        values.put(dNetPrice, dNetPrice_);
        values.put(dUnitNo, dUnitNo_);
        values.put(dId, dId_);

        // insert Event
        db.insert(table_Debits, null, values);

        // close database transaction
        // db.close();
    }

    public debit_model readDebit(int id) throws JSONException {
        // get reference of the EventDB database
        SQLiteDatabase db = this.getReadableDatabase();

        // get Event query
        Cursor cursor = db.query(table_Debits, // a. table
                COLUMNS, " id = ?", new String[]{String.valueOf(id)}, null, null, null, null);
//        JSONArray arr =   convertCursorToJSON(cursor);
        // if results !=null, parse the first one
        try {
            if (cursor != null)
                cursor.moveToFirst();

            debit_model EM = new debit_model();
            EM.setId(Integer.parseInt(cursor.getString(0)));
            EM.setdDate(cursor.getString(1));
            EM.setdAmount(cursor.getString(2));
            EM.setdAppointmentID(cursor.getString(3));
            EM.setdAppointmentName(cursor.getString(4));
            EM.setdProductID(cursor.getString(5));
            EM.setdProductName(cursor.getString(6));
            EM.setdNetPrice(cursor.getString(7));
            EM.setdUnitNo(cursor.getString(8));
            EM.setdId(cursor.getString(9));

            return EM;
        } catch (Exception ex) {

        }
        return null;
        // close database transaction
        //    db.close();

    }


    //-----------------------------------------------------------------------
    public List<debit_model> getAllDebits() {
        List<debit_model> eventsM = new LinkedList<debit_model>();

        // select Event query
        String query = "SELECT  * FROM " + table_Debits;

        // get reference of the EventDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        debit_model EM = null;
        if (cursor.moveToFirst()) {
            do {

                EM = new debit_model();
                EM.setId(Integer.parseInt(cursor.getString(0)));
                EM.setdDate(cursor.getString(1));
                EM.setdAmount(cursor.getString(2));
                EM.setdAppointmentID(cursor.getString(3));
                EM.setdAppointmentName(cursor.getString(4));
                EM.setdProductID(cursor.getString(5));
                EM.setdProductName(cursor.getString(6));
                EM.setdNetPrice(cursor.getString(7));
                EM.setdUnitNo(cursor.getString(8));
                EM.setdId(cursor.getString(9));

                eventsM.add(EM);
            } while (cursor.moveToNext());
        }

        return eventsM;
    }



    // Deleting single Event
    public void deleteEvent(appointment_Model EM) {

        // get reference of the EventDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete Event
        db.delete(table_Debits, ID + " = ?", new String[]{String.valueOf(EM.getId())});
        db.close();
    }



    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
