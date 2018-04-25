package com.htc.lib1.upm.uploader.budget.network;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;
import com.htc.lib1.upm.uploader.budget.flow.Flow;
import com.htc.lib1.upm.uploader.budget.flow.MobileFlowDL;
import com.htc.lib1.upm.uploader.budget.flow.MobileFlowTotal;
import com.htc.lib1.upm.uploader.budget.flow.MobileFlowUL;

public class MobileNetwork extends Network {
	
	private final static int DL = 0;
	private final static int UL = 1;
	private final static int TOTAL = 2;
	
	protected Flow[] mFlow;
	
	public MobileNetwork(BudgetPreference pref) {
		mFlow = new Flow[3];
		mFlow[DL] = new MobileFlowDL(pref);
		mFlow[UL] = new MobileFlowUL(pref);
		mFlow[TOTAL] = new MobileFlowTotal(pref);
	}

	@Override
	protected Flow getFlowDL() {
		return mFlow[DL];
	}

	@Override
	protected Flow getFlowUL() {
		return mFlow[UL];
	}

	@Override
	protected Flow getFlowTotal() {
		return mFlow[TOTAL];
	}

	@Override
	public String getTAG() {
		return "MobileNetwork";
	}
}