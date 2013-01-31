package fi.cie.chiru.servicefusionar;

import java.util.Vector;

import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.LightSource;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;

import javax.microedition.khronos.opengles.GL10;

import system.Setup;
import system.EventManager;
import util.EfficientList;
import util.IO;
import util.Log;
import util.Vec;
import util.Wrapper;
import v2.simpleUi.util.DragAndDropListener;
import worldData.MoveComp;
import worldData.Obj;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionMoveObject;
import actions.ActionRotateCameraBuffered;
import actions.ActionWaitForAccuracy;
import android.R;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import commands.Command;
import commands.ui.CommandInUiThread;
import commands.logic.CommandSetWrapperToValue2;

public class ServiceFusionSetup extends Setup 
{

	protected static final int delta = 5;
	private static final String LOG_TAG = "ServiceFusionSetup";
	private String fileName;
	private String textureName;
	private LightSource spotLight;
	private Wrapper targetMoveWrapper;
	private Wrapper selection;
	private GL1Renderer renderer;
	public GLCamera camera;
	public World world;
	private Action rotateGLCameraAction;
	private boolean addObjCalledOneTieme;
	private ActionWaitForAccuracy minAccuracyAction;
	private GDXLoader gdxLoader;
	private GDXConnection gdxConnection;
	private Vector<ServiceApplication> serviceApplications;

	public ServiceFusionSetup() 
	{
//		this.fileName = fileName;
//		this.textureName = textureName;
		targetMoveWrapper = new Wrapper();
		selection = new Wrapper();
		gdxLoader = new GDXLoader();
		gdxConnection = new GDXConnection();
		serviceApplications = new Vector<ServiceApplication>();
	}
	
	@Override
	public void _a_initFieldsIfNecessary() 
	{
		camera = new GLCamera(new Vec(0, 0, 0));
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
		spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(0, 10, 0));
		lights.add(spotLight);
		return true;
	}

	public void addObjectsTo(GL1Renderer renderer, final World world, GLFactory objectFactory) 
	{
		this.renderer = renderer;
	    gdxConnection.open(myTargetActivity, renderer);
		CreateApplications();
		addApplicationsToWorld(world); 
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
//		wasdAction = new ActionWASDMovement(camera, 25, 50, 20);
//		rotateGLCameraAction = new ActionRotateCameraBuffered(camera);

//		arView.addOnTouchMoveListener(wasdAction);
//		eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
//		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera, 5, 25));
//		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(world, camera));
//		minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 24.0f, 10) {
//			@Override
//			public void minAccuracyReachedFirstTime(Location l, ActionWaitForAccuracy a) 
//			{
//		callAddObjectsToWorldIfNotCalledAlready();
			
//				if (!eventManager.getOnLocationChangedAction().remove(a)) {
//					Log.e(LOG_TAG, "Could not remove minAccuracyAction from the onLocationChangedAction list");
//				}
//			}
//		};
//		eventManager.addOnLocationChangedAction(minAccuracyAction);
	}

//	protected void callAddObjectsToWorldIfNotCalledAlready() 
//	{
//		if (!addObjCalledOneTieme)
//			addObjectsTo(renderer, world, GLFactory.getInstance());
//		else
//			Log.w(LOG_TAG, "callAddObjectsToWorldIfNotCalledAlready() " + "called more then one time!");
//		
//		addObjCalledOneTieme = true;
//	}

	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater updater) 
	{
		updater.addObjectToUpdateCycle(world);
//		updater.addObjectToUpdateCycle(wasdAction);
		updater.addObjectToUpdateCycle(rotateGLCameraAction);
	}

	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) 
	{
		guiSetup.setRightViewAllignBottom();

//		guiSetup.addViewToTop(minAccuracyAction.getView());

		guiSetup.addImangeButtonToRightView(R.drawable.arrow_up_float, new Command() 
		{
			@Override
			public boolean execute() 
			{
				camera.changeZPositionBuffered(+delta);
				return false;
			}
		});
		guiSetup.addImangeButtonToRightView(R.drawable.arrow_down_float, new Command() 
		{
			@Override
			public boolean execute() 
			{
				camera.changeZPositionBuffered(-delta);
				return false;
			}
		});
	}
	
	private void AddServiceApplication(String name, String fileName, String textureName)
	{
		ServiceApplication serviceApp = new ServiceApplication(name);
		
		if(fileName!=null && textureName!=null)
		{
		    GDXMesh gdxMesh = gdxLoader.loadModelFromFile(fileName, textureName);
		    gdxMesh.enableMeshPicking();
		    serviceApp.setMesh(gdxMesh);
		    serviceApp.setOnClickCommand(new CommandTextPopUp("Test", new Vec(serviceApp.getPosition()), this));
		}
		
		serviceApplications.add(serviceApp);
	}
	
	private void CreateApplications()
	{
		AddServiceApplication("Twitter", "twitter_medium_397.dae", "twitter2.jpg");
//		AddServiceApplication("Firefox", "firefox_medium_617.dae", "firefox-logo-full.jpg");
	}
	
	private void addApplicationsToWorld(final World world)
	{
		for(int i=0; i<serviceApplications.size(); i++)
			world.add(serviceApplications.elementAt(i));//world.add(serviceApplications.elementAt(i).getMesh());
	}
	
	protected class myDragEventListener implements OnDragListener 
	{

		RelativeLayout root;
		
		@Override
		public boolean onDrag(View v, DragEvent event) 
		{
			
			 final int action = event.getAction();
//			 View view = (View) event.getLocalState();

			 switch(action) 
			 {
//			     case DragEvent.ACTION_DRAG_EXITED:
//				     root = (RelativeLayout)v.getParent();
//				     root.removeView(view);
//                     break;

			     case DragEvent.ACTION_DROP:
			    	 Log.d(LOG_TAG, "Dropped:  x=" + event.getX() + " y=" + event.getY());
//			    	 root = (RelativeLayout)v.getParent();
//				     root.removeView(view);
                     break;
                 
			 }				 
			 
			return true;
		}
		
	}
}
