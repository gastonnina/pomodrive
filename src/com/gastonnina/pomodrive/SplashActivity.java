package com.gastonnina.pomodrive;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iniciarAnimacion();

    }
    /**
     * Animacion de Splash
     */
    public void iniciarAnimacion(){
    	TextView txt = (TextView)findViewById(R.id.textSplahTitle);
    	Animation animacion= AnimationUtils.loadAnimation(this, R.anim.animacion);
    	txt.startAnimation(animacion);
	}

}
