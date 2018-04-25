package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class MobileFlowDL extends Flow {
	
	public MobileFlowDL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_MOBILE_DL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "MobileFlowDL";
	}
	
}