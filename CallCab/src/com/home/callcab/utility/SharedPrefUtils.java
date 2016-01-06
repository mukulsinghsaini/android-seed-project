package com.home.callcab.utility;

import java.util.UUID;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.home.callcab.CallCabApplication;
import com.home.callcab.info.SharedPrefKeyInfo;

public class SharedPrefUtils {

	public static String GetDefaultCity()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_CITY, 0);
		if (settings != null && settings.getAll().isEmpty() == false)
			return settings.getAll().keySet().toArray()[0].toString();

		return "";
	}
	
	public static String GetRecentlyUsedNumber(String cityName) {
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_RECENT_USE, 0);
		String recentlyCalled = null;

		if (settings.contains(cityName))
			recentlyCalled = settings.getString(cityName, null);

		return recentlyCalled;
	}

	public static void SetVoteHistory(Integer vendorId,Boolean isRecommended)
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_VENDOR_VOTE_HISTORY, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(Integer.toString(vendorId),Boolean.toString(isRecommended));
		editor.commit();		
	}
	
	public static String GetVoteHistory(Integer vendorId) {
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_VENDOR_VOTE_HISTORY, 0);
		String recentlyVoted = null;

		if (settings != null && settings.getAll().isEmpty() == false)
			recentlyVoted = settings.getString(Integer.toString(vendorId), null);

		return recentlyVoted;
	}

	public static void SaveCityPreference(String selectedCityName) {

		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(
				SharedPrefKeyInfo.PREFS_CITY, 0);

		SharedPreferences.Editor editor = settings.edit();

		editor.clear();
		editor.putBoolean(selectedCityName, true);
		editor.commit();
	}

	public static void SetCabOffers(String cabOfferJSON)
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(
				SharedPrefKeyInfo.PREFS_CAB_OFFERS, 0);

		SharedPreferences.Editor editor = settings.edit();

		editor.clear();
		editor.putString("cabOfferJSON", cabOfferJSON);
		editor.commit();
		
		SetLastUpdatedTime();
	}
	
	public static void SetLastUpdatedTime()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(
				SharedPrefKeyInfo.PREFS_LAST_UPDATE_TIME, 0);

		SharedPreferences.Editor editor = settings.edit();

		editor.clear();
		editor.putString("lastUpdateTime", CallCabUtils.GetCurrentDate());
		editor.commit();		
	}

	public static String GetLastUpdatedTime()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_LAST_UPDATE_TIME, 0);
		if (settings != null && settings.getAll().isEmpty() == false)
			return settings.getString("lastUpdateTime", "");

		return "";
	}
	
	public static String GetCabOffers()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_CAB_OFFERS, 0);
		if (settings != null && settings.getAll().isEmpty() == false)
			return settings.getString("cabOfferJSON", "");

		return "";
	}
	
	public static boolean IsCabOfferEverFetched()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_CAB_OFFERS, 0);
		if (settings == null || settings.getAll().isEmpty() == true)
			return false;
		
		return true;
	}
	
	public static void RemoveCabOffers()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_CAB_OFFERS, 0);
		if (settings != null && settings.getAll().isEmpty() == false)
		{
			SharedPreferences.Editor seditor = settings.edit(); 
			seditor.clear();
			seditor.commit();
		}
	}
	
	public static void UpdateRecentlyUsedList(String cityName, String calledVendorId) {
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_RECENT_USE, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(cityName, calledVendorId);
		editor.commit();
	}

	public static void SetUserIdentifier()
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_USER_ID, 0);
		SharedPreferences.Editor editor = settings.edit();

		String uuid = UUID.randomUUID().toString();
		
		editor.clear();
		editor.putString(SharedPrefKeyInfo.PREFS_USER_ID, uuid);
		editor.commit();		
	}
	
	public static String GetUserIdentifier()
	{				
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_USER_ID, 0);
		
		if (settings == null || settings.getAll().isEmpty() == true)
		{
			SetUserIdentifier();
			SharedPreferences setting2 = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_USER_ID, 0);
			return setting2.getString(SharedPrefKeyInfo.PREFS_USER_ID, "");
		}
		else
		{
			return settings.getString(SharedPrefKeyInfo.PREFS_USER_ID, "");
		}
		
	}
	
	public static void SetVendorRating(String vendorRatingJson,String cityName)
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(
				SharedPrefKeyInfo.PREFS_VENDOR_RATING, 0);

		SharedPreferences.Editor editor = settings.edit();

		//editor.clear();
		editor.putString(cityName, vendorRatingJson);
		editor.commit();

	}
	
	public static String GetVendorRating(String cityName)
	{
		SharedPreferences settings = CallCabApplication.getCallCabAppContext().getSharedPreferences(SharedPrefKeyInfo.PREFS_VENDOR_RATING, 0);
		if (settings != null && settings.getAll().isEmpty() == false)
			return settings.getString(cityName, "");

		return "";
	}
}
