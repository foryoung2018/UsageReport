package com.htc.lib1.masthead.view;

import com.htc.lib1.cc.view.util.BezierSplineInterpolator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Rotate Slide View.
 * @hide
 */
public class RotateSlideView {
	private static final String LOG_TAG = RotateSlideView.class.getSimpleName();

	private View mRoot;
	private View mParent;

	private View mAmPm;
	private View mInfoTemp;
	private View mInfoLoc;
	private int[] mAmPmWidth = new int[] { 0, 0 }; // prevent null data
	private AnimatorSet mAnimSlide3To4 = new AnimatorSet(); // 3 -> 4 digit
	private AnimatorSet mAnimSlide4To3 = new AnimatorSet(); // 4 -> 3 digit
	private AnimatorSet mAnimSlide4To4 = new AnimatorSet(); // 4 -> 4 digit
	private AnimatorSet mAnimSlide3To3 = new AnimatorSet(); // 3 -> 3 digit

	private int mTranslationX;
	private int mTabWidthOffset;
	private int mTabWidth;
	private boolean mCenterDate;
	private int mParentMarginLeft;
	private int mParentMarginRight;

	RotateSlideView(View root, boolean centerDate) {
		mRoot = root;
		mParent = (View) root.getParent();
		mCenterDate = centerDate;
		setAnimation();
	}

	public void setTabWidth(int tabWidth) {
		mTabWidth = (int) tabWidth;
		mTranslationX = (int) tabWidth * -1;
		mTabWidthOffset = mCenterDate ? (int) tabWidth : (int) mTranslationX;
	}

	public void setAmPmWidth(int[] amPmWidth) {
		if (amPmWidth != null && amPmWidth.length > 1) {
			mAmPmWidth = amPmWidth;
		}
	}

	public void setAlignView(View ampm, View infoLoc, View infoTemp) {
		mAmPm = ampm;
		mInfoLoc = infoLoc;
		mInfoTemp = infoTemp;
	}

	public void setValue(int digit) {
		Logger.d(LOG_TAG, "setValue, digit= " + digit);
		if (digit == 0) {
			mRoot.setTranslationX(mTranslationX);
			setLayoutOffset(mTabWidthOffset);
		} else {
			mRoot.setTranslationX(0);
			setLayoutOffset(0);
		}
	}

	public AnimatorSet applySlide3To4(boolean is24HourFormat, int ampm) {
		if (mAmPm == null || mInfoTemp == null) return null;
		if (!mCenterDate) {
			if (mInfoLoc == null) return null;
			int estWidth = getEstWidth(is24HourFormat, ampm, mTabWidth);
			int widgetWidth = ((View) mParent.getParent()).getWidth();
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int extendSize = (destWidth - mParent.getWidth());
			int moveSize = (extendSize < 0) ? 0 : (extendSize / 2);
			mAnimSlide3To4.playTogether(getAnimatorInfo(0 - moveSize), getAnimatorDigit(0, mTabWidth - moveSize));
			return mAnimSlide3To4;
		} else {
			int rootLeftPos = getLeftPos(mRoot) + mTabWidth;
			int ampmLeftPos = getLeftPos(mAmPm);
			int estWidth = ampmLeftPos + (!is24HourFormat ? mAmPmWidth[ampm] : 0) - rootLeftPos; // 4 digit
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
			int widgetWidth = ((View) mParent.getParent()).getWidth() - lp.leftMargin - lp.rightMargin;
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int srcWidth = ampmLeftPos + (!is24HourFormat ? mAmPm.getWidth() : 0) - rootLeftPos + mTabWidth; // it has empty space
			int extendSize = (destWidth - srcWidth);
			int moveSize = (extendSize / 2);
			// the same size with padding, move size = 0
			mAnimSlide3To4.play(getAnimatorDigit(mTranslationX, mTranslationX - moveSize));
			return mAnimSlide3To4;
		}
	}

