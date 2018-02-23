package com.wael.elmahask.chatapp;

/**
 * Created by elmah on 16-Feb-18.
 */

public class Users {
    public String userName;
    public String userStatus;
    public String userImageURL;
    public String userThumImage;

    public Users() {
    }

    public Users(String userName, String userStatus, String userImageURL, String userThumImage) {
        this.userName = userName;
        this.userStatus = userStatus;
        this.userImageURL = userImageURL;
        this.userThumImage = userThumImage;
    }

    public String getUserThumImage() {
        return userThumImage;
    }

    public void setUserThumImage(String userThumImage) {
        this.userThumImage = userThumImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }
}
