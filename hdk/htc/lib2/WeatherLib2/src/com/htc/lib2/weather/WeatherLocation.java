package com.htc.lib2.weather;

/**
 * Store weather location information
 */
public class WeatherLocation {
	/** id */
    private int id = -1; // for Location table use
    /** customLocation */
    private boolean customLocation = false;
    /** city code */
    private String code = "";
    /** city name */
    private String name = "";
    /** city state */
    private String state = "";
    /** country name */
    private String country = "";
    /** city latitude */
    private String latitude = "";
    /** city longitude */
    private String longitude = "";
    /** city TimeZone */
    private String timezone = "";
    /** city timezoneId */
    private String timezoneId = "";
    /** belong which app name*/
    private String app = "";

	/***********************************************
	 * Getter & Setter
	 ***********************************************/
    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     *  @return customLocation true or false
     */
	public boolean isCustomLocation() {
		return customLocation;
	}

	/**
	 * set CustomLocation
	 * @param customLocation true is customLocation, false is not customLocation
	 */
	public void setCustomLocation(boolean customLocation) {
		this.customLocation = customLocation;
	}
	
	/**
	 * get City Code
	 * @return String City Code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Set city code
	 * @param code city code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * Get City Name
	 * @return String City Name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set City Name
	 * @param name City Name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get City State
	 * @return String City State
	 */
	public String getState() {
		return state;
	}

	/**
	 * Set City State
	 * @param state City State
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Get Country Name
	 * @return Country Name
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Set Country Name
	 * @param country Country Name
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Get City Latitude
	 * @return City Latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	
	/**
	 * Set City Latitude
	 * @param latitude City Latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Get City Longitude 
	 * @return City Longitude 
	 */
	public String getLongitude() {
		return longitude;
	}
	
	/**
	 * Set City Longitude
	 * @param longitude City Longitude
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * get id 
	 * @return id
	 * 
	 * @deprecated [Not use any longer]
	 */
	/**@hide*/ 
	protected int getId() {
		return id;
	}

    /**
     *  Hide Automatically by SDK Team [U12000]
     *  @hide
     *  @param id input parameter
     */
	protected void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Get City TimeZone
	 * @return String City TimeZone
	 */
	public String getTimezone() {
		return timezone;
	}
	
	/**
	 * Set City TimeZone
	 * @param timezone City TimeZone
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	/**
	 * Get City TimeZone Id
	 * @return City TimeZone Id
	 */
	public String getTimezoneId() {
		return timezoneId;
	}
	
	/**
	 * Set City TimeZone Id
	 * @param timezoneId City TimeZone Id
	 */
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	/**
	 * Get belongs to the application name
	 * @return Belongs to the application name
	 */
	public String getApp() {
		return app;
	}
	
	/**
	 * Set belongs to the application name
	 * @param app Belongs to the application name
	 */
	public void setApp(String app) {
		this.app = app;
	}
}
