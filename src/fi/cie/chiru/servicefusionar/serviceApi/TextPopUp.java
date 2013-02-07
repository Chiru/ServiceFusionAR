package fi.cie.chiru.servicefusionar.serviceApi;

import fi.cie.chiru.servicefusionar.ServiceFusionSetup;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import util.IO;
import util.Vec;
import android.content.ClipData;
import android.graphics.Color;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.RelativeLayout;
import android.widget.TextView;
import commands.ui.CommandInUiThread;

public class TextPopUp
{

	private static final String LOG_TAG = "CommandTextPopUp";
	private String text;
	private ServiceFusionSetup setup;
	private Vec location;
	private boolean textVisible;
	private boolean textCreated;
	private MeshComponent textComponent;

	public TextPopUp(String text, Vec location, ServiceFusionSetup setup) 
	{
		this.text = text;
		this.location = location;
		this.setup = setup;
		this.location.add(2.0f, 3.0f, 0.0f);
		textVisible = false;
		textCreated = false;
	}

	public void visible() 
	{
		if(!textCreated)
		{
			createTextComponent();
			textCreated = true;
		}
		
		if(!textVisible)
		{	
			setup.world.add(textComponent);
			textVisible = true;
		}
		else
		{
			setup.world.remove(textComponent);
			textVisible = false;
		}
	}
	
	private void createTextComponent()
	{
		TextView tv = new TextView(setup.myTargetActivity);
		tv.setId(generateUniqueId());
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundColor(Color.WHITE);
		tv.setTextSize(45);
	    tv.setText(this.text);
	    textComponent = GLFactory.getInstance().newTexturedSquare("TextView", IO.loadBitmapFromView(tv));
	    setup.world.add(textComponent);
	    textComponent.setPosition(new Vec(this.location));
	    textComponent.setRotation(new Vec(90.0f, 0.0f, 180.0f));
	    textComponent.setScale(new Vec(0.8f,0.8f,0.8f));
	    textComponent.setOnLongClickCommand(new DragTextPopUpObject(tv));
	}
	
	private int generateUniqueId()
	{
		int id = 0;
		RelativeLayout root = (RelativeLayout) setup.getGuiSetup().getMainContainerView();
		
		while(root.findViewById(++id) != null );
		    return id;
	}

	private class DragTextPopUpObject extends CommandInUiThread 
	{
		View v;
		
		public DragTextPopUpObject(View DraggedItem)
		{
		    v = DraggedItem;
		}
		
	//	@Override
		public void executeInUiThread()
		{
			TextView tv = (TextView)v;
			RelativeLayout root = (RelativeLayout) setup.getGuiSetup().getMainContainerView();
			 
			if(root.findViewById(tv.getId())==null)
			    root.addView(tv);

		    tv.setVisibility(View.GONE);
			String text = (String)tv.getText();
			View.DragShadowBuilder shadow = new DragShadowBuilder(tv);
			ClipData data = ClipData.newPlainText("DragData", text);
			tv.startDrag(data, shadow, null, 0);
		}
		
	}
}