	public AnimatorSet applySlide4To3(boolean is24HourFormat, int ampm) {
		if (mAmPm == null || mInfoTemp == null) return null;
		if (!mCenterDate) {
			if (mInfoLoc == null) return null;
			int estWidth = getEstWidth(is24HourFormat, ampm, mTabWidth * -1);
			int widgetWidth = ((View) mParent.getParent()).getWidth();
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int shrinkSize = (mParent.getWidth() - destWidth);
			int moveSize = (shrinkSize < 0) ? 0 : (shrinkSize / 2);
			mAnimSlide4To3.playTogether(getAnimatorInfo(moveSize), getAnimatorDigit(0, mTranslationX + moveSize));
			return mAnimSlide4To3;
		} else {
			int rootLeftPos = getLeftPos(mRoot);
			int ampmLeftPos = getLeftPos(mAmPm);
			int estWidth = ampmLeftPos + (!is24HourFormat ? mAmPmWidth[ampm] : 0) - rootLeftPos - mTabWidth; // 3 digit
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
			int widgetWidth = ((View) mParent.getParent()).getWidth() - lp.leftMargin - lp.rightMargin;
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int srcWidth = ampmLeftPos + (!is24HourFormat ? mAmPm.getWidth() : 0) - rootLeftPos;
			int shrinkSize = srcWidth - destWidth;
			int moveSize = (shrinkSize / 2);
			mAnimSlide4To3.play(getAnimatorDigit(0, mTranslationX + moveSize));
			return mAnimSlide4To3;
		}
	}

	public AnimatorSet applySlide4To4(boolean is24HourFormat, int ampm) {
		if (mAmPm == null || mInfoTemp == null) return null;
		if (!mCenterDate) {
			if (mInfoLoc == null) return null;
			int estWidth = getEstWidth(is24HourFormat, ampm, 0);
			int widgetWidth = ((View) mParent.getParent()).getWidth();
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int srcWidth = mParent.getWidth();
			if ((srcWidth != destWidth)) { // 4-4
				int extendSize = (destWidth - mParent.getWidth());
				int moveSize = extendSize / 2;
				mAnimSlide4To4.playTogether(getAnimatorInfo(0 - moveSize), getAnimatorDigit(0, 0 - moveSize));
				return mAnimSlide4To4;
			}
		} else {
			int rootLeftPos = getLeftPos(mRoot);
			int ampmLeftPos = getLeftPos(mAmPm);
			int estWidth = ampmLeftPos + (!is24HourFormat ? mAmPmWidth[ampm] : 0) - rootLeftPos;
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
			int widgetWidth = ((View) mParent.getParent()).getWidth() - lp.leftMargin - lp.rightMargin;
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int srcWidth = ampmLeftPos + (!is24HourFormat ? mAmPm.getWidth() : 0) - rootLeftPos;
			if ((srcWidth != destWidth)) { // 4-4
				int extendSize = (destWidth - srcWidth);
				int moveSize = extendSize / 2;
				mAnimSlide4To4.playTogether(getAnimatorDigit(0, 0 - moveSize));
				return mAnimSlide4To4;
			}
		}
		return null;
	}

	public AnimatorSet applySlide3To3(boolean is24HourFormat, int ampm) {
		if (mAmPm == null || mInfoTemp == null) return null;
		if (!mCenterDate) {
			if (mInfoLoc == null) return null;
			int estWidth = getEstWidth(is24HourFormat, ampm, 0);
			int widgetWidth = ((View) mParent.getParent()).getWidth();
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int srcWidth = mParent.getWidth();
			if ((srcWidth != destWidth)) { // 3-3
				int extendSize = (destWidth - mParent.getWidth());
				int moveSize = extendSize / 2;
				mAnimSlide3To3.playTogether(getAnimatorInfo(0 - moveSize), getAnimatorDigit(0, 0 - moveSize));
				return mAnimSlide3To3;
			}
		} else {
			int rootLeftPos = getLeftPos(mRoot) + mTabWidth * 2; // add back margin left/right
			int ampmLeftPos = getLeftPos(mAmPm);
			int estWidth = ampmLeftPos + (!is24HourFormat ? mAmPmWidth[ampm] : 0) - rootLeftPos;
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
			int widgetWidth = ((View) mParent.getParent()).getWidth() - lp.leftMargin - lp.rightMargin;
			int destWidth = (estWidth > widgetWidth) ? widgetWidth : estWidth;
			int srcWidth = ampmLeftPos + (!is24HourFormat ? mAmPm.getWidth() : 0) - rootLeftPos;
			if ((srcWidth != destWidth)) { // 3-3
				int extendSize = (destWidth - srcWidth);
				int moveSize = extendSize / 2;
				mAnimSlide3To3.playTogether(getAnimatorDigit(mTranslationX, mTranslationX - moveSize));
				return mAnimSlide3To3;
			}
		}
		return null;
	}

