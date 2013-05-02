/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.commands;

import commands.Command;

import fi.cie.chiru.servicefusionar.serviceApi.DragDataObject;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class PlaySong extends Command
{
	private ServiceManager serviceManager;
	private String serviceApplicationName;
	private String song;
	
    public PlaySong(ServiceManager serviceManager, String serviceApplicationName)
    {
    	this.serviceApplicationName = serviceApplicationName;
    	this.serviceManager = serviceManager;
    }
    
    @Override
    public boolean execute(Object transfairObject) 
    {
    	DragDataObject ddo = (DragDataObject)transfairObject;
    	
    	if(ddo.getManager().compareTo("MusicManager") != 0)
    		return true;
    	
    	song = ddo.getData();
		return execute();
	}
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMusicManager().playSong(song);
		return true;
	}
}
