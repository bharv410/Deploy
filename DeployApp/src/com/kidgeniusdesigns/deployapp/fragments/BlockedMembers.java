package com.kidgeniusdesigns.deployapp.fragments;

public class BlockedMembers {
	@com.google.gson.annotations.SerializedName("blockedname")
    private String blockedName;

    @com.google.gson.annotations.SerializedName("eventcode")
    private String eventCode;

    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    
    public BlockedMembers(String username, String eventcode){
    	
    	this.blockedName=username;
    	this.eventCode=eventcode;
    	this.mId=eventCode.hashCode()+username;
    }
    public String getEventCode()
    {
        return eventCode;
    }
    public String getUsername()
    {
        return blockedName;
    }
    public String getId()
    {
        return mId;
    }
    
}
