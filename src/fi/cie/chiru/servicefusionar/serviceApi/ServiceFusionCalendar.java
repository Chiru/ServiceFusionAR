package fi.cie.chiru.servicefusionar.serviceApi;

import fi.cie.chiru.servicefusionar.R;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RelativeLayout;

public class ServiceFusionCalendar 
{
	private ServiceManager serviceManager;
	
	public ServiceFusionCalendar(ServiceManager serviceManager)
	{
	    this.serviceManager = serviceManager;
	    createCalendar();
	}
	
    public void addEvent(int dayOfMonth, String time, String eventName)
    {
    	
    }
    
    private void createCalendar()
    {
    	Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {

			@Override
			public void run() 
			{
				LayoutInflater li = (LayoutInflater) serviceManager.getSetup().myTargetActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View v = li.inflate(R.layout.calendar, null); 
			    //CalendarView calendar = new CalendarView(serviceManager.getSetup().myTargetActivity);
			    //calendar.setAlpha(0.9f);
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				
				root.addView(v); 
			}
		});
    }
}
