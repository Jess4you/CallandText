package com.jess.contactfiltergroup;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactGroupFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ContactGroupFragment";
    private static final String CONTACTFILTER_KEY = "ContactFilter_key";

    private ListView contactGroupListView;

    private ContactFilter mContactFilter;

    private Context mContext;

    private DatabaseHelper thesisDB;

    public ArrayList<ContactPerson> arrayListCP;
    public ArrayList<ContactGroup> contactGroupArrayList;
    public ArrayAdapter<ContactGroup> contactGroupListAdapter;


    //CONSTRUCTOR
    public ContactGroupFragment(){
        this.mContext = ContactFilter.getAppContext();
        if(mContext==null)
            Log.v("Fragment Context","NULL");
        this.thesisDB = new DatabaseHelper(ContactFilter.getAppContext());
        this.contactGroupArrayList = retrieveContactGroups(ContactFilter.getContactPersonArrayList());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactgroups,container,false);
        contactGroupListView = (ListView)view.findViewById(R.id.contactGroupListView);
        new LoadContacts().execute();
        return view;
    }

    public void setArrayListCP(ArrayList<ContactPerson> contactPersonArrayList){
        this.arrayListCP = contactPersonArrayList;
    }
    public void setContactGroupArrayList(ArrayList<ContactGroup> contactGroupArrayList){
        this.contactGroupArrayList = contactGroupArrayList;
    }
    public ArrayList<ContactGroup> getContactGroupArrayList(){
        return contactGroupArrayList;
    }
    public ArrayAdapter<ContactGroup> getContactGroupListAdapter(){
        return contactGroupListAdapter;
    }

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
                ContactGroup contactGroup = new ContactGroup(groupName,groupContactPerson,groupState);
                contactGroup.setId(groupID);
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
            Log.v("Load Groups","Loadding");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            contactGroupListView.setAdapter(contactGroupListAdapter);
            //Adjust listview layout to fit on the same activity
            ContactFilter.setContactGroupArrayAdapter(contactGroupListAdapter);
        }
    }
}
