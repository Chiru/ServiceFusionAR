package fi.cie.chiru.servicefusionar;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.opengl.Matrix;

import util.Log;
import util.Vec;
import worldData.Updateable;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceApplication;
import gl.GLCamera;
import gl.scenegraph.MeshComponent;

public class ServiceFusionCamera extends GLCamera
{
	private float i = 0.0f;
	private List<MeshComponent> attachedMeshes;
	private float prevAngle;
	private float currentAngle;
	private boolean angleChanged = false;
	Vec up = new Vec(0.0f, 1.0f, 0.0f);
	
	public ServiceFusionCamera(Vec initialCameraPosition)
	{
		super(initialCameraPosition);
		attachedMeshes = new ArrayList<MeshComponent>();
	}
	
	@Override
	public boolean update(float timeDelta, Updateable parent) 
	{
		if(angleChanged)
		{
			updateMeshPositions();
			angleChanged = false;
		}
		return super.update(timeDelta, parent);
	}
	
	public void attachToCamera(MeshComponent mc)
	{
		attachedMeshes.add(mc);
	}
	
	public void detachFromCamera(MeshComponent mc)
	{
		attachedMeshes.remove(mc);
	}
	
	public void setNewAngle(float angle)
	{
		prevAngle = currentAngle;
		currentAngle = angle;
		this.setRotation(0.0f, angle, 0.0f);
		angleChanged = true;
	}
	
	private void updateMeshPositions()
	{
		float rotAngleRad = (float)((currentAngle - prevAngle)*(Math.PI/180));
		for(int i = 0; i<attachedMeshes.size(); i++)
		{
			MeshComponent meshComp = attachedMeshes.get(i);
			Vec meshPos = meshComp.getPosition();
			Vec camPos = this.getPosition();

			float sin = (float)Math.sin(rotAngleRad);
			float cos = (float)Math.cos(rotAngleRad);
			float[] rotMat = {cos, 0, sin, 0, 0, 1, 0, 0, -sin, 0, cos, 0, 0,0,0,1};
			float [] pos ={meshPos.x, meshPos.y, meshPos.z, 1.0f};
			float [] res ={0.0f, 0.0f, 0.0f, 0.0f};
			Matrix.multiplyMV(res, 0, rotMat, 0, pos, 0);
			meshPos.setTo(camPos.x + res[0], camPos.y + res[1], camPos.z + res[2]);
			
			Vec meshRot = meshComp.getRotation();
			Vec camRot = this.getRotation();

		}		
	}
}
