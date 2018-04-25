package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class MobileFlowTotal extends Flow {
	
	public MobileFlowTotal(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_MOBILE_TOTAL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "MobileFlowTotal";
	}
	
}