package com.htc.lib1.upm.uploader.budget.network;

import com.htc.lib1.upm.uploader.budget.BudgetPreference;
import com.htc.lib1.upm.uploader.budget.flow.AllFlowDL;
import com.htc.lib1.upm.uploader.budget.flow.AllFlowTotal;
import com.htc.lib1.upm.uploader.budget.flow.AllFlowUL;
import com.htc.lib1.upm.uploader.budget.flow.Flow;

public class AllNetwork extends Network {
	
	private final static int DL = 0;
	private final static int UL = 1;
	private final static int TOTAL = 2;
	
	protected Flow[] mFlow;
	
	public AllNetwork(BudgetPreference pref) {
		mFlow = new Flow[3];
		mFlow[DL] = new AllFlowDL(pref);
		mFlow[UL] = new AllFlowUL(pref);
		mFlow[TOTAL] = new AllFlowTotal(pref);
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
		return "AllNetwork";
	}
}