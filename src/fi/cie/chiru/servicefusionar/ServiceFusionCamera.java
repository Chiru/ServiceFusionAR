package fi.cie.chiru.servicefusionar;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

import util.Log;
import util.Vec;
import worldData.Updateable;
import fi.cie.chiru.servicefusionar.sensors.Sensors;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceApplication;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;
import gl.GLCamera;
import gl.Renderable;
import gl.scenegraph.MeshComponent;

public class ServiceFusionCamera extends GLCamera
{
	private List<MeshComponent> attachedMeshes;
	private float prevAngle;
	private float currentAngle;
	ServiceManager serviceManager = null;
	
	public ServiceFusionCamera(ServiceManager serviceManager, Vec initialCameraPosition)
	{
		super(initialCameraPosition);
		this.serviceManager = serviceManager;
		attachedMeshes = new ArrayList<MeshComponent>();
	}
	
	public void attachToCamera(MeshComponent mc)
	{
		attachedMeshes.add(mc);
	}
	
	public void detachFromCamera(MeshComponent mc)
	{
		attachedMeshes.remove(mc);
	}

	@Override
	public synchronized void render(GL10 gl, Renderable parent) 
	{
		prevAngle = currentAngle;
		currentAngle = serviceManager.getSensors().getCurrentAngle();
		this.setRotation(0.0f, currentAngle, 0.0f);
		updateMeshPositions();
		super.render(gl, parent);
	}
	
	private void updateMeshPositions()
	{
		float angle = currentAngle - prevAngle;
		
		if(attachedMeshes.isEmpty())
			return;
		
		if(angle == 0)
			return;
		
		float rotAngleRad = (float)(angle*(Math.PI/180));
		
		float sin = (float)Math.sin(rotAngleRad);
		float cos = (float)Math.cos(rotAngleRad);
		float[] rotMat = {cos, 0, sin, 0, 0, 1, 0, 0, -sin, 0, cos, 0, 0,0,0,1};
		
		for(int i = 0; i<attachedMeshes.size(); i++)
		{
			MeshComponent meshComp = attachedMeshes.get(i);
			Vec meshPos = meshComp.getPosition();
			Vec meshRot = meshComp.getRotation();
			//Vec camRot = this.getRotation();
			Vec camPos = this.getPosition();

			float [] pos ={meshPos.x, meshPos.y, meshPos.z, 1.0f};
			float [] res ={0.0f, 0.0f, 0.0f, 0.0f};
			Matrix.multiplyMV(res, 0, rotMat, 0, pos, 0);
			meshPos.setTo(camPos.x + res[0], camPos.y + res[1], camPos.z + res[2]);
			
			meshRot.setTo(meshRot.x, -currentAngle , meshRot.z);
		}		
	}
}
