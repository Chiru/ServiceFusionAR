package fi.cie.chiru.servicefusionar;

import util.IO;
import util.Vec;


import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
	public Vector<ServiceApplication> parseFile(Context context, String fileName)
	{
		Vector<ServiceApplication> serviceApplications = new Vector<ServiceApplication>();
		
		try 
		{
			InputStream in = context.getAssets().open(fileName);
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
			Log.d(LOG_TAG, "jsonObj: " + jsonObj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
        		
        		ServiceApplication serviceApp = new ServiceApplication(name);
        		
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
//        		    serviceApp.setOnClickCommand(new CommandTextPopUp("Test", new Vec(serviceApp.getPosition()), this));
        		}
        		else
        			continue;
        		
        		serviceApplications.add(serviceApp);

        	}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return serviceApplications; 
	}
    
}
