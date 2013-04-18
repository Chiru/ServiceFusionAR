package fi.cie.chiru.servicefusionar.serviceApi;

import util.IO;
import util.Vec;


import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.cie.chiru.servicefusionar.gdx.GDXLoader;
import fi.cie.chiru.servicefusionar.gdx.GDXMesh;
import fi.cie.chiru.servicefusionar.commands.CommandFactory;

import android.util.Log;

public class SceneParser 
{
	private static final String LOG_TAG = "SceneParser";
	String sceneContent;
	private GDXLoader gdxLoader;
	private ServiceManager serviceManager = null;
	
	Vector<ServiceApplication> serviceApplications = null;
	Vector<InfoBubble> infoBubbles = null;

	public SceneParser()
	{
		gdxLoader = new GDXLoader();
	}
	
	public boolean parseFile(ServiceManager serviceManager, String fileName)
	{
		Log.i(LOG_TAG, "Scene parsing started!");
		this.serviceManager = serviceManager;
		serviceApplications = new Vector<ServiceApplication>();
		infoBubbles = new Vector<InfoBubble>();
		
		InputStream in = null;
		try 
		{
			in = this.serviceManager.getSetup().myTargetActivity.getAssets().open(fileName);
			sceneContent = IO.convertInputStreamToString(in);
		} 
		catch (IOException e) 
		{
			Log.e(LOG_TAG, "Could not read file: " +fileName);
			e.printStackTrace();
		} finally {
			try {
				Log.i(LOG_TAG, "Closing scene input stream");
				in.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, e.toString());
			}
		}
		
