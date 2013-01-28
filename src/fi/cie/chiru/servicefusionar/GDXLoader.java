package fi.cie.chiru.servicefusionar;

import gl.GL1Renderer;
import gl.Renderable;
import gl.scenegraph.MeshComponent;
import system.Setup;
import worldData.World;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.loaders.collada.ColladaLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

/**
 * The ModelCreator has to load the model from the rendering thread. Therefor it
 * implements {@link Renderable} and has to be passed to the {@link Renderer} in
 * the
 * {@link Setup#_b_addWorldsToRenderer(gl.GLRenderer, gl.GLFactory, geo.GeoObj)}
 * methods. The ModelCreator will call the
 * {@link GDXLoader#modelLoaded(StillModel, Texture)} method when the model
 * was loaded. Then you can create a {@link GDXMesh} with the returned data and
 * add it to the {@link World}
 * 
 * @author Spobo
 * 
 */
public class GDXLoader //implements gl.Renderable 
{

	private static final String LOGTAG = "ColladaModel";

//	private String fileName;
//	private String textureFileName;
	private Texture texture;
	private StillModel model;
//	private GL1Renderer renderer;

	public GDXLoader() 
	{
//		this.renderer = renderer;
//		renderer.addRenderElement(this);
	}

	public GDXMesh loadModelFromFile(String fileName, String textureFileName) 
	{
//		this.fileName = fileName;
//		this.textureFileName = textureFileName;

		
		try 
		{
			if (fileName != null)
		        model = ColladaLoader.loadStillModel(Gdx.files.internal(fileName));
		}
		
		catch (Exception e) 
		{
			Log.e(LOGTAG, "Could not load model:" + fileName);
			e.printStackTrace();
		}
		
		try 
		{
			if (textureFileName != null)
				texture = new Texture(Gdx.files.internal(textureFileName), true);
		} 
		catch (Exception e) 
		{
			Log.e(LOGTAG, "Could not load texture: " + textureFileName);
			e.printStackTrace();
		}

		Log.d(LOGTAG, "Result of trying is:");
		Log.d(LOGTAG, "fileName=" + fileName);
		Log.d(LOGTAG, "textureFileName=" + textureFileName);
		Log.d(LOGTAG, "model=" + model);
		Log.d(LOGTAG, "texture=" + texture);
		
		return new GDXMesh(model, texture);
	}

//	public MeshComponent getGDXShape() 
//	{
//		GDXMesh x = new GDXMesh(model, texture);
//		return x;
//	}
//
//	@Override
//	public void render(javax.microedition.khronos.opengles.GL10 gl, gl.Renderable parent) 
//	{
//
//		Log.d(LOGTAG, "Trying to load " + fileName);
//
//		try 
//		{
//			loadModelFromFile();
//		} 
//		catch (Exception e) 
//		{
//			Log.e(LOGTAG, "Could not load model");
//			e.printStackTrace();
//		}
//
//		if (model != null)
//			modelLoaded(new GDXMesh(model, texture));
//
//		Log.d(LOGTAG, "Result of trying is:");
//		Log.d(LOGTAG, "fileName=" + fileName);
//		Log.d(LOGTAG, "textureFileName=" + textureFileName);
//		Log.d(LOGTAG, "model=" + model);
//		Log.d(LOGTAG, "texture=" + texture);
//
//		renderer.removeRenderElement(this);
//
//	}
//
//	public abstract void modelLoaded(MeshComponent gdxMesh);

}
