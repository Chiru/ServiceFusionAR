package fi.cie.chiru.servicefusionar.serviceApi;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fi.cie.chiru.servicefusionar.groovesharkService.Grooveshark;
import fi.cie.chiru.servicefusionar.groovesharkService.PlaylistXmlParser;

public class MusicManager
{
    private final static String LOG_TAG = "GroovesharkMusicService";
    
    private Grooveshark grooveshark = null;
    private ServiceManager serviceManager = null;
	private InfoBubble infobubble;
	boolean musicPlaylistDownloaded;


    public MusicManager(ServiceManager serviceManager)
    {
    	musicPlaylistDownloaded = false;
    	Log.i(LOG_TAG, "Starting MusicManager");
    	this.serviceManager = serviceManager;
    	
//		String URL = "http://www.djonline.fi/playing/?id=487";
		
//		new XmlDownloader() { 
//	        protected void onPostExecute(String xmlData) {
//	    		PlaylistXmlParser parser = new PlaylistXmlParser();
//	    		parser.parse(xmlData);
//	    		fillMusicPlaylist();
//	        }
//	    }.execute(URL); 
		fillMusicPlaylist();
	    StartGrooveshark();
    }
    
    public void StartGrooveshark()
    {
    	if (grooveshark == null)
    		grooveshark = new Grooveshark();

    }
    
    public void StopPlaying(String serviceApplicationName)
    {
    	grooveshark.StopPlaying();
    }
    
	public void showPlaylist(String serviceApplicationName) 
	{
		if(!musicPlaylistDownloaded)
			fillMusicPlaylist();
    	
    	infobubble.visible();
		
	}
	
	public void playSong(String song)
	{
		grooveshark.SearchSong(song, 1);
		Log.d("MusicManager", "start playing song: " + song);
	}
	
	public InfoBubble getInfoBubble()
	{
	    return infobubble;
	}
	
	private void fillMusicPlaylist()
	{
		
		List<String> playlist = new ArrayList<String>();
		playlist.add("Pink Floyd - Comfortably Numb           ");
		playlist.add("Steven Wilson - Luminol                 ");
		playlist.add("Esbjörn Svensson - Seven days of Falling");
		
		infobubble = new InfoBubble(serviceManager);

 		if(infobubble.setInfoBubbleApplication("MusicInfobubble"))
 		    infobubble.populateItems(playlist, "MusicManager");
 		
 		musicPlaylistDownloaded = true;
	}
}
