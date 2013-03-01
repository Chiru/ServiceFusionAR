package fi.cie.chiru.servicefusionar.commands;

import commands.Command;

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
    	song = (String)transfairObject;
		return execute();
	}
    
	@Override
	public boolean execute() 
	{
		serviceManager.getMusicManager().playSong(song);
		return true;
	}
}
