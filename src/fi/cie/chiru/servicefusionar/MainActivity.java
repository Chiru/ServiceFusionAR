package fi.cie.chiru.servicefusionar;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;
import java.io.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import system.ArActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	private static final String TAG = "fi.cie.chiru.servicefusionar";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		LinearLayout l = new LinearLayout(this);
//		l.addView(newButton("boy.dae", "boy.png"));
		l.addView(newButton("firefox_medium_617.dae", "firefox-logo-full.jpg"));
		l.addView(newButton("skype_medium_330.dae", "skype.jpg"));
		l.addView(newButton("twitter_medium_397.dae", "twitter2.jpg"));

		l.setOrientation(LinearLayout.VERTICAL);
		setContentView(l);
		
		File f = extractScript("scripts", "hello.py");
        //f = extractScript("scripts", "feedparser.py");
				
		Context context = getApplicationContext();
		CharSequence text = f.toString();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
        
        //Intent intent = buildStartInTerminalIntent(f); // /mnt/sdcard/sl4a/scripts/hello_world.py")); //scripts/sftest_ylenews.py")); //
        //Log.d("SL4A Launcher", "The intent is " + intent.toString());
        //startActivity(intent);
	}

	private View newButton(final String fileName, final String textureName) 
	{
		Button b = new Button(this);
		b.setText("Load " + fileName);
		b.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
				ArActivity.startWithSetup(MainActivity.this, new ServiceFusionSetup(
						fileName, textureName));
			}
		});
		return b;
	}
    
    private File extractScript(String folder, String filename)
    {
    	File f = new File("/mnt/sdcard/" + TAG + "/" + filename); // getCacheDir()+"/script.py"); new FileOutputStream(f); //
    	try
    	{
    		f.createNewFile();

    		InputStream is = getAssets().open(folder + "/" + filename);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			
			FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());

			//FileOutputStream fos = openFileOutput("/mnt/sdcard/Temp/Scripts/script.py", MODE_WORLD_READABLE); //
			fos.write(buffer);
			fos.close();

    	}
    	catch (IOException e)
    	{
    		Log.d(TAG, e.getMessage());
    	}
    	
    	return f;
    }
    
    public static Intent buildStartInTerminalIntent(File script) {
	final ComponentName componentName = Constants.SL4A_SERVICE_LAUNCHER_COMPONENT_NAME;
    Intent intent = new Intent();
    intent.setComponent(componentName);
    intent.setAction(Constants.ACTION_LAUNCH_BACKGROUND_SCRIPT); //
    intent.putExtra(Constants.EXTRA_SCRIPT_PATH, script.getAbsolutePath());
    return intent;
    } // buildStartInTerminalIntent
}
