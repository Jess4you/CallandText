package com.jess.contactfiltergroup;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactGroupFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ContactGroupFragment";

    private final Context mContext;

    private final DatabaseHelper thesisDB;

    private ArrayList<ContactPerson> arrayListCP;
    private ContactGroupListAdapter contactGroupListAdapter;

    public static ArrayList<ContactGroup> contactGroupArrayList;

    public ListView contactGroupListView;

    //CONSTRUCTOR
    public ContactGroupFragment(){
        this.mContext = ContactFilter.getAppContext();
        if(mContext==null)
            Log.v("Fragment Context","NULL");
        this.thesisDB = new DatabaseHelper(ContactFilter.getAppContext());
        this.contactGroupArrayList = retrieveContactGroups(ContactPersonFragment.getContactPersonArrayList());
    }


    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactgroups,container,false);
        contactGroupListView = view.findViewById(R.id.contactGroupListView);
        new LoadContacts().execute();
        contactGroupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Test","ItemClick");
                final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + position);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int groupID = contactGroupArrayList.get(positionToRemove).getId();
                        if(thesisDB.deleteAppGroup(String.valueOf(groupID))){
                            Log.v(TAG,"Delete Successful");
                        }
                        contactGroupArrayList.remove(positionToRemove);
                        contactGroupListAdapter.notifyDataSetChanged();
                    }});
                adb.show();
                return false;
            }
        });
        return view;
    }

    //INTER-CLASS ARRAY COMMUNICATION
    public void setArrayListCP(ArrayList<ContactPerson> contactPersonArrayList){
        this.arrayListCP = contactPersonArrayList;
    }
    public static void setContactGroupArrayList(ArrayList<ContactGroup> contactGroupArrayList){
        ContactGroupFragment.contactGroupArrayList = contactGroupArrayList;
        for(int i = 0;i<ContactGroupFragment.contactGroupArrayList.size();i++){
            Log.v(TAG,"Check arraylist content: "+contactGroupArrayList.get(i).getName());
        }
    }
    public static ArrayList<ContactGroup> getContactGroupArrayList(){
        return contactGroupArrayList;
    }

    //BACKGROUND RETRIEVAL METHOD
    private ArrayList<ContactGroup> retrieveContactGroups(ArrayList<ContactPerson> contactPersonArrayList){
        contactGroupArrayList = new ArrayList<>();
        Cursor groupCursor = thesisDB.readContactGroup();
        if(groupCursor == null)
            return contactGroupArrayList;
        Cursor contactDetailCursor = thesisDB.readContactDetail();
        if(contactDetailCursor == null)
            return contactGroupArrayList;
        try{
            for(int i = 0; groupCursor.moveToNext(); i++){

                ArrayList<ContactPerson> groupContactPerson = new ArrayList<>();
                String groupID = groupCursor.getString(groupCursor.getColumnIndex("contactGroup_id"));
                //Test case
                String groupName = groupCursor.getString(groupCursor.getColumnIndex("name"));
                Log.v("TESTGROUPNAME",groupName);
                        //--end test
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
                ContactGroup contactGroup = new ContactGroup(Integer.parseInt(groupID),groupName,groupContactPerson,groupState);
                contactGroupArrayList.add(contactGroup);
                Log.v("Retrieved group from DB",contactGroup.getName());
                Log.v("State check",contactGroup.getState());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return contactGroupArrayList;
    }
    public class LoadContacts extends AsyncTask<Void,Void,Void> {
        ArrayList<ContactGroup> arrayListCG;

        @Override
        protected Void doInBackground(Void... voids) {

            arrayListCG = retrieveContactGroups(arrayListCP);
            contactGroupListAdapter = new ContactGroupListAdapter(getActivity(), R.layout.adapter_view_contactgroups, arrayListCG);
            contactGroupListAdapter.notifyDataSetChanged();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            contactGroupListView.setAdapter(contactGroupListAdapter);
            ContactFilter.setContactGroupArrayAdapter(contactGroupListAdapter);
            //Adjust listview layout to fit on the screen

        }
    }
    //--END OF BACKGROUND RETRIEVAL
}
