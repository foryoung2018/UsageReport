package com.htc.lib2.locationservicessetting.object;

public class LocationInfo {
	public String address;
	public double latitude;
	public double longitude;
	public double mode;
	// Add flag for determine current LocationInfo is valid
	public boolean isdatadefined;
	
	public LocationInfo() {
		this.address = "";
		this.latitude = 0;
		this.longitude = 0;
		this.mode = 0;
		this.isdatadefined = false;
	}
	public LocationInfo(String address, double latitude, double longitude, double mode){
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mode = mode;
		this.isdatadefined = true;
	}	
}
