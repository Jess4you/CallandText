package com.jess.contactfiltergroup;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "database";
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

    //table for applications
    public static final String TABLE_APPLICATIONS = "application";
    public static final String COLUMN_APP_ID = "appID";
    public static final String COLUMN_APP_NAME = "appName";
    public static final String COLUMN_APP_STAMP = "appTime";
    public static final String COLUMN_APP_STAMP2 = "appTime2";
    public static final String COLUMN_APP_STATE = "appState";


    //table for appgroups
    public static final String TABLE_APPGROUPS = "applicationGroup";
    public static final String COLUMN_APPGROUP_ID = "appGroupID";
    public static final String COLUMN_APPGROUP_NAME = "appGroupName";
    public static final String COLUMN_APPGROUP_STATE = "appGroupState";

    //composite keys
    public static final String TABLE_APP_DETAILS = "appDetails";
    public static final String COLUMN_FK_APP_ID = COLUMN_APP_ID;
    public static final String COLUMN_FK_APPGROUP_ID = COLUMN_APPGROUP_ID;
    public static final String COLUMN_FK_APP_NAME = COLUMN_APP_NAME;

    //table for locks
    public static final String TABLE_LOCKS = "locks";
    public static final String COLUMN_LOCK_ID = "lockID";
    public static final String COLUMN_LOCK_ONE = "firstLock";
    public static final String COLUMN_LOCKONE_PASS = "firstPass";
    public static final String COLUMN_LOCK_TWO = "secondLock";
    public static final String COLUMN_LOCKTWO_PASS = "secondPass";
    public static final String COLUMN_LOCK_STATE = "passState";

    //table for security questions
    public static final String TABLE_SECURITY_QUESTION = "securityQuestion";
    public static final String COLUMN_SEC_ID = "secID";
    public static final String COLUMN_QUESTION_ONE = "firstQuestion";
    public static final String COLUMN_QUESTIONONE_ANSWER = "firstAnswer";
    public static final String COLUMN_QUESTION_TWO = "secondQuestion";
    public static final String COLUMN_QUESTIONTWO_ANSWER = "secondAnswer";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CONFIGURATION TABLES
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOCKS + "("
                + COLUMN_LOCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCK_ONE + " VARCHAR, "
                + COLUMN_LOCKONE_PASS + " VARCHAR, "
                + COLUMN_LOCK_TWO + " VARCHAR, "
                + COLUMN_LOCKTWO_PASS + " VARCHAR, "
                + COLUMN_LOCK_STATE + " BOOLEAN); ");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SECURITY_QUESTION + "("
                + COLUMN_SEC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUESTION_ONE + " VARCHAR, "
                + COLUMN_QUESTIONONE_ANSWER + " VARCHAR, "
                + COLUMN_QUESTION_TWO + " VARCHAR, "
                + COLUMN_QUESTIONTWO_ANSWER + " VARCHAR); ");

        //CONTACT TABLES
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
                +"FOREIGN KEY("+COLUMN_FK_CONTACTGROUP_ID+") REFERENCES "+TABLE_CONTACTGROUPS+"("+COLUMN_CONTACTGROUP_ID+"));");

        //APPLICATION TABLES
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_APPLICATIONS + "("
                + COLUMN_APP_ID + " VARCHAR PRIMARY KEY, "
                + COLUMN_APP_NAME + " VARCHAR, "
                + COLUMN_APP_STAMP + " INTEGER, "
                + COLUMN_APP_STAMP2 + " INTEGER, "
                + COLUMN_APP_STATE + " VARCHAR); ");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_APPGROUPS + "("
                + COLUMN_APPGROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_APPGROUP_NAME + " VARCHAR, "
                + COLUMN_APPGROUP_STATE + " VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_APP_DETAILS + "("
                + COLUMN_FK_APPGROUP_ID + " INTEGER, "
                + COLUMN_FK_APP_NAME + " VARCHAR, "
                + "FOREIGN KEY(" + COLUMN_FK_APPGROUP_ID + ") REFERENCES "
                + TABLE_APPGROUPS + "(" + COLUMN_APPGROUP_ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    /*public void dropTable(SQLiteDatabase db, String tablename){

    }*/
    public boolean insertToLockTable(String appname, int stamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_APP_NAME, appname);
        contentValues.put(COLUMN_APP_STAMP, stamp);
        contentValues.put(COLUMN_APP_STATE, true);

        long result = db.insert(TABLE_APPLICATIONS, null, contentValues);
        if(result == -1){
            return false;
        }else{
            System.out.println(appname+" : "+stamp+" - STORED");
            return true;
        }
    }
    public int insertAppLockGroup(String name, ArrayList<ApplicationObj> applicationObjArrayList){
        long breaker;
        int lastColumnInsert;
        Log.v("Group name",name);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_APPGROUP_NAME,name);
        contentValues.put(COLUMN_APPGROUP_STATE,"0");
        breaker = db.insert(TABLE_APPGROUPS,null,contentValues);
        if(breaker == -1)
            return 0;
        else
            lastColumnInsert = (int)breaker;
        contentValues.clear();
        for (int i = 0; i < applicationObjArrayList.size(); i++){
            Log.v(TAG,"insertAppLockGroup for 1");
            contentValues.clear();
            contentValues.put(COLUMN_FK_APP_NAME,applicationObjArrayList.get(i).getAppPackageName());
            contentValues.put(COLUMN_FK_APPGROUP_ID,lastColumnInsert);
            breaker = db.insert(TABLE_APP_DETAILS,null,contentValues);
            if(breaker == -1)
                return 0;
            Log.v("Grouped ["+i+"]",applicationObjArrayList.get(i).getAppPackageName());
            contentValues.clear();
        }
        return lastColumnInsert;
    }
    //TODO:notifyDataSetChanges in change state makes lag
    public boolean changeAppGroupState(String groupID, String state, Context cf){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.v(TAG,"changeAppGroupState: Passing parameters ID = "+groupID+", State = "+state);
        contentValues.put(COLUMN_APPGROUP_STATE,state);
        if(db.update(TABLE_APPGROUPS,contentValues,COLUMN_APPGROUP_ID+"= ?", new String[]{groupID})>0){
            Log.v("State change group "+groupID, state);
            Cursor detailCursor = db.rawQuery("SELECT "+COLUMN_FK_APP_NAME+" FROM "+TABLE_APP_DETAILS+" WHERE "+COLUMN_FK_APPGROUP_ID+" = "+groupID,null);
            while(detailCursor.moveToNext()){
                String lockedID = detailCursor.getString(detailCursor.getColumnIndex(COLUMN_FK_APP_NAME));
                if(state.equalsIgnoreCase("1")) {
                    if(!isStored(lockedID)){
                        if (insertToLockTable(lockedID, 0)) {
                            Log.v(TAG, "Change state "+lockedID+": On");
                            ArrayAdapter<ApplicationObj> AOA = ApplicationObjFragment.getApplicationObjArrayAdapter();
                            AOA.notifyDataSetChanged();
                            ApplicationObjFragment.setApplicationObjArrayAdapter(AOA);
                        }
                    }
                }else {
                    if (changeAppState(lockedID)) {
                        Log.v(TAG, "Change state "+lockedID+": Off");
                        ArrayAdapter<ApplicationObj> AOA = ApplicationObjFragment.getApplicationObjArrayAdapter();
                        AOA.notifyDataSetChanged();
                        ApplicationObjFragment.setApplicationObjArrayAdapter(AOA);
                    }
                }
            }
            return true;
        }
        Log.v("State change","Fail");
        return false;
    }

    public boolean checkIfAppGroupState1(String groupID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_APPGROUP_STATE+" FROM "+TABLE_APPGROUPS+" WHERE "+COLUMN_APPGROUP_ID+" = "+groupID+";", null);
        while (cursor.moveToNext()){
            String state = cursor.getString(cursor.getColumnIndex(COLUMN_APPGROUP_STATE));
            if(state.equalsIgnoreCase("1")){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
    public boolean isStored(String app){
        SQLiteDatabase db = this.getWritableDatabase();
        String storedApp="";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APPLICATIONS + " WHERE " + COLUMN_APP_STATE+ " = 1", null);
        while (cursor.moveToNext()) {
            storedApp = cursor.getString(cursor.getColumnIndex(COLUMN_APP_NAME));
            if (app.equalsIgnoreCase(storedApp)) {
                return true;
            }
        }
        return false;

    }

    public boolean isLocked(String app){
        SQLiteDatabase db = this.getWritableDatabase();
        String appState="";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APPLICATIONS + " WHERE " + COLUMN_APP_NAME+ " LIKE '%"+app+"%'", null);
        while (cursor.moveToNext()) {
            appState = cursor.getString(cursor.getColumnIndex(COLUMN_APP_STATE));
            if (appState.equals("1")) {
                Log.v(TAG,"Is locked");
                return true;
            }
        }
        return false;

    }

    public boolean changeAppState(String app){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_APP_STATE, false);

        if(db.update(TABLE_APPLICATIONS,contentValues,COLUMN_APP_NAME+" LIKE '%"+app+"%'", null)>0){
            return true;
        }
        return false;
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

    /*public boolean checkIfBlockedByName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CONTACTS+" WHERE "+COLUMN_CONTACT_NAME+" = "+name+";", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }*/

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
    public int insertNewContactGroup(String name, ArrayList<ContactPerson> contactPersonArrayList){
        long breaker;
        int lastColumnInsert;
        Log.v("Group name",name);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTACTGROUP_NAME,name);
        contentValues.put(COLUMN_CONTACTGROUP_STATE,"0");
        breaker = db.insert(TABLE_CONTACTGROUPS,null,contentValues);
        if(breaker == -1)
            return 0;
        else
            lastColumnInsert = (int)breaker;
        contentValues.clear();
        for (int i = 0; i < contactPersonArrayList.size(); i++){
            contentValues.clear();
            contentValues.put(COLUMN_FK_CONTACT_ID,contactPersonArrayList.get(i).getContactID());
            contentValues.put(COLUMN_FK_CONTACTGROUP_ID,lastColumnInsert);
            breaker = db.insert(TABLE_CONTACT_DETAILS,null,contentValues);
            if(breaker == -1)
                return 0;
            Log.v("Grouped ["+i+"]",contactPersonArrayList.get(i).getContactName());
            contentValues.clear();
        }
        return lastColumnInsert;
    }


    //change state of the group and add contacts inside the group to the blacklist
    public boolean changeContactGroupState(String groupID, String state, Context cf){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.v("Changing state","Start");
        Log.v("Passing parameters","ID = "+groupID+", State = "+state);
        contentValues.put(COLUMN_CONTACTGROUP_STATE,state);
        if(db.update(TABLE_CONTACTGROUPS,contentValues,COLUMN_CONTACTGROUP_ID+"= ?", new String[]{groupID})>0){
            Log.v("State change group "+groupID, state);
            Cursor detailCursor = db.rawQuery("SELECT "+COLUMN_FK_CONTACT_ID+" FROM "+TABLE_CONTACT_DETAILS+" WHERE "+COLUMN_FK_CONTACTGROUP_ID+" = "+groupID,null);
            while(detailCursor.moveToNext()){
                String blockedID = detailCursor.getString(detailCursor.getColumnIndex(COLUMN_FK_CONTACT_ID));
                Log.v("Contact ID fetched:",blockedID);
                Cursor contactCursor = cf.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while(contactCursor.moveToNext()){
                    String contactID = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    if(blockedID.equalsIgnoreCase(contactID)) {
                        String contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Cursor phoneCursor = cf.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{contactID},null);
                        //Retrieve numbers of each contact
                        String[] contactNums = new String[phoneCursor.getCount()];
                        for(int i = 0;phoneCursor.moveToNext();i++){
                            String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if(number.substring(0,3).equals("+63")&&number.length()==13){
                                number = "0"+number.substring(3,13);
                                Log.v("substring","success");
                            }else if(number.substring(0,3).equals("+63")&&number.length()==11){
                                number = "0"+number.substring(3,11);
                                Log.v("substring","success");

                            }
                            contactNums[i] = number;
                            Log.d("contact",contactName);
                            Log.d("number",contactNums[i]);

                            if(state.equalsIgnoreCase("1")) {
                                insertInto(contactID, contactName, contactNums);
                                ContactFilter.getContactPersonArrayAdapter().notifyDataSetChanged();
                                /*ArrayList<ContactPerson> CPA = ContactPersonFragment.getContactPersonArrayList();
                                ContactPersonFragment.setContactPersonArrayList(CPA);*/
                            }

                            else {
                                removeBlocked(blockedID);
                                ContactFilter.getContactPersonArrayAdapter().notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            return true;
        }
        Log.v("State change","Fail");
        return false;
    }
    public boolean checkIfContactGroupState1(String groupID){
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
    public Cursor readAppGroup(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_APPGROUPS,null);
        return cursor;
    }
    public Cursor readAppDetail(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_APP_DETAILS,null);
        return cursor;
    }

    //TODO: Add configuration lines here
}
