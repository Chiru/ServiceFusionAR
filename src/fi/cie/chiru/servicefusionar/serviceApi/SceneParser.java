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

	public SceneParser()
	{
		gdxLoader = new GDXLoader();
	}
	public Vector<ServiceApplication> parseFile(ServiceManager serviceManager, String fileName)
	{
		Vector<ServiceApplication> serviceApplications = new Vector<ServiceApplication>();
		
		try 
		{
			InputStream in = serviceManager.getSetup().myTargetActivity.getAssets().open(fileName);
			sceneContent = IO.convertInputStreamToString(in);
            
		} 
		catch (IOException e) 
		{
			Log.e(LOG_TAG, "Could not read file: " +fileName);
			e.printStackTrace();
		}
		
		JSONObject jsonObj = null;
		try 
		{
			jsonObj = new JSONObject(sceneContent);
			
		} catch (JSONException e) {
			Log.e(LOG_TAG, "parsing failed");
			e.printStackTrace();
		}
		JSONArray entries = null;
        try 
        {
        	
        	entries = jsonObj.getJSONArray("ServiceApplication");
        	
        	for(int i=0; i<entries.length(); i++)
        	{
        		JSONObject ServiceAppObj = entries.getJSONObject(i);
        		String name = ServiceAppObj.getString("name");
        		
        		ServiceApplication serviceApp = new ServiceApplication(serviceManager,name);
        		
        		String meshName = ServiceAppObj.getString("meshRef");
        		String textureName = ServiceAppObj.getString("textRef");
        		
        		if(meshName!=null && textureName!=null)
        		{
        		    GDXMesh gdxMesh = gdxLoader.loadModelFromFile(meshName, textureName);
        		    gdxMesh.enableMeshPicking();
        		    serviceApp.setMesh(gdxMesh);
        		    
            		JSONObject posObj = ServiceAppObj.getJSONObject("position");
            		JSONObject rotObj = ServiceAppObj.getJSONObject("rotation");
            		JSONObject scaleObj = ServiceAppObj.getJSONObject("scale");
            		
        		    serviceApp.setPosition(new Vec((float)posObj.getDouble("x"), (float)posObj.getDouble("y"), (float)posObj.getDouble("z")));
        		    serviceApp.setRotation(new Vec((float)rotObj.getDouble("x"), (float)rotObj.getDouble("y"), (float)rotObj.getDouble("z")));
        		    serviceApp.setScale(new Vec((float)scaleObj.getDouble("x"), (float)scaleObj.getDouble("y"), (float)scaleObj.getDouble("z")));
        		}
        		
        		JSONObject commandObj = ServiceAppObj.getJSONObject("commands");
        		String clickCommand = commandObj.getString("CLICK");
        		String dropCommand = commandObj.getString("DROP");
        		String longClickCommand = commandObj.getString("LONG_CLICK");

        		serviceApp.setOnClickCommand(CommandFactory.createCommand(clickCommand, serviceManager, name));
        		serviceApp.setOnDoubleClickCommand(CommandFactory.createCommand(dropCommand, serviceManager, name));
        		serviceApp.setOnLongClickCommand(CommandFactory.createCommand(longClickCommand, serviceManager, name));
        		
        		boolean visible = ServiceAppObj.getBoolean("visible");
        		
        		if(!visible)
        			serviceApp.setvisible(false);
        		
        		serviceManager.getSetup().world.add(serviceApp);
        		serviceApplications.add(serviceApp);

        	}
			
		} catch (JSONException e) {
			Log.e(LOG_TAG, "parsing failed");
			e.printStackTrace();
		}
		
		return serviceApplications; 
	}
    
}
