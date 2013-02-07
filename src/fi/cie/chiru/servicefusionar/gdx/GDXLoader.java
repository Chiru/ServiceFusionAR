package fi.cie.chiru.servicefusionar.gdx;

import gl.Renderable;
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
public class GDXLoader
{

	private static final String LOGTAG = "ColladaModel";
	private Texture texture;
	private StillModel model;

	public GDXMesh loadModelFromFile(String fileName, String textureFileName) 
	{
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

}
