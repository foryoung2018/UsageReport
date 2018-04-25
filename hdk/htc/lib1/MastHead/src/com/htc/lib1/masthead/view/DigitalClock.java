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
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.htc.lib1.cc.view.util.BezierSplineInterpolator;
import com.htc.lib1.masthead.R;
import com.htc.lib1.masthead.view.Masthead.DisplayMode;
import com.htc.lib1.masthead.view.Masthead.ThemeTemplate;

class DigitalClock extends RelativeLayout {	
	private static final String LOG_TAG = DigitalClock.class.getSimpleName();

	public void initThemeRes(ThemeTemplate themeTemplate, DisplayMode displaymode) {
		resetTheme();
		if (themeTemplate.isTradition() || themeTemplate.isNone()) {
			CURRENT_DIGIT_HOUR_RES_IDS = new int[]{
					R.drawable.weather_clock_hour_0_code,
					R.drawable.weather_clock_hour_1_code,
					R.drawable.weather_clock_hour_2_code,
					R.drawable.weather_clock_hour_3_code,
					R.drawable.weather_clock_hour_4_code,
					R.drawable.weather_clock_hour_5_code,
					R.drawable.weather_clock_hour_6_code,
					R.drawable.weather_clock_hour_7_code,
					R.drawable.weather_clock_hour_8_code,
					R.drawable.weather_clock_hour_9_code
			};
			
			CURRENT_DIGIT_MINUTE_RES_IDS = new int[]{
					R.drawable.weather_clock_minute_0_code,
					R.drawable.weather_clock_minute_1_code,
					R.drawable.weather_clock_minute_2_code,
					R.drawable.weather_clock_minute_3_code,
					R.drawable.weather_clock_minute_4_code,
					R.drawable.weather_clock_minute_5_code,
					R.drawable.weather_clock_minute_6_code,
					R.drawable.weather_clock_minute_7_code,
					R.drawable.weather_clock_minute_8_code,
					R.drawable.weather_clock_minute_9_code
			};
			
			CURRENT_DOT_RES_IDS = R.drawable.weather_clock_point_code;
		} else if (themeTemplate.isCenter()) {
			CURRENT_DIGIT_HOUR_RES_IDS = new int[]{
					R.drawable.theme1_weather_clock_hour_0_code,
					R.drawable.theme1_weather_clock_hour_1_code,
					R.drawable.theme1_weather_clock_hour_2_code,
					R.drawable.theme1_weather_clock_hour_3_code,
					R.drawable.theme1_weather_clock_hour_4_code,
					R.drawable.theme1_weather_clock_hour_5_code,
					R.drawable.theme1_weather_clock_hour_6_code,
					R.drawable.theme1_weather_clock_hour_7_code,
					R.drawable.theme1_weather_clock_hour_8_code,
					R.drawable.theme1_weather_clock_hour_9_code
			};
			
			CURRENT_DIGIT_MINUTE_RES_IDS = new int[]{
					R.drawable.theme1_weather_clock_minute_0_code,
					R.drawable.theme1_weather_clock_minute_1_code,
					R.drawable.theme1_weather_clock_minute_2_code,
					R.drawable.theme1_weather_clock_minute_3_code,
					R.drawable.theme1_weather_clock_minute_4_code,
					R.drawable.theme1_weather_clock_minute_5_code,
					R.drawable.theme1_weather_clock_minute_6_code,
					R.drawable.theme1_weather_clock_minute_7_code,
					R.drawable.theme1_weather_clock_minute_8_code,
					R.drawable.theme1_weather_clock_minute_9_code
			};
			
			CURRENT_DOT_RES_IDS = R.drawable.theme1_weather_clock_point_code;
		} else if (themeTemplate.isDigitalMask()) {
			CURRENT_DIGIT_HOUR_RES_IDS = new int[]{
					R.drawable.theme2_weather_clock_hour_0_code,
					R.drawable.theme2_weather_clock_hour_1_code,
					R.drawable.theme2_weather_clock_hour_2_code,
					R.drawable.theme2_weather_clock_hour_3_code,
					R.drawable.theme2_weather_clock_hour_4_code,
					R.drawable.theme2_weather_clock_hour_5_code,
					R.drawable.theme2_weather_clock_hour_6_code,
					R.drawable.theme2_weather_clock_hour_7_code,
					R.drawable.theme2_weather_clock_hour_8_code,
					R.drawable.theme2_weather_clock_hour_9_code
			};
			
			CURRENT_DIGIT_MINUTE_RES_IDS = new int[]{
					R.drawable.theme2_weather_clock_minute_0_code,
					R.drawable.theme2_weather_clock_minute_1_code,
					R.drawable.theme2_weather_clock_minute_2_code,
					R.drawable.theme2_weather_clock_minute_3_code,
					R.drawable.theme2_weather_clock_minute_4_code,
					R.drawable.theme2_weather_clock_minute_5_code,
					R.drawable.theme2_weather_clock_minute_6_code,
					R.drawable.theme2_weather_clock_minute_7_code,
					R.drawable.theme2_weather_clock_minute_8_code,
					R.drawable.theme2_weather_clock_minute_9_code
			};
			
			CURRENT_DOT_RES_IDS = R.drawable.theme2_weather_clock_point_code;
			CURRENT_HOUR_MASK_RES_IDS = R.drawable.theme2_weather_clock_hour_mask_code;
			CURRENT_MINUTE_MASK_RES_IDS = R.drawable.theme2_weather_clock_minute_mask_code;			
		}		
		mThemeTemplate = themeTemplate;
		mDisplayMode = displaymode;
		init();
	}
	private static int[] CURRENT_DIGIT_HOUR_RES_IDS;
	private static int[] CURRENT_DIGIT_MINUTE_RES_IDS;
	private static int CURRENT_DOT_RES_IDS;
	private static int CURRENT_HOUR_MASK_RES_IDS;
	private static int CURRENT_MINUTE_MASK_RES_IDS;
	
	
	/* package */ static final int DIGIT_NONE = -1;

