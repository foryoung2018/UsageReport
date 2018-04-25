/**
 * HTC Corporation Proprietary Rights Acknowledgment
 *
 * Copyright (C) 2010 HTC Corporation
 *
 * All Rights Reserved.
 *
 * The information contained in this work is the exclusive property of HTC Corporation
 * ("HTC").  Only the user who is legally authorized by HTC ("Authorized User") has
 * right to employ this work within the scope of this statement.  Nevertheless, the 
 * Authorized User shall not use this work for any purpose other than the purpose 
 * agreed by HTC.  Any and all addition or modification to this work shall be 
 * unconditionally granted back to HTC and such addition or modification shall be 
 * solely owned by HTC.  No right is granted under this statement, including but not 
 * limited to, distribution, reproduction, and transmission, except as otherwise 
 * provided in this statement.  Any other usage of this work shall be subject to the 
 * further written consent of HTC.
 */

package com.htc.lib1.masthead.view;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htc.lib1.masthead.R;
import com.htc.lib1.masthead.view.WeatherTimeKeeper.WeatherTimeChangedListener;

/** 
 * This views show the current time, location, and weather.
 */
public class Masthead extends FrameLayout {
	private static final String LOG_TAG = Masthead.class.getSimpleName();
	private static int ANIMATE_STATE_DISABLED = -1;
	private static int ANIMATE_STATE_ENABLED = -2;
    
	/**
	 * Implement this to handle clicking on the clock and weather in masthead view.
	 *
	 */
    public interface ClickListener {
    	/**
    	 * This callback will be invoked when clock is clicked.
    	 */
        public void onClickClock();
        /**
         * This callback will be invoked when weather is clicked.
         */
        public void onClickWeather();
    }
    
    private static final String FALLBACK_TEXT = "??";

    private DigitalClock mClockView;
    private TextView mAmPmTextView;
    private WeatherDisplay mWeatherView;
    // info
    private View mInfoAreaView;
    private TextView mDateTextView;
    private TextView mHolidayTextView;
    private View mSplitView;
    private TextView mCityNameTextView;
    private TextView mCityCommaTextView;
    private TextView mWeatherTextView;
    private View mTemperatureLayout;
    private TextView mTemperatureTextView;
    private TextView mTemperatureDegreeSymbolTextView;
    private TextView mTemperatureDegreeTextView;
	private boolean mIsStopped = false;

	private boolean mbIsEnableWeather = true;
	private int mAnimationState = ANIMATE_STATE_ENABLED;
	
	// keep the update actions to run when attached since updates from weather/time could happen when this view is detached.
    private static final int ACTION_SET_CLOCK = 0;
    private static final int ACTION_SET_AMPM = 1;
    private static final int ACTION_SET_DATE = 2;
    private static final int ACTION_SET_WEATHER = 3;
    private static final int ACTION_REFRESH_TIME_COMPLETE = 4;
    private static final int NUM_ACTIONS = 5;
    private Runnable[] mPendingAsyncUpdates = new Runnable[NUM_ACTIONS];

    private String mTemperatureString = "";	
	private boolean mbTwolineInfo = false;
	private static boolean bHtcResUtilToUpperCase = true;
	
	//init as quick as possible
	private ThemeTemplate mThemeTemplate = ThemeTemplate.Tradition;
	private boolean mIsUpperCaseStyle = false;

	private boolean mIsClickable = true;
	private float mMastheadScaleRatio = 0f;
	
	private boolean mHasInitedIsChinaSense = false;
	private boolean mIsChinaSense = false;
	
	public boolean isChinaSense() {
		if (!mHasInitedIsChinaSense) {
			mIsChinaSense = AccCustomization.isSupportChinaSense();
			mHasInitedIsChinaSense = true;
		}
		return mIsChinaSense;
	}
	
	static enum ThemeTemplate {
    	Tradition, DigitalMask, Center, None;
    	boolean isTradition() {
    		return Tradition.equals(this);
    	}
    	
    	boolean isDigitalMask() {
    		return DigitalMask.equals(this);
    	}
    	boolean isCenter() {
    		return Center.equals(this);
    	}     	
    	boolean isNone() {
    		return None.equals(this);
    	}    	
    	
    	public static ThemeTemplate getThemeTemplate(int templateId) {
    		ThemeTemplate themeTemplate;
        	if (templateId == 1)
        		themeTemplate = ThemeTemplate.Center;
        	else if (templateId == 2)
        		themeTemplate = ThemeTemplate.DigitalMask;
       		else if (templateId == 0)
       			themeTemplate = ThemeTemplate.Tradition;
       		else
       			themeTemplate = ThemeTemplate.None;
        	
        	return themeTemplate;
    	}
    }	
	
    enum DisplayMode {
    	Normal, Simple, Large, Powersaving, Large2Line, None;
    	
    	boolean isNormal() {
    		return Normal.equals(this);
    	}
    	
    	boolean isSimple() {
    		return Simple.equals(this);
    	}
    	boolean isLarge() {
    		return Large.equals(this);
    	}     	
    	
    	boolean isPowersaving() {
    		return Powersaving.equals(this);
    	}
    	
    	boolean isLarge2Line() {
    		return Large2Line.equals(this);
    	}    	
    	
