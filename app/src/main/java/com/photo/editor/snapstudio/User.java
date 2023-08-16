package com.photo.editor.snapstudio;

import android.net.Uri;

public class User {

    private Uri photoUrl;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    private Boolean proEnabled;
    private String paymentId;
    private Long startTimeStamp;
    private Long endTimeStamp;

    private String age;

    private String gender;

//    private boolean isFromAssets;
    private String assetPos;

    public User() {
    }

//    public boolean isFromAssets() {
//        return isFromAssets;
//    }
//
//    public void setFromAssets(boolean fromAssets) {
//        isFromAssets = fromAssets;
//    }

    public String getAssetPos() {
        return assetPos;
    }

    public void setAssetPos(String assetPos) {
        this.assetPos = assetPos;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getProEnabled() {
        return proEnabled;
    }

    public void setProEnabled(Boolean proEnabled) {
        this.proEnabled = proEnabled;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(Long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public Long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(Long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }
}
