package fi.cie.chiru.servicefusionar;

import util.Log;
import util.Vec;

public class ServiceApplication 
{
	private static final String LOG_TAG = "ServiceApplication";
	private GDXMesh gdxMesh;
	private String name;
	
	public ServiceApplication(String name)
	{
		Log.d(LOG_TAG, "Creating application " + name);
		this.name = name;
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
	}
	
	public Vec getPosition() 
	{
		return gdxMesh.getPosition();
	}

	public void setPosition(Vec position) 
	{
		this.gdxMesh.setPosition(position);
	}

	public void setRotation(Vec rotation) 
	{
		this.gdxMesh.setScale(rotation);
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
	
}
