package org.ollide.rosandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        adjustAnimation();

        Handler hd = new Handler();
        hd.postDelayed(new SplashHandler(), 5000);

    }

    private void adjustAnimation(){

        ImageView wi_logo = (ImageView)findViewById(R.id.wi_logo);
        Animation wi_logo_translate = (Animation) AnimationUtils.loadAnimation
                (SplashActivity.this, R.anim.wi_logo_translate);

        wi_logo.startAnimation(wi_logo_translate);

    }

    private class SplashHandler implements Runnable{

        public void run(){

            startActivity(new Intent(getApplication(), MainActivity.class));

            SplashActivity.this.finish();
        }

    }

}
