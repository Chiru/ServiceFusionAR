/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar;

import fi.cie.chiru.servicefusionar.gdx.GDXConnection;
import fi.cie.chiru.servicefusionar.sensors.Sensors;
import fi.cie.chiru.servicefusionar.serviceApi.CustomDragEventListener;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;
import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.LightSource;
import gui.GuiSetup;

import javax.microedition.khronos.opengles.GL10;

import system.Setup;
import system.EventManager;
import util.EfficientList;
import util.Log;
import util.Vec;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionRotateCameraBuffered;
import android.R;
import android.app.Activity;
import android.os.Handler;

import commands.Command;

public class ServiceFusionSetup extends Setup 
{

	public ServiceFusionCamera camera;
	public World world;
	
	protected static final int delta = 5;
	
	private static final String LOG_TAG = "ServiceFusionSetup";
	private GL1Renderer renderer;
	private Action rotateGLCameraAction;
	private GDXConnection gdxConnection;
	private ServiceManager servicemanager;
	
//	private SFSocketService socketService;
//    private Handler handler;

	public ServiceFusionSetup() 
	{
		//private Thread socketThread;
		gdxConnection = new GDXConnection();
		servicemanager = new ServiceManager(this);
//		handler = new Handler();
//		socketService = new SFSocketService(handler);
//		final Thread socketThread = new Thread(socketService);
//        socketThread.start();
	}
	
	@Override
	public void _a_initFieldsIfNecessary() 
	{
		camera = new ServiceFusionCamera(servicemanager, new Vec(0, 0, 0));
		world = new World(camera);
	}

	public World getWorld() 
	{
		return world;
	}

	public GLCamera getCamera() 
	{
		return camera;
	}

	@Override
	public boolean _a2_initLightning(EfficientList<LightSource> lights) 
	{
		lights.add(LightSource.newDefaultAmbientLight(GL10.GL_LIGHT0));
		return true;
	}

	public void addObjectsTo(GL1Renderer renderer, final World world, GLFactory objectFactory) 
	{
		this.renderer = renderer;
	    gdxConnection.open(myTargetActivity, renderer);
	    servicemanager.createApplications();
	}
	
	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer, GLFactory objectFactory, GeoObj currentPosition) 
	{
		this.renderer = renderer;
		renderer.addRenderElement(world);
	}

	@Override
	public void _c_addActionsToEvents(final EventManager eventManager, CustomGLSurfaceView arView, SystemUpdater updater) 
	{
		addObjectsTo(renderer, world, GLFactory.getInstance());
//		rotateGLCameraAction = new ActionRotateCameraBuffered(camera);
//		eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
	}

	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater updater) 
	{
		updater.addObjectToUpdateCycle(world);
//		updater.addObjectToUpdateCycle(rotateGLCameraAction);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) 
	{	
		guiSetup.getMainContainerView().setOnDragListener(new CustomDragEventListener(servicemanager));
	}
	
	public void stopServer()
	{
		Log.d(LOG_TAG, "ServiceFusionSetup stopServer");
		//socketService.stopSocket();
	}
	
	@Override
	public void onDestroy(Activity a)
	{
		if (servicemanager != null)
			servicemanager.onDestroy();
		
		super.onDestroy(a);
	}
}
