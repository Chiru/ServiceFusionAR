package fi.cie.chiru.servicefusionar.serviceApi;

import android.content.ClipData;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import commands.ui.CommandInUiThread;

public class DraggableImage extends CommandInUiThread 
{
	private ImageView iv;
	private String str;
	private ServiceManager serviceManager;
	
	public DraggableImage(ServiceManager serviceManager, ImageView DraggedItem, String str)
	{
	    iv = DraggedItem;
	    this.str = str;
	    this.serviceManager = serviceManager;
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
