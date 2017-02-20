package com.filip.tomasovych.keystrokeauthentication.app.model;

/**
 * Created by nolofinwe on 5.10.2016.
 */

public class User {

    private static final String TAG = User.class.getSimpleName();

    private long mId;
    private String mName;
    private String mPassword;

    public User() {
        mName = null;
        mPassword = null;
    }

    public User(long id, String name, String password) {
        mId = id;
        mName = name;
        mPassword = password;
    }

    public User(String name, String password) {
        mName = name;
        mPassword = password;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mPassword='" + mPassword + '\'' +
                '}';
    }
}
