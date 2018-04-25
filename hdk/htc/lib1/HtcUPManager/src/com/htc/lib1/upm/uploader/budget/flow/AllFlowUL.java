package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class AllFlowUL extends Flow {
	
	public AllFlowUL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_ALL_UL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "AllFlowUL";
	}
	
}