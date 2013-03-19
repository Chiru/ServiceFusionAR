package fi.cie.chiru.servicefusionar.Finnkino;

import java.util.List;

import commands.Command;
import commands.ui.CommandInUiThread;

import fi.cie.chiru.servicefusionar.R;
import fi.cie.chiru.servicefusionar.calendar.CalendarEvent;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import util.IO;
import util.Log;
import util.Vec;

public class MoviePayment 
{
	private MeshComponent paymentScreen;
	private MeshComponent paymentArea;
	private MeshComponent cancelButton;
	private ServiceManager serviceManager;
	private String selectedMovie;
	private List<SeatNumber> selectedSeats;
	//private float camOffsetX = 0.0f;
	private float camOffsetY = 4.0f;
	private float camOffsetZ = -40.0f;
	private float paymentAreaOffsetY = 3.5f;
	private float cancelButtonOffsetY = -1.0f;
	
	public MoviePayment(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
	}
	
	public void paySelectedSeats(List<SeatNumber> seats)
	{
		selectedSeats = seats;
		createPaymentScreen();
	}
	
	public void setMovieName(String name)
	{
		this.selectedMovie = name;
	}
	
	private void createPaymentScreen()
	{
		Handler mHandler = new Handler(Looper.getMainLooper());

    	mHandler.post(new Runnable() 
    	{

			@Override
			public void run() 
			{
				TextView place;
				RelativeLayout.LayoutParams relativeLayoutParams;
				
				LayoutInflater li = (LayoutInflater) serviceManager.getSetup().myTargetActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				RelativeLayout rl = (RelativeLayout)li.inflate(R.layout.movie_payment, null); 
				TextView title = (TextView)rl.findViewById(R.id.elokuva_payment);
				title.setText(selectedMovie);

				for(int i=0; i<selectedSeats.size(); i++)
				{
					
					place = new TextView(serviceManager.getSetup().myTargetActivity);
					place.setId(10+i);
					place.setText(placeString(selectedSeats.get(i).getRow(), selectedSeats.get(i).getCol()));
					place.setTextSize(15);
					place.setTypeface(Typeface.MONOSPACE);
					place.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
					
					relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					
					
					if(i==0)
					{
						relativeLayoutParams.addRule(RelativeLayout.BELOW, title.getId());
						relativeLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, rl.findViewById(R.id.paikat).getId());
					}
					else
					{		
						relativeLayoutParams.addRule(RelativeLayout.BELOW, (10 + i-1));
						//relativeLayoutParams.setMargins(20, 5, 10, 0);
					}
					
					if(i==1)
						relativeLayoutParams.setMargins(25, 10, 10, 0);
					else
					    relativeLayoutParams.setMargins(25, 0, 10, 0);
					
					relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, rl.findViewById(R.id.paikat).getId());
					//relativeLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT);
					rl.addView(place, relativeLayoutParams);
				}
				
				TextView count = (TextView)rl.findViewById(R.id.yht);
				relativeLayoutParams = (LayoutParams)count.getLayoutParams();
				relativeLayoutParams.addRule(RelativeLayout.BELOW, (10 + selectedSeats.size()-1));
				
				//final price
				relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				relativeLayoutParams.addRule(RelativeLayout.BELOW, count.getId());
				relativeLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, count.getId());
				relativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, rl.findViewById(R.id.paikat).getId());
				relativeLayoutParams.setMargins(25, 0, 10, 0);
				
				place = new TextView(serviceManager.getSetup().myTargetActivity);
				place.setTextSize(15);
				place.setText("                             " + selectedSeats.size()*9 + "e");
				place.setTypeface(Typeface.MONOSPACE);
				place.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
				rl.addView(place, relativeLayoutParams);
				
				
				View v = (View)rl;
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				root.addView(rl);
				v.setVisibility(View.GONE);
				paymentScreen = GLFactory.getInstance().newTexturedSquare("paymentscreen", IO.loadBitmapFromView(v));
				
