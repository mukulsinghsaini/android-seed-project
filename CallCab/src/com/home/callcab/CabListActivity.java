package com.home.callcab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.home.callcab.info.CabServiceType;
import com.home.callcab.info.CabVendorInfo;
import com.home.callcab.info.CabVendorVoteCountInfo;
import com.home.callcab.info.CityInfo;
import com.home.callcab.info.CommonInfo;
import com.home.callcab.info.ServiceInfo;
import com.home.callcab.info.ServiceInfo.ServiceMethod;
import com.home.callcab.info.ServiceInfo.State;
import com.home.callcab.info.SharedPrefKeyInfo;
import com.home.callcab.parser.JSONParser;
import com.home.callcab.parser.XMLParser;
import com.home.callcab.service.DataReceiver;
import com.home.callcab.service.DataService;
import com.home.callcab.utility.CallCabUtils;
import com.home.callcab.utility.SharedPrefUtils;
import com.tjeannin.apprate.AppRate;

public class CabListActivity extends Activity implements DataReceiver.Receiver {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle ;
    public static List<ImageButton> offerBtnList;
    public static List<TextView> upVoteCountTxtList;
    public static List<TextView> downVoteCountTxtList;
    public static Dialog rateDialog;
	public DataReceiver mReceiver;
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_cab_list);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(CallCabApplication.CurrentCityName);
        actionBar.setDisplayShowHomeEnabled(false);        
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#CCffffff")));        
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77000000")));        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle("");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle("");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
        	 
        	  @Override
        	  public boolean onNavigationItemSelected(int position, long itemId) {
        	           	    return true;
        	  }
        	};
        
		LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		final View spinnerView = inflater.inflate(R.layout.template_filter_spinner, null);
		Spinner spinner = (Spinner) spinnerView.findViewById(R.id.my_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cab_filter_data, R.layout.template_filter_spinner_item);
		adapter.setDropDownViewResource(R.layout.template_filter_spinner_dropdown_item);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try {
					BindCabLinearList(CabServiceType.values()[position]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spinner.setAdapter(adapter);

		
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM  | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE );
		android.app.ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
	    layoutParams.gravity = Gravity.RIGHT; // set your layout's gravity to 'right'
	    actionBar.setCustomView(spinnerView,layoutParams); //place your layout on the actionbar        
	    actionBar.show();      
	    
		mReceiver = new DataReceiver(new Handler());
		mReceiver.setReceiver(this);

		
		Bundle extras = getIntent().getExtras();
		String currentCityName = (extras != null)? extras.getString(CommonInfo.BUNDLE_CURRENT_CITY_NAME):null;
		BindCityList(currentCityName);
		BindRecentlyCalled();
		BindOffers();
		if (CallCabUtils.IsNetworkAvailable())
		{
			CallCabUtils.FetchDataFromAPIAsync(ServiceMethod.GetAllOffersForCity, mReceiver, null);
			CallCabUtils.FetchDataFromAPIAsync(ServiceMethod.GetVendorRating, mReceiver, null);
		}
		initializeReviewPrompt();
	}
	
	private void initializeReviewPrompt()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
	    .setTitle("Let others know")
	    .setIcon(R.drawable.ic_launcher)
	    .setMessage("You seem to use this app a lot.Would you like to rate us :)")
	    .setPositiveButton("Rate it!", null)
	    .setNegativeButton("No Thanks", null)
	    .setNeutralButton("Remind me later", null);


		new AppRate(this)
		.setMinDaysUntilPrompt(7)
	    .setMinLaunchesUntilPrompt(20)
	    .setCustomDialog(builder)
	    .init();
	}
	
	
	private void setCityView() throws IOException {
		try {
			BindCabLinearList(CabServiceType.ALL_CAB);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void BindCabLinearList(CabServiceType serviceType) throws IOException {

		
		CabVendorInfo vendorInfo = null;
		offerBtnList = new ArrayList<ImageButton>();
		upVoteCountTxtList = new ArrayList<TextView>();
		downVoteCountTxtList = new ArrayList<TextView>();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup vGroup = (ViewGroup) findViewById(R.id.cabList);
		vGroup.removeAllViews();
		try {
			CallCabApplication.VendorList = CallCabUtils.GetCabVendorSortedByVote(CallCabApplication.VendorList, CallCabApplication.CurrentCityName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int current = 0; current < CallCabApplication.VendorList.size(); current++) {
			
			vendorInfo = CallCabApplication.VendorList.get(current);
			if (!showCabItem(vendorInfo, serviceType)) continue;
			
			View view = inflater.inflate(R.layout.template_cab_list, null,
					false);

			TextView vendor = (TextView) view.findViewById(R.id.vendorName);
			vendor.setText(vendorInfo.VendorName);

			RelativeLayout btnContainer = (RelativeLayout) view
					.findViewById(R.id.btnContainer);
			btnContainer.setTag(vendorInfo);

			TextView vendorUsp = (TextView) view.findViewById(R.id.vendorUsp);
			vendorUsp.setText(vendorInfo.Usp);

			TextView vendorNumber = (TextView) view
					.findViewById(R.id.vendorNumber);
			vendorNumber
					.setText(vendorInfo.PhoneNumber);
			
			TextView vendorNumberStd = (TextView) view
					.findViewById(R.id.vendorNumberStdPart);
			vendorNumberStd
					.setText(vendorInfo.PreNum);
			
			TextView vendorNumberPattern = (TextView) view
					.findViewById(R.id.vendorNumberPattern);
			vendorNumberPattern
					.setText(vendorInfo.PostNum);

			Button dialBtn = (Button) view.findViewById(R.id.btnCall);
			Button bookBtn = (Button) view.findViewById(R.id.btnBook);
			Button shareBtn = (Button) view.findViewById(R.id.btnSend);
			Button rateBtn = (Button) view.findViewById(R.id.btnRate);
			
			dialBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBtnCallClicked(arg0);
				}
			});

			bookBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBtnViewWebPage(arg0);
				}
			});
			
			shareBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBtnShareApp(arg0);
				}
			});
			
			
			
			ImageButton offerBtn = (ImageButton) view
					.findViewById(R.id.offerBtn);
			offerBtn.setVisibility(View.INVISIBLE);
			offerBtn.setTag(vendorInfo.VendorName);
			
			shareBtn.setTag(R.id.vendorName, vendorInfo.VendorName);
			shareBtn.setTag(R.id.phoneNumber, vendorInfo.PhoneNumber);
			
			rateBtn.setTag(R.id.vendorId, vendorInfo.Id);
			rateBtn.setTag(R.id.vendorName, vendorInfo.VendorName);
			
			
			offerBtnList.add(offerBtn);
			
			TextView txtUpVote = (TextView)view.findViewById(R.id.txtUpVoteCount);
			txtUpVote.setTag(vendorInfo.VendorName);
			upVoteCountTxtList.add(txtUpVote);
			
			TextView txtDownVote = (TextView)view.findViewById(R.id.txtDownVoteCount);
			txtDownVote.setTag(vendorInfo.VendorName);
			downVoteCountTxtList.add(txtDownVote);
			
			vGroup.addView(view);

		}
		BindOffers();
		BindRatings();
	}

	private void BindRecentlyCalled()
	{
		
		TextView tVendorName = (TextView)findViewById(R.id.txtRecentMsgCircle);
		Button btnCall = (Button)findViewById(R.id.btnRecentCallMsg);
		
		String lastCalledVendorId = SharedPrefUtils.GetRecentlyUsedNumber(CallCabApplication.CurrentCityName);
		CabVendorInfo vInfo = CallCabUtils.getVendorInfoForVendorId(CallCabApplication.VendorList, lastCalledVendorId);
		if (lastCalledVendorId == null || lastCalledVendorId == "" || vInfo == null)
		{
			tVendorName.setText("No calls yet :| ");
			btnCall.setVisibility(View.INVISIBLE); 
			return;
		}
		else
		{
			btnCall.setVisibility(View.VISIBLE);
			((View)btnCall.getParent()).setTag(vInfo);
			tVendorName.setText("You last called '"+vInfo.VendorName+"'");
		}
	}
	
	private void BindOffers() {
		if (CallCabApplication.CabOfferList == null || CallCabApplication.CabOfferList.size() == 0) 
		{
			((TextView)findViewById(R.id.txtOfferMsgCircle)).setText("No offers yet :|");
			((Button)findViewById(R.id.btnViewOfferMsg)).setVisibility(View.INVISIBLE);
			return;
		}
		else
		{
			((TextView)findViewById(R.id.txtOfferMsgCircle)).setText("We have "+Integer.toString(CallCabApplication.CabOfferList.size())+" offers to save some buck !" );
			((Button)findViewById(R.id.btnViewOfferMsg)).setVisibility(View.VISIBLE);
			
		}

		if (offerBtnList == null)
			return;
		
		for (int i = 0; i < offerBtnList.size(); i++) {
			String vendorName = offerBtnList.get(i).getTag().toString();
			if (cabOfferContains(vendorName) == true) {
				offerBtnList.get(i).setVisibility(View.VISIBLE);
			} else {
				offerBtnList.get(i).setVisibility(View.INVISIBLE);
			}
		}
	}

	private void BindRatings()
	{
		if (CallCabApplication.VendorVoteCountMap == null || upVoteCountTxtList == null || downVoteCountTxtList == null)
		{
			return;
		}
		
		CabVendorVoteCountInfo voteInfo;
		for (int i=0;i< upVoteCountTxtList.size();i++)
		{
			voteInfo = CallCabApplication.VendorVoteCountMap.get(upVoteCountTxtList.get(i).getTag());
			if (voteInfo == null) continue;
			
			upVoteCountTxtList.get(i).setText(voteInfo.UpVote+"");
		}
		
		for (int i=0;i< downVoteCountTxtList.size();i++)
		{
			voteInfo = CallCabApplication.VendorVoteCountMap.get(downVoteCountTxtList.get(i).getTag());
			if (voteInfo == null) continue;
			
			downVoteCountTxtList.get(i).setText(voteInfo.DownVote+"");
		}
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

		LinearLayout lLayout = (LinearLayout) findViewById(R.id.SCAcityRdList);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < cityList.size(); i++) {
			if (i > 0) {
				View v = new View(this);
				v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1));
				v.setBackgroundColor(0xffdddddd);
				lLayout.addView(v);
				}
			
			TextView rdBtn = new TextView(
					CallCabApplication.getCallCabAppContext());
			rdBtn.setText(cityList.get(i).CityName);
			rdBtn.setTextColor(Color.BLACK);			
			rdBtn.setLayoutParams(lp);
			rdBtn.setPadding(10, 20, 10, 20);
			rdBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// TODO Auto-generated method stub
					String selectedCityName = ((TextView)arg0).getText().toString();
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
					
					Intent i = new Intent();
					i.setClass(CallCabApplication.getCallCabAppContext(),
							CabListActivity.class);
					startActivity(i);
					finish();
				}
						
				
			});

			lLayout.addView(rdBtn);
						 
		}
		
		
	}

	private boolean cabOfferContains(String vendorName) {
		for (int i = 0; i < CallCabApplication.CabOfferList.size(); i++) {
			if (CallCabApplication.CabOfferList.get(i).cabVendorName
					.equalsIgnoreCase(vendorName))
				return true;
		}
		return false;
	}
	
	private boolean showCabItem(CabVendorInfo vendorInfo,CabServiceType serviceType)
	{
		switch (serviceType)
		{
			case ALL_CAB:
				return true;
				
			case AIRPORT:
				return vendorInfo.isAirport;
				
			case LOCAL:
				return vendorInfo.isLocal;
				
			case OUTSTATION:
				return vendorInfo.isOutstation;
				
			default:
				return true;
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Pass the event to ActionBarDrawerToggle, if it returns
	    // true, then it has handled the app icon touch event
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	      return true;
	    }
	    // Handle your other action bar items...

	    return super.onOptionsItemSelected(item);
	}
	
	public void onBtnRateClick(View v)
	{
		if (CallCabUtils.IsNetworkAvailable() == false)
		{
			CallCabUtils.MessageBox("Please turn on internet first !");
			return;
		}
		
		Integer vendorId = Integer.parseInt(v.getTag(R.id.vendorId).toString());
		String vendorName = (String) v.getTag(R.id.vendorName);
		
		// custom dialog
		final Dialog dialog = new Dialog(context);
		rateDialog = dialog;
		dialog.setContentView(R.layout.dialog_rate_vendor);
		dialog.setTitle(vendorName);
		
		RadioButton rdYesBtn = (RadioButton)dialog.findViewById(R.id.radio_rec_yes);
		rdYesBtn.setTag(true);
		RadioButton rdNoBtn = (RadioButton)dialog.findViewById(R.id.radio_rec_no);
		rdNoBtn.setTag(false);
		
		Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		dialogButtonOk.setTag(R.id.vendorId, vendorId);
		dialogButtonOk.setTag(R.id.vendorName,vendorName);
		
		dialogButtonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CallCabUtils.IsNetworkAvailable())
				{
					Integer vendorId = Integer.parseInt(v.getTag(R.id.vendorId).toString());
					String vendorName = (String) v.getTag(R.id.vendorName);
					RadioGroup radioButtonGroup = (RadioGroup) CabListActivity.this.rateDialog.findViewById(R.id.radioGroupReco);
					int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
					View radioButton = radioButtonGroup.findViewById(radioButtonID);
					
					Map<String, String> param = new HashMap<String, String>();
					param.put("vendorId", vendorId.toString());
					param.put("vendorName", vendorName);
					param.put("isRecommended", radioButton.getTag().toString());
					CallCabUtils.FetchDataFromAPIAsync(ServiceMethod.SetVendorRating, mReceiver, param);
				}

				dialog.dismiss();
			}
		});
		
		Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
		dialogButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
 
		dialog.show();
	}
	
	public void onBtnRefreshOffers(View v) {
		if (CallCabUtils.IsNetworkAvailable()) {
			CallCabUtils.FetchDataFromAPIAsync(
					ServiceMethod.GetAllOffersForCity, mReceiver, null);
		} else {
			CallCabUtils.MessageBox("Please turn on internet and then try !");
		}
	}

	public void onBtnShowOffer(View v) {

		String vendorName = v.getTag().toString();

		// google analytics
		EasyTracker.getTracker().sendEvent(CallCabApplication.CurrentCityName,
				"view cab offer", vendorName, null);

		Intent i = new Intent();
		i.putExtra(CommonInfo.BUNDLE_SELECTED_VENDOR_NAME, vendorName);
		i.setClass(CallCabApplication.getCallCabAppContext(),
				VendorOfferListActivity.class);
		startActivity(i);

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
		
		// google analytics
		EasyTracker.getTracker().sendEvent(CallCabApplication.CurrentCityName,
				"share cab number", v.getTag(R.id.vendorName).toString(), null);
		
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		String newline = System.getProperty("line.separator");
		
		String message = v.getTag(R.id.vendorName)+" ("+CallCabApplication.CurrentCityName+")"+newline+"Phone : "+v.getTag(R.id.phoneNumber)+newline+newline;
		message += "Sent by CallCab app! Get it on http://tinyurl.com/callcab";
		
		shareIntent
				.putExtra(
						Intent.EXTRA_TEXT,
						message);
		startActivity(Intent.createChooser(shareIntent, "Send cab phone number..."));
	}

	public void onBtnCallClicked(View v) {

		CabVendorInfo vendorInfo = (CabVendorInfo) ((View) v.getParent())
				.getTag();

		// google analytics
		EasyTracker.getTracker().sendEvent(CallCabApplication.CurrentCityName,
				"call cab number", vendorInfo.VendorName, null);

		SharedPrefUtils.UpdateRecentlyUsedList(
				CallCabApplication.CurrentCityName, vendorInfo.Id);

		CallCabUtils.MessageBox("Calling..." + vendorInfo.VendorName);
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:"
				+ vendorInfo.PhoneNumber.replaceAll("\\s", "")));
		startActivity(callIntent);
		BindRecentlyCalled();
	}

	public void onBtnViewWebPage(View v) {
		CabVendorInfo vendorInfo = (CabVendorInfo) ((View) v.getParent())
				.getTag();

		// google analytics
		EasyTracker.getTracker().sendEvent(CallCabApplication.CurrentCityName,
				"view cab page", vendorInfo.VendorName, (long) 3);

		SharedPrefUtils.UpdateRecentlyUsedList(
				CallCabApplication.CurrentCityName, (vendorInfo.Id));

		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(vendorInfo.Url));
		startActivity(browserIntent);
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {

		switch (resultCode) {
		case State.STATUS_RUNNING: // show progress
			return;

		case State.STATUS_ERROR: // handle the error;
			return;
		}
		
		if (resultCode == ServiceMethod.GetAllOffersForCity.getNumVal())
		{
			BindOffers();
			CallCabUtils.MessageBox("Offers Updated !");
		}
		else if (resultCode == ServiceMethod.SetVendorRating.getNumVal())
		{
			BindRatings();
			CallCabUtils.MessageBox("Rating Sent !");
		}
		else if (resultCode == ServiceMethod.GetVendorRating.getNumVal())
		{
			BindRatings();
		}
	}

	public void onViewAllCabtBtnClick(View v) {
		Intent i = new Intent();
		i.setClass(CallCabApplication.getCallCabAppContext(),
				CabListActivity.class);
		startActivity(i);
		finish();
	}

	public void onViewOfferBtnClick(View v) {
		// google analytics
		EasyTracker.getTracker().sendEvent(CallCabApplication.CurrentCityName,
				"view cab offer", "all", (long) 3);
		
		Intent i = new Intent();
		i.setClass(CallCabApplication.getCallCabAppContext(),
				VendorOfferListActivity.class);
		startActivity(i);
	}
	
}
