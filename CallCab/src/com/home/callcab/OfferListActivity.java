package com.home.callcab;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.home.callcab.info.CabOfferInfo;
import com.home.callcab.info.CommonInfo;
import com.home.callcab.info.ServiceInfo.ServiceMethod;
import com.home.callcab.info.ServiceInfo.State;
import com.home.callcab.service.DataReceiver;
import com.home.callcab.utility.CallCabUtils;
import com.home.callcab.utility.SharedPrefUtils;

public class OfferListActivity extends Activity implements DataReceiver.Receiver {

	public DataReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_offer_list);
		
		mReceiver = new DataReceiver(new Handler());
		mReceiver.setReceiver(this);

		
		try {
			SetOfferView();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private void SetOfferView() throws IOException
	{
		BindOfferLinearList();
	}

	private void BindOfferLinearList() throws IOException {

		if (CallCabApplication.CabOfferList == null
				|| CallCabApplication.CabOfferList.isEmpty() == true) {
			
			findViewById(R.id.emptyOfferContainer).setVisibility(View.VISIBLE);
			findViewById(R.id.offerScrollerControl).setVisibility(View.GONE);
			TextView emptyMsg = (TextView)findViewById(R.id.emptyOfferMsg);
			if (SharedPrefUtils.IsCabOfferEverFetched())
			{
				emptyMsg.setText("No offers available right now !");
			}
			else
			{
				if (CallCabUtils.IsNetworkAvailable())
				emptyMsg.setText("Press refresh to see offers !");
				else
				emptyMsg.setText("Turn on internet and press refresh to see latest offers !");	
			}
			
			return;
		}
		else
		{
			findViewById(R.id.emptyOfferContainer).setVisibility(View.GONE);
			findViewById(R.id.offerScrollerControl).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.txtLastUpdateDate)).setText(SharedPrefUtils.GetLastUpdatedTime());
		}

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup vGroup = (ViewGroup) findViewById(R.id.offerList);
		vGroup.removeAllViews();
		
		for (int i = 0; i < CallCabApplication.CabOfferList.size(); i++) {
			View view = inflater.inflate(R.layout.template_offer_list, null,
					false);

			CabOfferInfo offerInfo = CallCabApplication.CabOfferList.get(i);
			
			TextView tvVendorName = (TextView) view
					.findViewById(R.id.OlvendorName);
			tvVendorName.setText(offerInfo.cabVendorName);
			
			TextView tvOfferDesc = (TextView) view
					.findViewById(R.id.OlofferDescription);
			tvOfferDesc.setText(offerInfo.offerDescription);
			
			TextView tvOfferRevOn = (TextView) view
					.findViewById(R.id.OlofferRevisedOn);
			tvOfferRevOn.setText(offerInfo.offerRevisedOn.replace("00:00:00", ""));
			
			TextView tvOfferTitle = (TextView) view
					.findViewById(R.id.OlofferTitle);
			tvOfferTitle.setText(offerInfo.offerTitle);
			
		
			vGroup.addView(view);
		}
		
		
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {

		//ProgressBar prog = (ProgressBar) findViewById(R.id.loadingProgress);;
		
		switch (resultCode) {
		case State.STATUS_RUNNING: // show progress
			//prog.setVisibility(View.VISIBLE);
			break;

		case State.STATUS_ERROR: // handle the error;
			//prog.setVisibility(View.INVISIBLE);
			break;

		default:		
			//prog.setVisibility(View.INVISIBLE);
			try {
				
				BindOfferLinearList();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void onBtnRefreshOffers(View v)
	{
		if (CallCabUtils.IsNetworkAvailable())
		{
			CallCabUtils.FetchDataFromAPIAsync(ServiceMethod.GetAllOffersForCity, mReceiver, null);
		}
		else
		{
			CallCabUtils.MessageBox("Please turn on internet and then try !");
		}
	}		
	
	public void onViewAllCabtBtnClick(View v)
	{
		Intent i = new Intent();
		i.setClass(CallCabApplication.getCallCabAppContext(),
				CabListActivity.class);
		startActivity(i);
		finish();
	}
	
	public void onViewOfferBtnClick(View v)
	{
		Intent i = new Intent();
		i.setClass(CallCabApplication.getCallCabAppContext(),
				OfferListActivity.class);
		startActivity(i);
		finish();
	}
	
	public void onBtnShowCityMenu(View v) {
		Intent i = new Intent();
		i.putExtra(CommonInfo.BUNDLE_CURRENT_CITY_NAME,
				CallCabApplication.CurrentCityName);
		i.setClass(CallCabApplication.getCallCabAppContext(),
				SelectCityActivity.class);
		startActivity(i);
		finish();
	}
	
	public void onBtnShareApp(View v) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent
				.putExtra(
						Intent.EXTRA_TEXT,
						getResources().getText(R.string.promoteMsg));
		startActivity(Intent.createChooser(shareIntent, "Share..."));
	}
	


}