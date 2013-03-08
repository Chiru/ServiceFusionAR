package fi.cie.chiru.servicefusionar.calendar;

import commands.Command;

import fi.cie.chiru.servicefusionar.serviceApi.DragDataObject;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class CalendarAddEvent extends Command
{
	private ServiceManager serviceManager;
    private CalendarEvent ce;
	
    public CalendarAddEvent(ServiceManager serviceManager, CalendarEvent ce)
    {
    	this.ce = ce;
    	this.serviceManager = serviceManager;
    }
    
	@Override
	public boolean execute() 
	{
		serviceManager.getCalendar().addEvent(ce);
		return true;
	}

}
