package fi.cie.chiru.servicefusionar.serviceApi;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.cie.chiru.servicefusionar.R;
import util.Log;
import util.Vec;


public class MovieManager
{
	private static final String LOG_TAG = "MovieManager";
	private ServiceManager serviceManager;
	private String movieData[][];
	private TextPopUp movieInfo[];
	private boolean movieInfoFilled;
	private int maxMovies = 5;
	
	String test1[] = {"19:00", "Lincoln", "Sali 1"}; 
	String test2[] = {"19:00", "Django Unchained", "Sali 2"};
	String test3[] = {"21:00", "Argo", "Sali 1"};
	String test4[] = {"21:00", "Hobitti - Odottamaton matka (2D)", "Sali 3"};
	String test5[] = {"21:00", "Django Unchained", "Sali 3"}; 
	
	
	public MovieManager(ServiceManager serviceManager)
	{
		movieData = new String[maxMovies][3];
		movieData[0] = test1;
		movieData[1] = test2;
		movieData[2] = test3;
		movieData[3] = test4;
		movieData[4] = test5;
        this.serviceManager = serviceManager;
		movieInfoFilled = false;
        movieInfo= new TextPopUp[maxMovies];
	}
	
    public void showMovieInfo()
    {
        if(!movieInfoFilled)
    	    fillMovieInfo();
        
        for(int i=0; i<maxMovies; i++)
        {
            movieInfo[i].visible();
        }
        
    }
    
    public void seatSelection()
    {
    	
    }
    
    public void login()
    {

    }
    
    public void payment()
    {
    	
    }
    
    private void fillMovieInfo()
    {
    	int longestTitle = getLongestMovieTitlteLen(movieData);
    	
    	for(int i=0; i<maxMovies; i++)
    	{	
    	    TextPopUp movieInfoItem = new TextPopUp(this.serviceManager);
    	    String movieInfoString = new String();
    		String time = new String();
    		String movieTitle = new String();
    		String auditorium = new String();
    		
    		time = movieData[i][0];
    		movieTitle = movieData[i][1];
    		auditorium = movieData[i][2];
    		
    		movieInfoString = time + " " + movieTitle + fillWhiteSpace(longestTitle - movieTitle.length()) + "    " + auditorium;
    		Log.d(LOG_TAG, movieInfoString);
    		movieInfoItem.setDragText(movieInfoString);
    		movieInfoItem.setPosition(new Vec(5.0f, 4.0f + i, -15.0f));
   		
    		movieInfo[i] = movieInfoItem;
    		movieInfoFilled =true;
    	}
    }
    
    private String fillWhiteSpace(int number)
    {
    	String whitespace = new String();
    	
    	for(int j=0; j<number; j++)
    		whitespace += " ";
    	
    	return whitespace;
    }
    
    private int getLongestMovieTitlteLen(String str[][])
    {
    	int len = 0;
    	
    	for(int k=0; k<maxMovies; k++)
    	{
    	    if(str[k][1].length() > len)
    	    	len = str[k][1].length();
    	}
    	
    	return len;
    }


}
