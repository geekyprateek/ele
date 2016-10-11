package com.ele.Models;

import android.graphics.Bitmap;

/**
 * Created by prateekgupta on 11/10/16.
 */

public class Contact {

    String ContactName;
    String ImageUri;
    String LastCallTime;
    String Number;
    String email = null;
    Bitmap bitmap;
    String lastCall = null;

    public String getLastCall() {
        return lastCall;
    }

    public void setLastCall(String lastCall) {
        this.lastCall = lastCall;
    }



    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getLastCallTime() {
        return LastCallTime;
    }

    public void setLastCallTime(String lastCallTime) {
        LastCallTime = lastCallTime;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
