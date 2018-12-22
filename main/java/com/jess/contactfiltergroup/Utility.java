package com.jess.contactfiltergroup;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by USER on 12/16/2018.
 */


    //for displaying 2 listviews by managing their layout
    // refer to https://stackoverflow.com/questions/17693578/android-how-to-display-2-listviews-in-
    // one-activity-one-after-the-other
    //Also for adjusting height based on children

public class Utility {
    private static final String TAG = "Util";
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            //pre-condition
            return;
        }
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        Log.v(TAG,"Setting up ListView height based on "+listAdapter.getCount()+" children");
        for(int i = 0; i < listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null,listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight()*(listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
