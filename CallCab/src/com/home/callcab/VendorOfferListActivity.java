package com.home.callcab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.home.callcab.info.CabOfferInfo;
import com.home.callcab.info.CommonInfo;
import com.home.callcab.utility.CallCabUtils;
import com.home.callcab.utility.SharedPrefUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class VendorOfferListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		
		setContentView(R.layout.activity_vendor_offer_list);
		
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setIcon(R.drawable.ic_action_navigation_previous_item);
        actionBar.setTitle("Offers");
        actionBar.setDisplayShowHomeEnabled(false);
		
		Bundle extras = getIntent().getExtras();
		String vendorName =  (extras != null)? extras.getString(CommonInfo.BUNDLE_SELECTED_VENDOR_NAME):null;
		
		try {
								
			SetOfferView(vendorName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // go to previous screen when app icon in action bar is clicked
	        	finish();
	    }
	    return super.onOptionsItemSelected(item);
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

	public void onBackButton(View v)
	{
		Intent i = new Intent();
		i.setClass(CallCabApplication.getCallCabAppContext(),
				CabListActivity.class);
		startActivity(i);
		finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	private void SetOfferView(String vendorName) throws IOException
	{
		BindOfferLinearList(vendorName);
	}

	private void BindOfferLinearList(String vendorName) throws IOException {

		if (CallCabApplication.CabOfferList == null
				|| CallCabApplication.CabOfferList.isEmpty() == true) {
			
			
			return;
		}

		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup vGroup = (ViewGroup) findViewById(R.id.VofferList);
				
		for (int i = 0; i < CallCabApplication.CabOfferList.size(); i++) {
			CabOfferInfo offerInfo = CallCabApplication.CabOfferList.get(i);
			
			if (vendorName != null && (vendorName.equalsIgnoreCase("") == false) && offerInfo.cabVendorName.equalsIgnoreCase(vendorName) == false)
				continue;
			
			View view = inflater.inflate(R.layout.template_offer_list, null,
					false);
								
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

}
