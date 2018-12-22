package com.jess.contactfiltergroup;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * Created by USER on 12/17/2018.
 */

public class ApplicationGroupFragment extends Fragment {
    private static final String TAG = "AppGroupFragment";

    public ListView applicationGroupListView;
    private Context mContext;
    private PackageManager packageManager;
    DatabaseHelper thesisDB;

    private ArrayList<ApplicationObj> arrayListAO;

    public static ArrayList<ApplicationGroup> applicationGroupArrayList;
    private ArrayAdapter<ApplicationGroup> applicationGroupListAdapter;

    //CONSTRUCTOR
    public ApplicationGroupFragment(){
        this.mContext = ApplicationLock.getAppContext();
        if(mContext==null)
            Log.v("Fragment Context","NULL");
        this.packageManager = ApplicationLock.getmPackageManager();
        this.applicationGroupArrayList = retrieveApplicationGroups(ApplicationObjFragment.getApplicationObjArrayList());
    }
    public static void setApplicationGroupArrayList(ArrayList<ApplicationGroup> applicationGroupArrayList){
        ApplicationGroupFragment.applicationGroupArrayList = applicationGroupArrayList;
    }
    public static ArrayList<ApplicationGroup> getApplicationGroupArrayList(){
        return applicationGroupArrayList;
    }
    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appgroups,container,false);
        applicationGroupListView =  view.findViewById(R.id.applicationGroupListView);
        if(applicationGroupListView == null)
            Log.v(TAG,"applicationGroupListView is null");
        else
            new LoadApplicationGroups().execute();
        return view;
    }
    public void setArrayListAO(ArrayList<ApplicationObj> arrayListAO){
        this.arrayListAO = arrayListAO;
    }
    private ArrayList<ApplicationGroup> retrieveApplicationGroups(ArrayList<ApplicationObj> applicationObjArrayList){
        applicationGroupArrayList = new ArrayList<>();
        Cursor groupCursor = ApplicationLock.getThesisDB().readAppGroup();
        if(groupCursor == null) {
            Log.v(TAG, "Retrieve Applications Null");
            return applicationGroupArrayList;
        }
        Cursor appDetailCursor = ApplicationLock.getThesisDB().readAppDetail();
        if(appDetailCursor == null) {
            Log.v(TAG, "Retrieve Applications Null2");
            return applicationGroupArrayList;
        }
        try{
            for(int i = 0; groupCursor.moveToNext(); i++){
                Log.v(TAG, "Retrieve Applications for 1");

                ArrayList<ApplicationObj> groupApplicationObj = new ArrayList<>();
                String groupID = groupCursor.getString(groupCursor.getColumnIndex("appGroupID"));
                //Test case
                String groupName = groupCursor.getString(groupCursor.getColumnIndex("appGroupName"));
                Log.v("TESTGROUPNAME",groupName);
                //--end test
                String groupState = groupCursor.getString(groupCursor.getColumnIndex("appGroupState"));

                for(int k = 0; appDetailCursor.moveToNext(); k++){
                    Log.v(TAG, "Retrieve Applications for 2");

                    String aDetGroupID = appDetailCursor.getString(appDetailCursor.getColumnIndex("appGroupID"));
                    Log.v(TAG,"Retrieve Applications if ["+groupID+"] = ["+aDetGroupID+"]");
                    String aDetAppID = appDetailCursor.getString(appDetailCursor.getColumnIndex("appName"));

                    if(groupID.equals(aDetGroupID)){
                        Log.v(TAG,"Retrieve Applications if 1");
                        for(int j = 0; j < applicationObjArrayList.size(); j++){
                            Log.v(TAG, "Retrieve Applications for 3");
                            String packageName = applicationObjArrayList.get(j).getAppPackageName();
                            if(aDetAppID.equals(packageName)){
                                groupApplicationObj.add(applicationObjArrayList.get(j));
                            }
                        }
                    }
                }
                ApplicationGroup applicationGroup = new ApplicationGroup(groupName,groupApplicationObj,groupState);
                applicationGroup.setId(groupID);
                applicationGroupArrayList.add(applicationGroup);
                Log.v("Retrieved group from DB",applicationGroup.getName());
                Log.v("State check",applicationGroup.getState());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return applicationGroupArrayList;

    }
    private class LoadApplicationGroups extends AsyncTask<Void, Void, Void> {
        ArrayList<ApplicationGroup> arrayListAG;


        @Override
        protected Void doInBackground(Void... voids) {

            arrayListAG = retrieveApplicationGroups(ApplicationObjFragment.getApplicationObjArrayList());
            applicationGroupListAdapter = new ApplicationGroupAdapter(getActivity(), R.layout.adapter_view_appgroups, arrayListAG);


            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            applicationGroupListView.setAdapter(applicationGroupListAdapter);
        }
        protected void onPreExecute(){

        }
        protected void doOver(){
            new LoadApplicationGroups().execute();
        }
    }
}
