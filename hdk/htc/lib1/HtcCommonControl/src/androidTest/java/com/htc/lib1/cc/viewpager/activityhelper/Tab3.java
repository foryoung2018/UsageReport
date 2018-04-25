package com.htc.lib1.cc.viewpager.activityhelper;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Tab3 extends Fragment {
    private static final String LOG_TAG = HtcPagerFragmentDemo.LOG_TAG;

    public Tab3() {
        setHasOptionsMenu(true);
    }

    private int i;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        TextView v = new TextView(context);
        v.setBackgroundColor(Color.BLUE);
        v.setTextColor(Color.WHITE);
        v.setTextSize(50);
        v.setGravity(Gravity.CENTER);
        v.setText("Page3");
        return v;
    }



    @Override
    public void onAttach(Activity activity) {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onAttach");
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    public void onRestart() {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onRestart");
    }


    
    @Override
    public void onStart() {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onStart");
        super.onStart();
    }
    
    @Override
    public void onResume() {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onDetach");
        super.onDetach();
    }

    @Override
    public void onInflate(Activity activity, AttributeSet attrs,
            Bundle savedInstanceState) {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onInflate");
        super.onInflate(activity, attrs, savedInstanceState);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onActivityResult");
        Log.d(LOG_TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onLowMemory() {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
//      Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onHiddenChanged");
        super.onHiddenChanged(hidden);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.e(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onPrepareOptionsMenu");

    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(LOG_TAG, Thread.currentThread().getName() + ": " + this + " onCreateOptionsMenu");
        menu.add("Menu3-1");
        menu.add("Menu3-2");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }
}
