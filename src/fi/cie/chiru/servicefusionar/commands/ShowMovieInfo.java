package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class ShowMovieInfo extends Command
{
	private ServiceManager serviceManager;
	private String serviceApplicationName;
	
    public ShowMovieInfo(ServiceManager serviceManager, String serviceApplicationName)
    {
    	this.serviceManager = serviceManager;
    	this.serviceApplicationName = serviceApplicationName;
    }
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMovieManager().showMovieInfo(serviceApplicationName);
		return true;
	}

}
