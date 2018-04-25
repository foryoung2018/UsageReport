package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class MobileFlowUL extends Flow {
	
	public MobileFlowUL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_MOBILE_UL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "MobileFlowUL";
	}
	
}