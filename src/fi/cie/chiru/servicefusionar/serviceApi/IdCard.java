/** 
 *  Copyright (c) 2013 Center for Internet Excellence, University of Oulu, All Rights Reserved
 *  For conditions of distribution and use, see copyright notice in license.txt
 */

package fi.cie.chiru.servicefusionar.serviceApi;

import java.io.IOException;
import java.io.InputStream;

import commands.ui.CommandInUiThread;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import util.IO;
import util.Log;
import util.Vec;
import fi.cie.chiru.servicefusionar.R;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

public class IdCard 
{
	private static final String LOG_TAG = "IdCard";
	private String idDataFile = "user_idcard.txt";
    private String name;
    private String surName;
    private String dateOfBirth;
    private String gender;
    private String email;
    private String phoneNumber;
	private ServiceManager serviceManager;
	private MeshComponent idCard;
       
    public IdCard(ServiceManager serviceManager)
    {
    	this.serviceManager = serviceManager;
    	initCard();
    }

	@Override
	public String toString() 
	{
		String str = null;
		str = name + " " + surName + " " + dateOfBirth + " " + gender + " " + email + " " + phoneNumber;
	    return str;
	}
	
	private void initCard()
	{
		try 
		{
			InputStream in;
			in = serviceManager.getSetup().myTargetActivity.getAssets().open(idDataFile);
			String fileContent = IO.convertInputStreamToString(in);
			Log.d(LOG_TAG, fileContent);
			String idInfo[] = fileContent.split("\n");
			name = idInfo[0];
			surName = idInfo[1];
			dateOfBirth = idInfo[2];
			gender = idInfo[3];
			email = idInfo[4];
			phoneNumber = idInfo[5];
			
		} 
		catch (IOException e) 
		{
			Log.d(LOG_TAG, "couldn't open file: " + idDataFile);
			e.printStackTrace();
		}
		
		Bitmap bm = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.id_card);
		ImageView im = new ImageView(serviceManager.getSetup().myTargetActivity);
		im.setImageBitmap(bm);
        idCard = GLFactory.getInstance().newTexturedSquare("idCard", bm);
        idCard.setOnLongClickCommand(new DraggableImage(serviceManager, im, this.toString()));
        
        Vec camPos = serviceManager.getSetup().getCamera().getPosition();    	
    	serviceManager.getSetup().world.add(idCard);
    	idCard.setPosition(new Vec(camPos.x + 15.0f, 0.0f, camPos.z - 40.0f));
    	idCard.setRotation(new Vec(90.0f, 0.0f, 180.0f));
    	idCard.setScale(new Vec(4.5f, 1.0f, 4.0f));
    	
	}
	
	public void removeCard()
	{
		serviceManager.getSetup().world.remove(idCard);	
	}
    
}
