package fi.cie.chiru.servicefusionar.serviceApi;

import gl.ObjectPicker;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

public class CustomDragEventListener implements OnDragListener 
{
	float screenHeight;
	
	public CustomDragEventListener(float screenHeight)
	{
		this.screenHeight = screenHeight;
	}
	@Override
	public boolean onDrag(View v, DragEvent event) 
	{
		
		 final int action = event.getAction();

		 switch(action) 
		 {

		     case DragEvent.ACTION_DROP:
		    	 
		    	 String dragInfo[] = {"", ""};

		    	 for(int i = 0; i<event.getClipData().getItemCount(); i++)
		    	 {
		    		 dragInfo[i] = (String)event.getClipData().getItemAt(i).getText();
		    	 }
		    	 
		    	 float x, y;
		    	 x = event.getX();
		    	 y = screenHeight - event.getY();
		    	 
		    	 ObjectPicker.getInstance().setDoubleClickPosition(x, y, new DragDataObject(dragInfo[0], dragInfo[1]));
                 break;  
		 }				 
		 
		return true;
	}
	
}
