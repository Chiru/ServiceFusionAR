/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.Vector;

import android.location.Location;
import android.util.Log;

import fi.cie.chiru.servicefusionar.ServiceFusionSetup;
import fi.cie.chiru.servicefusionar.calendar.ServiceFusionCalendar;
import fi.cie.chiru.servicefusionar.sensors.Sensors;

public class ServiceManager 
{
	private static final String LOG_TAG = "ServiceManager";
	private ServiceFusionSetup setup;
	private Vector<ServiceApplication> serviceApplications;
	private Vector<InfoBubble> infobubbles;
	private MovieManager movieManager;
	private MusicManager musicManager;
	private ServiceFusionCalendar calendar;
	private Sensors sensors = null;
	
	private Location previousLocation = null;

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
		sensors = new Sensors(this);
		
		SceneParser parser = new SceneParser();
		if (parser.parseFile(this, "serviceFusion.txt"))
		{
			serviceApplications = parser.getApplications();
			infobubbles = parser.getInfobubbles();
		}
		movieManager = new MovieManager(this);
		musicManager = new MusicManager(this);
		calendar = new ServiceFusionCalendar(this);
		
	}
	
	/**
	 * 
	 * @param appName Application name
	 * @return ServiceApplication if it is found, otherwise return null.
	 */
	public ServiceApplication getApplication(String appName)
	{
		// Check that serviceApplications have already been created and if not return null.
		if (serviceApplications == null)
			return null;
		
		ServiceApplication serviceApp = null;
		
		for(int i=0; i<serviceApplications.size(); i++)
		{
		    serviceApp = serviceApplications.elementAt(i);
		    
			if(serviceApp.getName().equals(appName))
		        return serviceApp;
		}
		return serviceApp;
	}
	
	/**
	 * 
	 * @param name InfoBubble name
	 * @return InfoBubble if it is found, otherwise return null.
	 */
	public InfoBubble getInfobubble(String name)
	{
		// Check that infobubbles have already been created and if not return null.		
		if (infobubbles == null)
			return null;
		
		InfoBubble infobubble = null;
		
		for(int i=0; i < infobubbles.size(); i++)
		{
			infobubble = infobubbles.elementAt(i);
		    
			if(infobubble.getName().equals(name))
		        return infobubble;
		}
		return infobubble;
	}
	
	public void setVisibilityToAllApplications(boolean visible)
	{
		for(int i=0; i<serviceApplications.size(); i++)
		{
			serviceApplications.elementAt(i).setvisible(visible);
		}

		calendar.visible(visible);
	}
	
	public void geoLocationEstablished(Location location)
	{
		if (previousLocation == null)
		{
			for(int i=0; i<serviceApplications.size(); i++)
			{
				serviceApplications.elementAt(i).servicePlaceFromLocation(location);
			}
			for (int i = 0; i < infobubbles.size(); i++)
			{
				Log.i(LOG_TAG, "Settin infobubble locations");
				infobubbles.elementAt(i).servicePlaceFromLocation(location);
			}
			previousLocation = new Location("PreviousGpsLocation");
			previousLocation.set(location);
		}
		else
		{
			float distance = location.distanceTo(previousLocation);
			//Log.i(LOG_TAG, "DistanceTo: " + distance);
			if (distance > 20)
			{
				Log.i(LOG_TAG,  "Setting new positions for services, distance was: " + distance);
				for(int i=0; i<serviceApplications.size(); i++)
				{
					serviceApplications.elementAt(i).servicePlaceFromLocation(location);
				}
				for (int i = 0; i < infobubbles.size(); i++)
				{
					Log.i(LOG_TAG, "Settin infobubble locations");
					infobubbles.elementAt(i).servicePlaceFromLocation(location);
				}
				previousLocation.set(location);
			}
		}
		
	}
	
	public void onDestroy()
	{
		musicManager.onDestroy();
		sensors.onDestroy();
	}
}