	private boolean mNoAnimation = false; // skip animation if set
	
	private boolean m1stTime = true; // to prevent flipping when 1st shown on screen
	private boolean m3digits = false; // whether the clock has 3 digits only

	private int[] mAnimateDigits = new int[4]; // saved new digits when animating
	
	private static int DIGIT_INDEX_HOUR_TENS = 0;
	private static int DIGIT_INDEX_HOUR_UNITS = 1;
	private static int DIGIT_INDEX_MINUTE_TENS = 2;
	private static int DIGIT_INDEX_MINUTE_UNITS = 3;
	
	private static int DIGIT_INDEX_HOUR_TENS_LOWER = 4;
	
	
	// Arrangement of view is like this:
	// [upper#1]...[upper#4]
	// [lower#1]...[lower#4]
	private ImageView[] mDigitHalfImgs = new ImageView[8]; // upper#1~4, lower#1~4
	private ImageView[] mFlipHalfImgs = new ImageView[8]; // upper flip#1~4, lower flip#1~4
	
	// flip animations
	private AnimatorSet mLastPlayedDigitAnims = new AnimatorSet();
	private Animator[] mDigitAnims;
	private int mAmPm;
	private boolean mIs24H;
	private int[] mDigits = new int[4]; // 00:00
	private ThemeTemplate mThemeTemplate = ThemeTemplate.None;
	private DisplayMode mDisplayMode = DisplayMode.None;
	
	private ImageView[] mDigitImgs = new ImageView[4]; // hour#0~1, minute#2~3
	private ImageView mDotImg; 
	private boolean mHaveSetColon = false;
	
	private View mDigitalClockBlock; // mRoot
	private RotateSlideView mSlide;
    /** The {@link TouchFeedbackHelper} for touch feedback animations */
    private TouchFeedbackHelper mTouchFeedbackHelper;
    
    public DigitalClock(Context context) {
        this(context, null);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitalClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchFeedbackHelper = new TouchFeedbackHelper(0.96f);
        setFocusable(true);
    }