    	boolean isNone() {
    		return None.equals(this);
    	}
    }
    /**
     *  Normal display style of date/location/temperature info. Used in Prism and ReminderView.
     *  @see com.htc.lib1.masthead.view.Masthead#setDisplayMode(int)
     */    
    public static final int DISPLAY_MODE_NORMAL = 0;
    /**
     *  Simple display style of date/location/temperature info. Used in HtcSimpleLauncher.
     *  @see com.htc.lib1.masthead.view.Masthead#setDisplayMode(int)
     */        
    public static final int DISPLAY_MODE_SIMPLE = 1;
    /**
     *  Large display style of date/location/temperature info. Used in AutomotiveMode.
     *  @see com.htc.lib1.masthead.view.Masthead#setDisplayMode(int)
     */        
    public static final int DISPLAY_MODE_LARGE = 2;
    /**
     *  Powersaving display style of date/location/temperature info and weather. Used in PowerSavingLauncher.
     *  @see com.htc.lib1.masthead.view.Masthead#setDisplayMode(int)
     */        
    public static final int DISPLAY_MODE_POWERSAVING = 3;
    /**
     *  Large and 2line display style of date/location/temperature info. Used in AutomotiveMode.
     *  @see com.htc.lib1.masthead.view.Masthead#setDisplayMode(int)
     */        
    public static final int DISPLAY_MODE_LARGE_2LINE = 4;
    
    private DisplayMode mDisplayMode = DisplayMode.None;
    private int mVisibility;

	private boolean mbHaveLayouted = false;
	private boolean mbHaveSkipedLayoutWhenAttached = false;
    private WeatherTimeChangedListener mWeatherTimeListener = new WeatherTimeChangedListener() {
    	
    	@Override
    	public void onTimeChanged(final CharSequence timeString, final boolean immediately) {
    		final int[] digits = new int[4];    		
    		extractTimeDigits(timeString.toString(), digits);
    		scheduleUiUpdateAsync(ACTION_SET_CLOCK, new Runnable() {
    			public void run() {
    				setClockTime(digits, immediately);
    				CharSequence amPm = mAmPmTextView.getText();
    				if (TextUtils.isEmpty(amPm)) {
    					mClockView.setContentDescription(timeString);
    				} else {
    					mClockView.setContentDescription(timeString + amPm.toString());
    				}
    				Logger.d(LOG_TAG, "onTimeChanged %s ", timeString);
    			}
    		});
        }

    	private void extractTimeDigits(final String timeString, final int[] digits) {
    		final int colonIndex = timeString.indexOf(':');
    		// hour
    		String subStr;
    		int number;
    		try {
    			subStr = timeString.substring(0, colonIndex);
    			number = Integer.parseInt(subStr);
    			if (number <= 9) { // single digit
    				digits[0] = DigitalClock.DIGIT_NONE;
    				digits[1] = number;
    			} else {
    				digits[0] = number / 10;
    				digits[1] = number % 10;
    			}
    		} catch (NumberFormatException e) {
    			digits[0] = digits[1] = DigitalClock.DIGIT_NONE;
    			Logger.e(LOG_TAG, e.getMessage());
    		}
    		
    		// min
    		try {
    			subStr = timeString.substring(colonIndex + 1, colonIndex + 3); 
    			number = Integer.parseInt(subStr);
    			if (number <= 9) { // single digit
    				digits[2] = 0;
    				digits[3] = number;
    			} else {
    				digits[2] = number / 10;
    				digits[3] = number % 10;
    			}
    		} catch (NumberFormatException e) {
    			digits[2] = digits[3] = DigitalClock.DIGIT_NONE;
    			Logger.e(LOG_TAG, e.getMessage());
    		}
    	}

    	@Override
        public void onTimeOfDayChanged(final int amPm, final boolean is24H) {
    		scheduleUiUpdateAsync(ACTION_SET_AMPM, new Runnable() {
    			public void run() {
    				Context context = getContext();
    				if (context == null)
    					return;
    				Resources res = context.getResources();
    				if (res == null)
    					return;
    		        if(amPm == Calendar.AM) {
    		        	setTextAndLocale(mAmPmTextView, res.getString(R.string.masthead_st_am) + "  ");
    		        } else if(amPm == Calendar.PM) {
    		        	setTextAndLocale(mAmPmTextView, res.getString(R.string.masthead_st_pm) + "  ");
    		        } else {
    		        	setTextAndLocale(mAmPmTextView, "");
    		        }
    	        	mAmPmTextView.setVisibility(amPm >= 0? VISIBLE:INVISIBLE);
					if(mClockView != null) {
						mClockView.setAmPm(amPm, is24H);
					}
    	        	Logger.d(LOG_TAG, "set AM/PM %d", amPm);
    			}
    		});
        }

    	@Override
        public void onDateChanged(final CharSequence dateString, final CharSequence holidayString) {
    		scheduleUiUpdateAsync(ACTION_SET_DATE, new Runnable() {
    			public void run() {
    				mInfoAreaView.setVisibility(VISIBLE);   				
    				if (mDateTextView != null) {
    					setTextAndLocale(mDateTextView, toUpperCase(getContext(), dateString));
    				}
    				
    				if (supportHolidayView()) {
    					if (holidayString != null && isChinaLocale()) {
    						mHolidayTextView.setVisibility(View.VISIBLE); 
    						String holiday = holidayString.toString();
    					    if (!TextUtils.isEmpty(holiday)) 
    					    	holiday = " ".concat(holiday);
    						setTextAndLocale(mHolidayTextView, toUpperCase(getContext(), holiday));
    					} else {
    						mHolidayTextView.setVisibility(View.GONE);
    					}    					
    				}
    				Logger.d(LOG_TAG, "set date %s, %s", dateString, holidayString);
    			}
    		});
        }

    	@Override
    	public void onWeatherChanged(final WeatherTimeKeeper.WeatherInfo weather) {
    		scheduleUiUpdateAsync(ACTION_SET_WEATHER, new Runnable() {
    			public void run() {
    				setWeatherDisplay(weather);
    				Logger.d(LOG_TAG, "set weather");
    			}
    		});
    	}

    	private void scheduleUiUpdateAsync(final int index, final Runnable action) {
			try {									
				if (getHandler() != null) {
					mPendingAsyncUpdates[index] = null;
				if(action != null && Looper.myLooper() == Looper.getMainLooper()) {
					action.run();
				} else
					post(action);
				} else {
					mPendingAsyncUpdates[index] = action;
					Logger.d(LOG_TAG, "update async when detached. pending action %s, %d", action, index);
				}
			} catch (NullPointerException e) {
				Logger.w(LOG_TAG, "update async err." + e.getMessage());
			}
    	}
    	
    	@Override
    	public void onTimeRefreshed() {
    	}
    };
    
