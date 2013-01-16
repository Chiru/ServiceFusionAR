package fi.cie.chiru.servicefusionar;

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
import util.Log;
import util.Vec;
import util.Wrapper;
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
import android.location.Location;

import commands.Command;

public class ServiceFusionSetup extends Setup 
{

	protected static final int delta = 5;
	private static final String LOG_TAG = "ServiceFusionSetup";
	private String fileName;
	private String textureName;
	private LightSource spotLight;
	private Wrapper targetMoveWrapper;
	private GL1Renderer renderer;
	public GLCamera camera;
	public World world;
	private Action rotateGLCameraAction;
	private boolean addObjCalledOneTieme;
	private ActionWaitForAccuracy minAccuracyAction;

	public ServiceFusionSetup(String fileName, String textureName) 
	{
		this.fileName = fileName;
		this.textureName = textureName;
		targetMoveWrapper = new Wrapper();
	}
	
	@Override
	public void _a_initFieldsIfNecessary() 
	{
		camera = new GLCamera(new Vec(0, 0, 2));
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
		spotLight = LightSource.newDefaultDefuseLight(GL10.GL_LIGHT1, new Vec(0, 0, 0));
		lights.add(spotLight);
		return true;
	}

	public void addObjectsTo(GL1Renderer renderer, final World world, GLFactory objectFactory) 
	{
		this.renderer = renderer;
		GDXConnection.init(myTargetActivity, renderer);

		new ColladaModel(renderer, fileName, textureName) 
		{
			@Override
			public void modelLoaded(MeshComponent gdxMesh) 
			{
				final Obj o = new Obj();
				gdxMesh.setRotation(new Vec(240.0f,-90.0f,0.0f));
				o.setComp(gdxMesh);
				world.add(o);
//				o.setComp(new MoveComp(1));
				o.setOnClickCommand(new Command() {

					@Override
					public boolean execute() 
					{
						targetMoveWrapper.setTo(o);
						return true;
					}
				});
			}
		};

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
//		wasdAction = new ActionWASDMovement(camera, 25, 50, 20);
		rotateGLCameraAction = new ActionRotateCameraBuffered(camera);

//		arView.addOnTouchMoveListener(wasdAction);
		eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera, 5, 25));
		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(world, camera));
		minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 24.0f, 10) {
			@Override
			public void minAccuracyReachedFirstTime(Location l, ActionWaitForAccuracy a) 
			{
				callAddObjectsToWorldIfNotCalledAlready();
			
				if (!eventManager.getOnLocationChangedAction().remove(a)) {
					Log.e(LOG_TAG, "Could not remove minAccuracyAction from the onLocationChangedAction list");
				}
			}
		};
		eventManager.addOnLocationChangedAction(minAccuracyAction);
	}

	protected void callAddObjectsToWorldIfNotCalledAlready() 
	{
		if (!addObjCalledOneTieme)
			addObjectsTo(renderer, world, GLFactory.getInstance());
		else
			Log.w(LOG_TAG, "callAddObjectsToWorldIfNotCalledAlready() " + "called more then one time!");
		
		addObjCalledOneTieme = true;
	}

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

		guiSetup.addImangeButtonToRightView(R.drawable.arrow_up_float,
				new Command() {
					@Override
					public boolean execute() {
						camera.changeZPositionBuffered(+delta);
						return false;
					}
				});
		guiSetup.addImangeButtonToRightView(R.drawable.arrow_down_float,
				new Command() {
					@Override
					public boolean execute() {
						camera.changeZPositionBuffered(-delta);
						return false;
					}
				});

	}
}
