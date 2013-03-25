package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.Vector;

import android.content.Intent;

import commands.Command;

import util.Log;
import util.Vec;
import worldData.World;

import fi.cie.chiru.servicefusionar.ServiceFusionSetup;
import fi.cie.chiru.servicefusionar.calendar.ServiceFusionCalendar;
import fi.cie.chiru.servicefusionar.sensors.Sensors;

public class ServiceManager 
{
	private static final String LOG_TAG = "ServiceManager";
	private ServiceFusionSetup setup;
	private Vector<ServiceApplication> serviceApplications;
	private MovieManager movieManager;
	private MusicManager musicManager;
	private ServiceFusionCalendar calendar;
	private Sensors sensors;

	public ServiceManager(ServiceFusionSetup setup)
	{
		this.setup = setup;
		serviceApplications = new Vector<ServiceApplication>();
	}
	
	public ServiceFusionSetup getSetup()
	{
		return setup;
	}
	
	public MovieManager getMovieManager()
	{
		return movieManager;
	}
	
	public ServiceFusionCalendar getCalendar()
	{
		return calendar;
	}
	
	public MusicManager getMusicManager()
	{
		return musicManager;
	}
	
	public Sensors getSensors()
	{
		return sensors;
	}
	
	public void createApplications()
	{
		SceneParser parser = new SceneParser();
		serviceApplications = parser.parseFile(this, "serviceFusion.txt");
		movieManager = new MovieManager(this);
		musicManager = new MusicManager(this);
		calendar = new ServiceFusionCalendar(this);
		
		sensors = new Sensors(this);
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
			if(serviceApplications.elementAt(i).getName().contains("Infobubble"))
				continue;
			
			serviceApplications.elementAt(i).setvisible(visible);
		}
		
		calendar.visible(visible);
	
	}
	
	public void onDestroy()
	{
		musicManager.onDestroy();
		sensors.onDestroy();
	}
}
