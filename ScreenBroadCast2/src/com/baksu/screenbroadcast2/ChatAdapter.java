package com.baksu.screenbroadcast2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ChatAdapter extends ArrayAdapter<Chat>{
	private ArrayList<Chat> itemList;
	private Context context;
	private int rowResourceId;
	int m_nCount = 0;
	int m_nLayout = 0;
	
	public ChatAdapter(Context context, int textViewResoureId, ArrayList<Chat> itemList){
		super(context, textViewResoureId, itemList);
		this.itemList = itemList;
		this.context = context;
		this.rowResourceId = textViewResoureId;
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
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(position>m_nCount){
			return v;
		}
		if(v==null){
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(this.rowResourceId, null);
		}
		Chat item = (Chat)itemList.get(position);
		
		if(item!=null){
			//ImageView image = (ImageView)v.findViewById(R.id.screenShot);
			/*
			TextView roomName = (TextView)v.findViewById(R.id.room_name);
			TextView bjId = (TextView)v.findViewById(R.id.bj_id);
			TextView goodNum = (TextView)v.findViewById(R.id.good_num);
			TextView seeNum = (TextView)v.findViewById(R.id.see_num);
			if(roomName != null){
				roomName.setText(item.getRoomName());
			}
			if(bjId != null){
				bjId.setText(item.getBjId());
			}
			if(goodNum != null){
				goodNum.setText(item.getGood()+"");
			}
			if(seeNum != null){
				seeNum.setText(item.getSeePeople()+"");
			}*/
		}
		return v;
	}
}
