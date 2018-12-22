package com.jess.contactfiltergroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SMSInbox extends AppCompatActivity {

    DatabaseHelper thesisDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_inbox);
        thesisDB = new DatabaseHelper(this);

    }
}
