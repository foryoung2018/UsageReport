package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class OtherFlowDL extends Flow {
	
	public OtherFlowDL(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_OTHER_DL_APP_USAGE );
	}
	
	@Override
	public String getTAG() {
		return "OtherFlowDL";
	}
	
}