	private int getEstWidth(boolean is24HourFormat, int ampm, int tabOffest) {
		int tempPos = getRightPos(mInfoTemp);
		int ampmPos = getLeftPos(mAmPm);
		int destAmPmPos = ampmPos + (!is24HourFormat ? mAmPmWidth[ampm] : 0) + tabOffest; // no change layout am position
		int destEndPos = (destAmPmPos > tempPos) ? destAmPmPos : tempPos; // no change layout end position
		return destEndPos - getLeftPos(mParent);
	}

	private AnimatorSet getAnimatorInfo(int moveSize) {
		AnimatorSet animInfo = new AnimatorSet();
		ObjectAnimator slideTemp = ObjectAnimator.ofFloat(mInfoTemp, "translationX", 0, moveSize);
		ObjectAnimator slideLoc = ObjectAnimator.ofFloat(mInfoLoc, "translationX", 0, moveSize);
		animInfo.setDuration(RotateAnimConsts.SLIDE_DURATION);
		animInfo.setStartDelay(RotateAnimConsts.SLIDE_DELAY);
		animInfo.playTogether(slideTemp, slideLoc);
		return animInfo;
	}

	private AnimatorSet getAnimatorDigit(int fromX, int toX) {
		AnimatorSet animDigit = new AnimatorSet();
		ObjectAnimator slideDigit = ObjectAnimator.ofFloat(mRoot, "translationX", fromX, toX);
		animDigit.setDuration(RotateAnimConsts.SLIDE_DURATION);
		animDigit.setStartDelay(RotateAnimConsts.SLIDE_DELAY);
		animDigit.play(slideDigit);
		return animDigit;
	}

	private int getRightPos(View view) {
		Rect rt = new Rect();
		view.getGlobalVisibleRect(rt);
		return rt.left + view.getWidth();
	}

	private int getLeftPos(View view) {
		Rect rt = new Rect();
		view.getGlobalVisibleRect(rt);
		return rt.left;
	}