    private Runnable mStartWork = new Runnable() {
    	public void run() {
    		Logger.d(LOG_TAG, "mStartWork");
    		start();
    	}
    };
    
    private Runnable mStopWork = new Runnable() {
    	public void run() {
    		Logger.d(LOG_TAG, "mStopWork");
    		deinitWorker();
    	}
    };    

    private ClickListener mClickListener;
    private boolean mHasWeather = false;
    
    // Constructor comments are copied from android.view.View
    /**
     * Simple constructor to use when creating a Masthead from code.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     */
    public Masthead(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating a Masthead from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    public Masthead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of Mathead allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *        will be applied (beyond what is included in the theme). This may
     *        either be an attribute resource, whose value will be retrieved
     *        from the current theme, or an explicit style resource.
     * @see android.view.View#View(Context, AttributeSet)
     */
    public Masthead(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);        

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MastheadView, defStyle, 0);
        int nDisplayMode = a.getInt(R.styleable.MastheadView_DisplayMode, -1);
        setDisplayMode(nDisplayMode);
        a.recycle();             
       
    }
    
    private void setupDefaultTheme(Context context) {
    	ResourceHelper.releaseThemeResources();  
    	mThemeTemplate = ThemeTemplate.getThemeTemplate(-1);
    }
    
    private int setupTheme(Context context) {
    	ResourceHelper.initThemeResources(context);
    	int templateId = ResourceHelper.getThemeTemplate();
    	mThemeTemplate = ThemeTemplate.getThemeTemplate(templateId);
    	Logger.d(LOG_TAG, "template %d", templateId);
    	int layoutId = 0;    	
    	if(mThemeTemplate.isTradition()) {
    		layoutId = R.layout.specific_theme0_weather_clock_4x1;
    	} else if(mThemeTemplate.isCenter()) {
    		layoutId = R.layout.specific_theme1_weather_clock_4x1;
    		mIsUpperCaseStyle = true;
    	} else if (mThemeTemplate.isDigitalMask()) {
    		layoutId = R.layout.specific_theme2_weather_clock_4x1;    		
    	}
    	
    	return layoutId;
    }
        
    private void setupView(Context context, int layoutId) {
    	if (layoutId <= 0)
    		return;
        ResourceHelper.inflateLayout(context, layoutId, this, true);        

        mInfoAreaView = findViewById(R.id.info_temp_area);
        mDateTextView = (TextView) findViewById(R.id.week_date);
        mHolidayTextView = (TextView) findViewById(R.id.holiday_name);        
        mSplitView = findViewById(R.id.separatrix);
        mCityNameTextView = (TextView) findViewById(R.id.city_name);
        mWeatherTextView = (TextView) findViewById(R.id.info_weather_text);
        mTemperatureLayout = findViewById(R.id.temp_block);
        mTemperatureTextView = (TextView) findViewById(R.id.temparature);
        mTemperatureDegreeSymbolTextView = (TextView) findViewById(R.id.temparature_degree_symbol);
        mTemperatureDegreeTextView = (TextView) findViewById(R.id.temparature_degree_c_f);
        if (mInfoAreaView != null) {
        	mCityCommaTextView = (TextView) mInfoAreaView.findViewById(R.id.comma);
        }
        
        mClockView = (DigitalClock) findViewById(R.id.digital_clock);
        mAmPmTextView = (TextView) findViewById(R.id.digital_am_pm);
        mWeatherView = (WeatherDisplay) findViewById(R.id.sun_block);

        if (mClockView != null) {
	        mClockView.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                if(mClickListener != null) {
	                    mClickListener.onClickClock();
	                }
	            }
	        });
	        
			mClockView.initThemeRes(mThemeTemplate, mDisplayMode);
        }
        
        if (mWeatherView != null) {
	        mWeatherView.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                if(mClickListener != null) {
	                    mClickListener.onClickWeather();
	                }
	            }
	        });
        }

        setClickable(mIsClickable);
        
		boolean isPowersaving = mDisplayMode.isPowersaving();
		boolean isFlipEnabled = getContext().getResources().getBoolean(R.bool.config_flip_animation);
        if (isPowersaving)  {
        	enableWeather(false);
        }
		if (isPowersaving || !isFlipEnabled || mAnimationState == ANIMATE_STATE_DISABLED)  {
			if (mClockView != null)
				mClockView.forceNoAnimation(true);
		}
        // defer the start operations until view is ready
		boolean isNormal = mDisplayMode.isNormal();
        if (isItalicFontStyle()) {        	
        	if (mAmPmTextView != null) {        		
        		mAmPmTextView.setTypeface(mAmPmTextView.getTypeface(), Typeface.ITALIC);    
        	}
        	if(mClockView != null && isNormal) {
				mClockView.setAmPmWidth(getAmPmWidth(mThemeTemplate, true));
        	}
		}else {
			if(mClockView != null && isNormal) {
				mClockView.setAmPmWidth(getAmPmWidth(mThemeTemplate, false));
			}
        }
        
		mVisibility = getVisibility();
		if (mVisibility != View.GONE) {
			Logger.d(LOG_TAG, "defer start");
			post(mStartWork);
		}    	
    }

	private int[] getAmPmWidth(ThemeTemplate themeTemplate, boolean isItalic) {
		if (themeTemplate.isNone()) return null;
		Resources res = getContext().getResources();
		int styleId = themeTemplate.isCenter() ? R.style.fixed_masthead_05 : R.style.custom_fixed_masthead_02;
		return new int[] {
				getTextWidth(res.getString(R.string.masthead_st_am) + "  ", styleId, isItalic),
				getTextWidth(res.getString(R.string.masthead_st_am) + "  ", styleId, isItalic) }; // for theme default
	}

	private int getTextWidth(String text, int style, boolean isItalic) {
		Context context = getContext();
		TextView view = new TextView(context);
		view.setTextAppearance(context, style);
		if (isItalic) {
			view.setTypeface(view.getTypeface(), Typeface.ITALIC);
		}
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);
		view.setText(text);
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(widthMeasureSpec, heightMeasureSpec);
		return view.getMeasuredWidth();
	}

    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {    	
 		int w = ResourceHelper.getSharedDimensionPixelSize(getContext(), R.dimen.libmasthead_masthead_width);
		int h = 0;
		
		if (mDisplayMode.isSimple())
			h = ResourceHelper.getSharedDimensionPixelSize(getContext(),  R.dimen.libmasthead_masthead_height_simple);
		else if (mbTwolineInfo)
			h = ResourceHelper.getSharedDimensionPixelSize(getContext(),  R.dimen.libmasthead_masthead_height_twoline);
		else if (mDisplayMode.isLarge())
			h = ResourceHelper.getSharedDimensionPixelSize(getContext(),  R.dimen.libmasthead_masthead_height_large);
		else
			h = ResourceHelper.getSharedDimensionPixelSize(getContext(),  R.dimen.libmasthead_masthead_height);
		
		w = w > 0? MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY):widthMeasureSpec;
		h = h > 0? MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY):heightMeasureSpec;
		
		super.onMeasure(w, h);
	}    

    /**
     * This method is overridden from android.view.View and is called whenever the state of the screen this view is
     * attached to changes. A state change will usually occurs when the screen
     * turns on or off (whether it happens automatically or the user does it
     * manually.)
     *
     * @param screenState The new state of the screen. Can be either
     *                    {@link #SCREEN_STATE_ON} or {@link #SCREEN_STATE_OFF}
     *@see android.view.View#onScreenStateChanged(int)                   
     */    
    @Override
	public void onScreenStateChanged(int state) {
		super.onScreenStateChanged(state);
	    // play animation when screen on to support smooth transition from lockscreen to homescreen
		Logger.d(LOG_TAG, "onScreenStateChanged %d", state);
		if (state == SCREEN_STATE_ON) {
			resume();
		} else if (state == SCREEN_STATE_OFF){
			pause();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mbHaveLayouted = true;
		if (mbHaveSkipedLayoutWhenAttached) {
			Logger.d(LOG_TAG, "HaveSkipedLayoutWhenAttached");
			doPendingAsyncUpdate();
			post(new Runnable() {
				public void run() {
					Logger.d(LOG_TAG, "post requestLayout");
					requestLayout();
				}
			});
			mbHaveSkipedLayoutWhenAttached = false;
		}		
	}
	
	private boolean doPendingAsyncUpdate() {
		if (!mbHaveLayouted) {
			Logger.d(LOG_TAG, "doPendingAsyncUpdate skip");
			return true;
		}
		for (int i = 0; i < NUM_ACTIONS; i++) {
		    Runnable runnable = mPendingAsyncUpdates[i];
			if (runnable != null) {
			    runnable.run();
				Logger.d(LOG_TAG, "doPending#%d, %s", i, mPendingAsyncUpdates[i]);
				mPendingAsyncUpdates[i] = null;
			}
		}
		return false;
	}
	
    @Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Logger.d(LOG_TAG, "attached to window:%s", this);
		mbHaveSkipedLayoutWhenAttached = doPendingAsyncUpdate();
		resume();
		this.requestLayout();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Logger.d(LOG_TAG, "detached from window:%s", this);
		pause();
	}

    /**
     * This method is overridden from android.view.View and is called when the
     * window containing this view gains or loses focus.
     * Note that this is separate from view focus: to receive key events, both
     * your view and its window must have focus.  If a window is displayed
     * on top of yours that takes input focus, then your own window will lose
     * focus but the view focus will remain unchanged.
     *
     * @param hasWindowFocus True if the window containing this view now has
     *        focus, false otherwise.
     *@see android.view.View#onWindowFocusChanged(boolean)                   
     */ 	
	@Override
	public void onWindowFocusChanged(boolean focused) {
		super.onWindowFocusChanged(focused);
	}

    /**
     * Enables or disables click events for clock and weather area
     *      
     * @param clickable <code>true</code> to make the clock and weather clickable; <code>false</code> otherwise
     */	
	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);
		mIsClickable = clickable;
		
		if (mClockView != null) {
			mClockView.setClickable(clickable);
			mClockView.setFocusable(clickable);
		}
		
		if (mWeatherView != null) {
			mWeatherView.setClickable(clickable);
			mWeatherView.setFocusable(clickable);
		}
	}

	/* 
	 * Set temperature text.
	 * @param tempCurrent current temperature.
	 */	
    void setTemperatureText(CharSequence tempCurrent, CharSequence tempSymbol) {
    	if (tempCurrent != null && mbIsEnableWeather) {
	        String tempCurrentString = tempCurrent.toString();
	        mTemperatureString = toUpperCase(getContext(), tempCurrentString);
	        setTextAndLocale(mTemperatureTextView, mTemperatureString);
	        setTextAndLocale(mTemperatureDegreeTextView, tempSymbol);
	        mTemperatureLayout.setVisibility(View.VISIBLE);
    	} else {
    		mTemperatureLayout.setVisibility(View.GONE);
    	}
    }

    /*
     * Set city name text.
     * @param cityName city name.
     */
    void setCityNameText(CharSequence cityName) {
    	if (cityName == null) {
    		mCityNameTextView.setVisibility(View.GONE);
    		if (mSplitView != null)
    			mSplitView.setVisibility(View.GONE);
    		return;
    	}
    	
		if (mbIsEnableWeather && mSplitView != null)
			mSplitView.setVisibility(View.VISIBLE);    	
        String cityNameString = cityName != null ? cityName.toString(): FALLBACK_TEXT;
        mCityNameTextView.setVisibility(View.VISIBLE);
        setTextAndLocale(mCityNameTextView, toUpperCase(getContext(), cityNameString));
    }

    /*
     * Set weather description text.
     * @param cityName weather description.
     */
    void setWeatherDescriptionText(CharSequence weatherText) {
    	if (mWeatherTextView == null)
    		return;
        if (weatherText != null) {
        	mWeatherTextView.setVisibility(View.VISIBLE);
        	setTextAndLocale(mWeatherTextView, toUpperCase(getContext(), weatherText));
	        if (mCityCommaTextView != null) {
	        	mCityCommaTextView.setVisibility(View.VISIBLE);
	        }
        } else {
        	setTextAndLocale(mWeatherTextView, null);
            mWeatherTextView.setVisibility(View.GONE);
            if (mCityCommaTextView != null) {
            	mCityCommaTextView.setVisibility(View.GONE);
            }        	
        }
    }
    
    /* 
     * Set the clock digits.
     * @param digits array of 4 digits. use <code>-1</code> for no digit.
     * @param immediately true, for no animation case
     */
	void setClockTime(final int[] digits, boolean immediately) {
		if (mClockView == null)
			return;
		mClockView.setVisibility(VISIBLE);
		mClockView.setTime(digits, true, immediately);
	}
	
    void setWeatherText(WeatherTimeKeeper.WeatherInfo weather) {
    	if (mWeatherView == null)
    		return;
    	boolean isWeatherClickable = mWeatherView.isClickable();
    	if (isWeatherClickable && !weather.mIsLocationEnabled) {
    		mWeatherView.setWeatherText(toUpperCase(getContext(), weather.mText2));
       	}
    	else if (isWeatherClickable && !weather.mIsAutoSync) {
    		mWeatherView.setWeatherText(toUpperCase(getContext(), weather.mText3));
    	} else {
    		mWeatherView.setWeatherText(toUpperCase(getContext(), weather.mText));
    	}
    }	
	/* 
     * Set all info but current time.
     * @param weather includes weather, city and temperature info
     */
    void setWeatherDisplay(WeatherTimeKeeper.WeatherInfo weather) {
    	mHasWeather = weather.mId != WeatherTimeKeeper.WeatherInfo.NO_WEATHER;
		// use tag to store weather condition ID
		setTag(weather.mId);

		if (weather.mIcon != null && (mHasWeather || mThemeTemplate.isCenter())) {
	        mWeatherView.setWeatherIcon(weather.mIcon);
        	mWeatherView.setWeatherText((String)null);
    	} else {
	        mWeatherView.setWeatherIcon(null);
	        setWeatherText(weather);
    	}

    	mWeatherView.setContentDescription(weather.mText);
    	mWeatherView.setURL(weather.mWeatherWebLink);
        setCityNameText(weather.mCity);
        setTemperatureText(weather.mTemperature, weather.mTempSymbol);
        
    	if (mbIsEnableWeather) {
    		mInfoAreaView.setVisibility(VISIBLE);
    		mInfoAreaView.requestLayout();
    	}
        
        if (mDisplayMode.isSimple() && mWeatherTextView != null) {
        	setWeatherDescriptionText(weather.mText);
        }
    }
	
	/**
	 * Deprecated from sense 7
	 * 
	 * Set layer type for weather timeline view. (LAYER_TYPE_HARDWARE by default.)
	 * <p>
	 * 	When a timeline uses DST_OUT mode to draw, things drawn before that might be erased.
	 * 	To prevent background from erasing, weather view enables the HW(or SW) layer of its TimelineView to quarantine possible destructive drawing effects.
	 * </p>
	 * <p>
	 * 	Unfortunately this causes side effect in Idlescreen so this method is provided for it to turn off HW layer. (Idlescreen doesn't have background image behind weather anyway.)
	 * 	Also, Prism put the Masthead into a listview and it cause hardware layer trimming problem. (When memory space is not enough and trimLevle > 80, hardware UI will trim the hardware texture if it's detached from window and put into recyclebin)
	 * 	We provide the method avoid the side effects.
	 * </p>
	 * Besides, disabling HW/SW layer save some memory too!  
     * @param layerType The type of layer to use with this view, must be one of
     *        {@link android.view.View#LAYER_TYPE_NONE}, {@link android.view.View#LAYER_TYPE_SOFTWARE} or
     *        {@link android.view.View#LAYER_TYPE_HARDWARE}
     *        Default is <code>LAYER_TYPE_HARDWARE</code>.
     *        
     */
 
    @Deprecated
	public void setWeatherLayerType(int layerType) {
	}

	/** Deprecated from sense 7
	 * 
	 * Enable or disable software layer for text inside masthead.
	 * Call this method right after constructor with <code>true</code> to avoid allocating font texture for HW UI.
	 * @param enable <code>true</code> to enable, <code>false</code> to disable. Default is <code>false</code>.
	 */
    @Deprecated
	public void setEnableTextSWLayer(boolean enable) {
	}
	
	/** Deprecated from sense 7
	 * 
	 * Enable or disable software layer for Clock inside masthead.
	 * Call this method right after constructor with <code>true</code> to allocate software texture.
	 * Some platform may suffer from hardware acceleration when scaling and cause the digital clock visually abnormal.
	 * @param enable <code>true</code> to enable, <code>false</code> to disable. Default is <code>false</code>.
	 */
    @Deprecated
	public void setEnableClockSWLayer(boolean enable) {
	}
	
	
	/**
	 * To apply reminder view setting:
	 * 
	 *  */	
	public void applyReminderViewSetting() {	
		setEnableTextSWLayer(true);  
		changeAnimationState(-1);
		setDisplayMode(DISPLAY_MODE_NORMAL);
		setClickable(false);
	}
		
	/** 	 * 
	 * Set the display style of date and weather text.
	 * Call this method right after constructor with <code>0</code>, <code>1</code>, or <code>2</code> to decide view style.
	 * The funcation supports xml attribute to access.
	 * Ex:
	 * [xmlns:masthead="http://schemas.android.com/apk/res-auto"
	 * masthead:DisplayMode= Value]
	 * Value = "Normal", "Simple", "Large", "Powersaving", or "Large2line".
	 * 
	 * @param nDisplayMode
	 * <br>Prism/ReminderView text style will be used when nDisplayMode = {@link com.htc.lib1.masthead.view.Masthead.DISPLAY_MODE_NORMAL}
	 * <br>EasyMode text style will be used when nDisplayMode = {@link com.htc.lib1.masthead.view.Masthead.DISPLAY_MODE_SIMPLE}
	 * <br>Automotive text style will be used when nDisplayMode = {@link com.htc.lib1.masthead.view.Masthead.DISPLAY_MODE_LARGE}
	 * <br>PowerSavingLauncher text style will be used when nDisplayMode = {@link com.htc.lib1.masthead.view.Masthead.DISPLAY_MODE_POWERSAVING}
	 * <br>Automotive with twoline text style will be used when nDisplayMode = {@link com.htc.lib1.masthead.view.Masthead.DISPLAY_MODE_LARGE_2LINE}
	 */	
	public void setDisplayMode(int nDisplayMode) {
		Logger.d(LOG_TAG, "setDisplayMode %d", nDisplayMode);
		Context context = getContext();
		if (context == null)
			return;
		applyDisplayMode(nDisplayMode);
		
		if (mDisplayMode.isNone())
			return;
		
		mNeedAskDefaultFontStyle = true;
		mbIsEnableWeather = true;
		mbTwolineInfo = false;
		mMastheadScaleRatio = 0f;
		
		int layoutId = 0;
		
    	mIsUpperCaseStyle = false;
    	
		if (mDisplayMode.isNormal())
			layoutId = setupTheme(context);
		else {
			setupDefaultTheme(context);
			if (mDisplayMode.isSimple())
				layoutId = R.layout.specific_theme0_weather_clock_4x1_mode_simple;
			else if (mDisplayMode.isLarge())
				layoutId = R.layout.specific_theme0_weather_clock_4x1_mode_large;
			else if (mDisplayMode.isLarge2Line()) {
				layoutId = R.layout.specific_theme0_weather_clock_4x1_mode_large2line;
				mbTwolineInfo = true;
			} else if (mDisplayMode.isPowersaving()) { 				
				layoutId = R.layout.specific_theme0_weather_clock_4x1;
				mbIsEnableWeather = false;
			}
			
		}
		
		stop();
    	removeAllViewsInLayout();
    	mIsStopped = false;
    	
		setupView(context, layoutId);
	}
	
	private void applyDisplayMode(int nDisplayMode) {	
		if (nDisplayMode == DISPLAY_MODE_NORMAL)
        	mDisplayMode =  DisplayMode.Normal;
		else if (nDisplayMode == DISPLAY_MODE_SIMPLE)
        	mDisplayMode =  DisplayMode.Simple;
        else if (nDisplayMode == DISPLAY_MODE_LARGE)
        	mDisplayMode =  DisplayMode.Large;
        else if (nDisplayMode == DISPLAY_MODE_POWERSAVING)
        	mDisplayMode =  DisplayMode.Powersaving;
        else if (nDisplayMode == DISPLAY_MODE_LARGE_2LINE)
        	mDisplayMode = DisplayMode.Large2Line;
        else
        	mDisplayMode =  DisplayMode.None;
	}

	private int getDisplayModeInt(DisplayMode nDisplayMode) {
		int displayModeInt;
		if (nDisplayMode.isNormal())
			displayModeInt = DISPLAY_MODE_NORMAL;
		else if (nDisplayMode.isSimple())
			displayModeInt = DISPLAY_MODE_SIMPLE;
        else if (nDisplayMode.isLarge())
        	displayModeInt = DISPLAY_MODE_LARGE;
        else if (nDisplayMode.isPowersaving())
        	displayModeInt = DISPLAY_MODE_POWERSAVING;
        else if (nDisplayMode.isLarge2Line())
        	displayModeInt = DISPLAY_MODE_LARGE_2LINE;
        else
        	displayModeInt = -1;
		
		return displayModeInt;
	}	
	
	/**
	 * Change clock animation state for DisplayMode:Normal.
	 * @param state use <br><code>-1</code> to force stop and disable clock animation;
	 *				 	<br><code>-2</code> to enable clock animation;
	 */
	public void changeAnimationState(int state) {
		if (state == ANIMATE_STATE_DISABLED || state == ANIMATE_STATE_ENABLED) {
			mAnimationState = state;
			if(mClockView != null) {
				mClockView.forceNoAnimation(state == ANIMATE_STATE_DISABLED);
			}
		}
	}

	/**
	 * Start running. Register receiver to show info.
	 */
	public void start() {
		if (mIsStopped) {
			Logger.d(LOG_TAG, "mIsStopped return %s", this);
			return;
		}
		Logger.d(LOG_TAG, "start:%s", this);
		initWorker();
	}
	
	/**
	 * Stop running. Unregister receiver.
	 */
	public void stop() {
		Logger.d(LOG_TAG, "stop:%s", this);
		// clear weather animation to avoid leakage
		if (mWeatherView != null) {
			mWeatherView.setAnimation(null);
		}
    	removeCallbacks(mStartWork); // not always work, since mStartWork might not put into message queue yet.
    	mIsStopped = true;
		deinitWorker();
	}
	
	public void refreshTime() {
		if (mInfoKeeper != null) {
			mInfoKeeper.refreshDateTime(true);
		} else {
			Logger.d(LOG_TAG, "mInfoKeeper == null");
		}
	}
	
	private void resume() {
		if (mInfoKeeper != null) {
			mInfoKeeper.resumeDateTime();
			mInfoKeeper.resumeWeather();
			mInfoKeeper.notifyTimeSchedule();
		}
	}

	private void pause() {
		if (mInfoKeeper != null) {
			mInfoKeeper.pauseDateTime();
			mInfoKeeper.pauseWeather();
		}		
	}
	/**
	 * Set callback to handle click on clock/weather view.
	 * @param listener callback to handle click events
	 */
    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }
    
    /**
     * Return the availability of weather data.
     * @return <code>true</code> if weather data is available; <code>false</code> otherwise.
     */
    public boolean hasWeather() {
    	return mHasWeather;
    }

    private WeatherTimeKeeper mInfoKeeper;
    
    private void initWorker() {
		Context context = getContext();
    	// init weather/time keeper
    	if (mInfoKeeper == null) {
    		mInfoKeeper = new WeatherTimeKeeper(context, Masthead.this.mWeatherTimeListener, supportHolidayView());
    		context.registerReceiver(mInfoKeeper, WeatherTimeKeeper.getBroadcastFilter(context), "com.htc.sense.permission.APP_HSP", null);
    	}
    	mInfoKeeper.enableWeather(mbIsEnableWeather);
        if (isScreenOn((PowerManager) context.getSystemService(Context.POWER_SERVICE))) {
            Logger.d(LOG_TAG, "initWorker: screen on");
            resume();
        } else {
            Logger.d(LOG_TAG, "initWorker: screen off");
    		mInfoKeeper.refreshDateTime(false);
    		mInfoKeeper.refreshWeatherData(false);
        }
    }

    private boolean isScreenOn(PowerManager pm) {
        if (pm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) { // api = 20
            if (pm.isInteractive()) {
                return true;
            }
        } else {
            if (pm.isScreenOn()) {
                return true;
            }
        }
        return false;
    }

    private void deinitWorker() {
    	if (mInfoKeeper == null) {
    		return;
    	}
		Context context = getContext();
		context.unregisterReceiver(mInfoKeeper);
		mInfoKeeper.destroy();
		mInfoKeeper = null;
    }

	String toUpperCase(Context context, CharSequence str) {
		if (str == null)
			return null;		
		return toUpperCase(context, str.toString());
	}
	
	String toUpperCase(Context context, String str) {
		if (str == null || !mIsUpperCaseStyle)
			return str;
		
		if (bHtcResUtilToUpperCase) {
			try {
				return com.htc.lib1.cc.util.res.HtcResUtil.toUpperCase(context, str);
			} catch (Exception e) {
				e.printStackTrace();
				bHtcResUtilToUpperCase = false;
			}
		}	
		
		return str.toUpperCase();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (changedView == this && mDisplayMode != null && !mDisplayMode.isNone()) {
			Logger.d(LOG_TAG, "onVisibilityChanged " + mVisibility + visibility);
			if (mVisibility == View.GONE && visibility != View.GONE) {
				post(mStartWork);
			} else if (mVisibility != View.GONE && visibility == View.GONE) {
				post(mStopWork);
			}
			mVisibility = visibility;
		}			
	}

    /**
     * enable/disable weather and location info function for PowerSavingLauncher.
     * @param bEnable <code>false</code> to disable funcation and view of weather and location info;
     * 				 <code>true</code> otherwise.
     */	
	public void enableWeather(boolean bEnable) {
		Logger.d(LOG_TAG, "enableWeather %b", bEnable);
		if (!mDisplayMode.isPowersaving() && !bEnable) {
			setDisplayMode(DISPLAY_MODE_POWERSAVING);
			return;
		}
		mbIsEnableWeather = bEnable;
		int nVisibility = bEnable ? VISIBLE : GONE;
		
		if (mSplitView != null && !mbTwolineInfo)
			mSplitView.setVisibility(nVisibility);
		if (mCityNameTextView != null)
			mCityNameTextView.setVisibility(nVisibility);
		if (mWeatherTextView != null)
			mWeatherTextView.setVisibility(nVisibility);			
		if (mTemperatureLayout != null)
			mTemperatureLayout.setVisibility(nVisibility);		
        
		if (mWeatherView != null)
			mWeatherView.setVisibility(bEnable ? VISIBLE : INVISIBLE);		
		
		if (mInfoKeeper != null) {
			mInfoKeeper.enableWeather(bEnable);
		}		
	}	
	
	/**
     * This api is used if we want to scale the view but also want to remain the same font size.
     * Please don't trigger this frequently because it will reinflate the views and this is costy
     * 
     * @param ratio the ratio to scale Masthead. Because designer arranges the layout in landscape directly. 
     * But RD implements landscape layout via scaling portrait.
     * To match the scaled portrait dimension to landscape dimension on measurement, we need the ratio to enlarge the portrait dimension first.
     * This way can get approximately same layout with landscape measurement. 
     *  
     * @param nDisplayMode to reset and inflate the view according to the display mode
	 * */
	public void setMastheadScale(float ratio, int nDisplayMode) {
		if (ratio == 0 || ratio == mMastheadScaleRatio)
			return;		
		setDisplayMode(nDisplayMode);
		mMastheadScaleRatio = ratio;
		setScaleX(ratio);
		setScaleY(ratio);
		
		float reverseRatio = 1 / ratio;

		mDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mDateTextView.getTextSize() * reverseRatio);
		
		if (mHolidayTextView != null) {
			mHolidayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					mHolidayTextView.getTextSize() * reverseRatio);			
		}
		
		mCityNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mCityNameTextView.getTextSize() * reverseRatio);
		mTemperatureTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mTemperatureTextView.getTextSize() * reverseRatio);
		mTemperatureDegreeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mTemperatureDegreeTextView.getTextSize() * reverseRatio);
		mTemperatureDegreeSymbolTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				mTemperatureDegreeSymbolTextView.getTextSize() * reverseRatio);		
		
		if (mCityCommaTextView != null) {
			mCityCommaTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
			mCityCommaTextView.getTextSize() * reverseRatio);
		}

		
