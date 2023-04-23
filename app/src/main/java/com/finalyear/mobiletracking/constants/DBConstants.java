package com.finalyear.mobiletracking.constants;

public class DBConstants {

    //COLUMNS
  public static final String ROW_ID="id";
  public static final String USERNAME = "userName";
  public static final String EMAIL = "emailId";
  public static final String MOB_NUM = "mobNumber";
  public static final String IMEI_NUM = "imeiNumber";
  public static final String DEVICENAME = "deviceName";
  public static final String ANDROID_OS = "androidOs";
  public static final String LAT = "latitude";
  public static final String LONGI = "longitude";
  public static final String ADDRESS = "location";
  public static final String DATE = "date";
  public static final String TIME = "time";



    //DB PROPERTIES
   public static final String DB_NAME="tracking_DB";
   public static final String TB_NAME_LOC_UPDATES="tbl_location_updates";
   public static final int DB_VERSION='1';

    //CREATE TABLE
  public   static final String CREATE_TB="CREATE TABLE tbl_location_updates(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "userName TEXT NOT NULL,emailId TEXT NOT NULL,mobNumber TEXT NOT NULL,imeiNumber TEXT NOT NULL,androidOs TEXT NOT NULL,deviceName TEXT NOT NULL,longitude TEXT NOT NULL,latitude TEXT NOT NULL,location TEXT NOT NULL,date TEXT NOT NULL,time TEXT NOT NULL);";

}
