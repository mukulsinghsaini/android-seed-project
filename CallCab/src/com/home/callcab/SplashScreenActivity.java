package com.home.callcab;

import java.util.logging.Logger;

import com.google.analytics.tracking.android.EasyTracker;
import com.home.callcab.R;
import com.home.callcab.info.SharedPrefKeyInfo;
import com.home.callcab.info.ServiceInfo.ServiceMethod;
import com.home.callcab.service.DataReceiver;
import com.home.callcab.utility.CallCabUtils;
import com.home.callcab.utility.SharedPrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

public class SplashScreenActivity extends Activity implements DataReceiver.Receiver {

	//how long until we go to the next activity
    protected int _splashTime = 3000; 

    private Thread splashTread;
	public DataReceiver mReceiver;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        final SplashScreenActivity sPlashScreen = this; 
		mReceiver = new DataReceiver(new Handler());
		mReceiver.setReceiver(this);

        // thread for displaying the SplashScreen
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(this){

                            
                    		if (CallCabUtils.IsNetworkAvailable() && CallCabApplication.CurrentCityName != "")
                    		{
                    			CallCabUtils.FetchDataFromAPIAsync(ServiceMethod.GetAllOffersForCity, mReceiver, null);
                    		}
                    	
                            wait(_splashTime);
                    }

                } catch(InterruptedException e) {}
                finally {
                    finish();

                    //start a new activity
                    Intent i = new Intent();                  
                    
                    if (SharedPrefUtils.GetDefaultCity() == "")
                    {
                    	i.setClass(sPlashScreen, SelectCityActivity.class);                    
                    }
                    else
                    {
                    	i.setClass(sPlashScreen, CabListActivity.class);
                    }
                   // Log.d("mukul", "start city");        
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    //Log.d("mukul", "start stop()");
                    //stop();
                    //Log.d("mukul","stop stop()");
                    
                }
            }
        };

        splashTread.start();
    }


    
    //Function that will handle the touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized(splashTread){
                    splashTread.notifyAll();
            }
        }
        return true;
    }



	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		
	}
	
	
}
