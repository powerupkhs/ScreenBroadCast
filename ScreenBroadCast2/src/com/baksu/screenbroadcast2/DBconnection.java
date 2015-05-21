package com.baksu.screenbroadcast2;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;


public class DBconnection {
	private HttpEntity entity =null;
	private HttpResponse resp;
	private HttpClient httpclient;
	private HttpPost httpPost;
	private String id, password, name; //id는 로그인 할때만 쓰기
	private Handler handler;
	private OutputStream os = null;
	private InputStream is = null;
	private ByteArrayOutputStream baos = null;
	private String response, str, roomName;
	private Room room;
	private int num;
	private String regId;
	private ArrayList<Room> roomList = new ArrayList<Room>();
	private static StringBuilder jsonHtml = new StringBuilder();
	private ArrayList<NameValuePair> httpParms=new ArrayList<NameValuePair>();
	private static DBconnection instance = new DBconnection();
	private DBconnection(){
		this.httpclient = new DefaultHttpClient();
	}
	public void login(String id2,String password2, Handler handler2){
		this.id = id2;
		this.password = password2;
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appLogin";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("id",id));
				   httpParms.add(new BasicNameValuePair("password",password));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   Log.i("login","80");
				   if(entity != null){
					   is = entity.getContent();
					   Log.i("login","83");
					   baos = new ByteArrayOutputStream(); 
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("result",response);
					    //JSONObject responseJSON = new JSONObject(response);
					     
					    
					   // Log.i("result", "DATA response = " + response);
				   }
				}
				catch(IOException ie){
					Log.i("result","ioexception");
				}
				catch(Exception ex){
					Log.e("result",ex.getMessage());
				}
				Log.i("result","DBconnection 119");
				//return jsonHtml.toString();
				//is.close();
				//os.close();
				handler.sendEmptyMessage(0);
				}
			}.start();
		}
	
	public void join(String id2, String password2, String name2, Handler handler2){
		this.id = id2;
		this.password = password2;
		this.name = name2;
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appJoin";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("id",id));
				   httpParms.add(new BasicNameValuePair("password",password));
				   httpParms.add(new BasicNameValuePair("name",name));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("result",response);
				   }
				}
				catch(IOException ie){
					Log.i("result","ioexception");
				}
				//catch(JSONException je){
				//	Log.e("result","jsonException");
				//}
				
				catch(Exception ex){
					Log.e("result","error");
				}
				Log.i("result","DBconnection 119");
				//return jsonHtml.toString();
				//is.close();
				//os.close();
				//httpclient.getConnectionManager().shutdown();
				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void getRoom(Handler handler2){
		roomList.clear();
		this.handler = handler2;
		new Thread(){
			public void run(){
		HttpURLConnection   conn    = null;
		 
		OutputStream          os   = null;
		InputStream           is   = null;
		ByteArrayOutputStream baos = null;
		try{
		URL url = new URL("http://211.189.127.226:3000/appRoomList");
		//URLDecoder.decode()
		conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Cache-Control", "no-cache");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		JSONObject job = new JSONObject();
		os = conn.getOutputStream();
		os.write(job.toString().getBytes());
		os.flush();
		String response;
		 
		int responseCode = conn.getResponseCode();
		if(responseCode == HttpURLConnection.HTTP_OK) {
			Log.i("connection","DBconnection 243");
		    is = conn.getInputStream();
		    baos = new ByteArrayOutputStream();
		    byte[] byteBuffer = new byte[1024];
		    byte[] byteData = null;
		    int nLength = 0;
		    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
		        baos.write(byteBuffer, 0, nLength);
		    }
		    byteData = baos.toByteArray();
		     
		    response = new String(byteData);
		    String response2 = response.replace("&quot;","\"");
		    Log.i("connection","result"+response2);
		    Log.i("connection","DBconnection 255");
		    JSONArray jArr = new JSONArray(response2);
		    
		   
		    Log.i("json",jArr.length()+"");
		    
		    for(int i=0; i<jArr.length(); i++){
		    	JSONObject jsonobj = new JSONObject(jArr.get(i).toString());
		    	int num = Integer.parseInt(jsonobj.getString("number"));
		    	Log.i("roomNum","갖고오는 방넘버"+ num);
		    	String roomName = jsonobj.getString("name");
		    	boolean isPublic;
		    	if((jsonobj.getString("is_public")).equals("1")){
		    		isPublic = true;
		    	}
		    	else{
		    		isPublic = false;
		    	}
		    	String password = jsonobj.getString("password");
		    	int good = Integer.parseInt(jsonobj.getString("recommend"));
		    	Log.i("good",good+" ");
		    	String date = jsonobj.getString("date");
		    	String bjId = jsonobj.getString("id");
		    	Log.i("roomNum","add되는 num : "+num);
		    	roomList.add(new Room(num,roomName,isPublic,password,good,date,bjId));
		    }
		    
		    JSONObject jsonObj = new JSONObject(jArr.get(1).toString());
		    Log.i("json","방만든이 : " + jsonObj.getString("id"));
		    //JSONObject responseJSON = new JSONObject(response2);
		    
		    //String number = (String)responseJSON.get("number");
		    //Log.i("json",number);
		    //Boolean result = (Boolean) responseJSON.get("result"); 
		    //String age = (String) responseJSON.get("age");
		    //String job2 = (String) responseJSON.get("job");
		    
		   // Log.i("connection", "DATA response 2= " + response);
			}
		}
		catch(IOException ie){
			Log.i("result","ioexception");
		}
		catch(JSONException je){
			Log.i("reuslt","json error");
		}
		catch(Exception ex){
			Log.e("result","error");
		}
		conn.disconnect();
			handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	public void createRoom(Room room2,Handler handler2){
		this.room = room2;
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appMakeRoom";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("name",room.getRoomName()));
				   if(room.isPublic()==true){
				   httpParms.add(new BasicNameValuePair("isPublic","1"));
				   }
				   else{
					   httpParms.add(new BasicNameValuePair("isPublic","0"));
				   }
				   httpParms.add(new BasicNameValuePair("date",room.getDate()));
				   httpParms.add(new BasicNameValuePair("id", room.getBjId()));
				   httpParms.add(new BasicNameValuePair("password", room.getPassword()));
				  
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("result",response);
				   }
				}
				catch(IOException ie){
					Log.i("result","ioexception");
				}
				//catch(JSONException je){
				//	Log.e("result","jsonException");
				//}
				
				catch(Exception ex){
					Log.e("result","error");
				}
				Log.i("result","DBconnection 119");
				//return jsonHtml.toString();
				//is.close();
				//os.close();
				//httpclient.getConnectionManager().shutdown();
				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void search(String search, Handler handler2){
		
		this.handler = handler2;
		this.str = search;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appSearchRoom";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("keyword",str));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    response = response.replace("&quot;","\"");
					    Log.i("result",response);
					    
					    if(!(response.equals("false"))){
					   
					    
					    roomList.clear();
					    JSONArray jArr = new JSONArray(response);
					    
						   
					    Log.i("json",jArr.length()+"");
					    
					    for(int i=0; i<jArr.length(); i++){
					    	JSONObject jsonobj = new JSONObject(jArr.get(i).toString());
					    	int num = Integer.parseInt(jsonobj.getString("number"));
					    	String roomName = jsonobj.getString("name");
					    	boolean isPublic;
					    	if((jsonobj.getString("is_public")).equals("1")){
					    		isPublic = true;
					    	}
					    	else{
					    		isPublic = false;
					    	}
					    	String password = jsonobj.getString("password");
					    	int good = Integer.parseInt(jsonobj.getString("recommend"));
					    	String date = jsonobj.getString("date");
					    	String bjId = jsonobj.getString("id");
					    	roomList.add(new Room(num,roomName,isPublic,password,good,date,bjId));
					    }
					    
					    JSONObject jsonObj = new JSONObject(jArr.get(1).toString());
					    Log.i("json","방만든이 : " + jsonObj.getString("id"));
					    //JSONObject responseJSON = new JSONObject(response2);
					    
					    //String number = (String)responseJSON.get("number");
					    //Log.i("json",number);
					    //Boolean result = (Boolean) responseJSON.get("result"); 
					    //String age = (String) responseJSON.get("age");
					    //String job2 = (String) responseJSON.get("job");
					    
					   // Log.i("connection", "DATA response 2= " + response);
					    }
				   }
				}
				catch(IOException ie){
					Log.i("result","ioexception");
				}
				
				catch(Exception ex){
					Log.e("result","error");
				}
				Log.i("result","DBconnection 119");
				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void roomDelete(String roomNum, Handler handler2){
		this.str = roomNum;
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appDeleteRoom";
				   httpParms.clear();
				   Log.i("exit",str);
				   httpParms.add(new BasicNameValuePair("number",str));
				  
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("exit",response);
				   }
				}
				catch(IOException ie){
					Log.i("exit","ioexception");
				}
				catch(Exception ex){
					Log.e("exit","error");
				}

				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void checkRecommend(String roomNum, String id2, Handler handler2){
		this.str = roomNum;
		this.id = id2;
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appCheckRecommend";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("id",id));
				   httpParms.add(new BasicNameValuePair("number",str));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream(); 
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("checkRecommend",response);
					    //JSONObject responseJSON = new JSONObject(response);
					     
					   // Log.i("result", "DATA response = " + response);
				   }
				}
				catch(IOException ie){
					Log.i("checkRecommend","ioexception");
				}
				catch(Exception ex){
					Log.e("checkRecommend",ex.getMessage());
				}
				handler.sendEmptyMessage(0);
				}
			}.start();
	
	}
	
	public void recommend(String roomNum, String id2,Handler handler2){
		this.str = roomNum;
		this.id = id2;
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appIncreaseRecommend";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("id",id));
				   httpParms.add(new BasicNameValuePair("number",str));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream(); 
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("recommend",response);
				   }
				}
				catch(IOException ie){
					Log.i("recommend","ioexception");
				}
				catch(Exception ex){
					Log.e("recommend",ex.getMessage());
				}
				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void bookmarkReg(String bjId, String userId, Handler handler2){
		this.str = bjId;
		this.id = userId;
		Log.i("bookmarkReg","북마크 대상 : " + str);
		Log.i("bookmarkReg","사용자 : " +id);
		this.handler = handler2;
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appInsertFavorites";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("BjId",str));
				   httpParms.add(new BasicNameValuePair("UserId",id));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream(); 
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("bookmarkReg",response);
				   }
				}
				catch(IOException ie){
					Log.i("bookmarkReg","ioexception");
				}
				catch(Exception ex){
					Log.e("bookmarkReg",ex.getMessage());
				}
				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void myBookmark(String userId, Handler handler2){
		
		this.handler = handler2;
		this.str = userId;
		Log.i("bookmark","현재 회원 아이디 : " +str);
		
		new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/appFavoritesList";
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("UserId",str));
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    if(!(response.equals("[]"))){
					    	roomList.clear();
					    	response = response.replace("&quot;","\"");
					    	Log.i("result",response);
					    	JSONArray jArr = new JSONArray(response);
					    
						   
					    	Log.i("json",jArr.length()+"");
					    
					    	for(int i=0; i<jArr.length(); i++){
					    		JSONObject jsonobj = new JSONObject(jArr.get(i).toString());
					    		int num = Integer.parseInt(jsonobj.getString("number"));
					    		String roomName = jsonobj.getString("name");
					    		boolean isPublic;
					    		if((jsonobj.getString("is_public")).equals("1")){
					    		isPublic = true;
					    	}
					    	else{
					    		isPublic = false;
					    	}
					    	String password = jsonobj.getString("password");
					    	int good = Integer.parseInt(jsonobj.getString("recommend"));
					    	String date = jsonobj.getString("date");
					    	String bjId = jsonobj.getString("id");
					    	roomList.add(new Room(num,roomName,isPublic,password,good,date,bjId));
					    }
					    JSONObject jsonObj = new JSONObject(jArr.get(1).toString());
					    Log.i("json","방만든이 : " + jsonObj.getString("id"));
					    Log.i("connection", "DATA response 2= " + response);
					    }
				   }
				}
				catch(IOException ie){
					Log.i("result","ioexception");
				}
				
				catch(Exception ex){
					Log.e("result","error");
				}
				Log.i("result","DBconnection 119");
				handler.sendEmptyMessage(0);
				}
			}.start();
	}
	
	public void registerRegid(String regId2, Handler handler2){
		this.regId = regId2; 
        this.handler = handler2;
        new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/register";
					 Log.i("Regid","ggg");
					 Log.i("push", "현재 등록하는 회원의 아이디 : " + id);
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("regId",regId));
				   httpParms.add(new BasicNameValuePair("userId",id));
					  
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("Regid",response);
				   }
				}
				catch(IOException ie){
					Log.i("Regid","ioexception");
				}
				catch(Exception ex){
					Log.e("exit","error");
				}

				handler.sendEmptyMessage(0);
				}
			}.start();
        
	}
	
	public void sendpush(String id, String roomName2, Handler handler2){
		this.str = id; 
        this.roomName = roomName2;
        new Thread(){
			public void run(){
				try{
					String url2 = "http://211.189.127.226:3000/send";
					 Log.i("Regid","ggg");
					 //Log.i("push", "현재 등록하는 회원의 아이디 : " + id);
				   httpParms.clear();
				   httpParms.add(new BasicNameValuePair("id",str));
				   httpParms.add(new BasicNameValuePair("title",roomName));
					  
				   httpPost = new HttpPost(url2);
				   UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(httpParms,"UTF-8");
				   httpPost.setEntity(entityRequest);
				   HttpResponse response2 = httpclient.execute(httpPost);
				   if(response2.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					   entity = response2.getEntity();
				   }
				   if(entity != null){
					   is = entity.getContent();
					   baos = new ByteArrayOutputStream();
					    byte[] byteBuffer = new byte[1024];
					    byte[] byteData = null;
					    int nLength = 0;
					    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
					        baos.write(byteBuffer, 0, nLength);
					    }
					    byteData = baos.toByteArray();
					    response = new String(byteData);
					    Log.i("Regid",response);
				   }
				}
				catch(IOException ie){
					Log.i("Regid","ioexception");
				}
				catch(Exception ex){
					Log.e("exit","error");
				}

				handler.sendEmptyMessage(0);
				}
			}.start();
        
	}
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public static DBconnection getInstance() {
		return instance;
	}
	public static void setInstance(DBconnection instance) {
		DBconnection.instance = instance;
	}
	public HttpClient getHttpclient() {
		return httpclient;
	}
	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}
	public ArrayList<Room> getRoomList() {
		return roomList;
	}
	public void setRoomList(ArrayList<Room> roomList) {
		this.roomList = roomList;
	}
}
