/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.serviceApi;

import util.IO;
import util.Vec;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import commands.ui.CommandInUiThread;

import fi.cie.chiru.servicefusionar.R;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

public class CreditCard 
{
	private ServiceManager serviceManager;
	private MeshComponent creditCard;
	
	public CreditCard(ServiceManager serviceManager)
	{
	    this.serviceManager = serviceManager;
	    initCard();
	}
	
	private void initCard()
	{
		Bitmap bm = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.visa_card);
		ImageView im = new ImageView(serviceManager.getSetup().myTargetActivity);
		im.setImageBitmap(bm);
		creditCard = GLFactory.getInstance().newTexturedSquare("creditCard", bm);
		creditCard.setOnLongClickCommand(new DraggableImage(serviceManager, im, ""));
    	
		Vec camPos = serviceManager.getSetup().getCamera().getPosition();
    	serviceManager.getSetup().world.add(creditCard);
    	creditCard.setPosition(new Vec(camPos.x + 15.0f, 0.0f, camPos.z - 40.0f));
    	creditCard.setRotation(new Vec(90.0f, 0.0f, 180.0f));
    	creditCard.setScale(new Vec(4.5f, 1.0f, 4.0f));
    	
	}
	
	public void removeCard()
	{
		serviceManager.getSetup().world.remove(creditCard);	
	}
}
