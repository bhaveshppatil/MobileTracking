package com.finalyear.mobiletracking.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class UserLocationModel implements Parcelable {
    private String userName;
    private String emailId;
    private String mobNumber;
    private String imeiNumber;
    private String deviceName;
    private String androidOs;
    private double latitude;
    private double longitude;
    private String location;
    private String date;
    private String time;

    public UserLocationModel() {
    }

    public UserLocationModel(String userName, String emailId, String mobNumber, String imeiNumber, String deviceName, String androidOs, double latitude, double longitude, String location, String date, String time) {
        this.userName = userName;
        this.emailId = emailId;
        this.mobNumber = mobNumber;
        this.imeiNumber = imeiNumber;
        this.deviceName = deviceName;
        this.androidOs = androidOs;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    protected UserLocationModel(Parcel in) {
        userName = in.readString();
        emailId = in.readString();
        mobNumber = in.readString();
        imeiNumber = in.readString();
        deviceName = in.readString();
        androidOs = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        location = in.readString();
        date = in.readString();
        time = in.readString();
    }

    public static final Creator<UserLocationModel> CREATOR = new Creator<UserLocationModel>() {
        @Override
        public UserLocationModel createFromParcel(Parcel in) {
            return new UserLocationModel(in);
        }

        @Override
        public UserLocationModel[] newArray(int size) {
            return new UserLocationModel[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobNumber() {
        return mobNumber;
    }

    public void setMobNumber(String mobNumber) {
        this.mobNumber = mobNumber;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAndroidOs() {
        return androidOs;
    }

    public void setAndroidOs(String androidOs) {
        this.androidOs = androidOs;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(emailId);
        dest.writeString(mobNumber);
        dest.writeString(imeiNumber);
        dest.writeString(deviceName);
        dest.writeString(androidOs);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeString(time);
    }
}