	private void setParentWidth(int width) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
		lp.width = width;
		mParent.setLayoutParams(lp);
	}

	private void setLayoutOffset(int offset) {
		if (mCenterDate) { // theme center case:
			mRoot.setPadding(offset, 0, 0, 0);
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRoot.getLayoutParams();
			lp.rightMargin = offset * -1; // add this to enlarge space
			lp.leftMargin = offset * -1; // add this to enlarge space
			mRoot.setLayoutParams(lp);
		} else {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRoot.getLayoutParams();
			lp.rightMargin = offset;
			lp.leftMargin = 0;
			mRoot.setLayoutParams(lp);
		}
	}

	private void setAnimation() {
		BezierSplineInterpolator easeInOut = new BezierSplineInterpolator(RotateAnimConsts.SLIDE_BEZIER, 0f, 1 - RotateAnimConsts.SLIDE_BEZIER, 1f);
		// 3 -> 4
		mAnimSlide3To4.setInterpolator(easeInOut);
		mAnimSlide3To4.addListener(m3To4AnimationListener);
		// 4 -> 3
		mAnimSlide4To3.setInterpolator(easeInOut);
		mAnimSlide4To3.addListener(m4To3AnimationListener);
		// 4 -> 4
		mAnimSlide4To4.setInterpolator(easeInOut);
		mAnimSlide4To4.addListener(m4To4AnimationListener);
		// 3 -> 3
		mAnimSlide3To3.setInterpolator(easeInOut);
		mAnimSlide3To3.addListener(m3To3AnimationListener);
	}

	private void begin3Layout() {
		if (!mCenterDate) {
			mInfoLoc.setPadding(mParent.getLeft(), 0, 0, 0);
			// digit
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRoot.getLayoutParams();
			lp.leftMargin = (int) mTranslationX;
			mRoot.setLayoutParams(lp);
			mRoot.setTranslationX(0);
			setParentWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
		} else {
			beginCenterLayout();
		}
	}

	private void begin4Layout() {
		// info
		if (!mCenterDate) {
			mInfoLoc.setPadding(mParent.getLeft(), 0, 0, 0);
			setParentWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
		} else {
			beginCenterLayout();
		}
	}

	private void end3Layout() {
		// info
		if (!mCenterDate) {
			mInfoTemp.setTranslationX(0);
			mInfoLoc.setTranslationX(0);
			mInfoLoc.setPadding(0, 0, 0, 0);
			setParentWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
		} else {
			endCenterLayout();
		}
		// digit
		setValue(0); // share 2 cases
	}

	private void end4Layout() {
		// info
		if (!mCenterDate) {
			mInfoTemp.setTranslationX(0);
			mInfoLoc.setTranslationX(0);
			mInfoLoc.setPadding(0, 0, 0, 0);
			// digit
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRoot.getLayoutParams();
			lp.leftMargin = 0;
			lp.rightMargin = 0;
			mRoot.setLayoutParams(lp);
			mRoot.setTranslationX(0);
			// parent
			setParentWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
		} else {
			endCenterLayout();
			setValue(1); // end value
		}
	}

	private void beginCenterLayout() {
		// parent
		RelativeLayout.LayoutParams lpParent = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
		mParentMarginLeft = lpParent.leftMargin;
		mParentMarginRight = lpParent.rightMargin;
		lpParent.leftMargin = 0;
		lpParent.rightMargin = 0;
		mParent.setLayoutParams(lpParent);
		int parentLeft = mParent.getLeft();
		// info
		RelativeLayout.LayoutParams lpInfo = (RelativeLayout.LayoutParams) mInfoTemp.getLayoutParams();
		lpInfo.removeRule(RelativeLayout.CENTER_HORIZONTAL);
		lpInfo.leftMargin = parentLeft + mInfoTemp.getLeft(); // widget + current padding
		lpInfo.width = mInfoTemp.getWidth();
		mInfoTemp.setLayoutParams(lpInfo);
		// current
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRoot.getLayoutParams();
		lp.leftMargin = parentLeft + mRoot.getLeft(); // widget + current padding
		lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
		mRoot.setLayoutParams(lp);
		setParentWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
	}

	private void endCenterLayout() {
		// parent
		RelativeLayout.LayoutParams lpParent = (RelativeLayout.LayoutParams) mParent.getLayoutParams();
		lpParent.leftMargin = mParentMarginLeft;
		lpParent.rightMargin = mParentMarginRight;
		mParent.setLayoutParams(lpParent);
		// info
		RelativeLayout.LayoutParams lpInfo = (RelativeLayout.LayoutParams) mInfoTemp.getLayoutParams();
		lpInfo.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lpInfo.leftMargin = 0;
		lpInfo.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
		mInfoTemp.setLayoutParams(lpInfo);
		// current
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRoot.getLayoutParams();
		lp.leftMargin = 0;
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mRoot.setLayoutParams(lp);
		setParentWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
	}

	private Animator.AnimatorListener m4To3AnimationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) { // 4 -> 3 digit
			begin4Layout();
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			end3Layout();
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			Logger.d(LOG_TAG, "m4To3AnimationListener, cancel");
		}
	};

	private Animator.AnimatorListener m3To4AnimationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) { // 3 -> 4 digit
			begin3Layout();
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			end4Layout();
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			Logger.d(LOG_TAG, "m3To4AnimationListener, cancel");
		}
	};

	private Animator.AnimatorListener m4To4AnimationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) { // 4 -> 4 digit
			begin4Layout();
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			end4Layout();
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			Logger.d(LOG_TAG, "m4To4AnimationListener, cancel");
		}
	};

	private Animator.AnimatorListener m3To3AnimationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) { // 3 -> 3 digit
			begin3Layout();
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			end3Layout();
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			Logger.d(LOG_TAG, "m3To3AnimationListener, cancel");
		}
	};
}
