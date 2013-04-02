package fi.cie.chiru.servicefusionar.Finnkino;


import java.util.ArrayList;
import java.util.List;

import commands.Command;
import commands.system.CommandDeviceVibrate;

import util.IO;
import util.Log;
import util.Vec;
import fi.cie.chiru.servicefusionar.R;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Auditorium 
{
	private View plazaView;
	private Zoom zoom;
	private MeshComponent plaza;
	private ServiceManager serviceManager;
	private int plaza1Margins [][][];
	private List<SeatNumber> selectedSeats;
	private Bitmap green_seat;
	private Bitmap wheelchair_seat;
	private Bitmap yellow_seat;
	private Command vibratefeedback;
	private boolean small = false;
	
	public Auditorium(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
		
		if(serviceManager.getSetup().getScreenHeigth() == 897 && serviceManager.getSetup().getScreenWidth() == 540);
            small = true;
            
		if(small)
		{
		    green_seat = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.green_seat_small);
		    wheelchair_seat = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.wheelchair_small);
		    yellow_seat = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.yellow_seat_small);
		}
		else
		{
		    green_seat = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.green_seat);
		    wheelchair_seat = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.wheelchair);
		    yellow_seat = IO.loadBitmapFromId(serviceManager.getSetup().myTargetActivity, R.drawable.yellow_seat);
		}
		
		vibratefeedback = new CommandDeviceVibrate(serviceManager.getSetup().myTargetActivity, 30);
		selectedSeats = new ArrayList<SeatNumber>();
		zoom = new Zoom(true);
	}
	
	public void createAuditoriumScreen(final String plazaName)
	{
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {

			@Override
			public void run() 
			{
				LayoutInflater li = (LayoutInflater) serviceManager.getSetup().myTargetActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				if(plazaName.compareTo("Sali1")==0)
					plazaView = createPlaza1(li);
				
				plazaView.setOnTouchListener(new Zoom(false));
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				root.addView(plazaView);
				//plazaView.setVisibility(View.INVISIBLE);

//				plaza = GLFactory.getInstance().newTexturedSquare("plaza", IO.loadBitmapFromView(plazaView, 40, 40));
//
//				serviceManager.getSetup().world.add(plaza);
//				plaza.setPosition(new Vec(0.0f, -5.0f, 0.0f));
//				plaza.setRotation(new Vec(45.0f, 180.0f, 0.0f));
//				plaza.setScale(new Vec(15.5f, 10.0f, 15.0f));
			}
		});
	}
	
	private View createPlaza1(LayoutInflater l)
	{
		initPlaza1Margins();
		GridLayout gl = (GridLayout)l.inflate(R.layout.movie_seatselection2, null);
		
		setPlaza1Seats(gl);
		connectButtons(gl);
		View sv = (View)gl;
		
		return sv;
	}
	
	private void setPlaza1Seats(GridLayout gl)
	{	
		Spec colSpec;
        Spec rowSpec;
		GridLayout.LayoutParams p;
		ImageView seat;
		gl.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
		
		int seatCol;
		int seatRow = gl.getRowCount();

		for(int j=0; j<gl.getRowCount()-1; j++)
		{
			seatRow--;
			seatCol = 0;
			
		    for(int i=0; i<gl.getColumnCount()-1; i++)
		    {
		    	
		    	if((i==2 || i==3 || i==30 || i==31) && j>=1)
		    		continue;
		    	
		    	if((i<3 || i>30) && j>8)
		    		continue;
		    	
		    	if((i==4 || i==29) && j>8)
		    		continue;
		    	
		        colSpec = GridLayout.spec(i, 1);
			    rowSpec = GridLayout.spec(j, 1);
                p = new GridLayout.LayoutParams(rowSpec, colSpec);
                seatCol++;

        		if(plaza1Margins[i][j] != null)
			        p.setMargins(plaza1Margins[i][j][0], plaza1Margins[i][j][1], plaza1Margins[i][j][2], plaza1Margins[i][j][3]);
        		
			    p.setGravity(Gravity.START|Gravity.END);
			
			    seat = new ImageView(serviceManager.getSetup().myTargetActivity);
			    if(((i>4 && i<8) || (i>25 && i<29)) && j==14)
			    {
			        seat.setImageBitmap(wheelchair_seat);
			        seat.setTag(new SeatTag(seatRow, seatCol, "wheelchair"));
			    }
			    else
			    {
			        seat.setImageBitmap(green_seat);
			        seat.setTag(new SeatTag(seatRow, seatCol,"green_seat"));
			    }

			    seat.setOnClickListener(new View.OnClickListener() 
			    {
			    	@Override
                    public void onClick(View v) 
                    {
                        changeSeat(v);
                    }
                });

			    //seat.setOnTouchListener(zoom);
			    gl.addView(seat, p);
			}
		    
		}
	}
	
	private void changeSeat(View v)
	{
    	ImageView iv = (ImageView)v;
    	SeatTag tag = (SeatTag)iv.getTag();

    	if(tag.getType().compareTo("green_seat")==0)
    	{
    	    iv.setImageBitmap(yellow_seat);
    	    tag.setType("yellow_seat");
    	    selectedSeats.add(tag.getSeatNum());
    	}
    	else if(tag.getType().compareTo("wheelchair")==0)
    	{
    		iv.setImageBitmap(yellow_seat);
    		tag.setType("yellow_seat_wheelchair");
    		selectedSeats.add(tag.getSeatNum());
    	}
    	else if(tag.getType().compareTo("yellow_seat")==0)
    	{
    		iv.setImageBitmap(green_seat);
    		tag.setType("green_seat");
    		selectedSeats.remove(tag.getSeatNum());
    	}
    	else if(tag.getType().compareTo("yellow_seat_wheelchair")==0)
    	{
    		iv.setImageBitmap(wheelchair_seat);
    		tag.setType("wheelchair");
    		selectedSeats.remove(tag.getSeatNum());
    	}
	}
	
	private void connectButtons(GridLayout gl)
	{
		Button okb = (Button)gl.findViewById(R.id.seat_ok);
		okb.setOnClickListener(new View.OnClickListener()
		{
	        
			@Override
	        public void onClick(View v) 
	        {
				vibratefeedback.execute();
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				plazaView.setVisibility(View.GONE);
				root.removeView(plazaView);
				serviceManager.getMovieManager().payment(selectedSeats);
	        }
	    });
		
		Button cancelb = (Button)gl.findViewById(R.id.seat_peruuta);
		cancelb.setOnClickListener(new View.OnClickListener()
		{
	        
			@Override
	        public void onClick(View v) 
	        {
				vibratefeedback.execute();
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				plazaView.setVisibility(View.GONE);
				root.removeView(plazaView);
				serviceManager.setVisibilityToAllApplications(true);
				serviceManager.getMovieManager().getInfoBubble().visible();
				serviceManager.getSetup().camera.setUIMode(false);
				
	        }
	    });
	}
	
	private void initPlaza1Margins()
	{
		plaza1Margins = new int[34][15][];
		
		int topFirstRow;
		int leftFirstCol;
		int rightFirstCol;
		int left;
		int right;
		int bottom = 0;
		
		if(small)
		{
			left = 2;
			right = 2;
			topFirstRow = 64;		
			leftFirstCol = 92;
			rightFirstCol = 100;
		}
		else
		{
			left = 3;
			right = 3;
			topFirstRow = 74;		
			leftFirstCol = 117;
		    rightFirstCol = 120;
		}
		
	    plaza1Margins[0][0] = new int[] {leftFirstCol,topFirstRow,right,bottom};
	    plaza1Margins[1][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[2][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[3][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[4][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[5][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[6][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[7][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[8][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[9][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[10][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[11][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[12][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[13][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[14][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[15][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[16][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[17][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[18][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[19][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[20][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[21][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[22][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[23][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[24][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[25][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[26][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[27][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[28][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[29][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[30][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[31][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[32][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[33][0] = new int[] {left,topFirstRow,right,bottom};
	    plaza1Margins[33][0] = new int[] {left,topFirstRow,rightFirstCol,bottom};
	}
	
	private class SeatTag {
		
        private SeatNumber seatNum;
        private String type;
		
		public SeatTag(int row, int col, String type)
		{
		    this.seatNum = new SeatNumber(row, col);
		    this.type = type;
		}

		public String getType() 
		{
			return type;
		}

		public void setType(String type) 
		{
			this.type = type;
		}

		public SeatNumber getSeatNum() {
			return seatNum;
		}

		public void setSeatNum(SeatNumber seatNum) {
			this.seatNum = seatNum;
		}
		
	}
	
	private class Zoom implements OnTouchListener
	{
		PointF startPoint = new PointF();	
		PointF prevTransPoint = new PointF();
		PointF curTransPoint = new PointF(0,0);
		float prevDist = 1.0f;
		float curDist;
		float scaleFact = 1.06f;
		float transFact = 1.02f;
		float maxScale = 3.0f;
		float minScale = 1.0f;
		boolean passEventsOn = true;
		
	    public Zoom(boolean passEventsOn)
	    {
	    	this.passEventsOn = passEventsOn;
	    }
		 
		@Override
		public boolean onTouch(View arg0, MotionEvent event) 
		{	
			int action = event.getActionMasked();

			switch (action) 
			{
			case MotionEvent.ACTION_POINTER_DOWN:
				startPoint.set(event.getX(), event.getY());
//				if(event.getPointerCount() == 2)
//				{
//				    float x = (event.getX(0) + event.getX(1))/2.0f;
//				    float y = (event.getY(0) + event.getY(1))/2.0f;
//				    Log.d("--------------------", "startPoint x: "+ x + " y: " + y);
//				    startPoint.set(x, y);
//				}
				break;

			case MotionEvent.ACTION_MOVE:
				if(event.getPointerCount() == 2)
				{
					prevDist = curDist;
					curDist = distance(event);
					if(curDist > prevDist)
						zoom(true, startPoint);
					else
						zoom(false, startPoint);
					
				}
				else
				{
					prevTransPoint.set(curTransPoint.x, curTransPoint.y);
					curTransPoint.set(event.getX(), event.getY());
					translate(curTransPoint.x - prevTransPoint.x, curTransPoint.y - prevTransPoint.y);
				}
				break;
			}

			if(passEventsOn)
				return false;
			else
			    return true;
		}
		
		//true scale-up, false scale down
		private void zoom(boolean scale, PointF pivot) 
		{
//			plazaView.setPivotX(pivot.x);
//			plazaView.setPivotY(pivot.y);
			if(scale)
			{
			    plazaView.setScaleX(Math.min(plazaView.getScaleX()*scaleFact, maxScale));
			    plazaView.setScaleY(Math.min(plazaView.getScaleY()*scaleFact, maxScale));
			}
			else
			{
			    plazaView.setTranslationX(0.0f);
			    plazaView.setTranslationY(0.0f);
			    plazaView.setScaleX(Math.max(plazaView.getScaleX()/scaleFact, minScale));
			    plazaView.setScaleY(Math.max(plazaView.getScaleY()/scaleFact, minScale));
			}
	    }
		
		private float distance(MotionEvent event) 
		{
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float)Math.sqrt(x * x + y * y);
		}
		
		private void translate(float x, float y)
		{

			if(Math.abs(x) < 25 && Math.abs(y) < 25)
			{
			    if(plazaView.getScaleX() > 1 || plazaView.getScaleY() > 1)
			    {
			        plazaView.setTranslationX(plazaView.getTranslationX()+x);
			        plazaView.setTranslationY(plazaView.getTranslationY()+y);
			    }
			}
		}
	}
}

