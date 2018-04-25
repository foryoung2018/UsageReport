package com.htc.lib1.locationservicessettingmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.htc.lib1.cc.app.HtcProgressDialog;
import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.lib1.cc.widget.HtcListItem;
import com.htc.lib1.cc.widget.HtcListItem2LineText;
import com.htc.lib1.cc.widget.HtcListItemSeparator;
import com.htc.lib1.cc.widget.HtcListItemSingleText;
import com.htc.lib1.cc.widget.HtcListView;
import com.htc.lib1.cc.widget.IDividerController;
import com.htc.lib1.locationservicessettingmanager.R;
import com.htc.lib1.locationservicessettingmanager.base.HtcListActivity;
import com.htc.lib1.locationservicessettingmanager.base.LocationServicesMainBaseActivity;
import com.htc.lib1.locationservicessettingmanager.util.SMLog;
import com.htc.lib1.locationservicessettingmanager.util.Utils;
import com.htc.lib2.locationservicessetting.HtcLocationServiceClient;
import com.htc.lib2.locationservicessetting.object.AddressInfo;
import com.htc.lib2.locationservicessetting.object.WiFiInfo;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressNetworkActivity extends HtcListActivity{
	private static final String TAG = "[LocationSvSetting]";
	
	// Those definitions are copied from location picker
	public static final int FLAG_USE_INPUT_LOCATION	= 3;
	public static final String EXTRA_PICKER_INPUT_LOCATION = "InputLocation";
	public static final String EXTRA_PICKER_LATITUDE	= "PickerLatitude";
	public static final String EXTRA_PICKER_LONGITUDE	= "PickerLongitude";
    	
    private static final String BROADCAST_INTENT_LOCATION_CHANGED = "com.htc.locationservicessetting.LOCATION_CHANGED";
    private static final String BROADCAST_INTENT_WIFI_CHANGED = "com.htc.locationservicessetting.WIFI_CHANGED";
    private static final String BROADCAST_RESTORE_EVENT = "com.htc.locationservicessetting.RESTORE";
    private static final String PERMISSION_HSP = "com.htc.sense.permission.APP_HSP";
    private static final String ACTION_LOCATIONPICKER = "com.htc.android.locationpicker";
    
    private static final int DIALOG_CHOOSE_MENU_ADD_ACTION = 1;
    private static final int DIALOG_CHOOSE_ADDRESS_ACTION = 2; 
    private static final int DIALOG_CHOOSE_WIFI_ACTION = 3; 
    private static final int DIALOG_CHOOSE_WIFI_LIST = 4; 
    private static final int DIALOG_TURN_ON_WIFI = 5; 
    private static final int DIALOG_ADDRESS_DUPLICATE_OTHER_MODE = 6;
    private static final int LOCATION_SERVICE_DISABLED_DIALOG = 7;
    
    private static final int ITEMS_LIMIT = 20;
    private static boolean bSendLocationChangedBroadcast = false;
    private static boolean bSendWifiChangedBroadcast = false;
    
    private Context mContext = this;  
    private LayoutInflater mInflater;
    private String mTitle;
    private int mRequestCode;
    private List<Items> mItemList = new ArrayList<Items>();
    private List<Items> mItemAddress = new ArrayList<Items>();
    private List<Items> mItemWiFi = new ArrayList<Items>();
    private Items mCurrentModifyItem = null;
    private AddressInfo mUpdateLocationInfo = null;
    private List<String> mStoredWifiBssid = new ArrayList<String>();
    
    private BroadcastReceiver mRestoreEventReceiver;
    private IntentFilter mRestoreEventActionFilter;
    
    private HtcProgressDialog mSpinnerDialog;
    
	/**
	 * Id to identify a Location permission request.
	 */
	private static final int REQUEST_LOCATION = 1;

	/**
	 * Permissions required to Location
	 */
	private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION};
    
    private String PREFERENCES_PERMISSIONS_NAME = "Preferences_Permissions";
    private String PREFERENCES_PERMISSIONS_DENY = "Permissions_Deny";

    enum ListItemType {
    	SEPARATOR,
    	ADDRESS,
    	WIFI_NETOWRK
    }

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.htclistview_main);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setHeaderBarColor();
        
        mTitle = getIntent().getExtras().getString(LocationServicesMainBaseActivity.EXTRA_LAUNCH_WHICH);
        setTitle(mTitle);
        
        if (mTitle.equals(getResources().getString(R.string.home_label))) {
        	mRequestCode = HtcLocationServiceClient.CODE_HOME_ADDRESS;
        }
        else {
        	mRequestCode = HtcLocationServiceClient.CODE_WORK_ADDRESS;
        }
        mRestoreEventActionFilter = new IntentFilter(BROADCAST_RESTORE_EVENT);
        mRestoreEventReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(BROADCAST_RESTORE_EVENT))
				{
					SMLog.i(TAG, "Receive restore event broadcast");
					lookupAddress();
				}
			}
        	
        };
    }
    
	@Override
    public void onStart() {
    	super.onStart();

    	lookupAddress();
    }

    @Override
    public void onStop() {
    	super.onStop();
    	
    	if (bSendLocationChangedBroadcast) {
    		SMLog.d(TAG, "location database changed, send broadcast");
    		bSendLocationChangedBroadcast = false;
    		
    		Intent intent = new Intent(BROADCAST_INTENT_LOCATION_CHANGED);
    		sendBroadcast(intent, PERMISSION_HSP);
    	}
    	if(bSendWifiChangedBroadcast) {
    		SMLog.d(TAG, "wifi database changed, send broadcast");
    		bSendWifiChangedBroadcast = false;
    		
    		Intent intent = new Intent(BROADCAST_INTENT_WIFI_CHANGED);
    		sendBroadcast(intent, PERMISSION_HSP);
    	}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	registerReceiver(mRestoreEventReceiver, mRestoreEventActionFilter, PERMISSION_HSP, null);
    }
    @Override
    public void onPause(){
    	super.onPause();
    	unregisterReceiver(mRestoreEventReceiver);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	    	
    	mCurrentModifyItem = null;
    	int separatorCount = (mItemAddress.isEmpty()? 0 : 1) + (mItemWiFi.isEmpty()? 0 : 1);
    	
    	if ((mItemList.size() - separatorCount) < ITEMS_LIMIT) {
    		createDialog(DIALOG_CHOOSE_MENU_ADD_ACTION).show();
    	}
    	else {
    		Toast.makeText(mContext, R.string.too_much_item, Toast.LENGTH_SHORT).show();
    	}
    	
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

    	if (data == null || resultCode != RESULT_OK) {
    		return;
    	}
    	
    	switch(requestCode){
        case HtcLocationServiceClient.CODE_HOME_ADDRESS:
        case HtcLocationServiceClient.CODE_WORK_ADDRESS:
        	if (data.getExtras() != null) {
        		String address = data.getExtras().getString("RETURN_ADDRESS");     		
        		double latitude = data.getExtras().getDouble("RETURN_LATITUDE");
        		double longitude = data.getExtras().getDouble("RETURN_LONGITUDE");
        		
        		AddressInfo locationInfo = new AddressInfo(0, address, latitude, longitude, 1, requestCode);
        		HtcLocationServiceClient.UPDATE_ADDRESS_STATUS status;
        		if (mCurrentModifyItem != null) {
        			locationInfo._id = mCurrentModifyItem.mId;
        			status = HtcLocationServiceClient.updateAddressInfo(this, locationInfo, false);       			
        			mCurrentModifyItem = null;
        		}
        		else {
        			status = HtcLocationServiceClient.insertAddressInfo(this, locationInfo, false);
        		}
        		
       			if (HtcLocationServiceClient.UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_SAME_MODE == status) {
       				Toast.makeText(mContext, R.string.duplicate_address, Toast.LENGTH_SHORT).show();
    			}
       			else if (HtcLocationServiceClient.UPDATE_ADDRESS_STATUS.ADDRESS_DUPLICATE_OTHER_MODE == status) {
       				mUpdateLocationInfo = locationInfo;
    				createDialog(DIALOG_ADDRESS_DUPLICATE_OTHER_MODE).show();
       			}
    			else if (HtcLocationServiceClient.UPDATE_ADDRESS_STATUS.SUCCESS == status) {
    				bSendLocationChangedBroadcast = true;
    			}
    			else {
    				SMLog.w(TAG, "[updateAddressInfo] update address failed, status = " + status);
    			}
        	}
        	break;
        }
    	
    	loadDataFromHSP();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	mCurrentModifyItem = mItemList.get(position);
    	
    	if (mCurrentModifyItem.mType == ListItemType.ADDRESS) {
	    	createDialog(DIALOG_CHOOSE_ADDRESS_ACTION).show();
    	}
    	else if (mCurrentModifyItem.mType == ListItemType.WIFI_NETOWRK) {
    		createDialog(DIALOG_CHOOSE_WIFI_ACTION).show();
    	}
    	else {
    		// do nothing
    	}
    }
    
    protected Dialog createDialog(int id) {
        final String va_ok = getString(R.string.va_ok);
        final String va_cancel = getString(R.string.va_cancel);

        switch (id) {
        case DIALOG_CHOOSE_MENU_ADD_ACTION:
        	return new HtcAlertDialog.Builder(mContext)
        	.setTitle(mTitle)
            .setItems(R.array.choose_target,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (0 == which) {
                        	// Address
                          	Intent intent = new Intent();
                       		intent.setAction(ACTION_LOCATIONPICKER);
                           	startActivityForResult(intent, mRequestCode);
                            } else {
                                if (checkWifiStatus(mContext)) {
                                    // WiFi
                                    if(!Utils.isNetworkLocationEnabled(mContext)) {
                                        Log.i(TAG, "Location service is disabled.");
                                        createDialog(LOCATION_SERVICE_DISABLED_DIALOG).show();
                                    }
                                    else {
                                        if (Build.VERSION.SDK_INT < 23)
                                            createDialog(DIALOG_CHOOSE_WIFI_LIST).show();
                                        else
                                            Choose_WiFi_List();
                                    }
                                } else {
                                    createDialog(DIALOG_TURN_ON_WIFI).show();
                        	}
                        }
                    }
                })
            .create();
        case DIALOG_CHOOSE_ADDRESS_ACTION:
        	return new HtcAlertDialog.Builder(this)
	    	.setTitle(mCurrentModifyItem.mPrimaryText)
	        .setItems(R.array.address_action,
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    if (0 == which) {
	                    	// Edit
	                      	Intent intent = new Intent();
	                   		intent.setAction(ACTION_LOCATIONPICKER);
	                   		intent.putExtra(EXTRA_PICKER_LATITUDE, mCurrentModifyItem.mLatitude);
	            			intent.putExtra(EXTRA_PICKER_LONGITUDE, mCurrentModifyItem.mLongitude);
	            			//Need set this flag;otherwise location picker will use current location
	            			intent.putExtra(EXTRA_PICKER_INPUT_LOCATION, FLAG_USE_INPUT_LOCATION);
	                       	startActivityForResult(intent, mRequestCode);
	                    }
	                    else {
	                    	// Remove
	                        if (mItemAddress.contains(mCurrentModifyItem)) {
	                        	bSendLocationChangedBroadcast = true;
	                        	HtcLocationServiceClient.deleteAddressInfo(mContext, mCurrentModifyItem.mId);
	                        	mCurrentModifyItem = null;
	                        	loadDataFromHSP();
	                        }
	                    }
	                }
	            })
	         .create();
        case DIALOG_CHOOSE_WIFI_ACTION:
        	return new HtcAlertDialog.Builder(this)
	    	.setTitle(mCurrentModifyItem.mPrimaryText)
	        .setItems(R.array.wifi_action,
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    if (0 == which) {
	                    	// Remove
	                        if (mItemWiFi.contains(mCurrentModifyItem)) {
	                        	mItemWiFi.remove(mCurrentModifyItem);
	                        	
                        		// remove from HSP
	                        	bSendWifiChangedBroadcast = true;
                        		HtcLocationServiceClient.deleteWiFiInfo(mContext, mCurrentModifyItem.mId);

	                        	mCurrentModifyItem = null;
	                        	
	                        	loadDataFromHSP();
	                        }
	                    }
	                }
	            })
	         .create();

        case DIALOG_CHOOSE_WIFI_LIST:
			return ShowWiFiListDlg();
        
        case DIALOG_ADDRESS_DUPLICATE_OTHER_MODE:
        	String message = null;
        	if (mRequestCode == HtcLocationServiceClient.CODE_HOME_ADDRESS) {
        		message = getResources().getString(R.string.switch_mode_home);
        	}
        	else {
        		message = getResources().getString(R.string.switch_mode_work);
        	}
        	
        	return new HtcAlertDialog.Builder(mContext)
        	.setTitle(R.string.switch_mode_title)
        	.setMessage(message)
            .setPositiveButton(va_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mUpdateLocationInfo != null) {
                        	HtcLocationServiceClient.insertAddressInfo(mContext, mUpdateLocationInfo, true);
                        	mUpdateLocationInfo = null;
                        	
                        	loadDataFromHSP();
                        }
                    }
                })
            .setNegativeButton(va_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        
                    }
                })
            .create();
        case DIALOG_TURN_ON_WIFI:
        	return new HtcAlertDialog.Builder(mContext)
        	.setTitle(R.string.turn_on_wifi_title)
        	.setMessage(R.string.turn_on_wifi_message)
            .setPositiveButton(R.string.turn_on_wifi_positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
            .setNegativeButton(va_cancel, 
            	new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        
                    }
                })
            .create();

            case LOCATION_SERVICE_DISABLED_DIALOG:
                return new HtcAlertDialog.Builder(mContext)
                        .setTitle(R.string.dialog_title_location_src_disable)
                        .setMessage(R.string.dialog_message_location_src_disable)
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
        }
    
		return null;
    }
    
    ///When the app is installed and ran the first time, the shouldShowRequestPermissionRationale() return false
    //for UI guideline flow, we use this method to follow it.
    private void SetPermissionsDenyFlag(boolean bSet)
    {
        SharedPreferences permissonsFlag = getSharedPreferences(PREFERENCES_PERMISSIONS_NAME, MODE_PRIVATE);
        permissonsFlag.edit()
                .putBoolean(PREFERENCES_PERMISSIONS_DENY, bSet)
                .commit();

    }

    private boolean GetPermissionsDenyFlag()
    {
        boolean bSet = false ;

        SharedPreferences permissonsFlag = getSharedPreferences(PREFERENCES_PERMISSIONS_NAME, MODE_PRIVATE);
        bSet = permissonsFlag.getBoolean(PREFERENCES_PERMISSIONS_DENY, false);

        return bSet ;
    }

    private void Choose_WiFi_List()
    {

        // Check if the Location permission is already available.
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission has not been granted.

            RequestLocationPermissions() ;

        } else {
            // Location permissions is already available
            Log.i(TAG, "Location permission has already been granted.");
            ShowWiFiListDlg().show();
        }
    }

    private void RequestLocationPermissions()
    {
        final String DlgTitle = getString(R.string.hsp_app_name);
        final String firstDlgContent = getString(R.string.alert_dialog_first_permission_content);
        final String secondDlgContent = String.format(getString(R.string.alert_dialog_second_permission_content), DlgTitle) ;

        Log.i(TAG, "Location permission has NOT been granted. Requesting permission.");

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG, "Displaying Location permission rationale to provide additional context.");

            HtcAlertDialog.Builder firstInformAltDlg = new HtcAlertDialog.Builder(AddressNetworkActivity.this);
            firstInformAltDlg.setTitle(DlgTitle);
            firstInformAltDlg.setMessage(firstDlgContent);
            firstInformAltDlg.setPositiveButton(getString(R.string.alert_dialog_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                dialog.dismiss();
                                requestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

            try {
                firstInformAltDlg.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {

            boolean bDeny = GetPermissionsDenyFlag() ;
            if(bDeny)
            {
                //If shouldShowRequestPermissionRationale() return false, and Deny flag is true,
                //It means User has previously denied the permission, and choose the Never ask again.
                Log.i(TAG, "User has previously denied the permission, and choose the Never ask again.");
                HtcAlertDialog.Builder secondInformAltDlg = new HtcAlertDialog.Builder(AddressNetworkActivity.this);
                secondInformAltDlg.setTitle(DlgTitle);
                secondInformAltDlg.setMessage(secondDlgContent);
                secondInformAltDlg.setPositiveButton(getString(R.string.alert_dialog_permission_settings),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    dialog.dismiss();

                                    String packageName = "com.htc.sense.hsp";

                                    try {
                                        //Open the specific App Info page:
                                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + packageName));
                                        startActivity(intent);

                                    } catch (ActivityNotFoundException e) {
                                        //e.printStackTrace();

                                        //Open the generic Apps page:
                                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                secondInformAltDlg.setNegativeButton(getString(R.string.alert_dialog_permission_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    secondInformAltDlg.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else
            // Location permission has not been granted yet. Request it directly.
            requestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

	private Dialog ShowWiFiListDlg()
	{
		final String va_ok = getString(R.string.va_ok);
		final String va_cancel = getString(R.string.va_cancel);

        if (Build.VERSION.SDK_INT >= 23)
            SetPermissionsDenyFlag(false);

		final List<ScanResult> wifiPreResult = getWifiScanResults(mContext);
		List<ScanResult> wifiResult = filterWiFiAP(wifiPreResult);

        if (wifiResult.size() == 0) {
            Log.i(TAG, "No Wi-Fi networks available.");
            return new HtcAlertDialog.Builder(mContext)
                    .setTitle(R.string.select_wifi_title)
                    .setMessage(R.string.no_wifi_available)
                    .setPositiveButton(va_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            })
                    .create();
        }

		final String[] multiChoiceItems = new String[wifiResult.size()];
		final boolean[] checkedItems = new boolean[multiChoiceItems.length];
		for (int i = 0 ; i < wifiResult.size() ; i++) {
			multiChoiceItems[i] = wifiResult.get(i).SSID;
		}

		return new HtcAlertDialog.Builder(mContext)
				.setTitle(R.string.select_wifi_title)
				.setMultiChoiceItems(multiChoiceItems, checkedItems,
						new DialogInterface.OnMultiChoiceClickListener() {
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {

							}
						})
				.setPositiveButton(va_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								SparseBooleanArray itemPos = ((HtcAlertDialog) dialog).getListView()
										.getCheckedItemPositions();
								for (int i = 0; i < itemPos.size(); ++i) {
									int pos = itemPos.keyAt(i);
									boolean isChecked = itemPos.valueAt(i);
									if (!isChecked) {
										continue;
									}

									List<String> bssidArray = new ArrayList<String>();
									// sync to HSP
									for (ScanResult item : wifiPreResult) {
										if (item.SSID.equals(multiChoiceItems[pos])) {
											if (!mStoredWifiBssid.contains(item.BSSID)) {
												if (!bssidArray.contains(item.BSSID)) {
													bssidArray.add(item.BSSID);
												}
											}
										}
									}

									// insert to HSP
									bSendWifiChangedBroadcast = true;
									HtcLocationServiceClient.insertWiFiInfo(mContext, new WiFiInfo(0, multiChoiceItems[pos], bssidArray, mRequestCode));
								}

								loadDataFromHSP();
							}
						})
				.setNegativeButton(va_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						})
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

					}
				})
				.create();
	}
    
    private void loadDataFromHSP() {   		   	
        // Read data from HSP
    	mItemAddress.clear();
        List<AddressInfo> locationList = HtcLocationServiceClient.queryAddressInfo(this, mRequestCode);       	
        for (AddressInfo location:locationList) {
        	if (!TextUtils.isEmpty(location.address)) {
        		mItemAddress.add(new Items(ListItemType.ADDRESS, 
        								   location.address, 
        			            		   "",
        			            		   location.latitude,
        			            		   location.longitude,
        			            		   location._id));
        	}
        	else {
        		mItemAddress.add(new Items(ListItemType.ADDRESS, 
						   getResources().getString(R.string.unknown_address), 
	            		   "",
	            		   location.latitude,
	            		   location.longitude,
	            		   location._id));
        	}
        }
        
        mItemWiFi.clear();
        List<WiFiInfo> wifiList = HtcLocationServiceClient.queryWiFiInfo(this, mRequestCode);
        for (WiFiInfo wifi:wifiList) {
        	if (!TextUtils.isEmpty(wifi.ssid)) {
        		mItemWiFi.add(new Items(ListItemType.WIFI_NETOWRK, 
        								   wifi.ssid, 
        			            		   "",
        			            		   0,
        			            		   0,
        			            		   wifi._id));
        	}
        }
        
        mStoredWifiBssid = HtcLocationServiceClient.getBssidList(this, HtcLocationServiceClient.CODE_ALL_ADDRESS);
        
        setupListAdapter();
    }
    
    private void setupListAdapter() {
       	mItemList.clear();
        
       	if (!mItemAddress.isEmpty()) {
       		mItemList.add(new Items(ListItemType.SEPARATOR, getResources().getString(R.string.address_title_label), ""));
       	}
       	mItemList.addAll(mItemAddress);
       	
       	if (!mItemWiFi.isEmpty()) {
       		mItemList.add(new Items(ListItemType.SEPARATOR, getResources().getString(R.string.wifi_title_label), ""));
       	}
       	mItemList.addAll(mItemWiFi);
       	
       	MyListAdapter mlistAdapter = new MyListAdapter(this, mItemList);
    	setListAdapter(mlistAdapter);
    	HtcListView htcListView = (HtcListView) getListView();
    	htcListView.setDividerController(mlistAdapter);
    }
    
    private boolean checkWifiStatus(Context context) {  
    	boolean enabled = false;
    	if (context != null) {
			final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager != null) {
				enabled = wifiManager.isWifiEnabled();
			}
    	}
		return enabled;
    }
    
    private boolean checkNetworkStatus(Context context) {
		boolean enabled = false;
		if (context != null) {
			final ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				final NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
					enabled = true;
				}
			}
		}
		return enabled;
	}
    
    @SuppressWarnings("unchecked")
	private void lookupAddress() {
    	if (localeChanged()) {
    		mSpinnerDialog = new HtcProgressDialog(mContext);
            mSpinnerDialog.setMessage(getString(R.string.st_loading));
            mSpinnerDialog.setIndeterminate(true);
            mSpinnerDialog.setCancelable(false);
            mSpinnerDialog.show();
    		
	        List<AddressInfo> locationList = HtcLocationServiceClient.queryAddressInfo(this, HtcLocationServiceClient.CODE_ALL_ADDRESS);  
	        AddressSearchAsyncTask task = new AddressSearchAsyncTask();
	        task.execute(locationList);
        }
        else {
        	loadDataFromHSP();
        }    	
    }
    
    private List<ScanResult> getWifiScanResults(Context context) {
		List<ScanResult> results = new ArrayList<ScanResult>();
		if (context == null)
			return results;
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
        	List<ScanResult> scanResults = wifiManager.getScanResults();
        	if (scanResults != null) {
        		for (ScanResult sr : scanResults) {
	        		results.add(sr);
        		}
        	}
        } else {	// just in case Wifi-Not enabled, some body might get NPE
        	results = new ArrayList<ScanResult>();
        }
        return results;
	}
    
    private boolean localeChanged() {
    	// Autonavi didn't provide multi-language address
    	if (Utils.isChinaSku() || !checkNetworkStatus(mContext)) {
    		return false;
    	}
    	
    	Locale current = getResources().getConfiguration().locale;
    	
    	String lastLocale = HtcLocationServiceClient.getLastLocale(mContext);
    	if (!lastLocale.equals(current.toString())) {
    		SMLog.i(TAG, "[localeChanged] change locale to " + current);
    		HtcLocationServiceClient.setLastLocale(mContext, current.toString());
    		
    		return true;
    	}
    	
    	return false;
    }
    
    private List<ScanResult> filterWiFiAP(List<ScanResult> inputWiFiList) {
    	List<ScanResult> outputWiFiList = new ArrayList<ScanResult>();
    	List<String> existWiFiAP = new ArrayList<String>();
    	
    	for (ScanResult item:inputWiFiList) {
    		if (!TextUtils.isEmpty(item.SSID) && !mStoredWifiBssid.contains(item.BSSID)) {
    			if (!existWiFiAP.contains(item.SSID)) {
		    		outputWiFiList.add(item);
		    		existWiFiAP.add(item.SSID);
    			}
    		}
    	}
    	
    	return outputWiFiList;
    }
    
    private String composeAddressLine(final Address address) {
		int maxAddressLineNum = address.getMaxAddressLineIndex();
		StringBuffer addressLine = new StringBuffer();

		for (int i = 0; i <= maxAddressLineNum; i++) {
			if (i > 0)
			{
				if(Utils.isGoogleMapsSharedLibraryExist(mContext) || !(address.getLocale() == Locale.CHINA && address.getCountryCode().contentEquals("CN")))
					addressLine.append(", ");
			}
			addressLine.append(address.getAddressLine(i));
		}

		return addressLine.toString();
	}
    
    private class MyListAdapter extends BaseAdapter implements IDividerController {
        List<Items> mItems;

        public MyListAdapter(Context context, List<Items> items) {
            mItems = items;
        }

        public int getCount() {
            return mItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	Items item = mItems.get(position);
        	if (item == null) {
        		return null;
        	}
        	
            if (item.mType == ListItemType.SEPARATOR) {
            	HtcListItemSeparator separator = (HtcListItemSeparator) mInflater.inflate(R.layout.listitem_separator, null);
            	separator.setText(HtcListItemSeparator.TEXT_LEFT, mItems.get(position).mPrimaryText);
            	return separator;
            }
            else if (item.mType == ListItemType.ADDRESS) {
            	HtcListItem listitem = (HtcListItem) mInflater.inflate(R.layout.listitem_singlelinetext, null);
            	HtcListItemSingleText text = (HtcListItemSingleText) listitem.findViewById(R.id.text1);
            	text.setText(item.mPrimaryText);
            	return listitem;
            }
            else {
            	HtcListItem listitem = (HtcListItem) mInflater.inflate(R.layout.listitem_twolinetext, null);
            	HtcListItem2LineText text = (HtcListItem2LineText) listitem.findViewById(R.id.text1);
                text.setPrimaryText(item.mPrimaryText);
                if (TextUtils.isEmpty(item.mSecondaryText)) {
                	text.setSecondaryTextVisibility(View.GONE);
                }
                else {
                	text.setSecondaryText(item.mSecondaryText);
                }
                
                return listitem;
            }
            
            
        }

		@Override
		public int getDividerType(int position) {
			Items item = mItems.get(position);
        	if (item == null) {
        		return IDividerController.DIVIDER_TYPE_NORAML;
        	}
        	
        	if (item.mType == ListItemType.SEPARATOR) {
        		return IDividerController.DIVIDER_TYPE_NONE;
        	}
        	
        	if ((position + 1) < getCount()) {
        		item = mItems.get(position+1);
        		if (item.mType == ListItemType.SEPARATOR) {
            		return IDividerController.DIVIDER_TYPE_NONE;
            	}
        	}
			return IDividerController.DIVIDER_TYPE_NORAML;
		}
		
		@Override
		public boolean isEnabled(int position) {
			Items item = mItems.get(position);
        	if (item == null) {
        		return false;
        	}
        	
        	if (item.mType == ListItemType.SEPARATOR) {
        		return false;
        	}
        	
        	return true;
	    }
    }
    
    private static class Items {
    	ListItemType mType;
    	String mPrimaryText;
    	String mSecondaryText;
    	double mLatitude;
    	double mLongitude;
    	int mId;
    	
    	Items(ListItemType type, String primaryText, String secondaryText) {
    		mType = type;
    		mPrimaryText = primaryText;
    		mSecondaryText = secondaryText;
    		mLatitude = 0;
    		mLongitude = 0;
    		mId = 0;
    	}
    	
    	Items(ListItemType type, String primaryText, String secondaryText, double latitude, double longitude, int _id) {
    		mType = type;
    		mPrimaryText = primaryText;
    		mSecondaryText = secondaryText;
    		mLatitude = latitude;
    		mLongitude = longitude;
    		mId = _id;
    	}
    }

    private class AddressSearchAsyncTask extends AsyncTask<List<AddressInfo>, Void, Void> {
    	private static final int MAX_RETRY = 3;
    	
		@Override
		protected Void doInBackground(List<AddressInfo>... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			try {
				for (AddressInfo item : params[0]) {
					List<Address> newAddressList = null;
					for (int count = 0 ; count < MAX_RETRY ; count++) {
						newAddressList = geocoder.getFromLocation(item.latitude, item.longitude, 1);
						if (newAddressList != null && newAddressList.size() > 0 ) {
							Address address = newAddressList.get(0);
							item.address = composeAddressLine(address);
							HtcLocationServiceClient.updateAddressInfo(mContext, item, true);
							break;
						}
					}
					
					if (null == newAddressList) {
						SMLog.w(TAG, "[AddressSearchAsyncTask] failed to get location from geo code");
					}
				}
			} catch (Exception e) {
				SMLog.w(TAG, "[AddressSearchAsyncTask] Get address from geo code exception, e = " + e);
			}	
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mSpinnerDialog.dismiss();
			loadDataFromHSP();
		} 	
    }

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {

		boolean bPermissions = true ;

		if (requestCode == REQUEST_LOCATION) {
			// BEGIN_INCLUDE(permission_result)
			Log.i(TAG, "Received response for Location permission request.");

			if(grantResults.length < 1)
			{
				Log.i(TAG, "Location permissions were NOT granted.");
				bPermissions = false ;
			}
			else
			{
				// Verify that each required permission has been granted.
				for (int result : grantResults) {
					if (result != PackageManager.PERMISSION_GRANTED) {
						bPermissions = false ;
						break;
					}
				}
			}

			if(bPermissions)
			{
				Log.i(TAG, "Location permissions were granted.");
                SetPermissionsDenyFlag(false);
				ShowWiFiListDlg().show();
			}
			else {
				Log.i(TAG, "Location permissions were denied.");
                SetPermissionsDenyFlag(true);
			}
			// END_INCLUDE(permission_result)
		}
		else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
