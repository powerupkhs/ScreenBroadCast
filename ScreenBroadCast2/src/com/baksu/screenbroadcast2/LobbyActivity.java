package com.baksu.screenbroadcast2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LobbyActivity extends Activity implements OnClickListener{
	private ListView list;
	private Context context = this;
	private ArrayList<Room> rooms=new ArrayList<Room>();
	private int m_nPage = 1;
	private ProgressDialog pDialog;
	private Button roomCreate;
	private ImageButton search,logout,bookmark;
	private View layout;
	private EditText rCreate,roomPw,searchEt;
	private RadioGroup rg;
	private boolean isPublic;
	private DBconnection db = DBconnection.getInstance();
	AlertDialog.Builder aDialog;
	LayoutInflater inflater;
	private EditText passET;
	private String password;
	private int arg,roomNum;
	private BroadAdapter adapter;
	private Bitmap bm;
	public static HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
	private final Handler recoHandler = new Handler(){
		public void handleMessage(Message msg){
			//list.invalidate();
			//Log.i("reco","들어옴?");
		}
	};
	
	private final Handler searchHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			rooms = db.getRoomList();
			
			if(db.getResponse().equals("false")){
				Toast.makeText(LobbyActivity.this, "검색어를 포함한 방이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
				return;
			}
			adapter = new BroadAdapter(LobbyActivity.this, R.layout.lobby_row, rooms,recoHandler);
			adapter.setCount(m_nPage, rooms.size());
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener(){


				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					Log.i("test",arg2+" "+" 안들어옴?");
					// TODO Auto-generated method stub
					
					Intent intent = new Intent(LobbyActivity.this, WatchActivity.class);
					Log.i("tq",db.getRoomList().get(arg2).getRoomNum()+"");
					intent.putExtra("roomNum", db.getRoomList().get(arg2).getRoomNum()+"");
					startActivity(intent);
					}
				});
			}
		};
		
		private final Handler bookmarkHandler = new Handler(){
			public void handleMessage(Message msg){
				pDialog.dismiss();
				rooms = db.getRoomList();
				
				if(db.getResponse().equals("[]")){
					Toast.makeText(LobbyActivity.this, "즐겨찾기로 등록된 회원이 없습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				adapter = new BroadAdapter(LobbyActivity.this, R.layout.lobby_row, rooms,recoHandler);
				adapter.setCount(m_nPage, rooms.size());
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener(){


					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						
						Intent intent = new Intent(LobbyActivity.this, WatchActivity.class);
						intent.putExtra("roomNum", db.getRoomList().get(arg2).getRoomNum()+"");
						startActivity(intent);
						}
					});
				}
			};
			
			
		private final Handler joinHandler = new Handler(){
			public void handleMessage(Message msg){
				
				pDialog.dismiss();
				rooms = db.getRoomList();
				adapter = new BroadAdapter(LobbyActivity.this, R.layout.lobby_row, rooms,recoHandler);
				adapter.setCount(m_nPage, rooms.size());
				
				//for(int i=0; i<rooms.size(); i++){
					getRoomImage();
				//}
				while(true)
				{
					if(BroadAdapter.isDownBmp)
					{
						BroadAdapter.isDownBmp = false;
						break;
					}
				}
					
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener(){


					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						arg = arg2;
						Intent intent = new Intent(LobbyActivity.this, WatchActivity.class);
						if(db.getRoomList().get(arg2).isPublic()==false){
							layout = inflater.inflate(R.layout.password, (ViewGroup)findViewById(R.id.password_root));
							aDialog.setView(layout);	
							aDialog.setTitle("비밀번호 확인");
							aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

									//idet = (EditText)findViewById(R.id.edit_forid);
									passET = (EditText)layout.findViewById(R.id.et_room_pw);
									password = passET.getText().toString();

									if(password!=null){
										if(db.getRoomList().get(arg).getPassword().equals(password)){
											Intent intent = new Intent(LobbyActivity.this, WatchActivity.class);
											intent.putExtra("roomNum", db.getRoomList().get(arg).getRoomNum()+"");
											intent.putExtra("bjId", db.getRoomList().get(arg).getBjId());
											startActivity(intent);
										}
										else{
											Toast.makeText(LobbyActivity.this,"비밀번호가 올바르지 않습니다.",Toast.LENGTH_LONG).show();
										}
									}
									else{
										if(password == null){
											Toast.makeText(LobbyActivity.this,"비밀번호를 입력 해주세요",Toast.LENGTH_LONG).show();
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
						else{
						intent.putExtra("roomNum", db.getRoomList().get(arg2).getRoomNum()+"");
						intent.putExtra("bjId", db.getRoomList().get(arg2).getBjId());
						startActivity(intent);
						}
						}
					});
			}
		};
		private final Handler createHandler = new Handler(){
			public void handleMessage(Message msg){
				pDialog.dismiss();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		};
	public void onCreate(Bundle savedInstanceState){

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
			setContentView(R.layout.lobby2);
		}
		else
		{
			setContentView(R.layout.lobby);		
		}

		pDialog = ProgressDialog.show(LobbyActivity.this,"","정보를 불러오는 중");
		
		db.getRoom(joinHandler);
		aDialog = new AlertDialog.Builder(this);
		inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		
		list = (ListView)findViewById(R.id.list_forRoom);
		//roomCreate = (Button)findViewById(R.id.create);
		search = (ImageButton)findViewById(R.id.searchBtn);
		logout = (ImageButton)findViewById(R.id.logout);
		bookmark = (ImageButton)findViewById(R.id.bookmark);
		//roomCreate.setOnClickListener((android.view.View.OnClickListener)this);
		search.setOnClickListener((android.view.View.OnClickListener)this);
		logout.setOnClickListener((android.view.View.OnClickListener)this);
		bookmark.setOnClickListener((android.view.View.OnClickListener)this);
	}
	
	public void getRoomImage(){
		//this.roomNum = roomNum2;
		new Thread(){
			public void run(){
				for(int i=0; i<rooms.size(); i++){
					try { 
						URL url = new URL("http://211.189.127.226:3000/"+rooms.get(i).getRoomNum()+".jpg");
						Log.i("imageUp",url.toString());
						URLConnection conn =
								url.openConnection(); 
						conn.connect(); 
						BufferedInputStream  bis = new BufferedInputStream(conn.getInputStream());
						bm = BitmapFactory.decodeStream(bis); bis.close();
						
						Log.i("imageUp",rooms.get(i).getRoomNum()+"");
						imageMap.put(rooms.get(i).getRoomNum()+"", bm);
					
					} catch (IOException e) {
						Log.i("Androes", " " + e); 
					}
					//imageHandler.sendEmptyMessage(0);
				}
				
				BroadAdapter.isDownBmp = true;
			}
		}.start();
	}
	public void onClick(View view){
		
		if(view.getId()==R.id.searchBtn){
			searchEt = (EditText)findViewById(R.id.searchText);
			String search = searchEt.getText().toString();
			pDialog = ProgressDialog.show(LobbyActivity.this, "", "검색 중입니다.");
			db.search(search, searchHandler);
			
		}
		else if(view.getId()==R.id.logout){
			SharedPreferences  prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			
			editor.putString("id", "");
			editor.putString("pw", "");
			editor.commit();
			
			Intent intent = new Intent(LobbyActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		else if(view.getId() == R.id.bookmark){
			SharedPreferences  prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
			pDialog = ProgressDialog.show(LobbyActivity.this, "", "검색 중입니다.");
			db.myBookmark(prefs.getString("id", ""),bookmarkHandler);
		}
	}

}
