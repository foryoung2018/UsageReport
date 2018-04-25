package com.htc.lib1.upm.uploader.budget.flow;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;

public class OtherFlowTotal extends Flow {
	
	public OtherFlowTotal(BudgetPreference pref) {
		super( pref, BudgetPreference.KEY_OTHER_TOTAL_APP_USAGE	);
	}
	
	@Override
	public String getTAG() {
		return "OtherFlowTotal";
	}
	
}