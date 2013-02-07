package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class ShowMovieInfo extends Command
{
	ServiceManager serviceManager;
	
    public ShowMovieInfo(ServiceManager serviceManager)
    {
    	this.serviceManager = serviceManager;
    }
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMovieManager().showMovieInfo();
		return true;
	}

}
