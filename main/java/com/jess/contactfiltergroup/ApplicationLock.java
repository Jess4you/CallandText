package com.jess.contactfiltergroup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ApplicationLock extends AppCompatActivity {

    private static final String TAG = "ApplicationLock";

    public static Context appContext;

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    public static ArrayList<ApplicationObj> applicationObjArrayList;

    public static ArrayList<ContactGroup> contactGroupArrayList;

    public static DatabaseHelper thesisDB;


    public static ArrayAdapter<ContactPerson> contactPersonArrayAdapter;

    public static PackageManager mPackageManager;
    public static ApplicationLock mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_lock);

        //IMPORTANT -- set application context
        this.mPackageManager = this.getPackageManager();
        this.appContext = getApplicationContext();
        this.mContext = this;
        thesisDB = new DatabaseHelper(this);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //this.deleteDatabase("thesis.db");
        //Setting up the ViewPager with the SectionsAdapter
        mViewPager = (ViewPager)findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(1);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //--- End of ViewPager Set up
    }
    // Setting up the Fragment Tabs
    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        ApplicationObjFragment applicationObjFragment = new ApplicationObjFragment();
        adapter.addFragment(applicationObjFragment,"Applications");

        ApplicationGroupFragment applicationGroupFragment = new ApplicationGroupFragment();
        applicationGroupFragment.setArrayListAO(applicationObjArrayList);
        adapter.addFragment(applicationGroupFragment,"Application Groups");
        viewPager.setAdapter(adapter);
    }

    public static PackageManager getmPackageManager(){
        return mPackageManager;
    }
    public static Context getAppContext(){
        return mContext;
    }
    public static DatabaseHelper getThesisDB(){return thesisDB;}
}
