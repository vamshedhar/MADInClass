package io.vamshedhar.contacts;

import java.io.Serializable;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/13/17 6:31 PM.
 * vchinta1@uncc.edu
 */

public class Contact implements Serializable {
    String id;
    String name;
    String email;
    String phoneNo;
    String profilePic;

    public Contact() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", profileImage=" + profilePic +
                '}';
    }
}
