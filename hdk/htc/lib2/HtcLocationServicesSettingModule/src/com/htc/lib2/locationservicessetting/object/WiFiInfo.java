package com.htc.lib2.locationservicessetting.object;

import java.util.ArrayList;
import java.util.List;

public class WiFiInfo {
	public int _id;
	public String ssid;
	public List<String> bssid;
	public int contextualMode;
	
	public WiFiInfo() {
		this._id = 0;
		this.ssid = "";
		this.bssid = new ArrayList<String>();
		this.contextualMode = 0;
	}
	public WiFiInfo(int _id, String ssid, List<String> bssid, int contextualMode){
		this._id = _id;
		this.ssid = ssid;
		this.bssid = bssid;
		this.contextualMode = contextualMode;
	}	
}
