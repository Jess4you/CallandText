package com.jess.contactfiltergroups;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by USER on 11/23/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "thesis.db";

    //table for contacts
    public static final String TABLE_CONTACTS = "contact";
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_CONTACT_NAME = "name";

    //table for contactnumbers
    public static final String TABLE_CONTACTNUMS = "contactNum";
    public static final String COLUMN_CONTACTNUM_ID = "contactNum_id";
    public static final String COLUMN_CONTACTNUM_NUMBER = "number";
    //table for contact number bridge
    //s
    public static final String TABLE_CONTACT_NUM_DETAILS = "contactNumDetails";
    public static final String COLUMN_FK_CONTACT_ID = COLUMN_CONTACT_ID ;
    public static final String COLUMN_FK_CONTACTNUM_ID = COLUMN_CONTACTNUM_ID;

    public static final String TABLE_CONTACTGROUPS = "contactgroup";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACTS +" ("
                +COLUMN_CONTACT_ID+" VARCHAR PRIMARY KEY, "
                +COLUMN_CONTACT_NAME+" VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACTNUMS +" ("
                +COLUMN_CONTACTNUM_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_CONTACTNUM_NUMBER+" VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACT_NUM_DETAILS +" ("
                +COLUMN_FK_CONTACT_ID+" VARCHAR, "
                +COLUMN_FK_CONTACTNUM_ID+" INTEGER, "
                +"FOREIGN KEY("+COLUMN_FK_CONTACT_ID+") REFERENCES "+TABLE_CONTACTS+"("+COLUMN_CONTACT_ID+"), "
                +"FOREIGN KEY("+COLUMN_FK_CONTACTNUM_ID+") REFERENCES "+TABLE_CONTACTNUMS+"("+COLUMN_CONTACTNUM_ID+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void dropTable(SQLiteDatabase db, String tablename){

    }
    public boolean checkIfBlockedByID(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACT_ID+" FROM "+TABLE_CONTACTS+" WHERE "+COLUMN_CONTACT_ID+" = "+id+";", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public boolean checkIfBlockedByName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACTS+" WHERE "+COLUMN_CONTACT_NAME+" = "+name+";", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public boolean insertInto(String id, String name, String[] contactnums){
        long breaker;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTACT_ID,id);
        contentValues.put(COLUMN_CONTACT_NAME,name);
        breaker = db.insert(TABLE_CONTACTS,null,contentValues);
        if(breaker == -1)
            return false;
        contentValues.clear();
        for (int i = 0; i < contactnums.length; i++){
            contentValues.put(COLUMN_CONTACTNUM_NUMBER,contactnums[i]);
            long contactNumId = db.insert(TABLE_CONTACTNUMS,null,contentValues);
            if(contactNumId == -1)
                return false;
            contentValues.clear();
            contentValues.put(COLUMN_FK_CONTACT_ID,id);
            contentValues.put(COLUMN_FK_CONTACTNUM_ID,contactNumId);
            breaker = db.insert(TABLE_CONTACT_NUM_DETAILS,null,contentValues);
            if(breaker == -1)
                return false;
            contentValues.clear();
        }
        return true;
    }
    //remove blocked numbers from contacts
    public boolean removeBlocked(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_FK_CONTACTNUM_ID+" FROM "+TABLE_CONTACT_NUM_DETAILS+" WHERE "+COLUMN_FK_CONTACT_ID+" = "+id,null);
        String tempContactNumIds;
        for (int i = 0; cursor.moveToNext(); i++){
            tempContactNumIds = cursor.getString(cursor.getColumnIndex(COLUMN_FK_CONTACTNUM_ID));
            db.delete(TABLE_CONTACTNUMS, COLUMN_CONTACTNUM_ID+" = "+tempContactNumIds,null);
        }
        db.delete(TABLE_CONTACTS,COLUMN_CONTACT_ID+" = "+id,null);
        db.delete(TABLE_CONTACT_NUM_DETAILS,COLUMN_FK_CONTACT_ID+" = "+id,null);
        return true;
    }

}
