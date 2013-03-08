package fi.cie.chiru.servicefusionar.Finnkino;

import java.util.Calendar;

import util.IO;
import util.Vec;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.cie.chiru.servicefusionar.R;
import fi.cie.chiru.servicefusionar.serviceApi.DraggableImage;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

public class MovieTicket 
{
	private ServiceManager serviceManager;
	private MeshComponent movieTicket;
	
	public MovieTicket(ServiceManager serviceManager, String movieTitle, SeatNumber seat)
	{
	    this.serviceManager = serviceManager;
	    createTicket(movieTitle, seat);
	}
	
	private void createTicket(String movieTitle, SeatNumber seat)
	{
		Handler mHandler = new Handler(Looper.getMainLooper());
    	final String movTitle = new String(movieTitle);
    	final String colNum = new String("Paikka " + seat.getCol());
    	final String rowNum = new String("Rivi " + seat.getRow());
    	
    	mHandler.post(new Runnable() 
    	{

			@Override
			public void run() 
			{
				String movInfo[] = movTitle.split(("\\s{2,}"));
				LayoutInflater li = (LayoutInflater) serviceManager.getSetup().myTargetActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View ticket = li.inflate(R.layout.movie_ticket, null);
				
				TextView title = (TextView)ticket.findViewById(R.id.elokuva_o);
				title.setText(movInfo[1]);
				
				TextView time = (TextView)ticket.findViewById(R.id.aika);
				time.setText(movInfo[0]);
				
				TextView date = (TextView)ticket.findViewById(R.id.pvm);
				date.setText(setDate());
				
				TextView aud = (TextView)ticket.findViewById(R.id.sali);
				aud.setText(movInfo[2]);
				
				TextView row = (TextView)ticket.findViewById(R.id.rivi);
				row.setText(rowNum);
				
				TextView col = (TextView)ticket.findViewById(R.id.paikka);
				col.setText(colNum);
				
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				root.addView(ticket);
				
				ticket.setVisibility(View.GONE);
				
				//ImageView im = (ImageView)ticket;
				Bitmap bm = IO.loadBitmapFromView(ticket);
				ImageView im = new ImageView(serviceManager.getSetup().myTargetActivity);
				im.setImageBitmap(bm);
				
				movieTicket = GLFactory.getInstance().newTexturedSquare("movieTicekt", bm);
				movieTicket.setOnLongClickCommand(new DraggableImage(serviceManager, im, this.toString()));
		    	    	
		    	serviceManager.getSetup().world.add(movieTicket);
		    	movieTicket.setPosition(new Vec(-10.0f, -10.0f, 0.0f));
		    	movieTicket.setRotation(new Vec(90.0f, 180.0f, 0.0f));
		    	movieTicket.setScale(new Vec(7.0f, 6.0f, 1.0f));
			}

		});	
    	
	}
	
	public void removeTicket()
	{
		serviceManager.getSetup().world.remove(movieTicket);	
	}
	
	private String setDate()
	{
		final Calendar c = Calendar.getInstance();

		int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH)  +1;
	    int day = c.get(Calendar.DAY_OF_MONTH);

	    return " "+day+ "."+month +"."+year;
	}
}
