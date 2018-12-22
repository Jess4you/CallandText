package com.jess.contactfiltergroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactPersonListAdapter extends ArrayAdapter<ContactPerson> {
    private static final String TAG = "ContactPersonListAdapter";
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
        tvName.setText(name);
        blocked = thesisDB.checkIfBlockedByID(id);
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
                    if(thesisDB.insertInto(id,name,nums)){
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
