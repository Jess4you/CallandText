package com.jess.contactfiltergroup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USER on 12/17/2018.
 */

public class ApplicationObjListAdapter extends ArrayAdapter<ApplicationObj> {
    private static final String TAG = "ApplicationListAdapter";
    private Context mContext;
    int mResource;
    DatabaseHelper thesisDB;

    public ApplicationObjListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ApplicationObj> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        thesisDB = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //getting the contact informationposition
        final String name = getItem(position).getAppName();
        final String packageName = getItem(position).getAppPackageName();
        final Drawable icon = getItem(position).getAppIcon();
        //transfer the person object with the information
        final ApplicationObj applicationData = new ApplicationObj(name,packageName,icon);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);
        TextView tvName = (TextView)convertView.findViewById(R.id.textViewName);
        ImageView ivIcon = (ImageView)convertView.findViewById(R.id.imageViewApp);
        final Switch swBlock = (Switch)convertView.findViewById(R.id.switchFilter);

        tvName.setText(name);
        ivIcon.setImageDrawable(icon);


        boolean locked = thesisDB.isLocked(packageName);
        if(locked){
            swBlock.setChecked(true);
        }else{
            swBlock.setChecked(false);
        }
        swBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!thesisDB.isStored(packageName)) {
                        if (thesisDB.insertToLockTable(packageName, 0)) {
                            Log.v("Change state", name+": On");
                        }
                    }
                }else{
                    if(thesisDB.changeAppState(packageName)){
                        Log.v("Change state",name+": Off");
                    }
                }
            }
        });
        return convertView;
    }
}
