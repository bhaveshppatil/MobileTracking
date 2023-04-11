package com.finalyear.mobiletracking.sqlite_db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.finalyear.mobiletracking.constants.DBConstants;
import com.finalyear.mobiletracking.constants.Logger;
import com.finalyear.mobiletracking.model.UserLocationModel;

import java.util.ArrayList;

public class SqLiteDBHelper extends SQLiteOpenHelper {

    public SqLiteDBHelper(Context context) {
        super(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
    }

    //WHEN TB IS CREATED
    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            db.execSQL(DBConstants.CREATE_TB);
            Logger.addLog(DBConstants.CREATE_TB);

        }catch (SQLException e)
        {
            e.printStackTrace();
            Logger.addLog(DBConstants.CREATE_TB+"  : "+e.getMessage());
        }

    }

    //UPGRADE TB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +DBConstants.TB_NAME_LOC_UPDATES);
        onCreate(db);
    }

    public long insertLocDetails(UserLocationModel userLocationModel)
    {
        try
        {
            SQLiteDatabase db =this.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put(DBConstants.USERNAME,userLocationModel.getUserName());
            cv.put(DBConstants.EMAIL, userLocationModel.getEmailId());
            cv.put(DBConstants.MOB_NUM, userLocationModel.getMobNumber());
            cv.put(DBConstants.IMEI_NUM, userLocationModel.getImeiNumber());
            cv.put(DBConstants.DEVICENAME, userLocationModel.getDeviceName());
            cv.put(DBConstants.ANDROID_OS, userLocationModel.getAndroidOs());
            cv.put(DBConstants.LAT, userLocationModel.getLatitude());
            cv.put(DBConstants.LONGI, userLocationModel.getLongitude());
            cv.put(DBConstants.ADDRESS, userLocationModel.getLocation());
            cv.put(DBConstants.DATE, userLocationModel.getDate());
            cv.put(DBConstants.TIME, userLocationModel.getTime());
            Logger.addLog(" insert DBConstants.TB_NAME_LOC_UPDATES,DBConstants.ROW_ID,cv");
            return db.insert(DBConstants.TB_NAME_LOC_UPDATES,DBConstants.ROW_ID,cv);

        }catch (Exception e)
        {
            e.printStackTrace();
            Logger.addLog(e.getMessage());
        }

        return 0;
    }
    public ArrayList<UserLocationModel> getAllLocRecords(){
        String sql = "select * from " + DBConstants.TB_NAME_LOC_UPDATES;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<UserLocationModel> storeLoc = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                UserLocationModel model = new UserLocationModel();
                model.setUserName(cursor.getString(cursor.getColumnIndex(DBConstants.USERNAME)));
                model.setEmailId(cursor.getString(cursor.getColumnIndex(DBConstants.EMAIL)));
                model.setImeiNumber(cursor.getString(cursor.getColumnIndex(DBConstants.IMEI_NUM)));
                model.setDeviceName(cursor.getString(cursor.getColumnIndex(DBConstants.DEVICENAME)));
                model.setAndroidOs(cursor.getString(cursor.getColumnIndex(DBConstants.ANDROID_OS)));
                model.setMobNumber(cursor.getString(cursor.getColumnIndex(DBConstants.MOB_NUM)));
                model.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBConstants.LAT))));
                model.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBConstants.LONGI))));
                model.setLocation(cursor.getString(cursor.getColumnIndex(DBConstants.ADDRESS)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DBConstants.DATE)));
                model.setTime(cursor.getString(cursor.getColumnIndex(DBConstants.TIME)));

                storeLoc.add(model);
            }while (cursor.moveToNext());
        }
        Logger.addLog("select * from " + DBConstants.TB_NAME_LOC_UPDATES);
        cursor.close();
        return storeLoc;
    }

    public void deleteAllRecords(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+DBConstants.TB_NAME_LOC_UPDATES); //delete all rows in a table
            Logger.addLog("DELETE FROM "+DBConstants.TB_NAME_LOC_UPDATES);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}