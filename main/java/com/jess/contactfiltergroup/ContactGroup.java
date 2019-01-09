package com.jess.contactfiltergroup;

import java.util.ArrayList;

/**
 * Created by USER on 12/9/2018.
 */

public class ContactGroup {

    ArrayList<ContactPerson> contactPersonArrayList;
    int id;
    String name;
    String state = "0";
    public ContactGroup(int id, String name,ArrayList<ContactPerson> CPA,String state) {
        this.id = id;
        this.name = name;
        this.contactPersonArrayList = CPA;
        this.state = state;
    }

    public ArrayList<ContactPerson> getContactPersonArrayList() {
        return contactPersonArrayList;
    }

    public void setContactPersonArrayList(ArrayList<ContactPerson> contactPersonArrayList) {
        this.contactPersonArrayList = contactPersonArrayList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
