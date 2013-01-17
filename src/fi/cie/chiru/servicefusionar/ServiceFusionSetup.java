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
import util.IO;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.widget.LinearLayout;
import android.widget.TextView;

import commands.Command;
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

	public ServiceFusionSetup(String fileName, String textureName) 
	{
		this.fileName = fileName;
		this.textureName = textureName;
		targetMoveWrapper = new Wrapper();
		selection = new Wrapper();
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
		GDXConnection.init(myTargetActivity, renderer);

		new ColladaModel(renderer, fileName, textureName) 
		{
			@Override
			public void modelLoaded(MeshComponent gdxMesh) 
			{
				final Obj o = new Obj();
				gdxMesh.setRotation(new Vec(-90.0f,0.0f,0.0f));
				gdxMesh.enableMeshPicking();
				o.setComp(gdxMesh);
				world.add(o);
				Vec TextPos = new Vec(gdxMesh.getPosition());
				TextPos.add(0, 2, 0);
				o.setOnClickCommand(new TextPopUp("   test   ", TextPos));
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
//		rotateGLCameraAction = new ActionRotateCameraBuffered(camera);

//		arView.addOnTouchMoveListener(wasdAction);
//		eventManager.addOnOrientationChangedAction(rotateGLCameraAction);
//		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera, 5, 25));
//		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(world, camera));
//		minAccuracyAction = new ActionWaitForAccuracy(getActivity(), 24.0f, 10) {
//			@Override
//			public void minAccuracyReachedFirstTime(Location l, ActionWaitForAccuracy a) 
//			{
		callAddObjectsToWorldIfNotCalledAlready();
			
//				if (!eventManager.getOnLocationChangedAction().remove(a)) {
//					Log.e(LOG_TAG, "Could not remove minAccuracyAction from the onLocationChangedAction list");
//				}
//			}
//		};
//		eventManager.addOnLocationChangedAction(minAccuracyAction);
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
	
	private class TextPopUp extends Command 
	{

		private String text;
		private Vec location;
		private boolean textVisible;
		private boolean textCreated;
		private MeshComponent textComponent;

		public TextPopUp(String text, Vec location) 
		{
			this.text = text;
			this.location = location;
			this.location.add(0.0f, 1.5f, 0.0f);
			textVisible = false;
			textCreated = false;
		}

		@Override
		public boolean execute() 
		{
			if(!textCreated)
			{
				createTextComponent();
				textCreated = true;
			}
			
			if(!textVisible)
			{	
				world.add(textComponent);
				renderer.setUseLightning(true);
				textVisible = true;
			}
			else
			{
				world.remove(textComponent);
				textVisible = false;
			}
			
			return true;
		}
		
		private void createTextComponent()
		{
			TextView tv = new TextView(myTargetActivity);
			tv.setTextColor(Color.BLACK);
			tv.setBackgroundColor(Color.WHITE);
			tv.setTextSize(20);
		    tv.setText(this.text);
		    textComponent = GLFactory.getInstance().newTexturedSquare("TextView", IO.loadBitmapFromView(tv));
		    textComponent.setPosition(new Vec(this.location));
		    textComponent.setRotation(new Vec(90.0f, 0.0f, 180.0f));
		}

	}
}
