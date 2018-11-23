package com.jess.contactfiltergroups;

/**
 * Created by USER on 11/20/2018.
 */

public class ContactPerson {

    private String contactID;
    private String contactName;
    private String contactNum;
    private boolean contactBlocked = false;
    private String[] contactNums;

    public ContactPerson(String contactID, String contactName, String[] contactNums) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactNums = contactNums;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String[] getContactNums() {
        return contactNums;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public boolean isContactBlocked() {
        return contactBlocked;
    }

    public void setContactBlocked(boolean contactBlock) {
        this.contactBlocked = contactBlock;
    }
}
