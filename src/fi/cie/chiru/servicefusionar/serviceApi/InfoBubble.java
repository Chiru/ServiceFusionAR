package fi.cie.chiru.servicefusionar.serviceApi;

import util.Vec;

public class InfoBubble 
{
	private TextPopUp items[];
	private ServiceManager serviceManager;
	private ServiceApplication infobubble;
	private boolean visible;
	
	public InfoBubble(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
		visible = false;
	}
	
	public void visible()
	{
	    visible = !visible;
	    
		infobubble.setvisible(visible);
		
	    for(int i=0; i<items.length; i++)
	    {
	        items[i].visible();
	    }
	    
	}
	
	public boolean setInfoBubbleApplication(String name)
	{
		infobubble = this.serviceManager.getApplication(name);
		
		if(infobubble!=null)
			return true;
		else
			return false;
	}
	
	public void populateItems(String content[])
	{
		int contentLen = content.length;
		items = new TextPopUp[contentLen];
		
    	for(int i=0; i<contentLen; i++)
    	{
    		Vec infoBubblePosition = new Vec(infobubble.getPosition());
        	infoBubblePosition.add(0, 3.0f, 0);
	        TextPopUp infoItem = new TextPopUp(this.serviceManager);
	        infoItem.setDragText(content[i]);
	        Vec itemPosition = new Vec(infoBubblePosition);
    		itemPosition.add(0, i*1.1f, 0);
    		infoItem.setPosition(itemPosition);
   		
    		items[i] = infoItem;
    	}
		
	}

}
