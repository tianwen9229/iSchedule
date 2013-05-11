package com.android.iSchedule;

import android.R.integer;

public class Mode {
	
	private int modeId;
	private int volume;
	private int vibrate;
	// test branches
	// To Do 
	// Setter and Getter
	public Mode()
	{
	}
	
	public Mode(int id, int vo, int vi)
	{
	modeId =id;
	volume = vo;
	vibrate = vi;
	}
	public int getModeId()
	{
		return modeId;
	}
	public int getVolume()
	{
	return volume;
	}
	public int getVibrate()
	{
		return vibrate;
	}
    public void setModeId(int id)
    {
    	this.modeId = id;
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
