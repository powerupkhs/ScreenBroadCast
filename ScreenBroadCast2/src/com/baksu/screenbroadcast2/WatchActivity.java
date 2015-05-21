package com.baksu.screenbroadcast2;

import io.socket.*;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class WatchActivity extends ListActivity implements OnClickListener, OnKeyListener {

    private SocketIO socket;
   // private TextView textView;
    private String text,chat;
    private String id,loginId;
    private EditText chat_edit;
    private ArrayAdapter<String> cAdapter;
    private ArrayList<String> mString = new ArrayList<String>();
    private WebView webView;
    private String roomNum,bjId;
    private ImageButton bookmark_reg;
    private ProgressDialog pDialog;
    private final Handler handler = new Handler(){
    	public void handleMessage(Message msg){
    		cAdapter.add(chat);
    		//textView.setText(text);
    	}
    };
    private final Handler bmregHandler = new Handler(){
    	public void handleMessage(Message msg){
    		pDialog.dismiss();
    		if(DBconnection.getInstance().getResponse().equals("true")){
    			Toast.makeText(WatchActivity.this, bjId + " 님이 즐겨찾기로 등록되었습니다. ", Toast.LENGTH_SHORT).show();
    		}
    		else{
    			Toast.makeText(WatchActivity.this, "이미 즐겨찾기로 등록되어있는 회원입니다.", Toast.LENGTH_SHORT).show();
    		}
    		//textView.setText(text);
    	}
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        	setContentView(R.layout.watch_activity2);
        }
        else
        {
        	setContentView(R.layout.watch_activity);		
        }

        cAdapter = new ArrayAdapter<String>(this,R.layout.chat_row,R.id.chat_id_message,mString);
        setListAdapter(cAdapter);
        getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        roomNum = getIntent().getStringExtra("roomNum");
        bjId = getIntent().getStringExtra("bjId");
        
        Log.i("roomNum",roomNum+"  ");
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        loginId = prefs.getString("id", "");
        chat_edit = (EditText)findViewById(R.id.edit_chatText);
        chat_edit.setOnClickListener(this);
        webView = (WebView)findViewById(R.id.broad);
        bookmark_reg = (ImageButton)findViewById(R.id.bookmark_reg);
        bookmark_reg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//즐겨찾기 등록
				pDialog = ProgressDialog.show(WatchActivity.this, "","즐겨찾기 등록 중 ");
				if(bjId.equals(loginId)){
					Toast.makeText(WatchActivity.this, "본인은 즐겨찾기로 등록할 수 없습니다", Toast.LENGTH_SHORT).show();
					pDialog.dismiss();
					return;
				}
				DBconnection.getInstance().bookmarkReg(bjId, loginId, bmregHandler);
			}
		});
        webView.getSettings().setJavaScriptEnabled(true); //자바스크립트 허용
        webView.getSettings().setPluginState(PluginState.ON);
        String url2 = "http://211.189.127.226:3000/mobile_broadcast?roomNumber="+roomNum;
        webView.loadUrl(url2);  //보여줄 사이트 url
        webView.setWebViewClient(new WebViewClient(){   //현재의 엑티비티 내에서 url을 보여주고자 할 경우 설정.
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 // TODO Auto-generated method stub
                 view.loadUrl(url); //다른 url링크를 클릭했을때 url을 다시 로드
                 return true;
                }
        });
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
	            			handler.sendEmptyMessage(0);
	            		}
	            	}
	            }
	        });

			socket.send("connect");
			socket.emit("join", null, roomNum);
			
			findViewById(R.id.button1).setOnClickListener(mClickListener);
		} catch (MalformedURLException e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
    }
    Button.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			String str2 = chat_edit.getText().toString();
			String str = "{\"id\":\""+loginId+"\",\"data\":\""+str2+"\"}";			
			socket.emit("message", str);
			chat_edit.setText(null);
			str2 = loginId + " : " + str2;
			cAdapter.add(str2);
		}
	};
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if( event.getAction() == KeyEvent.ACTION_DOWN ){
				// 이 부분은 특정 키를 눌렀을때 실행 된다.
				// 만약 뒤로 버튼을 눌럿을때 할 행동을 지정하고 싶다면
			 
			if( KeyCode == KeyEvent.KEYCODE_BACK ){
				//여기에 뒤로 버튼을 눌렀을때 해야할 행동을 지정한다
				socket.disconnect();
				// 여기서 리턴값이 중요한데; 리턴값이 true 이냐 false 이냐에 따라 행동이 달라진다.
				// true 일경우 back 버튼의 기본동작인 종료를 실행하게 된다.
				// 하지만 false 일 경우 back 버튼의 기본동작을 하지 않는다.
				// back 버튼을 눌렀을때 종료되지 않게 하고 싶다면 여기서 false 를 리턴하면 된다.
				// back 버튼의 기본동작을 막으면 어플리케이션을 종료할 방법이 없기때문에
				// 따로 종료하는 방법을 마련해야한다.
				}
			 
			}
			 
			return super.onKeyDown( KeyCode, event );
	}
    
}
