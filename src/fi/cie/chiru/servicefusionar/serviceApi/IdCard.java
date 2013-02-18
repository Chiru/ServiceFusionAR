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
		Bitmap bms = Bitmap.createScaledBitmap(bm, 175, 120, false);
		ImageView im = new ImageView(serviceManager.getSetup().myTargetActivity);
		im.setImageBitmap(bms);
        MeshComponent idCard = GLFactory.getInstance().newTexturedSquare("idCard", bm);
        idCard.setOnLongClickCommand(new DragImage(im, this.toString()));
    	    	
    	serviceManager.getSetup().world.add(idCard);
    	idCard.setPosition(new Vec(15.0f, 0.0f, 0.0f));
    	idCard.setRotation(new Vec(90.0f, 180.0f, 0.0f));
    	idCard.setScale(new Vec(6.0f, 4.0f, 1.0f));
    	
	}
	
	private class DragImage extends CommandInUiThread 
	{
		private ImageView iv;
		private String str;
		
		public DragImage(ImageView DraggedItem, String str)
		{
		    iv = DraggedItem;
		    this.str = str;
		}
		
	//	@Override
		public void executeInUiThread()
		{

			RelativeLayout root = (RelativeLayout)serviceManager.getSetup().getGuiSetup().getMainContainerView();

			root.removeView(iv);
			root.addView(iv);
			
			//iv.setVisibility(View.GONE);
			iv.setVisibility(View.INVISIBLE);
		    
			View.DragShadowBuilder shadow = new DragShadowBuilder(iv);
			ClipData data = ClipData.newPlainText("DragData", str);
			iv.startDrag(data, shadow, null, 0);
		}
		
	}
    
}
