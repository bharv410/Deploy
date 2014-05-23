package com.kidgeniusdesigns.deployapp.fragments;

import java.util.Random;

public class Attendee {

	@com.google.gson.annotations.SerializedName("eventcode")
	private String eventCode;
	
	@com.google.gson.annotations.SerializedName("attendee")
	private String attendee;
	
	@com.google.gson.annotations.SerializedName("id")
	private String mId;
	
	public Attendee(){
		Random gen = new Random();
		mId=gen.nextInt()+"";
	}
	public String getEventCode() {
		return eventCode;
	}

	public final void setEventCode(String code) {
		eventCode = code;
	}
	
	public String getAttendee() {
		return attendee;
	}

	public final void setAttendee(String text) {
		attendee = text;
	}
	
	@Override
	public String toString() {
		return getAttendee();
	}
}
