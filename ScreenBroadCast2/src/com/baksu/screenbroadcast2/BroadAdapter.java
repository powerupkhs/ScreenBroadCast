package com.baksu.screenbroadcast2;

import imageLoader.ImageLoader;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BroadAdapter extends ArrayAdapter<Room>{
	static boolean isDownBmp = false;
	private ArrayList<Room> itemList;
	private Context context;
	private int rowResourceId;
	int m_nCount = 0;
	int m_nLayout = 0;
	private int position;
	public ImageLoader imageLoader; 
	private ProgressDialog pDialog;
	private String roomNum,id;
	private TextView goodNum;
	private Room item;
	private int temp;
	private Handler handler;
	private ImageView image;
	private Bitmap bm;
	private final ImageDownloader imageDownloader = new ImageDownloader();
	private final Handler checkRecoemmendHandler = new Handler(){
		public void handleMessage(Message msg){
			Log.i("checkRecommend",DBconnection.getInstance().getResponse());
			if(DBconnection.getInstance().getResponse().equals("false")){
				DBconnection.getInstance().recommend(roomNum, id, recommendHandler);
			}
			else{
				pDialog.dismiss();
				Toast.makeText(context, "이미 추천 하셨습니다", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private final Handler recommendHandler = new Handler(){
		public void handleMessage(Message msg){
				Log.i("reco","추천 상승");

				Log.i("reco",temp+"");
				//goodNum.setText(temp+"");
				pDialog.dismiss();
				handler.sendEmptyMessage(0);
		}
	};
	
	public BroadAdapter(Activity context, int textViewResoureId, ArrayList<Room> itemList, Handler handler){
		super(context, textViewResoureId, itemList);
		this.itemList = itemList;
		this.context = context;
		imageLoader=new ImageLoader(context.getApplicationContext());
		this.rowResourceId = textViewResoureId;
		this.handler = handler;
	}
	
	public boolean setCount(int page, int Lp){
		if(page != 0 && Lp != 0){
			m_nCount = page * Lp;
			return true;
		}
		else{
			return false;
		}
	}
	
	public int getCount(){
		return m_nCount;
	}
	public View getView(int position2, View convertView, ViewGroup parent){
		View v = convertView;
		this.position = position2;
		
		if(position>m_nCount){
			return v;
		}
		if(v==null){
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.rowResourceId, null);
		}
		item = itemList.get(position);
		ImageButton btn;
		if(item!=null){
			btn = (ImageButton)v.findViewById(R.id.good_icon);
			btn.setTag(position);
			TextView roomName = (TextView)v.findViewById(R.id.room_name);
			TextView bjId = (TextView)v.findViewById(R.id.bj_id);
			goodNum = (TextView)v.findViewById(R.id.good_num);
			image = (ImageView)v.findViewById(R.id.screenShot);
			if(roomName != null){
				roomName.setText(item.getRoomName());
			}
			if(bjId != null){
				bjId.setText(item.getBjId());
			}
			if(goodNum != null){
				goodNum.setText(item.getGood()+"");
			}
			bm = LobbyActivity.imageMap.get(item.getRoomNum()+"");
			image.setImageBitmap(bm);
			
			
			//String url = "http://211.189.127.226:3000/"+item.getRoomNum()+".jpg";
			//imageDownloader.download(url, (ImageView) image);
			/*
			new Thread(){
				public void run(){
			
			try { 
				URL url = new URL("http://211.189.127.226:3000/"+item.getRoomNum()+".jpg");
				Log.i("imageUp",url.toString());
				URLConnection conn =
				 url.openConnection(); 
				conn.connect(); 
				BufferedInputStream  bis = new BufferedInputStream(conn.getInputStream());
				bm = BitmapFactory.decodeStream(bis); bis.close();
				} catch (IOException e) {
				Log.i("Androes", " " + e); 
				}
			imageHandler.sendEmptyMessage(0);
				}
			}.start();
			*/
			
			
			//String path="http://211.189.127.266:3000/"+item.getRoomNum()+".jpg";
	       // imageLoader.DisplayImage(path, image);
	        
			btn.setOnClickListener(new ImageButton.OnClickListener(){

				@Override
				public void onClick(View arg0) {
					int position3 = (Integer)arg0.getTag();
					// TODO Auto-generated method stub
					pDialog = ProgressDialog.show(context,"","추천처리");
					roomNum = itemList.get(position3).getRoomNum()+"";
					id = MainActivity.id;
					temp = item.getGood()+1;
					DBconnection.getInstance().checkRecommend(itemList.get(position3).getRoomNum()+"", id, checkRecoemmendHandler);
				}
			});
			btn.setFocusable(false);
		}
		return v;
	}
}
