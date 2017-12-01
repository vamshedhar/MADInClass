package io.vamshedhar.contacts;

import java.io.Serializable;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/20/17 7:13 PM.
 * vchinta1@uncc.edu
 */

public class User implements Serializable {
    String firstName, lastName, profilePic;

    public User() {
    }

    public User(String firstName, String lastName, String profilePic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
