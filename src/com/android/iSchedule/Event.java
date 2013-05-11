package com.android.iSchedule;

import java.sql.Date;

import android.R.integer;
import android.R.string;
import android.text.format.Time;

public class Event {
	
	private int EventId;
	private String EventTitle;
	private String Place;
	private int frequency;
	private String content;
	
	private Date createTime;
	private Date remindTime;
	private Date startTime;
	private Date endTime;
	
	// To Do 
	// Setter and Getter
	public Event()
	{}
	
	public Event(int id, String title , String place, int fre, String con, Date cT, Date rT, Date sT, Date eT)
	{
		EventId = id;
		EventTitle = title;
		frequency = fre;
		content = con;
		createTime = cT;
		remindTime = rT;
		startTime = sT;
		endTime = eT;
	}
	
	public int getId()
	{
		return this.EventId;
	}
	public String getTitle() {
		return this.EventTitle;
		}
	public String getPlace() {
		return this.Place;
		}
	public int getFrequency() {
		return this.frequency;
		}
	public String getContent() {
		return this.content;
		}
	public Date getCreatTime() {
		return this.createTime;
		}
	public Date getRemindTime() {
		return this.remindTime;
		}
	public Date getStartTime() {
		return this.createTime;
		}
	public Date getEndTime() {
		return this.endTime;
		}
	
	public void setId(int id)
	{
		this.EventId = id;
	}
	
	public void setTitle(String title)
	{
		this.EventTitle = title;
	}
	public void setPlace(String place)
	{
		this.Place = place;
	}
	public void setFrequency(int frequency){
		this.frequency = frequency;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public void setCreatTime(Date cT)
	{
		this.createTime = cT;
	}
	public void setRemindTime(Date rT)
	{
		this.remindTime = rT;
	}
	public void setStartTime(Date sT)
	{
		this.startTime = sT;
	}
	public void setEndTime(Date eT)
	{
		this.endTime = eT;
	}
	
}