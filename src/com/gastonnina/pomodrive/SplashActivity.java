package com.gastonnina.pomodrive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends Activity {
SplashActivity that = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iniciarAnimacion();

    }
    protected boolean _active = true;
	protected int _splashTime = 5000; // time to display the splash screen in ms
    /**
     * Animacion de Splash
     */
    public void iniciarAnimacion(){
    	TextView txt = (TextView)findViewById(R.id.textSplahTitle);
    	Animation animacion= AnimationUtils.loadAnimation(this, R.anim.animacion);
    	txt.startAnimation(animacion);
    	
        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    startActivity(new Intent(that,MainActivity.class));                    
                    //stop();
                }
            }
        };
        
        splashTread.start();

	}

}
