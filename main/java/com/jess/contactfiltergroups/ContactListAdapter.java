package com.jess.contactfiltergroups;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 11/20/2018.
 */

public class ContactListAdapter extends ArrayAdapter<ContactPerson> {
    private static final String TAG = "ContactListAdapter";
    private Context mContext;

    public ContactListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ContactPerson> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }
}
