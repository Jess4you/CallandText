package com.jess.contactfiltergroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
    private static final String TAG = "SMSReceiver";
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    boolean blockMessage = false;
    SmsMessage messages;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        HandlerThread handlerThread =  new HandlerThread("database_helper");
        handlerThread.start();
        Handler handler =  new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle pudsBundle = intent.getExtras();
                Object[] pdus = (Object[]) pudsBundle.get("pdus");
                messages = SmsMessage.createFromPdu((byte[]) pdus[0]);

                String messageAddress = messages.getOriginatingAddress();

                //Start Application's MainActivity activity
                SQLiteDatabase db = context.openOrCreateDatabase("thesis.db", 0, null);
                DatabaseHelper thesisDB = new DatabaseHelper(context);
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
                contactCursor.close();
                if(db.rawQuery("SELECT * From contactNum where number = "+messageAddress,null)!= null) {
                    Log.v(TAG,"numQuery: Success");
                    Cursor contactNumCursor = db.rawQuery("SELECT * From contactNum where number = '"+messageAddress+"'", null);
                    if(contactNumCursor == null)
                        Log.v(TAG,"contactNumCursor is NULL!");
                    else
                        Log.v(TAG,"contactNumCursor size: "+contactNumCursor.getCount());
                    while (contactNumCursor.moveToNext()) {
                            String dbNumber = contactNumCursor.getString(contactNumCursor.getColumnIndex("number"));
                            Log.v(TAG,"Database number to match = "+dbNumber);
                            if (dbNumber.equalsIgnoreCase(messageAddress)) {
                                Log.v(TAG, "Number Matched: TRUE");
                                String dbNumID = contactNumCursor.getString(contactNumCursor.getColumnIndex("contactNum_id"));
                                Log.v(TAG, "Number ID =" + dbNumID);
                                Cursor numDetailCursor = db.rawQuery("SELECT * FROM contactNumDetails WHERE contactNum_id = '" + dbNumID + "'", null);
                                if (numDetailCursor == null)
                                    Log.v(TAG, "numDetailCursor is NULL!");
                                else
                                    Log.v(TAG, "numDetailCursor size: " + numDetailCursor.getCount());
                                while (numDetailCursor.moveToNext()) {
                                    String contactID = numDetailCursor.getString(numDetailCursor.getColumnIndex("contact_id"));
                                    Log.v(TAG, "check number contact ID: " + contactID);
                                    String dbState = thesisDB.checkContactState(contactID);
                                    switch (dbState) {
                                        case "1":
                                            blockMessage = true;
                                            Toast.makeText(context, "Blocked message from: " + messageAddress, Toast.LENGTH_SHORT).show();
                                            break;
                                        case "2":
                                            blockMessage = false;
                                            break;
                                        case "3":
                                            blockMessage = true;
                                            Toast.makeText(context, "Blocked message from: " + messageAddress, Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            blockMessage = false;
                                    }
                                    break;
                                }
                                break;
                            }
                    }
                    contactNumCursor.close();
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
        });
    }
}
