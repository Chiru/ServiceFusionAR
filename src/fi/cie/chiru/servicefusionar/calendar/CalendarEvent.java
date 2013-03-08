package fi.cie.chiru.servicefusionar.calendar;

public class CalendarEvent 
{

	String time;
	String event;
	
	public CalendarEvent(String time, String event)
	{
		this.time = time;
		this.event = event;
	}

	public String getTime() 
	{
		return time;
	}

	public void setTime(String time) 
	{
		this.time = time;
	}

	public String getEvent() 
	{
		return event;
	}

	public void setEvent(String event) 
	{
		this.event = event;
	}
	
}
