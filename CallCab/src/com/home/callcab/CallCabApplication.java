package com.home.callcab;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.home.callcab.info.CabOfferInfo;
import com.home.callcab.info.CabVendorInfo;
import com.home.callcab.info.CabVendorVoteCountInfo;
import com.home.callcab.info.CityInfo;
import com.home.callcab.parser.JSONParser;
import com.home.callcab.parser.XMLParser;
import com.home.callcab.utility.SharedPrefUtils;

import android.app.Application;
import android.content.Context;

public class CallCabApplication extends Application {

	private static Context context;
	public static String CurrentCityName;
	public static String UserId;
	public static CityInfo CurrentCityInfo;
	public static List<CabVendorInfo> VendorList; 
	public static List<CabOfferInfo> CabOfferList;
	public static Map<String,CabVendorVoteCountInfo> VendorVoteCountMap;
	
    @Override
	public void onCreate(){
      context=getApplicationContext();
      try {
    	UserId = SharedPrefUtils.GetUserIdentifier();
		SetApplicationMetaData();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }

    public static Context getCallCabAppContext(){
      return context;
    }
    
    public static void SetApplicationMetaData() throws IOException, JSONException
    {
    	CurrentCityName = SharedPrefUtils.GetDefaultCity();
    	if (CurrentCityName == "")
    		return;
    	
    	CurrentCityInfo = getCityInfo(CurrentCityName);
    	VendorList = XMLParser.GetCabList(CurrentCityInfo.CityXMl);
    	CabOfferList = JSONParser.getCabOfferList(SharedPrefUtils.GetCabOffers());
    	VendorVoteCountMap = JSONParser.getCabVendorVoteCount(SharedPrefUtils.GetVendorRating(CurrentCityName));
    }
    
    private static CityInfo getCityInfo(String cityName) throws IOException {
		
		List<CityInfo> cityInfoList = XMLParser.GetCityList();
		

		for (CityInfo cInfo : cityInfoList) {
			if (cInfo.CityName.equalsIgnoreCase(cityName) == true)
				return cInfo;
		}
		return null;
	}

    
}