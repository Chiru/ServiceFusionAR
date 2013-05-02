/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.commands;

import commands.Command;
import fi.cie.chiru.servicefusionar.serviceApi.DragDataObject;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;

public class RemoveMovieTickets extends Command
{
	private ServiceManager serviceManager;
	private String serviceApplicationName;
	
    public RemoveMovieTickets(ServiceManager serviceManager, String serviceApplicationName)
    {
    	this.serviceManager = serviceManager;
    	this.serviceApplicationName = serviceApplicationName;
    }
    
    @Override
    public boolean execute(Object transfairObject) 
    {
    	DragDataObject ddo = (DragDataObject)transfairObject;
    	
    	if(ddo.getManager().compareTo("") != 0)
    		return true;
    	
		return execute();
	}
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMovieManager().removeTickets();
		return true;
	}
}
