package fi.cie.chiru.servicefusionar.Finnkino;

import commands.Command;
import commands.ui.CommandInUiThread;

import util.IO;
import util.Vec;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.cie.chiru.servicefusionar.R;
import fi.cie.chiru.servicefusionar.serviceApi.ServiceManager;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;

public class LogInScreen 
{
	private MeshComponent loginScreen;
	private ServiceManager serviceManager;
	private MeshComponent loginScreenFilled;
	private MeshComponent okbutton;
	private MeshComponent cancelbutton;
	
	public LogInScreen(ServiceManager serviceManager)
	{
		this.serviceManager = serviceManager;
	}
	
	public void createLogInScreen(String movieTitle)
	{

		//View v;
    	Handler mHandler = new Handler(Looper.getMainLooper());
    	final String title = new String(movieTitle);
    	mHandler.post(new Runnable() {

			@Override
			public void run() 
			{
				LayoutInflater li = (LayoutInflater) serviceManager.getSetup().myTargetActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View v = li.inflate(R.layout.movielogin, null); 
				TextView tv = (TextView)v.findViewById(R.id.elokuva);
				tv.setText(title);
				RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
				root.addView(v);
				
				v.setVisibility(View.GONE);
				
				loginScreen = GLFactory.getInstance().newTexturedSquare("loginscreen", IO.loadBitmapFromView(v, 30, 30));
				loginScreen.setOnDoubleClickCommand(new fillLogInScreen(v));
				
				serviceManager.getSetup().world.add(loginScreen);
		    	loginScreen.setPosition(new Vec(0.0f, 4.0f, 0.0f));
		    	loginScreen.setRotation(new Vec(90.0f, 0.0f, 180.0f));
		    	loginScreen.setScale(new Vec(15.0f, 10.0f, 15.0f));
		    	
		    	createButtons(v);
		    	okbutton.setOnClickCommand(new Accept(false));
				cancelbutton.setOnClickCommand(new Cancel(false));
			}

		});
		
	}
	
	private class fillLogInScreen extends CommandInUiThread 
	{
		private String str;
		private View lv;

		public fillLogInScreen(View logView)
		{
			lv = logView;
		}

		@Override
		public boolean execute(Object transfairObject) 
		{
			str = (String)transfairObject;
			return execute();
		}


		@Override
		public void executeInUiThread() 
		{
			String idInfo[] = str.split(" ");

			EditText name_et = (EditText)lv.findViewById(R.id.et_etunimi);
			name_et.setText(idInfo[0]);

			EditText surName_et = (EditText)lv.findViewById(R.id.et_sukunimi);
			surName_et.setText(idInfo[1]);

			EditText dateOfBirth_et = (EditText)lv.findViewById(R.id.et_synaika);
			dateOfBirth_et.setText(idInfo[2]);

			if(idInfo[3].compareTo("Male")==0)
			{
				RadioButton gender_rb = (RadioButton)lv.findViewById(R.id.radioMies);
				gender_rb.setChecked(true);
			}

			if(idInfo[3].compareTo("Female")==0)
			{
				RadioButton gender_rb = (RadioButton)lv.findViewById(R.id.radioNainen);
				gender_rb.setChecked(true);

			}	

			EditText email_et = (EditText)lv.findViewById(R.id.et_email);
			email_et.setText(idInfo[4]);

			EditText phoneNumber_et = (EditText)lv.findViewById(R.id.et_puh);
			phoneNumber_et.setText(idInfo[5]);

			RelativeLayout root = (RelativeLayout) serviceManager.getSetup().getGuiSetup().getMainContainerView();
			root.removeView(lv);
			root.addView(lv);
			lv.setVisibility(View.GONE);

			loginScreenFilled = GLFactory.getInstance().newTexturedSquare("loginscreenFilled", IO.loadBitmapFromView(lv, 30, 30));
			serviceManager.getSetup().world.remove(loginScreen);
			serviceManager.getSetup().world.remove(okbutton);
			serviceManager.getSetup().world.remove(cancelbutton);

			serviceManager.getSetup().world.add(loginScreenFilled);
			loginScreenFilled.setPosition(new Vec(0.0f, 4.0f, 0.0f));
			loginScreenFilled.setRotation(new Vec(90.0f, 0.0f, 180.0f));
			loginScreenFilled.setScale(new Vec(15.0f, 10.0f, 1.0f));

			createButtons(lv);
			okbutton.setOnClickCommand(new Accept(true));
			cancelbutton.setOnClickCommand(new Cancel(true));

		}	
	}
	 
	private void createButtons(View v)
	{
		View ok = v.findViewById(R.id.ok);
		ok.setVisibility(View.INVISIBLE);
		
		View cancel = v.findViewById(R.id.peruuta);
		cancel.setVisibility(View.INVISIBLE);
		 
		okbutton = GLFactory.getInstance().newTexturedSquare("ok", IO.loadBitmapFromView(ok));
        cancelbutton = GLFactory.getInstance().newTexturedSquare("cancel", IO.loadBitmapFromView(cancel));
        
        serviceManager.getSetup().world.add(okbutton);
    	okbutton.setPosition(new Vec(7.8f, 0.0f, 0.0f));
    	okbutton.setRotation(new Vec(90.0f, 0.0f, 180.0f));
    	okbutton.setScale(new Vec(4.0f, 1.5f, 1.0f));
    	
    	serviceManager.getSetup().world.add(cancelbutton);
    	cancelbutton.setPosition(new Vec(-7.6f, 0.0f, 0.0f));
    	cancelbutton.setRotation(new Vec(90.0f, 0.0f, 180.0f));
    	cancelbutton.setScale(new Vec(4.0f, 1.5f, 1.0f));
	}
	 
	private class Accept extends Command
	{
		private boolean screenfilled = false;
		
		public Accept(boolean screenfilled)
		{
		    this.screenfilled = screenfilled;	
		}
		
		@Override
		public boolean execute() 
		{
			
			if(screenfilled)
			{
				serviceManager.getMovieManager().ic.removeCard();
				serviceManager.getMovieManager().ic = null;
				serviceManager.getSetup().world.remove(okbutton);
				serviceManager.getSetup().world.remove(cancelbutton);
			    serviceManager.getSetup().world.remove(loginScreenFilled);
			    okbutton = null;
			    cancelbutton = null;
			    loginScreenFilled = null;
			    serviceManager.getMovieManager().seatSelection();
			}
			else
			{
				//serviceManager.getSetup().world.remove(loginScreen);
				//serviceManager.setVisibilityToAllApplications(true);
			}
			
			return true;
		}
	}
	
	private class Cancel extends Command
	{
		private boolean screenfilled = false;
		
		public Cancel(boolean screenfilled)
		{
		    this.screenfilled = screenfilled;	
		}
		
		@Override
		public boolean execute() 
		{
			serviceManager.getMovieManager().ic.removeCard();
			serviceManager.getMovieManager().ic = null;
			serviceManager.getSetup().world.remove(okbutton);
			serviceManager.getSetup().world.remove(cancelbutton);
		    okbutton = null;
		    cancelbutton = null;
			
			if(screenfilled)
			{
			    serviceManager.getSetup().world.remove(loginScreenFilled);
			    loginScreenFilled = null;
			}
			    
			else
			{
				serviceManager.getSetup().world.remove(loginScreen);
			    loginScreen = null;
			}

			serviceManager.setVisibilityToAllApplications(true);
			serviceManager.getMovieManager().getInfoBubble().visible();
			
			return true;
		}
	}
}
