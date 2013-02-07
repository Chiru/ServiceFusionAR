package fi.cie.chiru.servicefusionar.serviceApi;

import gl.ObjectPicker;
import util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.RelativeLayout;

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
		    	 
		    	 float x, y;
		    	 x = event.getX();
		    	 y = screenHeight - event.getY();
		    	 ObjectPicker.getInstance().setDoubleClickPosition(x, y);
                 break;  
		 }				 
		 
		return true;
	}
	
}
