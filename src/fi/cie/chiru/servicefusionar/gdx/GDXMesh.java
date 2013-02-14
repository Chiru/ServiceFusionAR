package fi.cie.chiru.servicefusionar.gdx;

import gl.ObjectPicker;
import gl.Renderable;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gl.scenegraph.TriangulatedRenderData;

import javax.microedition.khronos.opengles.GL10;

import worldData.Updateable;
import worldData.Visitor;
import android.opengl.GLUtils;
import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;

public class GDXMesh extends MeshComponent 
{

	private static final String LOG_TAG = "GDXSMesh";
	private StillModel model;
	private Texture texture;
	private boolean reload;
	private boolean visible;
	
	public GDXMesh(StillModel model, Texture texture) 
	{
		super(null);
		this.model = model;
		this.texture = texture;
		reload = false;
		visible = true;
	}

	@Override
	public boolean accept(Visitor visitor) 
	{
		return false;
	}
	
	@Override
	public void draw(GL10 gl, Renderable parent) 
	{

		if(!visible)
			return;
		
//		gl.glEnable(GL10.GL_CULL_FACE);
		if (model != null) 
		{
			if (!ObjectPicker.readyToDrawWithColor) 
			{
				if(!reload)
				{
				    this.texture.load(this.texture.getTextureData());
				    reload = true;
				}

				gl.glEnable(GL10.GL_TEXTURE_2D);
				this.texture.bind();
				this.model.render();
				gl.glDisable(GL10.GL_TEXTURE_2D);
			} 
			else 
			{
				model.render();
			}
		} 
		else
			Log.e(LOG_TAG, "No model object existend");
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
}
