package com.home.callcab.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.home.callcab.info.CabVendorVoteCountInfo;
import com.home.callcab.info.ServiceInfo;
import com.home.callcab.info.CabOfferInfo;

public class JSONParser {

	public static Map<String, Integer> getCabOfferCount(String responseJSON) throws JSONException
	{
		if (responseJSON == null || responseJSON == "")
			return null;
		
		JSONArray cabOfferJSONList = new JSONArray(responseJSON);
		Map<String,Integer> mapping = new HashMap<String, Integer>();
		for (int i=0;i<cabOfferJSONList.length();i++)
		{			
			mapping.put(cabOfferJSONList.getJSONObject(i).getString("cabVendorName"), cabOfferJSONList.getJSONObject(i).getInt("offerCount"));
		}
		
		return mapping;
	}
	
	public static Map<String, CabVendorVoteCountInfo> getCabVendorVoteCount(String responseJSON) throws JSONException
	{
		if (responseJSON == null || responseJSON == "")
			return null;
		
		JSONArray cabOfferJSONList = new JSONArray(responseJSON);
		Map<String,CabVendorVoteCountInfo> mapping = new HashMap<String, CabVendorVoteCountInfo>();
		for (int i=0;i<cabOfferJSONList.length();i++)
		{			
			CabVendorVoteCountInfo obj = new CabVendorVoteCountInfo();
			obj.CabVendorName = cabOfferJSONList.getJSONObject(i).getString("cabVendorName");
			obj.DownVote = cabOfferJSONList.getJSONObject(i).getInt("downVote");
			obj.UpVote = cabOfferJSONList.getJSONObject(i).getInt("upVote");
			mapping.put(obj.CabVendorName,obj);
		}
		
		return mapping;
	}
	
	public static List<CabOfferInfo> getCabOfferList(String cabOfferJSON) throws JSONException
	{
		if (cabOfferJSON == null || cabOfferJSON == "")
			return null;
		
		List<CabOfferInfo> cabOfferList = new ArrayList<CabOfferInfo>();
		CabOfferInfo offerInfo;
		
		JSONObject offerObj = new JSONObject(cabOfferJSON);
		JSONArray cabOfferArray = offerObj.getJSONArray("offers");
		for (int i=0;i<cabOfferArray.length();i++)
		{
			offerInfo = new CabOfferInfo();
			offerInfo.cabVendorName = cabOfferArray.getJSONObject(i).getString("cabVendorName");		
			offerInfo.offerDescription = cabOfferArray.getJSONObject(i).getString("offerDescription");
			offerInfo.offerTitle = cabOfferArray.getJSONObject(i).getString("offerTitle");
			offerInfo.offerRevisedOn = cabOfferArray.getJSONObject(i).getString("offerRevisedOn");
			
			cabOfferList.add(offerInfo);
		}
		
		return cabOfferList;
	}
	
}
