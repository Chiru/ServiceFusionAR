package fi.cie.chiru.servicefusionar;

import gl.ObjectPicker;
import gl.Renderable;
import gl.scenegraph.MeshComponent;

import javax.microedition.khronos.opengles.GL10;

import worldData.Updateable;
import worldData.Visitor;
import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public class GDXMesh extends MeshComponent 
{

	private static final String LOGTAG = "GDXShape";
	private StillModel model;
	private Texture texture;

	public GDXMesh(StillModel model, Texture texture) 
	{
		super(null);
		this.model = model;
		this.texture = texture;

	}

	@Override
	public boolean accept(Visitor visitor) 
	{
		return false;
	}

	@Override
	public void draw(GL10 gl, Renderable parent) 
	{

//		gl.glEnable(GL10.GL_CULL_FACE);

		if (model != null) 
		{
			if (!ObjectPicker.readyToDrawWithColor && texture != null) 
			{
				gl.glEnable(GL10.GL_TEXTURE_2D);
				texture.bind();
				model.render();
				gl.glDisable(GL10.GL_TEXTURE_2D);
			} 
			else 
			{
				model.render();
			}
		} 
		else
			Log.e(LOGTAG, "No model object existend");
	}

	@Override
	public synchronized boolean update(float timeDelta, Updateable parent) 
	{
		super.update(timeDelta, parent);
		return true;
	}

}
