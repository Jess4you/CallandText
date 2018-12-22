package com.jess.contactfiltergroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import static com.jess.contactfiltergroup.MainMenu.CHANNEL_1_ID;

/**
 * Created by USER on 12/19/2018.
 */

public class SMSReceiver extends BroadcastReceiver {
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);

        String messageAddress = messages.getOriginatingAddress();
        //Start Application's MainActivity activity
        SQLiteDatabase db = context.openOrCreateDatabase("thesis.db", 0, null);
        Cursor contactCursor = db.rawQuery("SELECT * From contact", null);
        Log.v("substringnew",messageAddress.substring(0,3));
        if(messageAddress.substring(0,3).equals("+63")){
            messageAddress = "0"+messageAddress.substring(3,13);
            Log.v("substring","success");
        }

        boolean blockMessage = false;
        if(contactCursor.moveToFirst()) {
            do {
                if(contactCursor.getString(contactCursor.getColumnIndex("name"))
                        .equalsIgnoreCase(messageAddress)){

                    Log.v("Match","Match Success");
                    Toast.makeText(context,"BLOCKED",Toast.LENGTH_SHORT).show();
                    abortBroadcast();
                    blockMessage = true;
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

                    Toast.makeText(context,"Blocked message from: "+messageAddress,Toast.LENGTH_SHORT).show();
                    abortBroadcast();
                    blockMessage = true;
                    Log.v("Abortbroadcast", "success");
                }
                Log.v("Received:",messageAddress);
                Log.v("To Match:",contactNumCursor.getString(contactNumCursor.getColumnIndex("number")));
            }while(contactNumCursor.moveToNext());
        }
        if(!blockMessage){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_1_ID);
            builder.setSmallIcon(R.drawable.ic_sms_notif);
            builder.setContentTitle("Message from: "+messages.getOriginatingAddress());
            builder.setContentText(messages.getMessageBody());
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());
        }
    }
}
