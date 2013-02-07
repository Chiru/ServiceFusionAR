package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.Vector;

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

	public ServiceManager(ServiceFusionSetup setup)
	{
		this.setup = setup;
		serviceApplications = new Vector<ServiceApplication>();
		movieManager = new MovieManager();
	}
	
	public ServiceFusionSetup getSetup()
	{
		return setup;
	}
	
	public MovieManager getMovieManager()
	{
		return movieManager;
	}
	
	public void createApplications()
	{
		SceneParser parser = new SceneParser();
		serviceApplications = parser.parseFile(this, "serviceFusion.txt");
		addApplicationsToDroidAR(setup.world);
	}
	
	public ServiceApplication getApplication(String appName)
	{
		ServiceApplication serviceApp = null;
		
		for(int i=0; i<serviceApplications.size(); i++)
		{
		    serviceApp = serviceApplications.elementAt(i);
		    
			if(serviceApplications.elementAt(i).getName() == appName)
		        return serviceApp;
			else
				serviceApp = null;
		}
		return serviceApp;
	}
	
	private void addApplicationsToDroidAR(final World world)
	{
		ServiceApplication serviceApp; 
		
		for(int i=0; i<serviceApplications.size(); i++)
		{
			serviceApp = serviceApplications.elementAt(i);
			world.add(serviceApp);
		}
	}	
}
