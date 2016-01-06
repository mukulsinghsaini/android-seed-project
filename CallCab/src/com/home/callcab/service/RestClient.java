package com.home.callcab.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.home.callcab.utility.CallCabUtils;

public class RestClient {

	private static String baseUrl = "http://callcab.api.com/";
	
	public static String GetOfferCount(String cityName)
			throws ClientProtocolException, IOException, JSONException {
		String URL = baseUrl+"caboffercount.php";
		return CallCabUtils.GetResponseFromAPI(URL);
	}

	public static String GetOfferList(String cityName)
			throws ClientProtocolException, IOException {
		String URL = baseUrl+"callcab_api.php?action=get_city_offers_list&city="
				+ cityName;
		return CallCabUtils.GetResponseFromAPI(URL);
	}
	
	public static String SetVendorRating(int vendorId,int isRecommended,String cityName) throws ClientProtocolException, IOException
	{
		String URL = baseUrl+"callcab_api.php?action=set_vendor_rating&city="
				+ cityName+"&vendorId="+vendorId+"&recommend="+isRecommended;
		return CallCabUtils.GetResponseFromAPI(URL);
	}

	public static String GetVendorRating(String cityName) throws ClientProtocolException, IOException
	{
		String URL = baseUrl+"callcab_api.php?action=get_vendor_rating&city=" + cityName;
		return CallCabUtils.GetResponseFromAPI(URL);
	}
	
}
