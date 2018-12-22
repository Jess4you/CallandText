package com.jess.contactfiltergroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 12/17/2018.
 */

public class ApplicationObjFragment extends Fragment {
    private static final String TAG = "ContactPersonFragment";

    private ListView applicationObjListView;
    private Context mContext;
    private PackageManager packageManager;

    private DatabaseHelper thesisDB;

    public static ArrayList<ApplicationObj> applicationObjArrayList;
    public static ArrayAdapter<ApplicationObj> applicationObjArrayAdapter;
    private String searchText="";

    public ApplicationObjFragment(){
        this.mContext = ApplicationLock.getAppContext();
        if(mContext==null)
            Log.v("Fragment Context","NULL");
        this.packageManager = ApplicationLock.getmPackageManager();
        this.applicationObjArrayList = retrieveApplications(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        this.thesisDB = new DatabaseHelper(getContext());
    }

    public static void setApplicationObjArrayAdapter(ArrayAdapter<ApplicationObj> applicationObjArrayAdapter){
        ApplicationObjFragment.applicationObjArrayAdapter = applicationObjArrayAdapter;
    }
    public static ArrayAdapter<ApplicationObj> getApplicationObjArrayAdapter(){
        return applicationObjArrayAdapter;
    }
    public static void setApplicationObjArrayList(ArrayList<ApplicationObj> applicationObjArrayList){
        ApplicationObjFragment.applicationObjArrayList = applicationObjArrayList;
    }
    public static ArrayList<ApplicationObj> getApplicationObjArrayList(){
        return applicationObjArrayList;
    }
    //TODO: Fix database to refresh toggled switches


    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications,container,false);
        applicationObjListView = view.findViewById(R.id.applicationListView);
        Button btnAddGroup = (Button)view.findViewById(R.id.buttonAddGroup);
        new LoadApplications().execute();
        //Adding of new contact group
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText groupName = new EditText(getActivity());
                final AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
                groupName.setMaxWidth(200);
                saveDialog.setTitle("Save settings?");
                saveDialog.setMessage("Type a name for the filtered group");
                saveDialog.setView(groupName);
                saveDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!(groupName.getText().toString().equals(null))) {
                            ArrayList<ApplicationObj> newGroup = new ArrayList<>();
                            for (int i = 0; i < applicationObjArrayList.size(); i++) {
                                String packageName = applicationObjArrayList.get(i).getAppPackageName();
                                if (ApplicationLock.getThesisDB().isLocked(packageName))
                                    newGroup.add(applicationObjArrayList.get(i));
                            }
                            String grpName = groupName.getText().toString();
                            ApplicationGroup applicationGroup = new ApplicationGroup(grpName, newGroup,"1");
                            int rowID = ApplicationLock.getThesisDB().insertAppLockGroup(applicationGroup.getName(),applicationGroup.getApplicationObjArrayList());

                            //Updating grouplist
                            applicationGroup.setId(String.valueOf(rowID));
                            ArrayList<ApplicationGroup> AGA = ApplicationGroupFragment.getApplicationGroupArrayList();
                            AGA.add(applicationGroup);
                            ApplicationGroupFragment.setApplicationGroupArrayList(AGA);

                            Toast.makeText(getActivity(),"Group "+grpName+" added!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(),"Please input a name!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                //unknown log error sendUserActionEvent() returned on dialog implementation when saving
            }
        });
        return view;
    }
    private ArrayList<ApplicationObj> retrieveApplications(List<ApplicationInfo> appInfo){

        List<ApplicationInfo> applicationInfoList = appInfo;
        ArrayList<ApplicationObj> applicationDataArrayList = new ArrayList<>();
        for(int i = 0;i < applicationInfoList.size(); i++){
            String packageName = applicationInfoList.get(i).packageName;
            String name = packageManager.getApplicationLabel(applicationInfoList.get(i)).toString();
            try{

                if (packageManager.getLaunchIntentForPackage(packageName) != null && name.contains(searchText)) {
                    Drawable icon = applicationInfoList.get(i).loadIcon(packageManager);
                    ApplicationObj application = new ApplicationObj(name, packageName, icon);
                    applicationDataArrayList.add(application);
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }
        return applicationDataArrayList;
    }
    public class LoadApplications extends AsyncTask<Void,ApplicationObj,Void> {
        ArrayList<ApplicationObj> arrayList;
        ArrayAdapter applicationListAdapter;
        int count;
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            arrayList = retrieveApplications(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            applicationObjArrayAdapter = new ApplicationObjListAdapter(getActivity(),R.layout.adapter_view_applications,arrayList);
            applicationObjArrayAdapter.notifyDataSetChanged();
            final SearchView s = (SearchView)getActivity().findViewById(R.id.searchView);
            s.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchText = query;
                    rewindThisShit();

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    rewindThisShit();

//                    checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
//                    new appAdapter(applockInterface.this, R.layout.list_items, applist, search);

                    return false;
                }


            });
            return null;
        }

        @Override
        protected void onProgressUpdate(ApplicationObj... values) {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Display loaded applications
            applicationObjListView.setAdapter(applicationObjArrayAdapter);

            //Adjust height between two or more listViews

            Utility.setListViewHeightBasedOnChildren(applicationObjListView);
        }
        public void rewindThisShit(){
            new LoadApplications().execute();
        }
    }
}
