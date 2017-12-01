package io.vamshedhar.profilecreator;

import java.io.Serializable;

/**
 * InClass 03
 * @author Vamshedhar Reddy Chintala
 * @author Anjani Nalla
 */

class User implements Serializable {
    String name;
    String email;
    String department;
    int profileImage;
    int curentMood;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", profileImage=" + profileImage +
                ", curentMood=" + curentMood +
                '}';
    }

    public User(String name, String email, String department, int profileImage, int cuurentMood) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.profileImage = profileImage;
        this.curentMood = cuurentMood;
    }
}
