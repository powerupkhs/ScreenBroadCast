package com.baksu.screenbroadcast2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Room {
	private int roomNum;
	private String roomName;
	private boolean isPublic;
	private String password;
	private int good; //추천수
	private String date;
	private String bjId;
	private int seePeople; //시청자수
	
	public Room(){
		
	}
	
	public Room(String roomName,  int good, String bjId, int seePeople){
		this.roomName = roomName;
		this.bjId = bjId;
		this.good = good;
		this.seePeople = seePeople;
	}
	public Room(String roomName, boolean isPublic, String password, String bjid){
		this.roomName = roomName;
		this.isPublic = isPublic;
		this.password = password;
		this.bjId = bjid;
		Date today = new Date();
		SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
		this.date = date2.format(today);
	}
	public Room(int roomNum, String roomName, boolean isPublic, String password, int good, String date, String bjId){
		this.roomNum = roomNum;
		this.roomName = roomName;
		this.isPublic = isPublic;
		this.password = password;
		this.good = good;
		this.date = date;
		this.bjId = bjId;
		this.seePeople = 0;	
	}
	public Room(int roomNum, String roomName, boolean isPublic, String password, String date, String bjId){
		this.roomNum = roomNum;
		this.roomName = roomName;
		this.isPublic = isPublic;
		this.password = password;
		this.good = 0;
		this.date = date;
		this.bjId = bjId;
		this.seePeople = 0;	
	}
	public Room(int roomNum, String roomName,boolean isPublic, String password, int good, String date, String bjId, int seePeople){
		this.roomNum = roomNum;
		this.roomName = roomName;
		this.isPublic = isPublic;
		this.password = password;
		this.good = good;
		this.date = date;
		this.bjId = bjId;
		this.seePeople = seePeople;	
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getBjId() {
		return bjId;
	}

	public void setBjId(String bjId) {
		this.bjId = bjId;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public int getSeePeople() {
		return seePeople;
	}

	public void setSeePeople(int seePeople) {
		this.seePeople = seePeople;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
