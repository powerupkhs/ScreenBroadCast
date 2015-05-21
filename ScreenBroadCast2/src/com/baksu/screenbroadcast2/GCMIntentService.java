/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baksu.screenbroadcast2;

import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */



public class GCMIntentService extends GCMBaseIntentService {
	 private static final String tag = "GCMIntentService";
	    private static final String PROJECT_ID = "650121331050";
	    private final Handler regHandler = new Handler(){
	    	
			public void handleMessage(Message msg){
				Log.i("push","등록등록");
			}
		};
	    //구글 api 페이지 주소 [https://code.google.com/apis/console/#project:긴 번호]
	   //#project: 이후의 숫자가 위의 PROJECT_ID 값에 해당한다
	   
	    //public 기본 생성자를 무조건 만들어야 한다.
	    public GCMIntentService(){ this(PROJECT_ID); }
	   
	    public GCMIntentService(String project_id) { super(project_id); }
	    
	    /** 푸시로 받은 메시지 */
	    @Override
	    protected void onMessage(Context context, Intent intent) {
	        Bundle b = intent.getExtras();
	        String id=null;
	        String title=null;
	        Iterator<String> iterator = b.keySet().iterator();
	        while(iterator.hasNext()) {
	            String key = iterator.next();
	            String value = b.get(key).toString();
	            Log.i("push", "onMessage. "+key+" : "+value);
	            if(key.equals("id")){
	            	id = value;
	            }
	            else if(key.equals("title")){
	            	title = value;
	            }
	        }
	        generateNotification(context, id +" 님이  방 제목 " + title + "으로 방송을 시작했습니다.");
	    }

	    /**에러 발생시*/
	    protected void on_error(Context context, String errorId) {
	        Log.i("push", "on_error. errorId : "+errorId);
	    }
	 
	    /**단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다*/
	    @Override
	    protected void onRegistered(Context context, String regId) {
	        Log.i("push", "onRegistered. regId : "+regId);
	        
	        //registerRegid 호출
			DBconnection.getInstance().registerRegid(regId, regHandler);
	    }

	    /**단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다*/
	    @Override
	    protected void onUnregistered(Context context, String regId) {
	        Log.i("push", "onUnregistered. regId : "+regId);
	    }

		@Override
		protected void onError(Context arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}
		
		 private static void generateNotification(Context context, String message) {
			 Log.i("push","noti");
		        int icon = R.drawable.play;
		        long when = System.currentTimeMillis();
		        NotificationManager notificationManager = (NotificationManager)
		                context.getSystemService(Context.NOTIFICATION_SERVICE);
		        Notification notification = new Notification(icon, message, when);
		        String title = context.getString(R.string.app_name);
		        Intent notificationIntent = new Intent(context, MainActivity.class);
		        // set intent so it does not start a new activity
		        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		                Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        PendingIntent intent =
		                PendingIntent.getActivity(context, 0, notificationIntent, 0);
		        notification.setLatestEventInfo(context, title, message, intent);
		        notification.flags |= Notification.FLAG_AUTO_CANCEL;
		        notificationManager.notify(0, notification);
		    }
}