    @Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	private void init() {

		if (isNormalAnimation()) {
			mDigitHalfImgs[0] = (ImageView)findViewById(R.id.digital_hour_tens_upper);
			mDigitHalfImgs[1] = (ImageView)findViewById(R.id.digital_hour_units_upper);
	 		mDigitHalfImgs[2] = (ImageView)findViewById(R.id.digital_minute_tens_upper);
			mDigitHalfImgs[3] = (ImageView)findViewById(R.id.digital_minute_units_upper);
			mDigitHalfImgs[4] = (ImageView)findViewById(R.id.digital_hour_tens_lower);
			mDigitHalfImgs[5] = (ImageView)findViewById(R.id.digital_hour_units_lower);
			mDigitHalfImgs[6] = (ImageView)findViewById(R.id.digital_minute_tens_lower);
			mDigitHalfImgs[7] = (ImageView)findViewById(R.id.digital_minute_units_lower);
	    	
			mFlipHalfImgs[0] = (ImageView)findViewById(R.id.digital_hour_tens_upper_flip);
			mFlipHalfImgs[1] = (ImageView)findViewById(R.id.digital_hour_units_upper_flip);
			mFlipHalfImgs[2] = (ImageView)findViewById(R.id.digital_minute_tens_upper_flip);
			mFlipHalfImgs[3] = (ImageView)findViewById(R.id.digital_minute_units_upper_flip);
			mFlipHalfImgs[4] = (ImageView)findViewById(R.id.digital_hour_tens_lower_flip);
			mFlipHalfImgs[5] = (ImageView)findViewById(R.id.digital_hour_units_lower_flip);
			mFlipHalfImgs[6] = (ImageView)findViewById(R.id.digital_minute_tens_lower_flip);
			mFlipHalfImgs[7] = (ImageView)findViewById(R.id.digital_minute_units_lower_flip);
		} else {
			mDigitImgs[0] = (ImageView)findViewById(R.id.digital_hour_tens);
			mDigitImgs[1] = (ImageView)findViewById(R.id.digital_hour_units);
			mDigitImgs[2] = (ImageView)findViewById(R.id.digital_minute_tens);
			mDigitImgs[3] = (ImageView)findViewById(R.id.digital_minute_units);
		}
		mDotImg = (ImageView)findViewById(R.id.digital_dot);
    	
		mDigitalClockBlock = findViewById(R.id.digital_clock);
		if(isNormalAnimation()) { // only for theme default/center/slash
			mSlide = new RotateSlideView(mDigitalClockBlock, mThemeTemplate.isCenter());
			int width = 0;
			if(mDigitHalfImgs[0] != null) {
				width = mDigitHalfImgs[0].getLayoutParams().width;
			}
			mSlide.setTabWidth(width);
			View view = (View)getParent();
			if(view != null) {
				mSlide.setAlignView(view.findViewById(R.id.digital_am_pm),
				view.findViewById(R.id.sun_block), view.findViewById(R.id.info_temp_area));
			}
		}
		int[] none = { DIGIT_NONE, DIGIT_NONE, DIGIT_NONE, DIGIT_NONE };
		setTime(none, true, false);
		m1stTime = true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (isClickable()) {
            mTouchFeedbackHelper.onTouchEvent(this, event);
    	}
        return super.onTouchEvent(event);
    }

	public void setAmPm(int amPm, boolean is24H) {
		mAmPm = amPm;
		mIs24H = is24H;
	}

	public void setAmPmWidth(int[] amPmWidth) {
		if (mSlide != null) {
			mSlide.setAmPmWidth(amPmWidth);            
		}
	}
    
    public void setTime(int[] digits, boolean animate, boolean immediately) {
    	if (digits.length < mDigits.length) {
    		throw new IllegalArgumentException("Too few digits");
    	}

    	if (mNoAnimation || !isNormalAnimation()) {
    		animate = false;
    	}

        if (animate) { // lock screen unlock case
            boolean isShown = isShown();
            Logger.d("MastheadClock", "setTime: isShown = " + isShown + ", immediately = " + immediately);
            if (!isShown || immediately) {
                animate = false;
                mLastPlayedDigitAnims.end();
            }
        }
    	
    	// left align when 3 digits
    	//setTranslationX(digits[0] == DIGIT_NONE? -sDigitWidth:0);
		m3digits = (digits[0] == DIGIT_NONE);
		
		Logger.d("MastheadClock", "setTime: "+ digits[0] + ", " + digits[1] + ", " + digits[2] + ", " + digits[3] );
		if (m1stTime) {
			setDigits(digits, 0, 3);
			m1stTime = false;
		} else if (animate) {
			playDigitAnimations(digits);
		} else {
			setDigits(digits, 0, 3);
		}
		if (digits[0] != DIGIT_NONE || digits[1] != DIGIT_NONE) { // check time ready to init
			setDigitColonAsset();
		}
	}

