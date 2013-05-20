package com.android.iSchedule;

public class Mode {
	
	private long modeId;
	private String name;
	private int volume;
	private int vibrate;
	// test branches
	// To Do 
	// Setter and Getter
	
	public Mode(String na, int vo, int vi)
	{
		modeId = -1;
		name = na;
		volume = vo;
		vibrate = vi;
	}
	public long getModeId()
	{
		return modeId;
	}
	public String getName(){
		return name;
	}
	public int getVolume()
	{
		return volume;
	}
	public int getVibrate()
	{
		return vibrate;
	}
    public void setModeId(long id)
    {
    	this.modeId = id;
    }
    public void setName(String name){
    	this.name = name;
    }
	public void setVolume(int volume)
	{
		this.volume = volume;
	}
	public void setVibrate(int vibrate)
	{
		this.vibrate = vibrate;
	}
}
