package com.jess.contactfiltergroups;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by USER on 11/23/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "thesis.db";

    //table for contacts
    public static final String TABLE_CONTACTS = "contact";
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_CONTACT_NAME = "name";

    //table for contact numbers
    public static final String TABLE_CONTACTNUMS = "contactNum";
    public static final String COLUMN_CONTACTNUM_ID = "contactNum_id";
    public static final String COLUMN_CONTACTNUM_NUMBER = "number";

    //table for contact groups
    public static final String TABLE_CONTACTGROUPS = "contactGroup";
    public static final String COLUMN_CONTACTGROUP_ID = "contactGroup_id";
    public static final String COLUMN_CONTACTGROUP_NAME = "name";
    public static final String COLUMN_CONTACTGROUP_STATE = "state";

    //Composite keys
    public static final String TABLE_CONTACT_DETAILS = "contactDetails";
    public static final String TABLE_CONTACT_NUM_DETAILS = "contactNumDetails";

    //Foreign keys
    public static final String COLUMN_FK_CONTACTGROUP_ID = COLUMN_CONTACTGROUP_ID;
    public static final String COLUMN_FK_CONTACT_ID = COLUMN_CONTACT_ID ;
    public static final String COLUMN_FK_CONTACTNUM_ID = COLUMN_CONTACTNUM_ID;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACTGROUPS +" ("
                +COLUMN_CONTACTGROUP_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_CONTACTGROUP_NAME+" VARCHAR, "
                +COLUMN_CONTACTGROUP_STATE+" VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACT_DETAILS +" ("
                +COLUMN_FK_CONTACTGROUP_ID+" INTEGER, "
                +COLUMN_FK_CONTACT_ID+" VARCHAR, "
                +"FOREIGN KEY("+COLUMN_FK_CONTACTGROUP_ID+") REFERENCES "+TABLE_CONTACTGROUPS+"("+COLUMN_CONTACTGROUP_ID+"), "
                +"FOREIGN KEY("+COLUMN_FK_CONTACT_ID+") REFERENCES "+TABLE_CONTACTS+"("+COLUMN_CONTACT_ID+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void dropTable(SQLiteDatabase db, String tablename){

    }

    //Check contact if blocked by ID
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

    //Insert new contacts to blacklist
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

    //insert new contact group
    public boolean insertNewGroup(String name, ArrayList<ContactPerson> contactPersonArrayList){
        long breaker;
        int lastColumnInsert;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTACTGROUP_NAME,name);
        contentValues.put(COLUMN_CONTACTGROUP_STATE,"0");
        breaker = db.insert(TABLE_CONTACTGROUPS,null,contentValues);
        if(breaker == -1)
            return false;
        else
            lastColumnInsert = (int)breaker;
        contentValues.clear();
        for (int i = 0; i < contactPersonArrayList.size(); i++){
            contentValues.clear();
            contentValues.put(COLUMN_FK_CONTACT_ID,contactPersonArrayList.get(i).getContactID());
            contentValues.put(COLUMN_FK_CONTACTGROUP_ID,lastColumnInsert);
            breaker = db.insert(TABLE_CONTACT_DETAILS,null,contentValues);
            if(breaker == -1)
                return false;
            Log.v("Grouped ["+i+"]",contactPersonArrayList.get(i).getContactName());
            contentValues.clear();
        }
        return true;
    }
    //change state of the group
    public boolean changeGroupState(String groupID, String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTACTGROUP_STATE,state);
        if(db.update(TABLE_CONTACTGROUPS,contentValues,COLUMN_CONTACTGROUP_ID+"= ?", new String[]{groupID})>0){
            Log.v("State change group "+groupID, state);
            return true;
        }
        return false;
    }
    public boolean checkIfGroupState1(String groupID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACTGROUP_STATE+" FROM "+TABLE_CONTACTGROUPS+" WHERE "+COLUMN_CONTACTGROUP_ID+" = "+groupID+";", null);
        while (cursor.moveToNext()){
            String state = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACTGROUP_STATE));
            if(state.equalsIgnoreCase("1")){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;

    }
    public Cursor readContact(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACTS,null);
        return cursor;
    }
    public Cursor readContactGroup(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACTGROUPS,null);
        return cursor;
    }
    public Cursor readContactDetail(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACT_DETAILS,null);
        return cursor;
    }
}
