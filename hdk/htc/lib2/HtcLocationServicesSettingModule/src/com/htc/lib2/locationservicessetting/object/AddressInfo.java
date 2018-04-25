package com.htc.lib2.locationservicessetting.object;

public class AddressInfo {
	public int _id;
	public String address;
	public double latitude;
	public double longitude;
	public double mode;
	public int contextualMode;
	
	public AddressInfo() {
		this._id = 0;
		this.address = "";
		this.latitude = 0;
		this.longitude = 0;
		this.mode = 0;
		this.contextualMode = 0;
	}
	public AddressInfo(int _id, String address, double latitude, double longitude, double mode, int contextualMode){
		this._id = _id;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mode = mode;
		this.contextualMode = contextualMode;
	}	
	public AddressInfo(LocationInfo info, int which){
		this._id = 0;
		this.address = info.address;
		this.latitude = info.latitude;
		this.longitude = info.longitude;
		this.mode = info.mode;
		this.contextualMode = which;
	}
}
