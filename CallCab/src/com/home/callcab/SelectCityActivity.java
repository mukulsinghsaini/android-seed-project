package com.home.callcab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.google.analytics.tracking.android.EasyTracker;
import com.home.callcab.R;
import com.home.callcab.info.CityInfo;
import com.home.callcab.info.CommonInfo;
import com.home.callcab.info.SharedPrefKeyInfo;
import com.home.callcab.info.ServiceInfo.ServiceMethod;
import com.home.callcab.info.ServiceInfo.State;
import com.home.callcab.parser.XMLParser;
import com.home.callcab.service.DataReceiver;
import com.home.callcab.utility.CallCabUtils;
import com.home.callcab.utility.SharedPrefUtils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SelectCityActivity extends Activity implements DataReceiver.Receiver{

	public DataReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_city);
		mReceiver = new DataReceiver(new Handler());
		mReceiver.setReceiver(this);

		Bundle extras = getIntent().getExtras();
		String currentCityName = (extras != null)? extras.getString(CommonInfo.BUNDLE_CURRENT_CITY_NAME):null;
		BindCityList(currentCityName);
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_select_city, menu);
		return true;
	}

	public void onBackButton(View v)
	{
		Intent i = new Intent();
		i.setClass(CallCabApplication.getCallCabAppContext(),
				CabListActivity.class);
		startActivity(i);
		finish();		
	}
	
	private void BindCityList(String currentCityName) {
		LayoutInflater inflater = getLayoutInflater();
		List<CityInfo> cityList = null;
		try {
			cityList = XMLParser.GetCityList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (currentCityName != null && currentCityName != "")
		{
			TextView tv = (TextView)findViewById(R.id.txtSelectMsg);
			tv.setText("Please select your location");
		}
		
		
		RadioGroup lLayout = (RadioGroup) findViewById(R.id.SCAcityRdList);
		

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < cityList.size(); i++) {
			if (i > 0) {
				View v = new View(this);
				v.setLayoutParams(new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, 1));
				v.setBackgroundColor(0xffdddddd);
				lLayout.addView(v);
				}
			
			RadioButton rdBtn = new RadioButton(
					CallCabApplication.getCallCabAppContext());
			rdBtn.setText(cityList.get(i).CityName);
			rdBtn.setTextColor(Color.BLACK);			
			rdBtn.setLayoutParams(lp);
			rdBtn.setPadding(80, 20, 0, 20);
			lLayout.addView(rdBtn);
			if (currentCityName != null && currentCityName.equalsIgnoreCase(cityList.get(i).CityName) == true)
			{
				lLayout.check(rdBtn.getId());
			}
						 
		}
		
		lLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				String selectedCityName = ((RadioButton) (group
						.findViewById(checkedId))).getText().toString();
				SharedPrefUtils.SaveCityPreference(selectedCityName);
				SharedPrefUtils.RemoveCabOffers();
				CallCabApplication.CurrentCityName = selectedCityName;
				CallCabApplication.CabOfferList = null;
				
				try {
					CallCabApplication.SetApplicationMetaData();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (CallCabUtils.IsNetworkAvailable())
        		{
        			CallCabUtils.FetchDataFromAPIAsync(ServiceMethod.GetAllOffersForCity, mReceiver, null);
        		}
				else
				{
				Intent i = new Intent();
				i.setClass(CallCabApplication.getCallCabAppContext(),
						CabListActivity.class);
				startActivity(i);
				finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			}
		});

	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
//ProgressBar prog = (ProgressBar) findViewById(R.id.loadingProgress);;
		Intent i;
		switch (resultCode) {
		case State.STATUS_RUNNING: // show progress
			//prog.setVisibility(View.VISIBLE);
			break;

		case State.STATUS_ERROR: // handle the error;
			//prog.setVisibility(View.INVISIBLE);
			i = new Intent();
			i.setClass(CallCabApplication.getCallCabAppContext(),
					CabListActivity.class);
			startActivity(i);
			finish();
			break;

		default:		
			//prog.setVisibility(View.INVISIBLE);
			//BindOffers();
			i = new Intent();
			i.setClass(CallCabApplication.getCallCabAppContext(),
					CabListActivity.class);
			startActivity(i);
			finish();
		}
		
		
	}
	
}
