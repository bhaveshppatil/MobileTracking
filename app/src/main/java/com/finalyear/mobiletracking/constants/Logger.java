package com.finalyear.mobiletracking.constants;

import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class
Logger {


	public static void addLog(String text) {

			try {
				Log.i("test", text);
				// Make a new directory/folder
				File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/", ("TRACKING_APP_LOGS"));

				if (!directory.exists()) {
					directory.mkdir();
				}
				// make a new text file in that created new directory/folder
				File file = new File(directory.getPath(), "UPLOAD_OFFLINE_DATA.txt");

				if (!file.exists() && directory.exists()) {
					file.createNewFile();
				}
				try {
					text= Calendar.getInstance().getTime().toString()+":"+text;
					FileOutputStream stream = new FileOutputStream(file, true);
					stream.write(("\r\n").getBytes());
					stream.write((text).getBytes());
					stream.close();

				} catch (Exception e) {
					// Log.i("File creation failed for ","file");
				}
			} catch (Exception e) {
				// Log.i("Directory creation failed for ","Directory");
			}

	}



}
