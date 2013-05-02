/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

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
    	
    	else if(commandName.compareTo("ShowMusicPlaylist")==0)
    		return new ShowMusicPlaylist(serviceManager, serviceApplicationName);
    	
    	else if(commandName.compareTo("RemoveMovieTickets")==0)
    		return new RemoveMovieTickets(serviceManager, serviceApplicationName);
    	
    	else if(commandName.compareTo("PlaySong")==0)
    		return new PlaySong(serviceManager, serviceApplicationName);
    	
    	else if(commandName.compareTo("StopPlaying")==0)
    		return new StopPlaying(serviceManager, serviceApplicationName);
    	
    	else
    	    return null;
    }
}
