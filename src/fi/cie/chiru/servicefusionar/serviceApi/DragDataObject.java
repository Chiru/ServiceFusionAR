/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.serviceApi;

public class DragDataObject 
{
	private String data;
	private String manager;
	
	public DragDataObject(String data, String manager)
	{
	    this.data = data;	
		this.manager = manager;
	}
	
	public String getData() 
	{
		return data;
	}
	
	public void setData(String data) 
	{
		this.data = data;
	}
	
	public String getManager() 
	{
		return manager;
	}
	
	public void setManager(String manager) 
	{
		this.manager = manager;
	}
	
	
}