	private void setDigitColonAsset() {
		if (mHaveSetColon)
			return;
		
    	int templateId = ResourceHelper.getThemeTemplate();
    	ThemeTemplate themeTemplate = ThemeTemplate.getThemeTemplate(templateId);
    	
    	Drawable colon = null;
    	if (!(themeTemplate.isDigitalMask() || themeTemplate.isNone())) {
    		colon = ResourceHelper.getThemeDigitColonIcon();
    	}

		if (colon != null)
			mDotImg.setImageDrawable(colon);
		else
    		mDotImg.setImageResource(CURRENT_DOT_RES_IDS);
    	mHaveSetColon = true;
	}
	
	private void setDigitVisibility(ImageView iv, int visibility) {
		if (iv != null)
			iv.setVisibility(visibility);
	}
	
	private void setDigitAlpha(ImageView iv, float alpha) {
		if (iv != null)
			iv.setAlpha(alpha);
	}
	
	private void setDigitsAsset(ImageView iv, int index, int[] digits) {
		if (iv == null)
			return;
		Drawable d = null;
		int num = digits[index % DIGIT_INDEX_HOUR_TENS_LOWER];
		int[] resIds;
		if (isDigitHour(index)) {			
			d = ResourceHelper.getThemeDigitHourIcon(num);
			resIds = CURRENT_DIGIT_HOUR_RES_IDS;
		} else {
			d = ResourceHelper.getThemeDigitMinuteIcon(num);
			resIds = CURRENT_DIGIT_MINUTE_RES_IDS;
		}
		
		if (d == null)
			d = iv.getContext().getResources().getDrawable(resIds[num]);
		
		if (d == null) {
			Logger.w(LOG_TAG, "null d %d, %d", num, index);
			return;
		}
		
		iv.setImageDrawable(d);
		
		if (isNormalAnimation()) {
			iv.setScaleType(ScaleType.MATRIX);
			Matrix matrix = iv.getImageMatrix();
			matrix.reset();
			
			if (mRectHalfImageView == null || mRectAsset == null) {
				mRectHalfImageView = new RectF();
				mRectAsset = new RectF();
			}
			
			int width = iv.getWidth();
			int height = iv.getHeight();
			if(width == 0 || height == 0) { // layout no ready
				ViewGroup.LayoutParams lp =  iv.getLayoutParams();
				width = lp.width;
				height = lp.height;
			}
			mRectHalfImageView.set(0, 0, width, height* 2);
			mRectAsset.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.setRectToRect(mRectAsset, mRectHalfImageView, Matrix.ScaleToFit.CENTER);
			if (isDigitLower(index)) {
				matrix.postTranslate(0, -height);
			}
			iv.setImageMatrix(matrix);
		}
	}	

	private RectF mRectHalfImageView = null;
	private RectF mRectAsset = null;
	
	private boolean isDigitHour(int index) {
		index = index % DIGIT_INDEX_HOUR_TENS_LOWER;
		return index == DIGIT_INDEX_HOUR_TENS || index == DIGIT_INDEX_HOUR_UNITS;
	}
	
	private boolean isDigitLower(int index) {
		return index >= DIGIT_INDEX_HOUR_TENS_LOWER;
	}
	
	private void setDigits(int[] digits, int index) {
		setDigits(digits, index, index);
	}
	
	private void setDigits(int[] digits, int start, int end) {	
    	for (int i = start; i <= end; i++) {			
			if (isNormalAnimation() && i == 0) {
                if (digits[i] == DIGIT_NONE) {
                    if (mSlide != null) {
                        mSlide.setValue(0);
                    }
                } else {
                    if (mSlide != null) {
                        mSlide.setValue(1);
                    }
                }
			}  
			
    		if (mDigits[i] == digits[i])
    			continue;
			ImageView upper = mDigitHalfImgs[i];
			ImageView lower = mDigitHalfImgs[i + 4];

			ImageView digit = mDigitImgs[i];
			
    		if (digits[i] == DIGIT_NONE) {    			
    			if (isNormalAnimation()) {
	    			setDigitVisibility(upper, INVISIBLE);
	    			setDigitVisibility(lower, INVISIBLE);
    			} else {
    				setDigitVisibility(digit, GONE);
    			}
    			
    		} else if (digits[i] > 9) {
    			throw new IllegalArgumentException("Digit value at index: " + i + " is out of bound. Should be 0-9, is " + digits[i]);
    		} else {
    			if (isNormalAnimation()) {
	    			setDigitVisibility(upper, VISIBLE);
	    			setDigitsAsset(upper, i, digits);
	    			setDigitAlpha(upper, 1f);
	    			
	    			setDigitVisibility(lower, VISIBLE);
	    			setDigitsAsset(lower, i + 4, digits);
	    			setDigitAlpha(lower, 1f);	    			
    			} else {
	    			setDigitVisibility(digit, VISIBLE);
	    			setDigitsAsset(digit, i, digits);
	    			setDigitAlpha(digit, 1f);
    			} 
			}
    		mDigits[i] = digits[i];
    	}				
	}

