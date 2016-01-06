package com.home.callcab.info;

public class ServiceInfo {

	public class State
	{
		public static final int STATUS_RUNNING = -1;
		public static final int STATUS_ERROR = -2;		
	}
	
	public class CabOfferCount
	{
		public String CabVendorName;
		public int OfferCount;
	}
	
	
	
	public enum ServiceMethod
	{
		GetAllOffersForCity(0),
		SetVendorRating(1),
		GetVendorRating(2);
		
		private int numVal;

		ServiceMethod(int numVal) {
	        this.numVal = numVal;
	    }

	    public int getNumVal() {
	        return numVal;
	    }
	}
}
