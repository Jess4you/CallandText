package com.jess.contactfiltergroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactPersonListAdapter extends ArrayAdapter<ContactPerson> {
    private static final String TAG = "CPListAdapter";
    private Context mContext;
    int mResource;
    DatabaseHelper thesisDB;

    public ContactPersonListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ContactPerson> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        thesisDB = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //getting the contact informationposition
        final String id = getItem(position).getContactID();
        final String name = getItem(position).getContactName();
        boolean blocked = getItem(position).isContactBlocked();
        final String[] nums = getItem(position).getContactNums();

        //transfer the person object with the information
        final ContactPerson contactPerson = new ContactPerson(id, name, nums);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
        final Switch swBlock = (Switch) convertView.findViewById(R.id.switchFilter);
        final CheckBox cbText = (CheckBox) convertView.findViewById(R.id.textFilter);
        final CheckBox cbCall = (CheckBox) convertView.findViewById(R.id.callFilter);
        tvName.setText(name);
        blocked = thesisDB.checkIfBlockedByID(id);
        String state = thesisDB.checkContactState(id);
        switch(state){
            case "1":
                cbText.setChecked(true);
            break;
            case "2":
                cbCall.setChecked(true);
            break;
            case "3":
                cbText.setChecked(true);
                cbCall.setChecked(true);
                break;
            default:
        }
        cbText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbText.isChecked() && cbCall.isChecked()){
                    //change state to 3 (block call and texts)
                    thesisDB.changeContactState(id,"3");
                    Log.v(TAG,"Contact State: Blocking Calls and Texts");
                }else if(!cbText.isChecked() && cbCall.isChecked()){
                    //change state to 2 (block calls)
                    thesisDB.changeContactState(id,"2");
                    Log.v(TAG,"Contact State: Blocking Calls");
                }else if(cbText.isChecked() && !cbCall.isChecked()){
                    //insert with state 1 (block texts)
                    thesisDB.insertIntoBlocked(id,name,nums,"1");
                    Log.v(TAG,"Contact State: Blocking Texts");
                }else{
                    //delete from db
                    thesisDB.removeBlocked(id);
                    Log.v(TAG,"Contact State: No Block");
                }
            }
        });
        cbCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbText.isChecked() && cbCall.isChecked()){
                    //change state to 3 (block call and texts)
                    thesisDB.changeContactState(id,"3");
                    Log.v(TAG,"Contact State: Blocking Calls and Texts");
                }else if(!cbText.isChecked() && cbCall.isChecked()){
                    //change state to 2 (block calls)
                    thesisDB.insertIntoBlocked(id,name,nums,"2");
                    Log.v(TAG,"Contact State: Blocking Calls");
                }else if(cbText.isChecked() && !cbCall.isChecked()){
                    //insert with state 1 (block texts)
                    thesisDB.changeContactState(id,"1");
                    Log.v(TAG,"Contact State: Blocking Texts");
                }else{
                    //delete from db
                    thesisDB.removeBlocked(id);
                    Log.v(TAG,"Contact State: No Block");
                }
            }
        });






        if(blocked){
            swBlock.setChecked(true);
        }else{
            swBlock.setChecked(false);
        }
        swBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if(swBlock.isChecked()) {
                    contactPerson.setContactBlocked(true);
                    //insert values of contactperson into database
                    if(thesisDB.insertIntoBlocked(id,name,nums,"1")){
                        Log.v("Insert:","Success");
                    }
                }
                else {
                    //delete values of contactperson in database
                    contactPerson.setContactBlocked(false);
                    if(thesisDB.removeBlocked(id)){
                        Log.v("Delete:","Success");
                    }
                }
                Log.v("Contact state=",""+contactPerson.isContactBlocked());
            }
        });
        return convertView;
    }
}
