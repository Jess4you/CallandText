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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactGroupListAdapter extends ArrayAdapter<ContactGroup> {
    private static final String TAG = "ContactGroupListAdapter";

    private List<ContactGroup> listGroupsSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<ContactGroup> listGroups;
    private DatabaseHelper thesisDB;
    String state = "0";
    Context mContext;
    int mResource;
    public ContactGroupListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ContactGroup> arrayListGroups) {
        super(context, resource, arrayListGroups);
        this.mContext = context;
        this.mResource = resource;
        thesisDB = new DatabaseHelper(context);

        //For multiple selection
        this.listGroups = arrayListGroups;
        listGroupsSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
    }

    public void handleLongPress(int position, View view){
        if(listSelectedRows.contains(view)){
            listSelectedRows.remove(view);
            listGroupsSelected.remove(listGroups.get(position));
            view.setBackgroundResource(R.color.colorWhite);
        }else{
            listGroupsSelected.add(listGroups.get(position));
            listSelectedRows.add(view);
            view.setBackgroundResource(R.color.colorDarkGray);
        }

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getting the contact informationposition
        final String id = String.valueOf(getItem(position).getId());
        final String name = getItem(position).getName();
        final ArrayList<ContactPerson> contactPersonArrayList = getItem(position).getContactPersonArrayList();
        Log.v("Group:",name);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView tvName = (TextView)convertView.findViewById(R.id.textViewName);
        tvName.setText(name);
        final CheckBox cbText = (CheckBox) convertView.findViewById(R.id.textFilter);
        final CheckBox cbCall = (CheckBox) convertView.findViewById(R.id.callFilter);
        String state = thesisDB.checkContactGroupState(id);
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
                    thesisDB.changeContactGroupState(id,"3",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: Blocking Calls and Texts");
                }else if(!cbText.isChecked() && cbCall.isChecked()){
                    //change state to 2 (block calls)
                    thesisDB.changeContactGroupState(id,"2",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: Blocking Calls");
                }else if(cbText.isChecked() && !cbCall.isChecked()){
                    //insert with state 1 (block texts)
                    thesisDB.changeContactGroupState(id,"1",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: Blocking Texts");
                }else{
                    //delete from db
                    thesisDB.changeContactGroupState(id,"0",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: No Block");
                }
            }
        });
        cbCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbText.isChecked() && cbCall.isChecked()){
                    //change state to 3 (block call and texts)
                    thesisDB.changeContactGroupState(id,"3",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: Blocking Calls and Texts");
                }else if(!cbText.isChecked() && cbCall.isChecked()){
                    //change state to 2 (block calls)
                    thesisDB.changeContactGroupState(id,"2",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: Blocking Calls");
                }else if(cbText.isChecked() && !cbCall.isChecked()){
                    //insert with state 1 (block texts)
                    thesisDB.changeContactGroupState(id,"1",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: Blocking Texts");
                }else{
                    //delete from db
                    thesisDB.changeContactGroupState(id,"0",mContext);
                    Log.v(TAG,"["+name+"] ContactGroup State: No Block");
                }
            }
        });

        //OLD switch
        /*final Switch swBlock = (Switch)convertView.findViewById(R.id.switchFilter);
        boolean active = thesisDB.checkIfContactGroupState1(id);
        swBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);

                if(swBlock.isChecked()) {
                    state = "1";
                    if(thesisDB.changeContactGroupState(id,state,mContext))
                        Log.v("On:", "Success");
                }
                else {
                    state = "0";
                    if(thesisDB.changeContactGroupState(id,state,mContext))
                        Log.v("Off","Success");
                }
                Log.v("Switch State", "Change state to "+state);
            }
        });
        if(active){
            swBlock.setChecked(true);
        }else{
            swBlock.setChecked(false);
        }
        //transfer the person object with the information
        final ContactGroup contactGroup = new ContactGroup(name,contactPersonArrayList,state);*/

        return convertView;
    }
}
