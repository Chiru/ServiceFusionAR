package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.List;

import android.location.Location;
import android.util.Log;

import util.Vec;

public class InfoBubble extends ServiceApplication
{
	private final static String LOG_TAG = "InfoBubble"; 
	private DraggableText items[] = null;
	private ServiceManager serviceManager;
	
	public InfoBubble(ServiceManager serviceManager, String name)
	{
		super (serviceManager, name);
		this.serviceManager = serviceManager;
		visible = false;
	}
	
	@Override
	public void setvisible(boolean visible)
	{
	    super.setvisible(visible);
	    if (items != null)
	    {
		    for(int i=0; i<items.length; i++)
		    {
		        items[i].visible();
		    }
	    }
	    
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void populateItems(List<String> content, String contentManager)
	{
		int contentLen = content.size();
		items = new DraggableText[contentLen];
		
    	for(int i=0; i<contentLen; i++)
    	{
    		Vec infoBubblePosition = new Vec(this.getPosition());
        	infoBubblePosition.add(0, 7.0f, 0);
        	DraggableText infoItem = new DraggableText(this.serviceManager);
	        infoItem.setDragText(content.get(i));
	        infoItem.setDragTextManager(contentManager);
	        Vec itemPosition = new Vec(infoBubblePosition);
    		itemPosition.add(0, -i*1.1f, 0);
    		infoItem.setPosition(itemPosition);
   		
    		items[i] = infoItem;
    	}
	}
	
	@Override
	public void servicePlaceFromLocation(Location location)
	{
		if (geoLocation == null)
		{
			geoLocation = new Location("LocationInfo");
			if (this.getName().equals("MovieInfobubble"))
			{
				geoLocation.set(serviceManager.getApplication("MovieIcon").getGeoLocation());
			}
			else if (this.getName().equals("MusicInfobubble"))
			{
				geoLocation.set(serviceManager.getApplication("MusicIcon").getGeoLocation());
			}
		}
		
		float[] results= new float[3];
		// The computed distance in meters is stored in results[0].
		// If results has length 2 or greater, the initial bearing is stored in results[1].
		// If results has length 3 or greater, the final bearing is stored in results[2].
		Location.distanceBetween(location.getLatitude(), location.getLongitude(), this.geoLocation.getLatitude(), this.geoLocation.getLongitude(), results);
		float bearing = (float)((360 + results[2]) % 360f);
		
		Log.i(LOG_TAG, "Bearing for application " + this.getName() + ": " + bearing);
		
		Vec pos = new Vec();
		float z;
		float x;
		
		if (this.getName().equals("MovieInfobubble"))
		{
			z = 38f * (float)Math.cos(Math.toRadians(bearing + 4));
			x = 38f * (float)Math.sin(Math.toRadians(bearing + 4));
							
			Log.i(LOG_TAG, "position = " + x + ", " + z);
			pos.setTo(x, 3, -z);
			this.setPosition(pos);
			
			serviceManager.getMovieManager().positionInitialized();
		}
		else if (this.getName().equals("MusicInfobubble"))
		{
			z = 38f * (float)Math.cos(Math.toRadians(bearing + 6));
			x = 38f * (float)Math.sin(Math.toRadians(bearing + 6));
			
			Log.i(LOG_TAG, "position = " + x + ", " + z);
			pos.setTo(x, 3, -z);
			this.setPosition(pos);
			
			serviceManager.getMusicManager().positionInitialized();
		}
	}
	
	@Override
	public void attachToCamera(boolean attach)
	{
		super.attachToCamera(attach);
		for (int i = 0; i < items.length; i++)
		{
			items[i].attachToCamera(attach);
		}
	}

}
