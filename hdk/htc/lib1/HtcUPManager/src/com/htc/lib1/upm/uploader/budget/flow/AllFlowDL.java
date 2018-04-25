package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;


public class AllFlowDL extends Flow {
	
	public AllFlowDL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_ALL_DL_APP_USAGE );
	}

	@Override
	public String getTAG() {
		return "AllFlowDL";
	}
	
}