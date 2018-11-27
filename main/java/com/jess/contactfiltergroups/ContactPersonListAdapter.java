package com.jess.contactfiltergroups;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by USER on 11/21/2018.
 */

public class ContactPersonListAdapter extends ArrayAdapter<ContactPerson> {
    private static final String TAG = "ContactPersonListAdapter";
    private Context mContext;
    int mResource;
    ContactFilterGroups main;
    public ContactPersonListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ContactPerson> objects,ContactFilterGroups cfg) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.main = cfg;
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
        final ContactPerson contactPerson = new ContactPerson(id,name,nums);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);
        TextView tvName = (TextView)convertView.findViewById(R.id.textViewName);
        final Switch swBlock = (Switch)convertView.findViewById(R.id.switchFilter);

        blocked = main.getThesisdb().checkIfBlockedByID(id);
        tvName.setText(name);
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
                    if(main.getThesisdb().insertInto(id,name,nums)){
                        Log.v("Insert:","Success");
                    }
                }
                else {
                    //delete values of contactperson in database
                    contactPerson.setContactBlocked(false);
                    if(main.getThesisdb().removeBlocked(id)){
                        Log.v("Delete:","Success");
                    }
                }
                Log.v("Contact state=",""+contactPerson.isContactBlocked());
            }
        });
        return convertView;
    }
}
