package net.silsoft.nobs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class SplashScreenActivity extends Activity{

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean show_splash = preferences.getBoolean("splash_screen", true);

        if (!show_splash) {
            Intent mainIntent = new Intent(SplashScreenActivity.this, SelectionActivity.class);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        }

        setContentView(R.layout.splash_screen);

        final Button btn = (Button)findViewById(R.id.btnStart);
        btn.setVisibility(View.INVISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SplashScreenActivity.this, SelectionActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        });

        //show start button only after few secs ? or not
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // show button here if needed
                btn.setVisibility(View.VISIBLE);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
