package com.kidgeniusdesigns.deployapp.fragments;


public class Events {

	@com.google.gson.annotations.SerializedName("eventtitle")
	private String eventTitle;
	
	@com.google.gson.annotations.SerializedName("eventcode")
	private String eventCode;
	
	@com.google.gson.annotations.SerializedName("id")
	private String mId;
	
	@com.google.gson.annotations.SerializedName("complete")
	private boolean mComplete;
	
	@com.google.gson.annotations.SerializedName("location")
	private String loc;
	
	@com.google.gson.annotations.SerializedName("eventtime")
	private double eventTime;
	
	@com.google.gson.annotations.SerializedName("ownerid")
	private String ownerId;
	
	@com.google.gson.annotations.SerializedName("description")
	private String descrip;
	
	public Events() {

	}

	@Override
	public String toString() {
		return getTitle();
	}

	public Events(String eventTitle, String eventCode) {
		this.setTitle(eventTitle);
		this.setEventCode(eventCode);
		this.mId=eventCode.hashCode()+eventCode;
	}

	public String getTitle() {
		return eventTitle;
	}

	public final void setTitle(String text) {
		eventTitle = text;
	}
	public String getOwnerId() {
		return ownerId;
	}

	public final void setOwnerId(String text) {
		ownerId = text;
	}
	public String getLocation() {
		return loc;
	}

	public final void setLocation(String text) {
		loc = text;
	}

	public String getEventCode() {
		return eventCode;
	}

	public final void setEventCode(String code) {
		eventCode = code;
	}
	
	public String getId() {
		return mId;
	}

	public final void setId(String id) {
		mId = id;
	}
	public String getDescrip() {
		return descrip;
	}

	public final void setDescrip(String id) {
		descrip = id;
	}
	
	public double getTime(){
		return eventTime;
	}
	public final void setTime(double millis) {
		eventTime = millis;
	}

	public boolean isComplete() {
		return mComplete;
	}

	public void setComplete(boolean complete) {
		mComplete = complete;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Events && ((Events) o).eventCode == eventCode;
	}
}