		// Create JSONObject from scene file.
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(sceneContent);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Could not create JSONObject from file!");
			e.printStackTrace();
		}
		
		JSONArray applicationsArray = null;
		JSONArray infobubbleArray = null;
		
		// Try to read applications JSONArray
        try {
        	applicationsArray = jsonObj.getJSONArray("ServiceApplication");
        } catch (JSONException e) {
        	Log.e(LOG_TAG, e.toString());
        }
        
        // Try to read info bubbles JSONArray 
        try {
        	infobubbleArray = jsonObj.getJSONArray("InfoBubble");
        } catch (JSONException e) {
        	Log.e(LOG_TAG, e.toString());
        }
        	
        try 
        {
        	for(int i=0; i<applicationsArray.length(); i++)
        	{
        		JSONObject ServiceAppObj = applicationsArray.getJSONObject(i);
        		ServiceApplication serviceApp = createApplication(ServiceAppObj);
        		
        		JSONObject geoLocation = null;
        		try {
        			geoLocation = ServiceAppObj.getJSONObject("geoLocation");
        		} catch (JSONException e){}
        		if (geoLocation != null)
        		{
        			double latitude = 0.0d;
        			double longitude = 0.0d;
        			try {
        				latitude = geoLocation.getDouble("Latitude");
        				longitude = geoLocation.getDouble("Longitude");
        			} catch (JSONException e) {}
        			Log.i(LOG_TAG, "Location: " + geoLocation.toString());
        			serviceApp.setGeoLocation(latitude, longitude);
        		}
        		
        		boolean visible = ServiceAppObj.getBoolean("visible");
        		
        		if(!visible)
        			serviceApp.setvisible(false);
        		
        		boolean attached = ServiceAppObj.getBoolean("attached");
                serviceApp.attachToCamera(attached);
        		
        		serviceManager.getSetup().world.add(serviceApp);
        		serviceApplications.add(serviceApp);
        	}
        	
        	for (int i = 0; i < infobubbleArray.length(); i++)
        	{
        		JSONObject InfobubbleObj = infobubbleArray.getJSONObject(i);
        		InfoBubble infobubble = createInfobubble(InfobubbleObj);
        		
        		serviceManager.getSetup().world.add(infobubble);
        		infobubble.setvisible(false);
        		infoBubbles.add(infobubble);
        	}
			
		} catch (JSONException e) {
			Log.e(LOG_TAG, "parsing failed");
			e.printStackTrace();
			return false;
		}
		return true; 
	}
    
	public Vector<InfoBubble> getInfobubbles()
	{
		return infoBubbles;
	}
	
	public Vector<ServiceApplication> getApplications()
	{
		return serviceApplications;
	}
	
	private ServiceApplication createApplication(JSONObject appData)
	{
		ServiceApplication serviceApp = null;
		try
		{
			String name = appData.getString("name");
			
			serviceApp = new ServiceApplication(serviceManager, name);
			
			String meshName = appData.getString("meshRef");
			String textureName = appData.getString("textRef");
			
			if(meshName!=null && textureName!=null)
			{
			    GDXMesh gdxMesh = gdxLoader.loadModelFromFile(meshName, textureName);
			    gdxMesh.enableMeshPicking();
			    serviceApp.setMesh(gdxMesh);
			    
	    		JSONObject posObj = appData.getJSONObject("position");
	    		JSONObject rotObj = appData.getJSONObject("rotation");
	    		JSONObject scaleObj = appData.getJSONObject("scale");
	    		
			    serviceApp.setPosition(new Vec((float)posObj.getDouble("x"), (float)posObj.getDouble("y"), (float)posObj.getDouble("z")));
			    serviceApp.setRotation(new Vec((float)rotObj.getDouble("x"), (float)rotObj.getDouble("y"), (float)rotObj.getDouble("z")));
			    serviceApp.setScale(new Vec((float)scaleObj.getDouble("x"), (float)scaleObj.getDouble("y"), (float)scaleObj.getDouble("z")));
			}
			JSONObject commandObj = appData.getJSONObject("commands");
			String clickCommand = commandObj.getString("CLICK");
			String dropCommand = commandObj.getString("DROP");
			String longClickCommand = commandObj.getString("LONG_CLICK");
	
			serviceApp.setOnClickCommand(CommandFactory.createCommand(clickCommand, serviceManager, name));
			serviceApp.setOnDoubleClickCommand(CommandFactory.createCommand(dropCommand, serviceManager, name));
			serviceApp.setOnLongClickCommand(CommandFactory.createCommand(longClickCommand, serviceManager, name));
		} catch (JSONException e){}
		Log.i(LOG_TAG, "New service created!");
		return serviceApp;
	}
	
	private InfoBubble createInfobubble(JSONObject bubbleData)
	{
		InfoBubble infobubble = null;
		try
		{
			String name = bubbleData.getString("name");
			
			infobubble = new InfoBubble(serviceManager, name);
			
			String meshName = bubbleData.getString("meshRef");
			String textureName = bubbleData.getString("textRef");
			
			if(meshName!=null && textureName!=null)
			{
			    GDXMesh gdxMesh = gdxLoader.loadModelFromFile(meshName, textureName);
			    gdxMesh.enableMeshPicking();
			    infobubble.setMesh(gdxMesh);
			    
//	    		JSONObject posObj = bubbleData.getJSONObject("position");
	    		JSONObject rotObj = bubbleData.getJSONObject("rotation");
	    		JSONObject scaleObj = bubbleData.getJSONObject("scale");
//	    		
//			    infobubble.setPosition(new Vec((float)posObj.getDouble("x"), (float)posObj.getDouble("y"), (float)posObj.getDouble("z")));
			    infobubble.setRotation(new Vec((float)rotObj.getDouble("x"), (float)rotObj.getDouble("y"), (float)rotObj.getDouble("z")));
			    infobubble.setScale(new Vec((float)scaleObj.getDouble("x"), (float)scaleObj.getDouble("y"), (float)scaleObj.getDouble("z")));
			}
	
		} catch (JSONException e){
			Log.e(LOG_TAG, e.toString());
		}
		
		Log.i(LOG_TAG, "New infobubble created!");
		return infobubble;
	}
}
