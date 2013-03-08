package fi.cie.chiru.servicefusionar.serviceApi;

import android.content.ClipData;
import android.os.Handler;
import android.os.Looper;
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
	
	public DraggableImage(ServiceManager serviceManager, ImageView draggedItem, String str)
	{
	    iv = draggedItem;
	    this.str = str;
	    this.serviceManager = serviceManager;
	    setImageViewToLayout();
	}
	
//	@Override
	public void executeInUiThread()
	{
		View.DragShadowBuilder shadow = new DragShadowBuilder(iv);
		ClipData data = ClipData.newPlainText("DragData", str);
		iv.startDrag(data, shadow, null, 0);
	}
	
	private void setImageViewToLayout()
	{
		 Handler mHandler = new Handler(Looper.getMainLooper());

		 mHandler.post(new Runnable() 
		 {

			 @Override
			 public void run() 
			 {
				 RelativeLayout root = (RelativeLayout)serviceManager.getSetup().getGuiSetup().getMainContainerView();
				 root.removeView(iv);
				 root.addView(iv);
				 iv.setVisibility(View.INVISIBLE);
			 }

		 });
	}
	
}
