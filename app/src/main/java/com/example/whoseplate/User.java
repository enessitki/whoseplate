package com.example.whoseplate;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String plate;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String plate, String password) {
        this.plate = plate;
        this.password = password;
    }

}