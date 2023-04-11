package com.finalyear.mobiletracking.sharePref;


/**
 * Created by Rohit on 1/9/18.
 */
public class SessionRepository {

    private static final String LOGOUT_ALERT_ACTION = "logout-alert";

    private static final String MOBILE_NO = "mobile_no";
    private static final String IMEI_NO = "imei_no";
    private static final String DEVICE_NAME = "device_name";

    private static final String MR_NAME = "mr_name";
    private static final String MR_EMAIL_ID = "mr_email";
    private static final String CURRENT_LOCATION = "loc";
    private static final String REGISTR_DATE = "reg_date";
    private static final String LOGOUT_SESSION = "LOGOUT_SESSION";

    private static SessionRepository repositoryInstance;


    public static SessionRepository getInstance() {
        if (repositoryInstance == null) {
            repositoryInstance = new SessionRepository();
        }
        return repositoryInstance;
    }


    public void storeName(String mr_name) {
        SharedPrefs.getInstance().addString(MR_NAME, mr_name);

    }

    public String getName() {
        return SharedPrefs.getInstance().getString(MR_NAME, "na");
    }

    public void storeIMEI_NO(String unique_code) {
        SharedPrefs.getInstance().addString(IMEI_NO, unique_code);

    }

    public String getIMEI_NO() {
        return SharedPrefs.getInstance().getString(IMEI_NO, "na");
    }


    public void storeDeviceName(String devicename) {
        SharedPrefs.getInstance().addString(DEVICE_NAME, devicename);

    }

    public String getDeviceName() {
        return SharedPrefs.getInstance().getString(DEVICE_NAME, "na");
    }

    public void storeEmailId(String email_id) {
        SharedPrefs.getInstance().addString(MR_EMAIL_ID, email_id);

    }

    public String getEmailId() {
        return SharedPrefs.getInstance().getString(MR_EMAIL_ID, "na");
    }

    public void storeMobileNo(String mob_no) {
        SharedPrefs.getInstance().addString(MOBILE_NO, mob_no);

    }

    public String getMobileNo() {
        return SharedPrefs.getInstance().getString(MOBILE_NO, "na");
    }

    public void storeCurrentLoc(String loc) {
        SharedPrefs.getInstance().addString(CURRENT_LOCATION, loc);

    }

    public String getCurrentLocation() {
        return SharedPrefs.getInstance().getString(CURRENT_LOCATION, "na");
    }

    public void storeRegistrastionDate(String regDate) {
        SharedPrefs.getInstance().addString(REGISTR_DATE, regDate);

    }

    public String getRegistrastionDate() {
        return SharedPrefs.getInstance().getString(REGISTR_DATE, "na");
    }

    public void storeLogoutSession(boolean logout) {
        SharedPrefs.getInstance().addBoolean(LOGOUT_SESSION, logout);

    }

    public boolean getLogoutSession() {
        return SharedPrefs.getInstance().getBoolean(LOGOUT_SESSION, false);
    }
}
