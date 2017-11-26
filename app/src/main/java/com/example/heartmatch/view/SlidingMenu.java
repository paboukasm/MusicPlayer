package com.example.heartmatch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlidingMenu extends ViewGroup {
	private static final String TAG = "SlidingMenu";
	private View mLeftView;
	private View mContentView;
	private int mLeftWidth;
	private float mDownX;
	private float mDownY;

	private Scroller mScroller;

	private boolean isLeftShow = false;

	public SlidingMenu(Context context) {
		this(context, null);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(context);
	}

	@Override
	protected void onFinishInflate() {
		// xml return when finish loading

		mLeftView = getChildAt(0);
		mContentView = getChildAt(1);

		LayoutParams params = mLeftView.getLayoutParams();
		mLeftWidth = params.width;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 测量孩子

		//
		// 父和子的测量关系
		// child.measure():期望孩子的大小该怎么设置
		// widthMeasureSpec:期望值--
		// 2. 头2位：代表的是模式
		// @ 1. UNSPECIFIED： 不确定，随意，自己去定-->0
		// @ 2. EXACTLY：精确的 ---> 200 希望宽度确定为200px
		// @ 3. AT_MOST：最大的---> <200
		// 3. 后30位：数值

		// int mode = MeasureSpec.getMode(widthMeasureSpec);获得头2位
		// int size = MeasureSpec.getSize(widthMeasureSpec);获得后30位的值
		// MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);组装32位的01010

		// widthMeasureSpec:父容器希望 自己的宽度是多大

		// measure left
		int leftWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mLeftWidth, MeasureSpec.EXACTLY);
		mLeftView.measure(leftWidthMeasureSpec, heightMeasureSpec);

		// measure right
		mContentView.measure(widthMeasureSpec, heightMeasureSpec);

		// set width and height
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		// setMeasuredDimension(10, 10);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// 相对性：父容器确定孩子的位置

		int width = mLeftView.getMeasuredWidth();
		int height = mLeftView.getMeasuredHeight();

		Log.d(TAG, "width : " + width);
		Log.d(TAG, "height : " + height);

		// set left layout
		int lvLeft = -width;
		int lvTop = 0;
		int lvRight = 0;
		int lvBottom = height;
		mLeftView.layout(lvLeft, lvTop, lvRight, lvBottom);// 有width和height

		// set right layout
		mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = ev.getX();
			mDownY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = ev.getX();
			float moveY = ev.getY();

			if (Math.abs(moveX - mDownX) > Math.abs(moveY - mDownY)) {
				// move horizontal
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = event.getX();
			mDownY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getX();
			float moveY = event.getY();

			int diffX = (int) (mDownX - moveX + 0.5f);

			int scrollX = getScrollX() + diffX;

			if (scrollX < 0 && scrollX < -mLeftView.getMeasuredWidth()) {
				// slide from left to right
				scrollTo(-mLeftView.getMeasuredWidth(), 0);
			} else if (scrollX > 0) {
				scrollTo(0, 0);
			} else {
				scrollBy(diffX, 0);
			}
			mDownX = moveX;
			mDownY = moveY;
			break;
		case MotionEvent.ACTION_UP:


			int width = mLeftView.getMeasuredWidth();
			int currentX = getScrollX();
			float middle = -width / 2f;
			switchMenu(currentX <= middle);
			break;
		default:
			break;
		}
		return true;
	}

	public void switchMenu(boolean showLeft) {

		isLeftShow = showLeft;
		int width = mLeftView.getMeasuredWidth();
		int currentX = getScrollX();
		if (!showLeft) {
			// close menu
			// scrollTo(0, 0);
			// starting point---》end point
			// -100------->0 -100,-99,-98.....0

			int startX = currentX;
			int startY = 0;

			int endX = 0;
			int endY = 0;

			int dx = endX - startX;
			int dy = endY - startY;

			int duration = Math.abs(dx) * 10;
			if (duration >= 600) {
				duration = 600;
			}


			mScroller.startScroll(startX, startY, dx, dy, duration);

		} else {
			// open:show list
			// scrollTo(-width, 0);

			int startX = currentX;
			int startY = 0;

			int endX = -width;
			int endY = 0;

			int dx = endX - startX;
			int dy = endY - startY;

			int duration = Math.abs(dx) * 10;
			if (duration >= 600) {
				duration = 600;
			}


			mScroller.startScroll(startX, startY, dx, dy, duration);
		}
		invalidate();// UI refresh---> draw() -->drawChild() --> computeScroll()
	}


	@Override
	public void computeScroll() {
		// the animation is not yet finished.
		if (mScroller.computeScrollOffset()) {
			// 更新位置
			scrollTo(mScroller.getCurrX(), 0);
			invalidate();
		}
	}


	public void toggle() {
		switchMenu(!isLeftShow);
	}
}
