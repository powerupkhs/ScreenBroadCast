package com.baksu.screenbroadcast2;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;


public class CamViewActivity extends Activity {
	public CamView _camView = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cam);
		FrameLayout _frameLayout = (FrameLayout)findViewById(R.id.mainFrameLayout01);
		//	sample=new Sample3View(this);
			//_frameLayout.addView(sample);
			_camView = new CamView(this);
			_frameLayout.addView(_camView);
	}	
}
