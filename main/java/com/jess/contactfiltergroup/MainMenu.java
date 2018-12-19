package com.jess.contactfiltergroup;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "MainMenu";
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    Button btnAppLock;
    Button btnFilter;
    DatabaseHelper thesisDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //PERMISSIONS
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        ActivityCompat.requestPermissions(this
                ,new String[]{Manifest.permission.READ_CONTACTS}
                ,MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        btnAppLock = (Button)findViewById(R.id.btnAppLock);
        btnFilter = (Button)findViewById(R.id.btnFilter);
        thesisDB = new DatabaseHelper(this);
        btnAppLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this,ApplicationLock.class));
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this,ContactFilter.class));
            }
        });
        createNotificationChannels();
    }
    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"smsChannel", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This channel is for sms receiving");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }

    }
    @Override
    protected void onResume(){
        super.onResume();
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if(!Telephony.Sms.getDefaultSmsPackage(this).equals("com.jess.contactfiltergroup")){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setTitle("Alert!")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }
    }
    //Permissions: sms receive request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES
            Log.i(TAG, "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            // YES
            Log.i(TAG, "MY_PERMISSIONS_REQUEST_READ_CONTACTS --> YES");
        }
    }
}
