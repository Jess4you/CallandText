package com.jess.contactfiltergroup;

import java.util.ArrayList;

/**
 * Created by USER on 12/17/2018.
 */

public class ApplicationGroup {

    ArrayList<ApplicationObj> applicationObjArrayList;
    String id;
    String name;
    String state = "0";
    public ApplicationGroup(String name,ArrayList<ApplicationObj> CPA,String state) {
        this.name = name;
        this.applicationObjArrayList = CPA;
        this.state = state;
    }

    public ArrayList<ApplicationObj> getApplicationObjArrayList() {
        return applicationObjArrayList;
    }

    public void setApplicationObjArrayList(ArrayList<ApplicationObj> contactPersonArrayList) {
        this.applicationObjArrayList = contactPersonArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
