package fi.cie.chiru.servicefusionar;

import gl.ObjectPicker;
import gl.Renderable;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gl.scenegraph.TriangulatedRenderData;

import javax.microedition.khronos.opengles.GL10;

import worldData.Updateable;
import worldData.Visitor;
import android.util.Log;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.model.SubMesh;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;

public class GDXMesh extends MeshComponent 
{

	private static final String LOGTAG = "GDXSMesh";
	private StillModel model;
	private Texture texture;
	private TriangulatedRenderData trData;
	private boolean reload;
	
	public GDXMesh(StillModel model, Texture texture) 
	{
		super(null);
		this.model = model;
		this.texture = texture;
		reload=false;
//		trData = new TriangulatedRenderData();
//		
//		setMyRenderData();
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
				if(!reload)
				{
				    this.texture.load(this.texture.getTextureData());
				    reload = true;
				    Log.d(LOGTAG, "Texture reloaded.........................");
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
			Log.e(LOGTAG, "No model object existend");
	}
//
//	@Override
//	public synchronized boolean update(float timeDelta, Updateable parent) 
//	{
//		super.update(timeDelta, parent);
//		return true;
//	}
	
//	private void setMyRenderData()
//	{
//		SubMesh[] subMeshes = model.getSubMeshes();
//		float[] vertices;
//		short[] indices;
//		int i,j;
//		
//		for(i=0; i<subMeshes.length-1; i++);
//		{	
//		    Mesh mesh = subMeshes[i].mesh;
//		    
//		    vertices = new float[mesh.getNumVertices()];
//		    indices = new short[mesh.getNumIndices()];
//		    
//		    mesh.getVertices(vertices);
//		    mesh.getIndices(indices);
//		    
////		    Log.d(LOGTAG, "Vertices number: " + mesh.getNumVertices());
////		    for(j=0; j<vertices.length; j++)
////		    {
////		    	 Log.d(LOGTAG, " " + vertices[j]);
////		    }
//		   
//		    Log.d(LOGTAG, "Indices: " + mesh.getNumIndices());
//		    for(j=0; j<indices.length; j++)
//		    {
//		    	 Log.d(LOGTAG, " " + indices[j]);
//		    }
//		    
//		}
//		
//		trData.setDrawModeToTriangles();
//	    trData.setVertexArray(vertices);
//	    trData.setIndeceArray(indices);
//	    this.setMyRenderData(trData);
//	}

}
