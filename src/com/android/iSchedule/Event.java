package com.android.iSchedule;

import java.sql.Date;

import android.R.integer;
import android.R.string;
import android.text.format.Time;

public class Event {
	
	private long EventId;
	private String EventTitle;
	private String Place;
	private String content;
	
	private Date createTime;
	private Date remindTime;
	private Date startTime;
	private Date endTime;
	
	// To Do 
	// Setter and Getter
	public Event( String title , String place, String con, Date cT, Date rT, Date sT, Date eT)
	{
		EventId = -1;
		EventTitle = title;
		content = con;
		Place = place;
		createTime = cT;
		remindTime = rT;
		startTime = sT;
		endTime = eT;
	}
	
	public long getEventId()
	{
		return this.EventId;
	}
	public String getTitle()
	{
		return this.EventTitle;
	}
	public String getPlace() 
	{
		return this.Place;
	}
	public String getContent() 
	{
		return this.content;
	}
	public Date getCreatTime()
	{
		return this.createTime;
	}
	public Date getRemindTime() 
	{
		return this.remindTime;
	}
	public Date getStartTime()
	{
		return this.startTime;
	}
	public Date getEndTime() 
	{
		return this.endTime;
	}
	
	public void setEventId(long id) 
	{
		EventId = id;
	}
	public void setTitle(String title)
	{
		this.EventTitle = title;
	}
	public void setPlace(String place)
	{
		this.Place = place;
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
