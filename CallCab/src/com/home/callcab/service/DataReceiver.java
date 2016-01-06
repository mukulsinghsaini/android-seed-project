package com.home.callcab.service;

import java.io.IOException;

import org.json.JSONException;

import com.home.callcab.CallCabApplication;
import com.home.callcab.info.ServiceInfo;
import com.home.callcab.info.ServiceInfo.State;
import com.home.callcab.utility.CallCabUtils;
import com.home.callcab.utility.SharedPrefUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DataReceiver extends ResultReceiver {

	private Receiver mReceiver;

	public DataReceiver(Handler handler) {
		super(handler);
	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		
		switch (resultCode) {
		case State.STATUS_RUNNING:
			
			return;
		case State.STATUS_ERROR:
			//CallCabUtils.MessageBox(resultData.getString(Intent.EXTRA_TEXT));
			return;
		}

		String results;
		if (resultCode == ServiceInfo.ServiceMethod.GetAllOffersForCity.getNumVal())
		{
				results = resultData.getString("results");
			
				SharedPrefUtils.SetCabOffers(results);
				try {
					CallCabApplication.SetApplicationMetaData();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		else if (resultCode == ServiceInfo.ServiceMethod.SetVendorRating.getNumVal())
		{
			results = resultData.getString("results");
			SharedPrefUtils.SetVendorRating(results,CallCabApplication.CurrentCityName);
			try {
				CallCabApplication.SetApplicationMetaData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (resultCode == ServiceInfo.ServiceMethod.GetVendorRating.getNumVal())
		{
			results = resultData.getString("results");
			SharedPrefUtils.SetVendorRating(results,CallCabApplication.CurrentCityName);
			try {
				CallCabApplication.SetApplicationMetaData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if (mReceiver != null) {
			mReceiver.onReceiveResult(resultCode, resultData);
		}
	}
}
