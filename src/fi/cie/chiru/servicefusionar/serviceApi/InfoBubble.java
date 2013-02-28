package fi.cie.chiru.servicefusionar.serviceApi;

import java.util.List;

import util.Vec;

public class InfoBubble 
{
	private DraggableText items[];
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
	
	public void populateItems(List<String> content, String contentManager)
	{
		int contentLen = content.size();
		items = new DraggableText[contentLen];
		
    	for(int i=0; i<contentLen; i++)
    	{
    		Vec infoBubblePosition = new Vec(infobubble.getPosition());
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

}
