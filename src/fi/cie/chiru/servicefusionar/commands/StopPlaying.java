package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class StopPlaying extends Command
{
	private ServiceManager serviceManager;
	private String serviceApplicationName;
	
    public StopPlaying(ServiceManager serviceManager, String serviceApplicationName)
    {
    	this.serviceManager = serviceManager;
    	this.serviceApplicationName = serviceApplicationName;
    }
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMusicManager().StopPlaying(serviceApplicationName);
		return true;
	}

}