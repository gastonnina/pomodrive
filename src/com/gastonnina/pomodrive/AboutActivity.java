package com.gastonnina.pomodrive;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
/**
 * 
 * @author Gaston Nina
 *
 */
public class AboutActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		WebView webViewAbout = (WebView) findViewById(R.id.webViewAbout);
		webViewAbout.loadUrl("http://gastonnina.com/");
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}