/*        View marginView = findViewById(R.id.background_block);        
        if (marginView != null && marginView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
        	ViewGroup.MarginLayoutParams ml = (ViewGroup.MarginLayoutParams) marginView.getLayoutParams();
        	ml.topMargin *= reverseRatio;
        }         
        
        marginView = mWeatherView;        
        if (marginView != null && marginView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
        	ViewGroup.MarginLayoutParams ml = (ViewGroup.MarginLayoutParams) marginView.getLayoutParams();
        	ml.leftMargin *= reverseRatio;
        } 
        
        marginView = mInfoAreaView;        
        if (marginView != null && marginView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
        	ViewGroup.MarginLayoutParams ml = (ViewGroup.MarginLayoutParams) marginView.getLayoutParams();
        	ml.leftMargin *= reverseRatio;
        	ml.topMargin *= reverseRatio;
        } 		
	*/	
	}
	
	/**
     * This layout styles is used in carmode landscape when it receives broadcast.
     * 
     * @param ratio the ratio to scale Masthead. Because designer arranges the layout in landscape directly. 
     * But RD implements landscape layout via scaling portrait.
     * To match the scaled portrait dimension to landscape dimension on measurement, we need the ratio to enlarge the portrait dimension first.
     * This way can get approximately same layout with landscape measurement. 
     *  
     * 
	 * */
	public void applyCarmodeTwolineLayout(float scaleX) {
		setMastheadScale(scaleX, DISPLAY_MODE_LARGE_2LINE);
	}

    /**
     * Return the weather web link of current city.
     */
    public String getWeatherWebLink() {
    	if (mWeatherView != null)
    		return mWeatherView.getURL();
    	return null;
    }	
    
    public boolean isItalicFontStyle() {
    	return isDefaultFontStyle() 
    			&& (mDisplayMode.isSimple() || mDisplayMode.isLarge() || mDisplayMode.isLarge2Line() || mDisplayMode.isPowersaving()
    			|| (mDisplayMode.isNormal() && (mThemeTemplate.isTradition() || mThemeTemplate.isDigitalMask())) );
    }    
    
    private static final String DEFAULT_FLIPFONT_TYPEFACE_FILENAME = "default";
    private static final String DEFAULT_FLIPFONT_VALUE = Integer.toString(Math.abs(DEFAULT_FLIPFONT_TYPEFACE_FILENAME.hashCode()) + 1);
    private static final String SYSTEM_PROPERTIES_KEY_FONT_HASH ="persist.sys.flipfont_hashcode";
    private boolean mIsDefaultFontStyle = false;
    private boolean mNeedAskDefaultFontStyle = false;
    
    private boolean isDefaultFontStyle() {
    	if (mNeedAskDefaultFontStyle) {
    		String defaultString = DEFAULT_FLIPFONT_VALUE;
    		mIsDefaultFontStyle = DEFAULT_FLIPFONT_VALUE.equals(getSystemProperties(SYSTEM_PROPERTIES_KEY_FONT_HASH, defaultString));
    		mNeedAskDefaultFontStyle = false;
    		Logger.d(LOG_TAG, "isDefaultFontStyle %s, %b", defaultString, mIsDefaultFontStyle);
    	}
    	return mIsDefaultFontStyle;
    }
    
    public static String getSystemProperties(String key, String def) {
        String ret = def;
        try{
          Class<?> SystemProperties = Class.forName("android.os.SystemProperties");

          //Parameters Types
          Class[] paramTypes= new Class[2];
          paramTypes[0]= String.class;
          paramTypes[1]= String.class;          

          Method method_get = SystemProperties.getMethod("get", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0] = new String(key);
          params[1] = new String(def);

          ret = (String) method_get.invoke(SystemProperties, params);

        } catch(Exception e) {
            Logger.w(LOG_TAG, "Fail get SystemProperties: "+e);
            ret = def;
        }
        return ret;
    }    
    
    static void setTextAndLocale (TextView tv, CharSequence text) {
    	
    	if (tv == null || tv.getContext() == null) {
    		Logger.w(LOG_TAG, "Fail setTextAndLocale %s, %s", text, tv);
    		return;
    	}
    	Context context = tv.getContext();
    	Locale oldLocale = tv.getTextLocale();
    	
    	Locale locale = context.getResources().getConfiguration().locale;
    	Logger.d(LOG_TAG, "setTextAndLocale %s, %s", oldLocale, locale);
    	if (locale != null && !locale.equals(oldLocale)) {
    		tv.setTextLocale(locale);
    	}
    	
    	tv.setText(text);
    }
    
    private boolean supportHolidayView() {
    	return mHolidayTextView != null && mDisplayMode.isNormal() && isChinaSense();
    }
    
    private boolean isChinaLocale() {   	
		String systemLanguage = Locale.getDefault().getLanguage();
		if (TextUtils.isEmpty(systemLanguage)) {
			return false;
		}
		
		final String SUPPORT_LANGUAGE_CHINA = Locale.CHINA.getLanguage();
		final String SUPPORT_LANGUAGE_CHINESE = Locale.CHINESE.getLanguage();
		
		if (systemLanguage.equalsIgnoreCase(SUPPORT_LANGUAGE_CHINESE) || systemLanguage.equalsIgnoreCase(SUPPORT_LANGUAGE_CHINA)) {
			return true;
		} else {
			return false;
		}  	
    }
}

