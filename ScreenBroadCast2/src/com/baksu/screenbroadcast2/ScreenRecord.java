package com.baksu.screenbroadcast2;


import android.app.*;
import android.content.*;
import android.os.*;


public class ScreenRecord extends Service{

	public int onStartCommand(Intent intent, int flags, int startId){
		String result = intent.getStringExtra("roomNum");
		Intent serviceIntent = new Intent(this, ScreenRecordService.class);
		serviceIntent.putExtra("roomNum", result);
		startService(serviceIntent);
		this.stopSelf();

		return Service.START_NOT_STICKY;
	}
	
	public void onCreate() {
		super.onCreate();
	}
	
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}