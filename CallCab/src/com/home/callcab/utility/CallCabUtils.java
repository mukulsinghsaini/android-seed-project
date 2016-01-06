package com.home.callcab.utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.home.callcab.CallCabApplication;
import com.home.callcab.info.CabVendorInfo;
import com.home.callcab.info.CabVendorVoteCountInfo;
import com.home.callcab.info.ServiceInfo;
import com.home.callcab.parser.JSONParser;
import com.home.callcab.service.DataReceiver;
import com.home.callcab.service.DataService;

public class CallCabUtils {

	private static int CONNECTION_TIME_OUT = 10000;
	private static int REQUEST_TIME_OUT = 10000;
	
	public static CabVendorInfo getVendorInfoForVendorId(
			List<CabVendorInfo> vendorList, String vendorId) {
		CabVendorInfo searchedInfo = null;
		for (CabVendorInfo info : vendorList) {
			if (info.Id.equalsIgnoreCase(vendorId) == true) {
				searchedInfo = info;
				break;
			}
		}
		return searchedInfo;
	}
	
	public static void MessageBox(String message) {
		Toast.makeText(CallCabApplication.getCallCabAppContext(), message, Toast.LENGTH_SHORT).show();
	}

	public static boolean IsNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) CallCabApplication.getCallCabAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static String GetCurrentDate()
	{
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd h:mm a");
		String formattedDate = df.format(c.getTime());
		return formattedDate;
	}
	
	public static String GetResponseFromAPI(String url) throws ClientProtocolException, IOException
	{
		HttpGet httpGet = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = CONNECTION_TIME_OUT;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = REQUEST_TIME_OUT;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpGet);
		
		return EntityUtils.toString(response.getEntity());
		
	}

	public static void FetchDataFromAPIAsync(ServiceInfo.ServiceMethod method,DataReceiver mReceiver,
			Map<String, String> param) {
		
		final Intent intent = new Intent(Intent.ACTION_SYNC, null,CallCabApplication.getCallCabAppContext(), DataService.class);
		intent.putExtra("receiver", mReceiver);
		intent.putExtra("command", method.getNumVal());

		if (param != null) {
			Iterator<Entry<String, String>> iterator = param.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> pairs = iterator.next();
				intent.putExtra(pairs.getKey(), pairs.getValue());
			}
		}

		CallCabApplication.getCallCabAppContext().startService(intent);
	}

	public static List<CabVendorInfo> GetCabVendorSortedByVote(List<CabVendorInfo> vendorInfoList,String cityName) throws JSONException
	{
		Map<String, CabVendorVoteCountInfo> cabVendorVoteCountMap =  JSONParser.getCabVendorVoteCount(SharedPrefUtils.GetVendorRating(cityName));
		if (cabVendorVoteCountMap == null) return vendorInfoList;
		
		CabVendorVoteCountInfo voteInfo;
		for(CabVendorInfo vInfo:vendorInfoList)
		{
			voteInfo = cabVendorVoteCountMap.get(vInfo.VendorName);
			if (voteInfo == null) continue;
			
			vInfo.UpVoteCount = voteInfo.UpVote;
			vInfo.DownVoteCount = voteInfo.DownVote;
		}
	
		Collections.sort(vendorInfoList, new Comparator<CabVendorInfo>() {
	        @Override public int compare(CabVendorInfo p1, CabVendorInfo p2) {
	            return (p2.UpVoteCount - p2.DownVoteCount) - (p1.UpVoteCount - p1.DownVoteCount); // Descending
	        }
	    });
	
		return vendorInfoList;
	}

}
