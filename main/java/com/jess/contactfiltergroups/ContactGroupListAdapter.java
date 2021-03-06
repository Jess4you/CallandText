package com.jess.contactfiltergroups;

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
 * Created by USER on 11/26/2018.
 */

public class ContactGroupListAdapter extends ArrayAdapter<ContactGroup> {
    private static final String TAG = "ContactGroupListAdapter";
    String state = "0";
    Context mContext;
    int mResource;
    ContactFilterGroups main;
    public ContactGroupListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ContactGroup> objects, ContactFilterGroups cfg) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.main = cfg;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getting the contact informationposition
        final String id = getItem(position).getId();
        final String name = getItem(position).getName();
        final ArrayList<ContactPerson> contactPersonArrayList = getItem(position).getContactPersonArrayList();
        Log.v("Group:","Success");
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView tvID = (TextView)convertView.findViewById(R.id.textViewIDGroups);
        TextView tvName = (TextView)convertView.findViewById(R.id.textViewNameGroups);
        final Switch swBlock = (Switch)convertView.findViewById(R.id.switchFilterGroups);

        boolean active = main.getThesisdb().checkIfGroupState1(id);
        swBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);

                if(swBlock.isChecked()) {
                    state = "1";
                    if(main.getThesisdb().changeGroupState(id,state,main))
                        Log.v("On:", "Success");
                }
                else {
                    state = "0";
                    if(main.getThesisdb().changeGroupState(id,state,main))
                        Log.v("Off","Success");
                }
            }
        });
        if(active){
            swBlock.setChecked(true);
        }else{
            swBlock.setChecked(false);
        }
        //transfer the person object with the information
        final ContactGroup contactGroup = new ContactGroup(name,contactPersonArrayList,state);
        tvName.setText(name);
        return convertView;
    }
}
