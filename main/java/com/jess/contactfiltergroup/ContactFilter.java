package com.jess.contactfiltergroup;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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

import java.io.Serializable;
import java.util.ArrayList;

public class ContactFilter extends AppCompatActivity{

    private static final String TAG = "ContactFilter";

    public static Context appContext;

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    public static ArrayList<ContactPerson> contactPersonArrayList;

    public static ArrayList<ContactGroup> contactGroupArrayList;

    public static DatabaseHelper thesisDB;

    public static ArrayAdapter<ContactGroup> contactGroupArrayAdapter;

    public static ArrayAdapter<ContactPerson> contactPersonArrayAdapter;

    public static ContactFilter mContext;

    //IMPORTANT -- retrieve static appContext
    public static Context getAppContext(){
        return appContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_filter);

        //IMPORTANT -- set application context
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

    //Retrieve the helper
    public static DatabaseHelper getThesisDB(){
        return thesisDB;
    }

    // Setting up the Fragment Tabs
    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        ContactPersonFragment contactPersonFragment = new ContactPersonFragment();
        if(contactPersonFragment.getContactPersonArrayList() == null)
            Log.v("Array","NULL");
        adapter.addFragment(contactPersonFragment,"Contacts");
        setContactPersonArrayList(contactPersonFragment.getContactPersonArrayList());

        ContactGroupFragment contactGroupFragment = new ContactGroupFragment();
        contactGroupFragment.setArrayListCP(contactPersonArrayList);
        adapter.addFragment(contactGroupFragment,"Contact Groups");
        viewPager.setAdapter(adapter);
    }




    //Getters and Setters for ArrayLists
    public void setContactPersonArrayList(ArrayList<ContactPerson> contactPersonArrayList){
        this.contactPersonArrayList = contactPersonArrayList;
    }
    public void setContactGroupArrayList(ArrayList<ContactGroup> contactGroupArrayList){
        this.contactGroupArrayList = contactGroupArrayList;
    }
    public static ArrayList<ContactPerson> getContactPersonArrayList(){
        return contactPersonArrayList;
    }
    public static ArrayList<ContactGroup> getContactGroupArrayList(){
        return contactGroupArrayList;
    }
    //Getters and Setters for Adapter
    public static void setContactGroupArrayAdapter(ArrayAdapter<ContactGroup> contactGroupArrayAdapter){
        ContactFilter.contactGroupArrayAdapter = contactGroupArrayAdapter;
    }
    public static ArrayAdapter<ContactGroup> getContactGroupArrayAdapter(){
        return contactGroupArrayAdapter;
    }
    public static void setContactPersonArrayAdapter(ArrayAdapter<ContactPerson> contactPersonArrayAdapter){
        ContactFilter.contactPersonArrayAdapter = contactPersonArrayAdapter;
    }
    public static ArrayAdapter<ContactPerson> getContactPersonArrayAdapter(){
        return contactPersonArrayAdapter;
    }
    //---Getters and Setters end





}
