//package fi.cie.chiru.servicefusionar;
//
//import android.os.Bundle;
//import android.app.Activity;
//import android.view.Menu;
//
//public class MainActivity extends Activity {
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
//}

package fi.cie.chiru.servicefusionar;

import system.ArActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
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

}
