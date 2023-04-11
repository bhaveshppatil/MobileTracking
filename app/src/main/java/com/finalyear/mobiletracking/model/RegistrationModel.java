package com.finalyear.mobiletracking.model;

public class RegistrationModel {
   private String userName;
   private String emailId;
   private String password;
   private String mobNumber;
   private String imeiNumber;
   private String deviceName;
   private String androidOs;
   private String registrationDate;

    public RegistrationModel() {
    }

    public RegistrationModel(String userName, String emailId, String password, String mobNumber, String imeiNumber, String deviceName, String androidOs, String registrationDate) {
        this.userName = userName;
        this.emailId = emailId;
        this.password = password;
        this.mobNumber = mobNumber;
        this.imeiNumber = imeiNumber;
        this.deviceName = deviceName;
        this.androidOs = androidOs;
        this.registrationDate = registrationDate;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
