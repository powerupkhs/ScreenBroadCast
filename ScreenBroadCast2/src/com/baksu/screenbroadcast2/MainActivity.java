package com.baksu.screenbroadcast2;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener{
	private ImageButton loginButton, joinButton, watchButton, recordButton, cameraButton, bownapkButton, webButton, exitButton;
	private boolean isPublic;
	private EditText rCreate,roomPw,searchEt;
	private RadioGroup rg;
	private ProgressDialog pDialog;
	private Context context = this;
	private DBconnection db;
	private EditText idet, pwet, pwet2, nameet;
	private View layout;
	private String loginId, loginPw;
	private String roomName;
	private View tempView;
	private NotificationManager mNotiManager;
	public static String roomNum;
	public static SharedPreferences prefs;
	public static String id;
	private Thread thumbnailThread;
	private ThumbnailSend thumbnailSend;
	static boolean isRecording = false;
	static boolean isDownApk = false;
	static boolean isRooting = false;
	static final int NAPNOTI = 1;

	TextView mDisplay;
	AsyncTask<Void, Void, Void> mRegisterTask;

	private final Handler deleteHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			if(db.getResponse().equals("true")){
				//stopService(new Intent(MainActivity.this, ScreenRecordService.class));
				//stopService(new Intent(MainActivity.this, AlwaysOnTopService.class));
				finish();
			}
			else{
				//Toast.makeText(MainActivity.this,"에러 메세지", Toast.LENGTH_LONG).show();
			}
		}
	};
	private final Handler createHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			if(db.getResponse().equals("false")){
				Toast.makeText(MainActivity.this,"방제목 중복입니다", Toast.LENGTH_LONG).show();
			}
			else{
				prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("roomNum",db.getResponse());
				Log.d("msg", "녹화버튼 들어옴");
				tempView.postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(MainActivity.this,MainActivity.class);
						Notification noti = new Notification(R.drawable.onair, "방송중 입니다.", System.currentTimeMillis());
						noti.defaults |= Notification.DEFAULT_SOUND;
						noti.defaults |= Notification.DEFAULT_VIBRATE;
						//noti.flags |= Notification.FLAG_AUTO_CANCEL;					
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						PendingIntent content = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
						noti.setLatestEventInfo(MainActivity.this, "방송중 입니다.", "방송중 입니다.", content);
						mNotiManager.notify(MainActivity.NAPNOTI, noti);
					}
				}, 0);
				Log.d("msg", "녹화 알림 완료");

				Intent intent = new Intent(MainActivity.this,ScreenRecord.class);
				Log.i("tq",db.getResponse()+"");
				intent.putExtra("roomNum", db.getResponse());
				finish();
				startService(intent);
				Intent intent2 = new Intent(MainActivity.this, AlwaysOnTopService.class);
				roomNum = db.getResponse();
				intent.putExtra("roomNum", db.getResponse());
				startService(intent2);
				thumbnailThread = new Thread(thumbnailSend);
				thumbnailSend.roomNum = roomNum;
				thumbnailThread.start();
				Log.d("msg", "썸네일 스레드 시작");
				isRecording = true;
				SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
				loginId = prefs.getString("id", "");
				
				Log.i("push","MainActivity 127 : " + loginId + " " + roomName);
				db.sendpush(loginId, roomName, pushHandler);
			}
		}
	};
	
	private final Handler pushHandler = new Handler(){
		public void handleMessage(Message msg){
			//pDialog.dismiss();
			Log.i("push","푸쉬 보냄");
		}
	};
	
	private final Handler joinHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			if(db.getResponse().equals("true")){
				Toast.makeText(MainActivity.this,"회원가입 성공!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(MainActivity.this,"아이디 중복입니다", Toast.LENGTH_LONG).show();
			}
		}
	};

	private final Handler regHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			Log.i("push","등록성공");

		}
	};

	private final Handler loginHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			if(db.getResponse().equals("true")){
				Toast.makeText(MainActivity.this,"로그인 성공!", Toast.LENGTH_LONG).show();
				//db.getHttpclient().getConnectionManager().shutdown();
				SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();

				editor.putString("id", loginId);
				editor.putString("pw", loginPw);

				id = loginId;


				editor.commit();
				finish();
				startActivity(new Intent(MainActivity.this, MainActivity.class));
			}
			else{
				Toast.makeText(MainActivity.this,"로그인 실패!", Toast.LENGTH_LONG).show();
			}	
		}
	};
	public void registerRegid(){
		GCMRegistrar.unregister(context);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if("".equals(regId)){   //구글 가이드에는 regId.equals("")로 되어 있는데 Exception을 피하기 위해 수정
			GCMRegistrar.register(this, "650121331050");
			Log.i("push","gogo");
		}
		else{
			Log.d("==============", regId);
		}
	}
	public void onCreate(Bundle savedInstanceState){
		
		SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
		mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		db = DBconnection.getInstance();
		//자동로그인
		/*
		if(prefs.getString("id", "")!=null || prefs.getString("id","")!=""){
			Log.i("login","자동로그인"+prefs.getString("id",""));
			Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
			startActivity(intent);
			finish();
		}*/
		super.onCreate(savedInstanceState);

		// 기기 해상도 구하는 부분 
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int deviceWidth = displayMetrics.widthPixels;
		int deviceHeight = displayMetrics.heightPixels; 
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); 
		int dipWidth  = (int) (120  * displayMetrics.density);
		int dipHeight = (int) (90 * displayMetrics.density);

		Log.d("msg","displayMetrics.density : " + displayMetrics.density);
		Log.d("msg","deviceWidth : " + deviceWidth + ", deviceHeight : " + deviceHeight);

		super.onCreate(savedInstanceState);

		if(deviceWidth <= 480)
		{
			setContentView(R.layout.activity_main2);
		}
		else
		{
			setContentView(R.layout.activity_main);			
		}

		try
		{ 
			Runtime.getRuntime().exec("su");
			isRooting = true;
			Log.d("msg", "루팅 되어있도다 !!!");
		}
		catch(Exception e)
		{
			isRooting = false;
			Log.d("msg", "루팅 안되있어 ...");
		}

		loginButton = (ImageButton)findViewById(R.id.login);
		loginButton.setOnClickListener((android.view.View.OnClickListener) this);
		joinButton = (ImageButton)findViewById(R.id.join);
		joinButton.setOnClickListener((android.view.View.OnClickListener) this);
		watchButton = (ImageButton)findViewById(R.id.watch);
		watchButton.setOnClickListener((android.view.View.OnClickListener) this);
		recordButton = (ImageButton)findViewById(R.id.record);
		recordButton.setOnClickListener((android.view.View.OnClickListener) this);
		cameraButton = (ImageButton)findViewById(R.id.camera);
		cameraButton.setOnClickListener((android.view.View.OnClickListener) this);
		bownapkButton = (ImageButton)findViewById(R.id.downapk);
		bownapkButton.setOnClickListener((android.view.View.OnClickListener) this);
		webButton = (ImageButton)findViewById(R.id.web);
		webButton.setOnClickListener((android.view.View.OnClickListener) this);
		exitButton = (ImageButton)findViewById(R.id.exit);
		exitButton.setOnClickListener((android.view.View.OnClickListener) this);

		if(!(prefs.getString("id", "").equals(""))){
			loginButton.setVisibility(View.INVISIBLE);
			joinButton.setVisibility(View.INVISIBLE);
		}

		thumbnailSend = new ThumbnailSend();

		//loginButton.setVisibility(View.INVISIBLE);
		//joinButton.setVisibility(View.INVISIBLE);
		//pDialog.show(context, "", "등록중");

	}
	public void onClick(View view){
		tempView = view;
		if(view.getId()==R.id.login){
			Context mContext = MainActivity.this;
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.login_dialog, (ViewGroup)findViewById(R.id.layout_root));
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
			aDialog.setView(layout);	
			aDialog.setTitle("로그인");
			aDialog.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					//idet = (EditText)findViewById(R.id.edit_forid);
					idet = (EditText)layout.findViewById(R.id.edit_forid);
					pwet = (EditText)layout.findViewById(R.id.edit_forpw);
					loginId = idet.getText().toString();
					loginPw = pwet.getText().toString();

					if(!(loginId.equals("")) && !(loginPw.equals(""))){
						pDialog = ProgressDialog.show(context,"","로그인중");
						db.login(loginId, loginPw, loginHandler);
						registerRegid();
					}
					else{
						if(loginId.equals("")){
							Toast.makeText(MainActivity.this,"아이디를 입력 해주세요",Toast.LENGTH_LONG).show();
						}
						else if(loginPw.equals("")){
							Toast.makeText(MainActivity.this, "비밀번호를 입력 해주세요", Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			aDialog.show();
		}
		else if(view.getId()==R.id.join){
			Context mContext = MainActivity.this;
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.join_dialog, (ViewGroup)findViewById(R.id.layout_root2));
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
			aDialog.setView(layout);	
			aDialog.setTitle("회원가입");
			Log.i("test2","회원ㄱ아입");
			aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					idet = (EditText)layout.findViewById(R.id.edit_forjoinid);
					pwet = (EditText)layout.findViewById(R.id.edit_forjoinpw);
					pwet2 = (EditText)layout.findViewById(R.id.edit_forjoinpw2);
					nameet = (EditText)layout.findViewById(R.id.edit_forjoinname);
					//Log.i("result",idet.toString());
					Log.i("reuslt",idet.getText().toString());
					String id = idet.getText().toString();
					String pw = pwet.getText().toString();
					String pw2 = pwet2.getText().toString();
					String name = nameet.getText().toString();
					//id = "";
					//pw = "";
					//pw2 = "";
					Log.i("test2",id);
					Log.i("test2",pw);
					Log.i("test2",pw2);
					if(!(id.equals("")) && !(pw.equals("")) && !(pw2.equals("")) && !(name.equals(""))){
						if(!(pw.equals(pw2))){
							Toast.makeText(MainActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
						}
						else{
							pDialog = ProgressDialog.show(context,"","회원가입 중");
							Log.i("test2","db");
							db.join(id, pw,name,joinHandler);
						}
					}
					else{
						if(id.equals("")){
							Toast.makeText(MainActivity.this,"아이디를 입력 해주세요",Toast.LENGTH_LONG).show();
						}
						else if(pw.equals("")){
							Toast.makeText(MainActivity.this, "비밀번호를 입력 해주세요", Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			aDialog.show();
		}
		else if(view.getId()==R.id.watch){
			SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);

			if(prefs.getString("id", "").equals("")){
				Toast.makeText(MainActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
			}
			else{
				id = prefs.getString("id", "");
				Intent intent = new Intent(MainActivity.this, LobbyActivity.class);
				startActivity(intent);
			}

		}
		else if(view.getId()==R.id.record){
			if(isRooting)
			{
				SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
				if(prefs.getString("id", "").equals("")){
					Toast.makeText(MainActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
				}
				else{
					id = prefs.getString("id", "");
					Context mContext = MainActivity.this;
					LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
					layout = inflater.inflate(R.layout.room_create, (ViewGroup)findViewById(R.id.layout_root3));
					AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
					rg = (RadioGroup)layout.findViewById(R.id.radiogroup1);
					rCreate = (EditText)layout.findViewById(R.id.edit_forcreate);
					roomPw = (EditText)layout.findViewById(R.id.edit_forroompw);

					rg.setOnCheckedChangeListener(this);
					aDialog.setView(layout);	
					aDialog.setTitle("방 개설");
					aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//db = DBconnection.getInstance();
							roomName = rCreate.getText().toString();
							String password = roomPw.getText().toString();
							SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
							String id = prefs.getString("id","");
							if(roomName.equals("")){
								Toast.makeText(MainActivity.this, "방 제목을 입력해주세요. " , Toast.LENGTH_SHORT).show();
								return;
							}

							Room tempR;
							if(isPublic){
								tempR = new Room(roomName,true,"공개방",id);
							} 
							else{
								if(password.equals("")){
									Toast.makeText(MainActivity.this, "비밀번호를 입력해주세요. " , Toast.LENGTH_SHORT).show();
									return;
								}
								tempR = new Room(roomName,false,password,id);
							}
							pDialog = ProgressDialog.show(MainActivity.this, "","방 개설 중");
							db.createRoom(tempR, createHandler);

							//idet = (EditText)findViewById(R.id.edit_forid);

						}
					});
					aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					aDialog.show();		
				}
			}
			else
			{
				Toast.makeText(this, "루팅을 해야만 방송할 수 있습니다.", Toast.LENGTH_LONG).show();
			}
		}
		else if(view.getId()==R.id.exit){
			if(isRecording)
			{
				Log.d("msg", "종료버튼 들어옴");
				roomNum = roomNum.trim();
				Log.i("exit","trim : " + roomNum);
				stopService(new Intent(MainActivity.this, ScreenRecordService.class));
				stopService(new Intent(MainActivity.this, AlwaysOnTopService.class));
				NotificationManager mangager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );
				mangager.cancel( 1 );
				pDialog = ProgressDialog.show(MainActivity.this,"", "영상 중지 및 삭제 중");
				db.roomDelete(roomNum,deleteHandler);
				thumbnailSend.onStop();
				isRecording = false;
			}
			finish();
		}
		else if(view.getId()==R.id.downapk){
			//Log.d("msg", "apkdown 들어옴");
			if(isDownApk)
			{
				Log.d("msg", "apk down 하지마");
				Toast.makeText(this, "이미 AdobeFlashPlayer11 을 다운받고 있습니다.\n잠시만 기다려주세요.", Toast.LENGTH_LONG).show();
			}
			else
			{
				Log.d("msg", "apkdown 해");
				isDownApk = true;
				new DownAPK().execute();
				Toast.makeText(this, "AdobeFlashPlayer11 을 다운받는 중입니다.\n잠시후에 설치가 시작됩니다.", Toast.LENGTH_LONG).show();
			}


		}
		else if(view.getId()==R.id.camera){
			Intent intent = new Intent(MainActivity.this,CamViewActivity.class);
			startActivity(intent);
			//finish();
		}
		else if(view.getId()==R.id.web){
			//stopService(new Intent(MainActivity.this, ScreenRecordService.class));
			startActivity(new Intent(this, WebSite.class));
			//finish();
		}
	}


	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		if(arg1==R.id.is_private){
			Log.i("create","비공개");
			roomPw.setFocusable(true);
			roomPw.setClickable(true);
			roomPw.setFocusableInTouchMode(true);
			isPublic = false;
		}
		if(arg1==R.id.is_public){
			Log.i("create","공개");
			roomPw.setFocusable(false);
			roomPw.setClickable(false);
			roomPw.setFocusableInTouchMode(false);
			roomPw.setText("");
			isPublic = true;
		}
	}

	class DownAPK extends AsyncTask<Void, Void, Void>
	{
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		protected void onProgressUpdate(Void... values)
		{
			super.onProgressUpdate(values);
		}


		protected void onCancelled()
		{
			super.onCancelled();
		}

		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
		}

		protected Void doInBackground(Void... params)
		{
			// 플레시 플레이어  설치 코드
			String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AdobeFlashPlayer11.apk";

			try
			{
				URL url = new URL("http://211.189.127.226:3000/etc/AdobeFlashPlayer11.apk");
				//URL url = new URL("http://121.137.116.49:9000/DBServer/SaveFile/10cm.mp3");
				Log.d("msg", ""+url);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				Log.d("msg", "url connect1");
				//urlConnection.setDoOutput(true);

				Log.d("msg", "url connect2");
				//urlConnection.setRequestMethod("GET");    
				urlConnection.connect();    
				Log.d("msg", "url connect");

				File file = new File(filename);
				FileOutputStream fileOutput = new FileOutputStream(file);    
				InputStream inputStream = urlConnection.getInputStream();    
				Log.d("msg", "stream connect");

				byte[] buffer = new byte[1024];   
				int bufferLength = 0;

				while ( (bufferLength = inputStream.read(buffer)) > 0 )
				{      
					fileOutput.write(buffer, 0, bufferLength);   
				}   

				fileOutput.close();    
				inputStream.close();
			}
			catch (Exception e)
			{
				Log.d("msg", "apk down error   " + e.getMessage());
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri apkUri = null;

			apkUri = Uri.fromFile(new File(filename));
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
			startActivity(intent);
			Log.d("msg", "intent in");
			isDownApk = false;

			return null;
		}
	}
}
