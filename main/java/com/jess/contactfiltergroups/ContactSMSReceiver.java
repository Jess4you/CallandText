package com.jess.contactfiltergroups;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.*;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by USER on 11/24/2018.
 */

public class ContactSMSReceiver extends BroadcastReceiver {
    private static final String TAG = "Message received";
    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper thesisdb = new DatabaseHelper(context);
        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages = SmsMessage.createFromPdu((byte[])pdus[0]);
        String messageAddress = messages.getOriginatingAddress();
        //Start Application's MainActivity activity
        SQLiteDatabase db = context.openOrCreateDatabase("thesis.db", 0, null);
        Cursor contactCursor = db.rawQuery("SELECT * From contact", null);
        Log.v("substringnew",messageAddress.substring(0,3));
        if(messageAddress.substring(0,3).equals("+63")){
            messageAddress = "0"+messageAddress.substring(3,13);
            Log.v("substring","success");
        }
        if(contactCursor.moveToFirst()) {
            do {
                if(contactCursor.getString(contactCursor.getColumnIndex("name"))
                        .equalsIgnoreCase(messageAddress)){

                    Intent smsIntent = new Intent(context, ContactFilterGroups.class);
                    smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(smsIntent);
                    Log.v("Match","Match Success");
                    Toast.makeText(context,"BLOCKED",Toast.LENGTH_SHORT).show();
                }
                Log.v("Received:",messageAddress);
                Log.v(
                        "To Match:",
                        contactCursor.getString(contactCursor.getColumnIndex("name"))
                );
            } while(contactCursor.moveToNext());
        }
        Cursor contactNumCursor = db.rawQuery("SELECT * From contactNum",null);
        if(contactNumCursor.moveToFirst()){
            do{
                if(contactNumCursor.getString(contactNumCursor.getColumnIndex("number")).equalsIgnoreCase(messageAddress)) {
                    Log.v("test", "successs");

                    Toast.makeText(context,"BLOCKED",Toast.LENGTH_SHORT).show();
                }
                Log.v("Received:",messageAddress);
                Log.v("To Match:",contactNumCursor.getString(contactNumCursor.getColumnIndex("number")));
            }while(contactNumCursor.moveToNext());
        }
    }
}
