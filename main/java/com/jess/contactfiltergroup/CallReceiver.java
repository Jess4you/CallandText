package com.jess.contactfiltergroup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by USER on 12/20/2018.
 */
//Credits to https://dev.to/hitman666/how-to-make-a-native-android-app-that-can-block-phone-calls--4e15
public class CallReceiver extends BroadcastReceiver {
    public static final String TAG = "CallReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase db = context.openOrCreateDatabase("thesis.db", 0, null);
        DatabaseHelper thesisDB = new DatabaseHelper(context);
        ITelephony telephonyService;
        try{
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(number.substring(0,3).equals("+63")){
                number = "0"+number.substring(3,13);
                Log.v("substring","success");
            }

            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                Toast.makeText(context,"Ring "+number, Toast.LENGTH_SHORT).show();
                try{
                    //https://blog.previewtechs.com/blocking-call-and-sms-in-android-programmatically
                    String serviceManagerName = "android.os.ServiceManager";
                    String serviceManagerNativeName = "android.os.ServiceManagerNative";
                    String telephonyName = "com.android.internal.telephony.ITelephony";
                    Class<?> telephonyClass;
                    Class<?> telephonyStubClass;
                    Class<?> serviceManagerClass;
                    Class<?> serviceManagerNativeClass;
                    Method telephonyEndCall;
                    Object telephonyObject;
                    Object serviceManagerObject;
                    telephonyClass = Class.forName(telephonyName);
                    telephonyStubClass = telephonyClass.getClasses()[0];
                    serviceManagerClass = Class.forName(serviceManagerName);
                    serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
                    Method getService = // getDefaults[29];
                            serviceManagerClass.getMethod("getService", String.class);
                    Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
                    Binder tmpBinder = new Binder();
                    tmpBinder.attachInterface(null, "fake");
                    serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
                    IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
                    Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                    telephonyObject = serviceMethod.invoke(null, retbinder);

                    //Database Retrieval
                    if(db.rawQuery("SELECT * From contactNum where number = "+number,null)!= null) {
                        Log.v(TAG, "numQuery: Success");
                        Cursor contactNumCursor = db.rawQuery("SELECT * From contactNum where number = '" + number + "'", null);
                        if (contactNumCursor == null)
                            Log.v(TAG, "contactNumCursor is NULL!");
                        else
                            Log.v(TAG, "contactNumCursor size: " + contactNumCursor.getCount());
                        while (contactNumCursor.moveToNext()) {
                            String dbNumber = contactNumCursor.getString(contactNumCursor.getColumnIndex("number"));
                            Log.v(TAG, "Database number to match = " + dbNumber);
                            if (dbNumber.equalsIgnoreCase(number)) {
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
                                        case "2":
                                            //Blocking Mechanism
                                            Toast.makeText(context,"Blocked call from: "+number,Toast.LENGTH_SHORT).show();
                                            telephonyEndCall = telephonyClass.getMethod("endCall");
                                            telephonyEndCall.invoke(telephonyObject);
                                            break;
                                        case "3":
                                            //Blocking Mechanism
                                            Toast.makeText(context,"Blocked call from: "+number,Toast.LENGTH_SHORT).show();
                                            telephonyEndCall = telephonyClass.getMethod("endCall");
                                            telephonyEndCall.invoke(telephonyObject);
                                            break;
                                        default:
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                        contactNumCursor.close();
                    }
                    /*if(contactNumCursor.moveToFirst()){
                        do{
                            String dbNumber = contactNumCursor.getString(contactNumCursor.getColumnIndex("number"));
                            if(dbNumber.equalsIgnoreCase(number) ) {
                            }
                            Log.v("To Match:",contactNumCursor.getString(contactNumCursor.getColumnIndex("number")));
                        }while(contactNumCursor.moveToNext());
                    }*/

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(context,"Answered "+number, Toast.LENGTH_SHORT).show();
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context,"Idle "+number, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.v(TAG,"onReceive: Successful");

    }
}
