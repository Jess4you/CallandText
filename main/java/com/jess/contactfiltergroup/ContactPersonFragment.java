package com.jess.contactfiltergroup;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactPersonFragment extends Fragment {
    private static final String TAG = "ContactPersonFragment";

    private ListView contactPersonListView;
    private Context mContext;
    DatabaseHelper thesisDB;

    public static ArrayList<ContactPerson> contactPersonArrayList;
    public ArrayAdapter<ContactPerson> contactPersonListAdapter;

    //CONSTRUCTOR
    public ContactPersonFragment(){
        this.mContext = ContactFilter.getAppContext();
        if(mContext==null)
            Log.v("Fragment Context","NULL");
        this.contactPersonArrayList = retrieveContactPersons();
    }

    //INTER-CLASS ARRAY COMMUNICATION
    public static void setContactPersonArrayList(ArrayList<ContactPerson> contactPersonArrayList){
        ContactPersonFragment.contactPersonArrayList = contactPersonArrayList;
    }
    public static ArrayList<ContactPerson> getContactPersonArrayList(){
        return contactPersonArrayList;
    }

    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);
        contactPersonListView = view.findViewById(R.id.contactPersonListView);
        Button btnAddGroup = (Button)view.findViewById(R.id.buttonAddGroup);
        thesisDB = new DatabaseHelper(mContext);

        //Start background thread
        new LoadContacts().execute();

        //Adding of new contact group
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText groupName = new EditText(getActivity());
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
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
                                if (ContactFilter.getThesisDB().checkIfBlockedByID(id))
                                    newGroup.add(contactPersonArrayList.get(i));
                            }
                            String grpName = groupName.getText().toString();
                            ContactGroup contactGroup = new ContactGroup(grpName, newGroup,"1");
                            int rowID = ContactFilter.getThesisDB().insertNewContactGroup(contactGroup.getName(),contactGroup.getContactPersonArrayList());

                            //Updating grouplist
                            contactGroup.setId(String.valueOf(rowID));
                            ArrayList<ContactGroup> CGA = ContactGroupFragment.getContactGroupArrayList();
                            CGA.add(contactGroup);
                            ContactGroupFragment.setContactGroupArrayList(CGA);

                            Toast.makeText(getActivity(),"Group "+grpName+" added!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"Please input a name!",Toast.LENGTH_SHORT).show();
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
        //--end of group adding
        return view;
    }






    //BACKGROUND RETRIEVAL METHOD
    private ArrayList<ContactPerson> retrieveContactPersons(){
        ArrayList<ContactPerson> arrayListCP = new ArrayList<>();

        Cursor contactCursor = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        /*---start of retrieval--*/
        if (contactCursor.moveToFirst()) { // must check the result to prevent exception
            do {
                final String contactID = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                final String contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor phoneCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{contactID},null);

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
                //---End of retrieve numbers

                ContactPerson contactPerson = new ContactPerson(contactID,contactName,contactNums);
                arrayListCP.add(contactPerson);
            } while (contactCursor.moveToNext());
        } else {
            // empty
        }
        /*---end of retrieval---*/
        return arrayListCP;
    }
    public class LoadContacts extends AsyncTask<Void,Void,Void> {
        ArrayList<ContactPerson> arrayListCP;

        @Override
        protected Void doInBackground(Void... voids) {

            arrayListCP = retrieveContactPersons();
            contactPersonListAdapter = new ContactPersonListAdapter(getActivity(), R.layout.adapter_view_contacts, arrayListCP);
            contactPersonListAdapter.notifyDataSetChanged();
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {

            contactPersonListView.setAdapter(contactPersonListAdapter);
            //Adjust listview layout to fit on the same activity
            ContactFilter.setContactPersonArrayAdapter(contactPersonListAdapter);
            Utility.setListViewHeightBasedOnChildren(contactPersonListView);

        }
    }
    //END OF BACKGROUND RETRIEVAL
}