	private void playDigitAnimations(int[] digits) {
		ensureDigitAnim();
    	int numOfAnimatorToPlay = 0;
		ArrayList<Animator> animators = new ArrayList<Animator>();
		boolean lastAnimationIsRunning = false;
		int[] lastDigits;
		if (mLastPlayedDigitAnims != null && mLastPlayedDigitAnims.isRunning()) {
			lastAnimationIsRunning = true;
			lastDigits = mAnimateDigits; 		
		} else {
			lastDigits = mDigits;
		}
		
		for(int i = 3; i >= 0; --i) {
			if (lastDigits[i] == digits[i])
				continue;
			prepareDigit(i, digits);
			mDigitAnims[i].setStartDelay(RotateAnimConsts.ROTATE_OFFSET * numOfAnimatorToPlay);
			animators.add(mDigitAnims[i]);
			numOfAnimatorToPlay++;
	 	}
    	
		if (lastDigits[0] != digits[0]) {
			if (lastDigits[0] == DIGIT_NONE && digits[0] != DIGIT_NONE) { // 3 -> 4 digit(lastDigits:old)
				AnimatorSet anim3To4 = mSlide.applySlide3To4(mIs24H, mAmPm);
				if (anim3To4 != null) {
					animators.add(anim3To4);
				}
			} else if (lastDigits[0] != DIGIT_NONE && digits[0] == DIGIT_NONE) { // 4 -> 3 digit(digits:new)
				AnimatorSet anim4To3 = mSlide.applySlide4To3(mIs24H, mAmPm);
				if (anim4To3 != null) {
					animators.add(anim4To3);
				}
			} else if (lastDigits[0] != DIGIT_NONE && digits[0] != DIGIT_NONE) { // 4 -> 4 & am <-> pm case
				AnimatorSet anim4To4 = mSlide.applySlide4To4(mIs24H, mAmPm);
				if (anim4To4 != null) {
					animators.add(anim4To4);
				}
			} else if (lastDigits[0] == DIGIT_NONE && digits[0] == DIGIT_NONE) { // 3 -> 3 & am <-> pm case
				AnimatorSet anim3To3 = mSlide.applySlide3To3(mIs24H, mAmPm);
				if (anim3To3 != null) {
					animators.add(anim3To3);
				}
			}
		}

		if (animators.isEmpty())
			return;
    	
		if (lastAnimationIsRunning)
			mLastPlayedDigitAnims.cancel();
    	
		System.arraycopy(digits, 0, mAnimateDigits, 0, mAnimateDigits.length);
		AnimatorSet playingAnimators = new AnimatorSet();
		playingAnimators.playTogether(animators);
		playingAnimators.start();

		mLastPlayedDigitAnims = playingAnimators;
	}
    
	private void prepareDigit(int index, int[] digits) {
		final int indexLower = index + 4;
    	
		mDigitHalfImgs[index].setPivotY(mDigitHalfImgs[index].getHeight());
		mDigitHalfImgs[indexLower].setPivotY(0); // down
		
		mFlipHalfImgs[index].setPivotY(mFlipHalfImgs[index].getHeight());
		mFlipHalfImgs[indexLower].setPivotY(0); // down
		
		mFlipHalfImgs[index].setVisibility(View.INVISIBLE);
		mFlipHalfImgs[indexLower].setVisibility(View.INVISIBLE); // down

		if (digits[index] != DIGIT_NONE) {
		setDigitsAsset(mFlipHalfImgs[index], index, digits);
		setDigitsAsset(mFlipHalfImgs[indexLower], indexLower, digits);			
		}
	}

