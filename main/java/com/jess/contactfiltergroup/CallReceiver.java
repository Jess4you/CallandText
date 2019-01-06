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
        Cursor contactNumCursor = db.rawQuery("SELECT * From contactNum",null);
        ITelephony telephonyService;
        try{
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
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
                    if(contactNumCursor.moveToFirst()){
                        do{
                            String dbNumber = contactNumCursor.getString(contactNumCursor.getColumnIndex("number"));
                            if(dbNumber.equalsIgnoreCase(number) ) {

                                Toast.makeText(context,"Blocked call from: "+number,Toast.LENGTH_SHORT).show();
                                telephonyEndCall = telephonyClass.getMethod("endCall");
                                telephonyEndCall.invoke(telephonyObject);
                            }
                            Log.v("To Match:",contactNumCursor.getString(contactNumCursor.getColumnIndex("number")));
                        }while(contactNumCursor.moveToNext());
                    }

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
