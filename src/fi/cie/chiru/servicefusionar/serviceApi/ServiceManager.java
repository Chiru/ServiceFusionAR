package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.Vector;

import android.content.Intent;

import commands.Command;

import util.Log;
import util.Vec;
import worldData.World;

import fi.cie.chiru.servicefusionar.ServiceFusionSetup;

public class ServiceManager 
{
	private static final String LOG_TAG = "ServiceManager";
	private ServiceFusionSetup setup;
	private Vector<ServiceApplication> serviceApplications;
	private MovieManager movieManager;
	private MusicManager musicManager;

	public ServiceManager(ServiceFusionSetup setup)
	{
		this.setup = setup;
		serviceApplications = new Vector<ServiceApplication>();
		movieManager = new MovieManager(this);
		musicManager = new MusicManager(this);
	}
	
	public ServiceFusionSetup getSetup()
	{
		return setup;
	}
	
	public MovieManager getMovieManager()
	{
		return movieManager;
	}
	
	public MusicManager getMusicManager()
	{
		return musicManager;
	}
	
	public void createApplications()
	{
		SceneParser parser = new SceneParser();
		serviceApplications = parser.parseFile(this, "serviceFusion.txt");
	}
	
	public ServiceApplication getApplication(String appName)
	{
		ServiceApplication serviceApp = null;
		
		for(int i=0; i<serviceApplications.size(); i++)
		{
		    serviceApp = serviceApplications.elementAt(i);
		    
			if(serviceApplications.elementAt(i).getName().compareTo(appName)==0)
		        return serviceApp;
			else
				serviceApp = null;
		}
		return serviceApp;
	}
	
	public void setVisibilityToAllApplications(boolean visible)
	{
		for(int i=0; i<serviceApplications.size(); i++)
		{
			serviceApplications.elementAt(i).setvisible(visible);
		}
	
	}
}
