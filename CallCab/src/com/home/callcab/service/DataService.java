package com.home.callcab.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.home.callcab.CallCabApplication;
import com.home.callcab.info.ServiceInfo;
import com.home.callcab.info.ServiceInfo.ServiceMethod;
import com.home.callcab.info.ServiceInfo.State;
import com.home.callcab.parser.JSONParser;
import com.home.callcab.utility.SharedPrefUtils;

public class DataService extends IntentService {


	public DataService() {
		super("DataService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		Bundle b = new Bundle();
		
			receiver.send(State.STATUS_RUNNING, Bundle.EMPTY);
			try {
				b.putString("results", RequestCallCabRestAPI(intent));
				receiver.send(intent.getIntExtra("command", -1), b);
			} catch (Exception e) {
				b.putString(Intent.EXTRA_TEXT, e.toString());
				receiver.send(State.STATUS_ERROR, b);
			}
		
		//this.stopSelf();
	}
	
	
	
	
	private String RequestCallCabRestAPI(Intent intent) throws ClientProtocolException, IOException, JSONException
	{
		String response = null;
		ServiceInfo.ServiceMethod method =  ServiceMethod.values()[intent.getIntExtra("command", -1)];
		
		switch (method)
		{
		case GetAllOffersForCity:
			return RestClient.GetOfferList(CallCabApplication.CurrentCityName);
			
		case SetVendorRating:
			Integer vendorId = Integer.parseInt(intent.getStringExtra("vendorId"));
			String vendorName = intent.getStringExtra("vendorName");
			Boolean isRecommended = Boolean.parseBoolean(intent.getStringExtra("isRecommended"));
			Integer recommend = -1;
			
			String vendorHistory = SharedPrefUtils.GetVoteHistory(vendorId);
			if (vendorHistory != null && vendorHistory.equalsIgnoreCase(Boolean.toString(isRecommended)))
			{
				// do nothing as user has already voted once
				return "";
			}
			
			if (vendorHistory == null || vendorHistory.equals(""))
			{
				if (isRecommended == true) recommend = 1;
				else recommend =2;
				
			}
			else if (Boolean.parseBoolean(vendorHistory) == true)
			{
				if (isRecommended == false) recommend = 3;
			}
			else
			{
				if (isRecommended == true) recommend = 4;
			}
			
			if (recommend == -1) return "";
			
			SharedPrefUtils.SetVoteHistory(vendorId, isRecommended);
			return RestClient.SetVendorRating(vendorId,recommend,CallCabApplication.CurrentCityName);
			
		case GetVendorRating:
			return RestClient.GetVendorRating(CallCabApplication.CurrentCityName);
			
		}
			
		return response;
	}

}
