package com.jess.contactfiltergroups;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactFilterGroups extends AppCompatActivity {
    DatabaseHelper thesisdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_filter_groups);
        ListView contactPersonListView = (ListView)findViewById(R.id.contactPersonListView);
        thesisdb = new DatabaseHelper(this);

        //database destroyer do not uncomment and run!!
        //this.deleteDatabase("thesis.db");

        String msgData;
        String msgID="";
        ArrayList<ContactPerson> contactPersonArrayList = new ArrayList<>();
        int total=0;

        //Retrieve contacts via cursor method
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.moveToFirst()) { // must check the result to prevent exception
            do {
                final String contactID = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                final String contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor phonecursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{contactID},null);

                //Retrieve numbers of each contact
                String[] contactNums = new String[phonecursor.getCount()];
                for(int i = 0;phonecursor.moveToNext();i++){
                    contactNums[i] = phonecursor.getString(phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d("contact",contactName);
                    Log.d("number",contactNums[i]);
                }
                ContactPerson contactPerson = new ContactPerson(contactID,contactName,contactNums);
                contactPersonArrayList.add(contactPerson);
                total++;
            } while (cur.moveToNext());
        } else {
            // empty
        }
        //Add new contactgroup
        Button btnAddGroup = (Button)findViewById(R.id.buttonAddGroup);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Responsible for the listing of the contact persons
        ContactPersonListAdapter contactListAdapter = new ContactPersonListAdapter(this,R.layout.adapter_view_contacts,contactPersonArrayList,this);
        contactPersonListView.setAdapter(contactListAdapter);

    }
    //method for using the helper in other classes
    public DatabaseHelper getThesisdb(){
        return thesisdb;
    }
}
