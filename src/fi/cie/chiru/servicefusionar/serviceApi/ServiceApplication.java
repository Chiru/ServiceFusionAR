/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.serviceApi;

import fi.cie.chiru.servicefusionar.gdx.GDXMesh;
import gl.Renderable;

import javax.microedition.khronos.opengles.GL10;

import android.location.Location;

import commands.Command;

import util.Log;
import util.Vec;
import worldData.AbstractObj;
import worldData.Updateable;
import worldData.Visitor;

public class ServiceApplication extends AbstractObj 
{
	private static final String LOG_TAG = "ServiceApplication";
	private GDXMesh gdxMesh = null;
	private String name;
	protected boolean visible = false;
	protected boolean isAttached = false;
	protected boolean initialized = false;
	protected Location geoLocation = null;
	protected final float DISTANCE_LIMIT = 200f;
	private final float RADIUS = 25f;
	
	private ServiceManager serviceManager; 
	
	public ServiceApplication(ServiceManager serviceManager, String name)
	{
		Log.d(LOG_TAG, "Creating application " + name);
		this.name = name;
		this.serviceManager = serviceManager;
	}
	
	public String getName()
	{
		return this.name;
	}
    //should return MeshComponent?
	public GDXMesh getMesh() {
		return gdxMesh;
	}

	public void setMesh(GDXMesh gdxMesh) 
	{
		this.gdxMesh = gdxMesh;
		this.gdxMesh.setVisible(visible);
	}
	
	public Vec getPosition() 
	{
		return gdxMesh.getPosition();
	}

	public void setPosition(Vec position) 
	{
		this.gdxMesh.setPosition(position);
	}
	public void setPosition(float x, float y, float z)
	{
		this.gdxMesh.setPosition(new Vec(x, y, z));
	}

	public void setRotation(Vec rotation) 
	{
		this.gdxMesh.setRotation(rotation);
	}
	
	public Vec getRotation() 
	{
		return gdxMesh.getRotation();
	}
	
	public void setScale(Vec scale) 
	{
		this.gdxMesh.setScale(scale);
	}
	
	public Vec getScale() 
	{
		return gdxMesh.getScale();
	}
	public void setvisible(boolean visible)
	{
		if (this.visible == visible)
			return;
		
		this.visible = visible;
		gdxMesh.setVisible(this.visible);
	}
	
	public void attachToCamera(boolean attached)
	{
		this.isAttached = attached;
		if(attached)
		{
			Log.d(LOG_TAG, "Attaching service " + this.name + " to camera");
			serviceManager.getSetup().camera.attachToCamera(this.gdxMesh);
		}
		else
		{
			Log.d(LOG_TAG, "Detaching service " + this.name + " from camera");
			serviceManager.getSetup().camera.detachFromCamera(this.gdxMesh);
		}
	}
	
	public void setGeoLocation(double latitude, double longitude)
	{	
		if (geoLocation == null)
			geoLocation = new Location("pre-saved location info");
		
		geoLocation.setLatitude(latitude);
		geoLocation.setLongitude(longitude);
		
		if (serviceManager.getSensors().getCurrentLocation() == null)
		{
			Log.d(LOG_TAG,  "Setting app " + this.getName() + " to unvisible");
			this.setvisible(false);
		}
	}
	
	public Location getGeoLocation()
	{
		return geoLocation;
	}
	
	/**
	 * 
	 * @param angleInDegrees
	 * @param radius
	 * @return Position vector in 2D space
	 */
	protected Vec positionFromAngle(float angleInDegrees, float radius)
	{
		float z = radius * (float)Math.cos(Math.toRadians(angleInDegrees));
		float x = radius * (float)Math.sin(Math.toRadians(angleInDegrees));
		
		Vec pos = new Vec();
		pos.setTo(x, z);
		return pos;
	}
	
	public void servicePlaceFromLocation(Location location)
	{
		if (geoLocation != null)
		{
			float[] results= new float[3];
			// The computed distance in meters is stored in results[0].
			// If results has length 2 or greater, the initial bearing is stored in results[1].
			// If results has length 3 or greater, the final bearing is stored in results[2].
			Location.distanceBetween(location.getLatitude(), location.getLongitude(), this.geoLocation.getLatitude(), this.geoLocation.getLongitude(), results);
			
			// Attach service to camera if device is located inside predefined circle radius from physical service location.
			if (results[0] < DISTANCE_LIMIT)
			{
				// TODO This is a bad design and should be refactored if there is time for it.
				
				if (this.isAttached)
					return;
				
				float angle = serviceManager.getSetup().getCamera().getRotation().y;
				Log.i(LOG_TAG, "Within DISTANCE_LIMIT, angle: " + angle + "\n");

				Vec position = null;
				
				if (this.getName().equals("MovieIcon"))
					position = positionFromAngle(angle, RADIUS);

				else if (this.getName().equals("PubIcon"))
					position = positionFromAngle(angle, RADIUS);
	
				this.setPosition(new Vec(position.x, -4.0f, -position.y));			
				this.setvisible(true);
				this.attachToCamera(true);
				
				return;
			}
			
			// Set service icon positions if service is further away than value of DISTANCE_LIMIT
			float bearing = (float)((360 + results[2]) % 360f);
			Vec position = positionFromAngle(bearing, RADIUS);
			
			//Log.i(LOG_TAG, "position = " + position.x + ", " + position.y);
			this.setPosition(position.x, -4.0f, -position.y);
			this.setvisible(true);
			this.attachToCamera(false);
		}
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) 
	{
		gdxMesh.update(timeDelta, parent);
		return true;
	}

	@Override
	public boolean accept(Visitor visitor) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(GL10 gl, Renderable parent) 
	{
		gdxMesh.render(gl, parent);	
	}

	@Override
	public Command getOnClickCommand() 
	{
        return gdxMesh.getOnClickCommand();
	}

	@Override
	public void setOnClickCommand(Command c) 
	{
        gdxMesh.setOnClickCommand(c);
	}
	
	@Override
	public Command getOnLongClickCommand() 
	{
        return gdxMesh.getOnLongClickCommand();
	}

	@Override
	public void setOnLongClickCommand(Command c) {
        gdxMesh.setOnLongClickCommand(c);
	}

	@Override
	public Command getOnDoubleClickCommand() 
	{
        return gdxMesh.getOnDoubleClickCommand();
	}

	@Override
	public void setOnDoubleClickCommand(Command c) 
	{
        gdxMesh.setOnDoubleClickCommand(c);
	}	
}
