package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class CommandFactory 
{
    public static Command createCommand(String commandName, ServiceManager serviceManager, String serviceApplicationName)
    {
    	if(commandName.compareTo("MovieLogin")==0)
    		return new MovieLogin(serviceManager, serviceApplicationName);

    	else if(commandName.compareTo("ShowMovieInfo")==0)
    		return new ShowMovieInfo(serviceManager, serviceApplicationName);
    	
    	else
    	    return null;
    }
}
