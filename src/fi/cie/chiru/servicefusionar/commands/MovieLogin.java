package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class MovieLogin extends Command
{
	ServiceManager serviceManager;
	
    public MovieLogin(ServiceManager serviceManager)
    {
    	this.serviceManager = serviceManager;
    }
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMovieManager().login();
		return true;
	}

}
