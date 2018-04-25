package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class OtherFlowUL extends Flow {
	
	public OtherFlowUL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_OTHER_UL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "OtherFlowUL";
	}
	
}