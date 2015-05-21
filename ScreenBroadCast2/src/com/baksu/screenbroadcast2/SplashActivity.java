package com.baksu.screenbroadcast2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SplashActivity extends Activity implements OnClickListener{
	private Button btn;
	private ProgressDialog pDialog;
	private Context context=this;
	private DBconnection db;
	private EditText idet,pwet,pwet2,nameet;
	private View layout;
	private String loginId, loginPw;
	private final Handler joinHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			if(db.getResponse().equals("true")){
				Toast.makeText(SplashActivity.this,"회원가입 성공!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(SplashActivity.this,"아이디 중복입니다", Toast.LENGTH_LONG).show();
			}
		}
	};
	private final Handler loginHandler = new Handler(){
		public void handleMessage(Message msg){
			pDialog.dismiss();
			if(db.getResponse().equals("true")){
				Toast.makeText(SplashActivity.this,"로그인 성공!", Toast.LENGTH_LONG).show();
				SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				
				editor.putString("id", loginId);
				editor.putString("pw", loginPw);
				editor.commit();
				Intent intent = new Intent(SplashActivity.this, LobbyActivity.class);
				startActivity(intent);
				finish();
			}
			else{
				Toast.makeText(SplashActivity.this,"로그인 실패!", Toast.LENGTH_LONG).show();
			}		
		}
	};
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		btn = (Button)findViewById(R.id.login);
		btn.setOnClickListener((android.view.View.OnClickListener)this);
		btn = (Button)findViewById(R.id.join);
		btn.setOnClickListener((android.view.View.OnClickListener)this);
		btn = (Button)findViewById(R.id.watch);
		btn.setOnClickListener((android.view.View.OnClickListener)this);
//		idet = (EditText)findViewById(R.id.edit_forid);
//		pwet = (EditText)findViewById(R.id.edit_forpw);
		//로딩이 끝나면 화면 이동
	}
	public void onClick(View view){
		if(view.getId()==R.id.login){
			Context mContext = SplashActivity.this;
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.login_dialog, (ViewGroup)findViewById(R.id.layout_root));
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
			aDialog.setView(layout);	
			aDialog.setTitle("로그인");
			aDialog.setPositiveButton("로그인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					db = DBconnection.getInstance();
					//idet = (EditText)findViewById(R.id.edit_forid);
					idet = (EditText)layout.findViewById(R.id.edit_forid);
					pwet = (EditText)layout.findViewById(R.id.edit_forpw);
					loginId = idet.getText().toString();
					loginPw = pwet.getText().toString();

					if(loginId!=null && loginPw!= null){
						pDialog = ProgressDialog.show(context,"","로그인중");
						db.login(loginId, loginPw,loginHandler);
					}
					else{
						if(loginId == null){
						Toast.makeText(SplashActivity.this,"아이디를 입력 해주세요",Toast.LENGTH_LONG).show();
						}
						else if(loginPw == null){
							Toast.makeText(SplashActivity.this, "비밀번호를 입력 해주세요", Toast.LENGTH_LONG).show();
						}
					}
				}
			});
			aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			aDialog.show();
			//Intent intent = new Intent(SplashActivity.this,LobbyActivity.class);
			//startActivity(intent);
		}
		if(view.getId()==R.id.join){
			Context mContext = SplashActivity.this;
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.join_dialog, (ViewGroup)findViewById(R.id.layout_root2));
			AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
			aDialog.setView(layout);	
			aDialog.setTitle("회원가입");
			aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					db = DBconnection.getInstance();
					//idet = (EditText)findViewById(R.id.edit_forid);
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
					if(id!=null && pw != null && pw2 != null && name !=null){
						if(!(pw.equals(pw2))){
							Toast.makeText(SplashActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
						}
						else{
							pDialog = ProgressDialog.show(context,"","회원가입 중");
							db.join(id, pw,name,joinHandler);
						}
					}
					else{
						if(id == null){
						Toast.makeText(SplashActivity.this,"아이디를 입력 해주세요",Toast.LENGTH_LONG).show();
						}
						else if(pw == null){
							Toast.makeText(SplashActivity.this, "비밀번호를 입력 해주세요", Toast.LENGTH_LONG).show();
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
		if(view.getId()==R.id.watch){
			SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
			if(prefs.getString("id", "")==null){
				Intent intent = new Intent(SplashActivity.this, LobbyActivity.class);
				startActivity(intent);
			}
			else{
				Toast.makeText(SplashActivity.this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
