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
 * Created by USER on 12/17/2018.
 */

public class ApplicationGroupAdapter extends ArrayAdapter<ApplicationGroup> {
    private static final String TAG = "ApplicationGroupAdapter";

    private Context mContext;

    int mResource;
    private String state;
    DatabaseHelper thesisDB;

    public ApplicationGroupAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ApplicationGroup> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        thesisDB = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getting the contact informationposition
        final String id = getItem(position).getId();
        final String name = getItem(position).getName();
        final ArrayList<ApplicationObj> applicationObjArrayList = getItem(position).getApplicationObjArrayList();
        Log.v("Group:",name);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);

        TextView tvName = (TextView)convertView.findViewById(R.id.textViewName);
        final Switch swBlock = (Switch)convertView.findViewById(R.id.switchFilter);
        boolean active = thesisDB.checkIfAppGroupState1(id);
        swBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);

                if(swBlock.isChecked()) {
                    state = "1";
                    if(thesisDB.changeAppGroupState(id,state,mContext))
                        Log.v("On:", "Success");
                }
                else {
                    state = "0";
                    if(thesisDB.changeAppGroupState(id,state,mContext))
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
        tvName.setText(name);
        return convertView;
    }
}
