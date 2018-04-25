package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;


public class AllFlowTotal extends Flow {
	
	public AllFlowTotal(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_ALL_TOTAL_APP_USAGE );
	}

	@Override
	public String getTAG() {
		return "AllFlowTotal";
	}
	
}