package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class CommandFactory 
{
    public static Command createCommand(String commandName, ServiceManager serviceManager)
    {
    	if(commandName.compareTo("MovieLogin")==0)
    		return new MovieLogin(serviceManager);

    	else if(commandName.compareTo("ShowMovieInfo")==0)
    		return new ShowMovieInfo(serviceManager);
    	
    	else
    	    return null;
    }
}