    private void ensureDigitAnim() {
		if (mDigitAnims == null) {
			mDigitAnims = new Animator[4];
			for(int i = 0; i < 4; ++i)
				mDigitAnims[i] = getAnim(i);
		}    	
	}

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	super.onLayout(changed, l, t, r, b);
    	if (mDigitHalfImgs[0] != null) {
			int shiftAmount = mDigitHalfImgs[0].getWidth();
			if (mSlide != null) {
				mSlide.setTabWidth(shiftAmount);
			}
		}
	}
    
	private AnimatorSet getAnim(final int index) {
		final int indexLower = index + 4;

		AnimatorSet upperOut = new AnimatorSet();
		AnimatorSet lowerOut = new AnimatorSet();
		AnimatorSet upperIn = new AnimatorSet();
		AnimatorSet lowerIn = new AnimatorSet();

		buildDigitFlipAnimationUpperOut(index, upperOut); // up front [6]
		buildDigitFlipAnimationLowerOut(indexLower, lowerOut); // down front [6]
		buildDigitFlipAnimationUpperIn(index, upperIn); // up rear [7]
		buildDigitFlipAnimationLowerIn(indexLower, lowerIn); // down rear [7]
		
		upperIn.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				mFlipHalfImgs[index].setAlpha(0f);
				mFlipHalfImgs[index].setVisibility(mAnimateDigits[index] != DIGIT_NONE ? View.VISIBLE: View.INVISIBLE);
			}
		});
		
		lowerIn.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				mFlipHalfImgs[indexLower].setAlpha(0f);
				mFlipHalfImgs[indexLower].setVisibility(mAnimateDigits[index] != DIGIT_NONE ? View.VISIBLE: View.INVISIBLE);
			}
		});
		// full 
		AnimatorSet hour = new AnimatorSet();
		hour.setInterpolator(getCubicBezierInterPolator());
		hour.playTogether(upperOut, lowerOut, upperIn, lowerIn);
		
		hour.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				onEndFlip(index); 
				// hide lowers flips
				mFlipHalfImgs[index].setVisibility(View.INVISIBLE);
				mFlipHalfImgs[indexLower].setVisibility(View.INVISIBLE);
			}
		});
		
		return hour;
	}

	private Interpolator getCubicBezierInterPolator() {
		return getCubicBezierInterPolator(RotateAnimConsts.ROTATE_BEZIER, 0f, 1 - RotateAnimConsts.ROTATE_BEZIER, 1f);
	}
	
	private Interpolator getCubicBezierInterPolator(float controlX1, float controlY1, float controlX2, float controlY2) {
		BezierSplineInterpolator interpolator = new BezierSplineInterpolator(controlX1, controlY1, controlX2, controlY2);
		return interpolator;
	}
	
	private void buildDigitFlipAnimationUpperOut(final int index, AnimatorSet anim) { // up front [6]
		ObjectAnimator upperFlip = ObjectAnimator.ofFloat(mDigitHalfImgs[index], "rotationX", 0, -180f);

		upperFlip.setDuration(RotateAnimConsts.UP_FRONT_ROTATION_DURATION);
		ObjectAnimator upperFade = ObjectAnimator.ofFloat(mDigitHalfImgs[index], "alpha", 1f, 0.0f);
		upperFade.setDuration(RotateAnimConsts.UP_FRONT_OPACITY_DURATION);
		upperFade.setStartDelay(RotateAnimConsts.UP_FRONT_OPACITY_DELAY);

		anim.playTogether(upperFlip, upperFade);
	}

	private void buildDigitFlipAnimationLowerOut(final int indexLower, AnimatorSet anim) { // down front [6]
		ObjectAnimator lowerFlip = ObjectAnimator.ofFloat(mDigitHalfImgs[indexLower], "rotationX", 0, -180f);
		lowerFlip.setDuration(RotateAnimConsts.DOWN_FRONT_ROTATION_DURATION);

		ObjectAnimator lowerFade = ObjectAnimator.ofFloat(mDigitHalfImgs[indexLower], "alpha", 1f, 0.0f);
		lowerFade.setDuration(RotateAnimConsts.DOWN_FRONT_OPACITY_DURATION);
		lowerFade.setStartDelay(RotateAnimConsts.DOWN_FRONT_OPACITY_DELAY);

		anim.playTogether(lowerFlip, lowerFade);
	}

	private void buildDigitFlipAnimationUpperIn(final int index, AnimatorSet anim) { // up rear [7]
		ObjectAnimator upperFlip = ObjectAnimator.ofFloat(mFlipHalfImgs[index], "rotationX", 180f, 0);
		upperFlip.setDuration(RotateAnimConsts.UP_REAR_ROTATION_DURATION);

		ObjectAnimator upperFade = ObjectAnimator.ofFloat(mFlipHalfImgs[index], "alpha", 0.0f, 1f);
		upperFade.setDuration(RotateAnimConsts.UP_REAR_OPACITY_DURATION);
		upperFade.setStartDelay(RotateAnimConsts.UP_REAR_OPACITY_DELAY);

		anim.playTogether(upperFlip, upperFade);
	}

	private void buildDigitFlipAnimationLowerIn(final int indexLower, AnimatorSet anim) { // down rear [7]
		ObjectAnimator lowerFlip = ObjectAnimator.ofFloat(mFlipHalfImgs[indexLower], "rotationX", 180f, 0);
		lowerFlip.setDuration(RotateAnimConsts.DOWN_REAR_ROTATION_DURATION);

		ObjectAnimator lowerFade = ObjectAnimator.ofFloat(mFlipHalfImgs[indexLower], "alpha", 0.0f, 1f);
		lowerFade.setDuration(RotateAnimConsts.DOWN_REAR_OPACITY_DURATION);
		lowerFade.setStartDelay(RotateAnimConsts.DOWN_REAR_OPACITY_DELAY);

		anim.playTogether(lowerFlip, lowerFade);
	}

	private void onEndFlip(int index) {
		Logger.d("MastheadClock", "onEndFlip " + mAnimateDigits[index]);
		setDigits(mAnimateDigits, index);
		mDigitHalfImgs[index].setAlpha(1f);
		mDigitHalfImgs[index].setRotationX(0);
		mDigitHalfImgs[index + 4].setAlpha(1f);
		mDigitHalfImgs[index + 4].setRotationX(0);
	}

	public void forceNoAnimation(boolean noAnimation) {
		mNoAnimation = !isNormalAnimation() || noAnimation;
		if (noAnimation) {
			mLastPlayedDigitAnims.end();
		}
	}

    private Drawable mFocusIndicator;
    private boolean mDrawFocusIndicator;

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        if (focusable && mFocusIndicator == null) {
            mFocusIndicator = getContext().getResources().getDrawable(R.drawable.common_focused);

            if (mFocusIndicator != null) {
                mFocusIndicator.mutate();
                mFocusIndicator.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.overlay_color), PorterDuff.Mode.SRC_ATOP));
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mDrawFocusIndicator = gainFocus;
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mDrawFocusIndicator) {
            Rect bound = canvas.getClipBounds();
            View parent = (View)getParent();
            if ( parent != null) {
                int nParentRight = parent.getWidth();
                int nParentHeight = parent.getHeight();
                int nX = Math.round(getX());
                int nY = Math.round(getY());

                // left boundary is smaller than parent
                if (nX < 0) {
                    bound.left -= nX;
                }

                // top boundary is smaller than parent
                if (nY < 0) {
                    bound.top -= nY;
                }

                // check right bound of parent
                if (nX + getWidth() > nParentRight) {
                    bound.right -= (nX + getWidth() - nParentRight);
                }

                // check bottom bound of parent
                if (nY + getHeight() > nParentHeight) {
                    bound.bottom -= (nY + getHeight() - nParentHeight);
                }
            }

            mFocusIndicator.setBounds(bound);
            mFocusIndicator.draw(canvas);
        }
    }
    
    //for three digits layout
	int getFirstDigitWidth () {
		if (isNormalAnimation()) {
			int upperWidth = mDigitHalfImgs[0] == null ? 0 : mDigitHalfImgs[0].getMeasuredWidth();
			int lowerWidth = mDigitHalfImgs[4] == null ? 0 : mDigitHalfImgs[4].getMeasuredWidth();
			return Math.max(upperWidth, lowerWidth);
		} else {
			int firstWidth = mDigitImgs[0] == null ? 0 : mDigitImgs[0].getMeasuredWidth();
			return firstWidth;
		}
	}   
	
	public void resetTheme() {
		mHaveSetColon = false;
		mDigits[0] = -2; // hide tens case
		mDigits[1] = DIGIT_NONE;
		mDigits[2] = DIGIT_NONE;
		mDigits[3] = DIGIT_NONE;
		mThemeTemplate = ThemeTemplate.None;
	}
	
	private boolean isNormalAnimation() {
		return mDisplayMode.isNormal() || mDisplayMode.isPowersaving();
	}
}
