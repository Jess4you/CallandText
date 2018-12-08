package com.jess.contactfiltergroups;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactFilterGroups extends AppCompatActivity {
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    DatabaseHelper thesisdb;
    String searchText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_filter_groups);
        final ListView contactPersonListView = (ListView)findViewById(R.id.contactPersonListView);
        final ListView contactGroupListView = (ListView)findViewById(R.id.contactGroupListView);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        thesisdb = new DatabaseHelper(this);

        //[uncomment to destroy database] database destroyer do not uncomment and run!!
        //this.deleteDatabase("thesis.db");

        //Retrieve list of contacts via cursor method
        final ArrayList<ContactPerson> contactPersonArrayList = new ArrayList<>();
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.moveToFirst()) { // must check the result to prevent exception
            do {
                final String contactID = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                final String contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{contactID},null);

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
                }
                ContactPerson contactPerson = new ContactPerson(contactID,contactName,contactNums);
                contactPersonArrayList.add(contactPerson);
            } while (cur.moveToNext());
        } else {
            // empty
        }



        //[uncomment to display contact list] Responsible for the listing of the contact persons
        final ContactPersonListAdapter contactListAdapter = new ContactPersonListAdapter(this,R.layout.adapter_view_contacts,contactPersonArrayList,this);
        contactPersonListView.setAdapter(contactListAdapter);


        //Database retrieval of contactgroups
        final ArrayList<ContactGroup> contactGroupArrayList = new ArrayList<>();
        Cursor groupCursor = thesisdb.readContactGroup();
        Cursor contactDetailCursor = thesisdb.readContactDetail();
        for(int i = 0; groupCursor.moveToNext(); i++){
            ArrayList<ContactPerson> groupContactPerson = new ArrayList<>();
            String groupID = groupCursor.getString(groupCursor.getColumnIndex("contactGroup_id"));
            String groupState = groupCursor.getString(groupCursor.getColumnIndex("state"));
            for(int k = 0; contactDetailCursor.moveToNext(); k++){
                String cDetGroupID = contactDetailCursor.getString(contactDetailCursor.getColumnIndex("contactGroup_id"));
                String cDetContactID = contactDetailCursor.getString(contactDetailCursor.getColumnIndex("contact_id"));
                if(groupID.equals(cDetGroupID)){
                    for(int j = 0; j < contactPersonArrayList.size(); j++){
                        String contactID = contactPersonArrayList.get(j).getContactID();
                        if(cDetContactID.equals(contactID)){
                            groupContactPerson.add(contactPersonArrayList.get(j));
                        }
                    }
                }
            }
            ContactGroup contactGroup = new ContactGroup(groupCursor.getColumnName(groupCursor.getColumnIndex("name")),groupContactPerson,groupState);
            contactGroup.setId(groupID);
            contactGroupArrayList.add(contactGroup);
            Log.v("Retrieved group from DB",contactGroup.getName());
            Log.v("State check",contactGroup.getState());

        }
        //Attack contact group arraylist retrieved from database to listview
        final ContactGroupListAdapter contactGroupListAdapter = new ContactGroupListAdapter(this,R.layout.adapter_view_contactgroups,contactGroupArrayList,this);
        contactGroupListView.setAdapter(contactGroupListAdapter);

        //Adjust listview layout to fit on the same activity
        Utility.setListViewHeightBasedOnChildren(contactGroupListView);
        Utility.setListViewHeightBasedOnChildren(contactPersonListView);

        //Add new contactgroup
        Button btnAddGroup = (Button)findViewById(R.id.buttonAddGroup);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText groupName = new EditText(ContactFilterGroups.this);
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(ContactFilterGroups.this);
                groupName.setMaxWidth(200);
                saveDialog.setTitle("Save settings?");
                saveDialog.setMessage("Type a name for the filtered group");
                saveDialog.setView(groupName);
                saveDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!(groupName.getText().toString().equals(null))) {
                            ArrayList<ContactPerson> newGroup = new ArrayList<>();
                            for (int i = 0; i < contactPersonArrayList.size(); i++) {
                                String id = contactPersonArrayList.get(i).getContactID();
                                if (thesisdb.checkIfBlockedByID(id))
                                    newGroup.add(contactPersonArrayList.get(i));

                            }
                            ContactGroup contactGroup = new ContactGroup(groupName.getText().toString(), newGroup,"1");
                            contactGroupArrayList.add(contactGroup);
                            thesisdb.insertNewGroup(contactGroup.getName(),contactGroup.getContactPersonArrayList());

                            contactGroupListAdapter.notifyDataSetChanged();
                            Utility.setListViewHeightBasedOnChildren(contactGroupListView);
                            Utility.setListViewHeightBasedOnChildren(contactPersonListView);
                            Toast.makeText(getApplicationContext(),"Group "+groupName+" added!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Please input a name!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                //unknown log error sendUserActionEvent() returned on dialog implementation when saving
            }
        });
    }
















    //method for using the helper in other classes
    public DatabaseHelper getThesisdb () {
        return thesisdb;
    }
    //Permissions: sms receive request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(!Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(getApplicationContext().getPackageName())) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                        getApplicationContext().getPackageName());
                startActivity(intent);
            }
        }**/
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if(!Telephony.Sms.getDefaultSmsPackage(this).equals("com.jess.contactfiltergroups")){
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactFilterGroups.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false).setTitle("Alert!")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }*/
    }

    
}