				Vec camPos = serviceManager.getSetup().getCamera().getPosition();
				int offset = selectedSeats.size();
				serviceManager.getSetup().world.add(paymentScreen);
				paymentScreen.setPosition(new Vec(camPos.x, camPos.y + camOffsetY, camPos.z + camOffsetZ));
				paymentScreen.setRotation(new Vec(90.0f, 0.0f, 180.0f));
				paymentScreen.setScale(new Vec(15.0f, 1.0f, (14.0f + (float)offset)));
				
				createPaymentArea(v, offset);
				createCancelButton(v, offset);
			}
    	});
			
	}
	
	private class handlePayment extends CommandInUiThread 
	{


		@Override
		public boolean execute(Object transfairObject) 
		{
			return execute();
		}


		@Override
		public void executeInUiThread() 
		{
			serviceManager.getSetup().world.remove(paymentArea);
			serviceManager.getSetup().world.remove(paymentScreen);
			serviceManager.getSetup().world.remove(cancelButton);
			paymentArea = null;
			paymentScreen = null;
			cancelButton = null;
			serviceManager.getMovieManager().cc.removeCard();
			serviceManager.getMovieManager().cc = null;
			serviceManager.setVisibilityToAllApplications(true);
			serviceManager.getMovieManager().getInfoBubble().visible();
			serviceManager.getMovieManager().createTickets(selectedSeats);
			
			String cEvent[] = selectedMovie.split(("\\s{2,}"));
			serviceManager.getCalendar().addEvent(new CalendarEvent(cEvent[0], (cEvent[1] + " " + cEvent[2])));
		}	
	}
	
    private void createPaymentArea(View v, int offset)
    {
    	View pArea = v.findViewById(R.id.maksukortti);
		pArea.setVisibility(View.INVISIBLE);
		pArea.setAlpha(0.9f);
		
		paymentArea = GLFactory.getInstance().newTexturedSquare("paymentArea", IO.loadBitmapFromView(pArea));
		
		Vec camPos = serviceManager.getSetup().getCamera().getPosition();
		serviceManager.getSetup().world.add(paymentArea);
		paymentArea.setPosition(new Vec(camPos.x, (camPos.y + paymentAreaOffsetY - ((float)offset)*0.7f), camPos.z + camOffsetZ));
		paymentArea.setRotation(new Vec(90.0f, 0.0f, 180.0f));
		paymentArea.setScale(new Vec(6.0f, 1.0f, 6.0f));
		paymentArea.setOnDoubleClickCommand(new handlePayment());
    }
    
    private void createCancelButton(View v, int offset)
    {
    	View cancel = v.findViewById(R.id.peruuta);
    	cancel.setVisibility(View.GONE);
    	//cancel.setAlpha(0.9f);
		
    	cancelButton = GLFactory.getInstance().newTexturedSquare("paymentCancel", IO.loadBitmapFromView(cancel));
    	
    	Vec camPos = serviceManager.getSetup().getCamera().getPosition();
		serviceManager.getSetup().world.add(cancelButton);
		cancelButton.setPosition(new Vec(camPos.x, (camPos.y + cancelButtonOffsetY - ((float)offset)*0.7f), camPos.z + camOffsetZ));
		cancelButton.setRotation(new Vec(90.0f, 0.0f, 180.0f));
		cancelButton.setScale(new Vec(6.0f, 1.0f, 2.0f));
		cancelButton.setOnClickCommand(new Cancel());
    	
    }
    
	private String placeString(int row, int col)
	{
		String colWhiteSpace = "";
		String rowWhiteSpace = "";
		
		if(row<10)
			rowWhiteSpace = " ";
		
		if(col<10)
			colWhiteSpace = " ";
			
		return "rivi: " + row + rowWhiteSpace + "  paikka: " + col + colWhiteSpace + "  hinta: 9e";
		
	}
	
	private class Cancel extends Command
	{		
		
		@Override
		public boolean execute() 
		{
			serviceManager.getSetup().world.remove(paymentArea);
			serviceManager.getSetup().world.remove(paymentScreen);
			serviceManager.getSetup().world.remove(cancelButton);
			paymentArea = null;
			paymentScreen = null;
			cancelButton = null;
			serviceManager.getMovieManager().cc.removeCard();
			serviceManager.getMovieManager().cc = null;
			serviceManager.setVisibilityToAllApplications(true);
			serviceManager.getMovieManager().getInfoBubble().visible();		
			
			return true;
		}
	}
}
