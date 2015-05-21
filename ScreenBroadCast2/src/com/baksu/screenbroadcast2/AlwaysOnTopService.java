package com.baksu.screenbroadcast2;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.IOAcknowledge;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class AlwaysOnTopService extends Service {
	private int flag = 0;
	private ListView mPopupView;							//항상 보이게 할 뷰
	private ArrayAdapter<String> cAdapter;
	private ArrayList<String> mString = new ArrayList<String>();
	private SocketIO socket;
	private WindowManager.LayoutParams mParams;		//layout params 객체. 뷰의 위치 및 크기를 지정하는 객체
	private WindowManager mWindowManager;			//윈도우 매니저
	private Button btn;								//투명도 조절 seek bar
	private WindowManager.LayoutParams params;
	private float START_X, START_Y;							//움직이기 위해 터치한 시작 점
	private int PREV_X, PREV_Y;								//움직이기 이전에 뷰가 위치한 점
	private int MAX_X = -1, MAX_Y = -1;					//뷰의 위치 최대 값
	private String text,chat;
    private String roomNum;
    private Intent intent;
    private String id,loginId;//항상 보이게 할 뷰
	private final Handler handler = new Handler(){
    	public void handleMessage(Message msg){
    		cAdapter.add(chat);
    		//textView.setText(text);
    	}
    };
    
	private OnTouchListener mViewTouchListener = new OnTouchListener() {
		@Override public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:				//사용자 터치 다운이면
					if(MAX_X == -1)
						setMaxPosition();
					START_X = event.getRawX();					//터치 시작 점
					START_Y = event.getRawY();					//터치 시작 점
					PREV_X = mParams.x;							//뷰의 시작 점
					PREV_Y = mParams.y;							//뷰의 시작 점
					break;
				case MotionEvent.ACTION_MOVE:
					int x = (int)(event.getRawX() - START_X);	//이동한 거리
					int y = (int)(event.getRawY() - START_Y);	//이동한 거리
					
					//터치해서 이동한 만큼 이동 시킨다
					mParams.x = PREV_X + x;
					mParams.y = PREV_Y + y;
					
					optimizePosition();		//뷰의 위치 최적화
					mWindowManager.updateViewLayout(mPopupView, mParams);	//뷰 업데이트
					break;
			}
			
			return true;
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) { return null; }
	
	@Override
	public void onCreate() {
		super.onCreate();

		this.roomNum = MainActivity.roomNum;
		this.roomNum = this.roomNum.trim();
		 cAdapter = new ArrayAdapter<String>(this,R.layout.chat_row,R.id.chat_id_message,mString);
			mPopupView = new ListView(this);
											//글자 색상
		mPopupView.setBackgroundColor(Color.argb(50, 127, 200, 200));								//텍스트뷰 배경 색
		
		mPopupView.setAdapter(cAdapter);
		mPopupView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		//mPopupView.setOnTouchListener(mViewTouchListener);										//팝업뷰에 터치 리스너 등록

		//최상위 윈도우에 넣기 위한 설정
		mParams = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. 
																					//포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
			PixelFormat.TRANSLUCENT);										//투명
		mParams.gravity = Gravity.TOP; //| Gravity.TOP;						//왼쪽 상단에 위치하게 함.
		mParams.height = 130;
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);	//윈도우 매니저 불러옴.
		mWindowManager.addView(mPopupView, mParams);		//최상위 윈도우에 뷰 넣기. *중요 : 여기에 permission을 미리 설정해 두어야 한다. 매니페스트에
		
		addOpacityController();		//팝업 뷰의 투명도 조절하는 컨트롤러 추가
		try {
			socket = new SocketIO("http://211.189.127.226:3000/");
			
			socket.connect(new IOCallback() {
	            @Override
	            public void onMessage(JSONObject json, IOAcknowledge ack) {
	                try {
	                    System.out.println("Server said:" + json.toString(2));
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            }

	            @Override
	            public void onMessage(String data, IOAcknowledge ack) {
	                Log.d("test", data);
	            }

	            @Override
	            public void onError(SocketIOException socketIOException) {
	                System.out.println("an Error occured");
	                socketIOException.printStackTrace();
	            }

	            @Override
	            public void onDisconnect() {
	                System.out.println("Connection terminated.");
	            }

	            @Override
	            public void onConnect() {
	                System.out.println("Connection established");
	            }

	            @Override
	            public void on(String event, IOAcknowledge ack, Object... args) {
	            	if(event.equals("message")) {
	            		String temp = args[0].toString();
	            		try {
							JSONObject object = new JSONObject(temp);
							id = object.getString("id");
							text = object.getString("data");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		Log.i("chatting","날라온 id"+id);
	            		Log.i("chatting","로그인한 id"+loginId);
	            		if(!(id.equals(loginId))){
	            		chat =  id + " : " + text;
	            		Log.i("chatting",chat);
	            		handler.sendEmptyMessage(0);
	            		}
	            	}
	            }
	        });

			socket.send("connect");
			socket.emit("join", null, roomNum);
			Log.i("chatting","연결 : " + roomNum);
			
			//findViewById(R.id.button1).setOnClickListener(mClickListener);

		} catch (MalformedURLException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
	}
	
	/**
	 * 뷰의 위치가 화면 안에 있게 최대값을 설정한다
	 */
	private void setMaxPosition() {
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서
		
		MAX_X = matrix.widthPixels - mPopupView.getWidth();			//x 최대값 설정
		MAX_Y = matrix.heightPixels - mPopupView.getHeight();			//y 최대값 설정
	}
	
	/**
	 * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
	 */
	private void optimizePosition() {
		//최대값 넘어가지 않게 설정
		if(mParams.x > MAX_X) mParams.x = MAX_X;
		if(mParams.y > MAX_Y) mParams.y = MAX_Y;
		if(mParams.x < 0) mParams.x = 0;
		if(mParams.y < 0) mParams.y = 0;
	}
	
	/**
	 * 알파값 조절하는 컨트롤러를 추가한다
	 */
	private void addOpacityController() {
		btn = new Button(this);		//투명도 조절 seek bar
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(flag == 0){
				Log.i("reco","들어옴");
				mParams.height = 0;
				params.y = 0;
				mWindowManager.updateViewLayout(mPopupView, mParams);	//팝업 뷰 업데이트
				mWindowManager.updateViewLayout(btn, params);
				flag = 1;
				}
				else{
					Log.i("reco","띄워라");
					mParams.height = 130;
					params.y = 130;
					mWindowManager.updateViewLayout(mPopupView, mParams);	//팝업 뷰 업데이트
					mWindowManager.updateViewLayout(btn, params);
					flag = 0;
				}
			}
		});
		//btn.setMax(100);					//맥스 값 설정.
		//btn.setProgress(100);			//현재 투명도 설정. 100:불투명, 0은 완전 투명
		/*
		btn.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override public void onProgressChanged(SeekBar seekBar, int progress,	boolean fromUser) {
				mParams.alpha = progress / 100.0f;			//알파값 설정
				mWindowManager.updateViewLayout(mPopupView, mParams);	//팝업 뷰 업데이트
			}
		});*/
		
		//최상위 윈도우에 넣기 위한 설정
		params = new WindowManager.LayoutParams(
			60,
			60,
			WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. 
																					//포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
			PixelFormat.TRANSLUCENT);										//투명
		params.gravity = Gravity.RIGHT | Gravity.TOP;							//왼쪽 상단에 위치하게 함.
		//params.height = 30;
		params.y = 130;
		
		
		mWindowManager.addView(btn, params);
	}

	/**
	 * 가로 / 세로 모드 변경 시 최대값 다시 설정해 주어야 함.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setMaxPosition();		//최대값 다시 설정
		optimizePosition();		//뷰 위치 최적화
	}
	
	@Override
	public void onDestroy() {
		if(mWindowManager != null) {		//서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
			if(mPopupView != null) mWindowManager.removeView(mPopupView);
			if(btn != null) mWindowManager.removeView(btn);
		}
		super.onDestroy();
	}
}