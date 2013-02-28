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
		Bitmap bms = Bitmap.createScaledBitmap(bm, 175, 120, false);
		ImageView im = new ImageView(serviceManager.getSetup().myTargetActivity);
		im.setImageBitmap(bms);
		creditCard = GLFactory.getInstance().newTexturedSquare("creditCard", bm);
		creditCard.setOnLongClickCommand(new DragImage(im, this.toString()));
    	    	
    	serviceManager.getSetup().world.add(creditCard);
    	creditCard.setPosition(new Vec(15.0f, 0.0f, 0.0f));
    	creditCard.setRotation(new Vec(90.0f, 180.0f, 0.0f));
    	creditCard.setScale(new Vec(6.0f, 4.0f, 1.0f));
    	
	}
	
	public void removeCard()
	{
		serviceManager.getSetup().world.remove(creditCard);	
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
