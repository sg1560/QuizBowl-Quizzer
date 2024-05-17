package com.example.androidexample.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class UserObject implements Serializable {
    private String userName;
    private String password;
    private int groupID;
    private long id;
    private String joinDate, profilePicture;
    private boolean admin, coach;

    public UserObject(long id, String user, String pass) {
        this.id = id;
        userName = user;
        password = pass;
        groupID = 0;
        profilePicture = null;
        admin = false;
        coach = false;
    }

    public void joinGroup(int group) {
        this.groupID = group;
    }

    public void leaveGroup() {
        this.groupID = 0;
    }

    public int getGroupID() {
        return groupID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJoinDate(String s) {
        joinDate = s;
    }

    public long getId() {
        return id;
    }

    public void setProfilePicture(String pic) {
        profilePicture = pic;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void makeAdmin() {
        admin = true;
    }

    public static UserObject getUserFromString(String string) throws JSONException {
        return getUserFromJSON(new JSONObject(string));
    }

    public static UserObject getUserFromJSON(JSONObject jsonObject) throws JSONException {
        UserObject userObject = new UserObject(
                jsonObject.getInt("cid"),
                jsonObject.getString("userName"),
                jsonObject.getString("password"));
        try {
            String pic = jsonObject.getString("profileImage");
            if (pic != null) {
                userObject.setProfilePicture(pic);
            }
        } catch (JSONException e) {
        }

        try {
            int gid = jsonObject.getJSONArray("teams").getJSONObject(0).getInt("tid");
            userObject.joinGroup(gid);
        } catch (JSONException e) {
        }
        return userObject;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserObject)) return false;
        return id == ((UserObject)o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void makeCoach(){coach = true;}

    public boolean isCoach(){return coach;}

    public void setPassword(String password) {this.password = password;}

    public void setUserName(String userName) {this.userName = userName;}
}
