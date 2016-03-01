package com.wenchaos.draw;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;


public class WeightView extends View {

	protected int oldX;// 上传Touch的X轴坐标*
	protected int mTouchSlop;//*
	protected boolean mIsBeingDragged = false;// 滑动是否开始*

	protected Paint mPaint;// 画笔*
	private Paint mInnerCiclePaint; // 绘制内圆画笔*
	private Paint mOuterCiclePaint;// 绘制外圆画笔*
	private Paint mTitlePaint;// 绘制标题画笔*

	private Path mPath;// 绘制趋势图对于的Path对象
	private Rect mYTitelRect;// Y轴对应的Rect*
	private int mDrawCount = 7;// 绘制多少个点 ，默认绘制5个*
	/** Y轴文字对应的宽度 */
	private int mYTitleWitdh = 30; //*

	private int mActivePointerId;
	protected List<Float> mPointes = new ArrayList<Float>(); // 数据集*
	private List<String> mXAxisValus;// x轴坐标集*

	protected int mDistance = 100;// 绘制点之间的距离*
	private float mMaxYValue = 100F;// y轴最大值*
	private int mYAxisUnit = 25;//*
	private int mYxisCount = 5;//y轴刻度的数量*
	
	private IActionEndListener mAEndListener;//*
//	private float mTrendLineSize = 3; // 趋势图背景颜色
	private int mTowards;// 滑动方向*
//	private int[] mPointColors;// 趋势图中折现颜色
	private int mTextColor = 0xFF131313;// 字体颜色
	private int mDataLineColor = 0xffd73c1e;//数据线颜色值*
	private float mTextColorSize = 10;// 字体颜色
	private float mTextColorSmall = 9;// 字体颜色
	private float mTextTitleColorSize = 16;// 字体颜色
	// Y轴中心线
	private int mYCenterColor = 0xFFFFD583;// Y轴中心线的颜色
	private int mYCenterSize = 2;// y轴中心线的大小
	/** 背景线条颜色  0xffe5e5e5*/
	 private int mBcakLineColor = 0xffe5e5e5;// 背景线条颜色
	private int mLineSize = 2;// 背景线条大小
	private int foucusTextSize = 13;// 焦点对应的时间值大小
	private int foucusTextColor = 0xff0ea3f3;// 中心轴线的时间颜色值

	private int mInnerClicleColor = 0xFFFFFEFF; // 内圆颜色
	private int mInnerClicleSize = 10; // 内圆大小
	private int mOuterClicleSize = 16;// 外圆大小
	private int mOuerCicleRadius = 6;
	private int mInnerCicleRadius = 4;

	/** 表示：中心轴的X方向的距离 */
	private int currentCenter;
	private int centerPosition;

	public WeightView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public WeightView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeightView(Context context) {
		this(context, null);
	}

	private void init(Context context) {

		mTextTitleColorSize = sp2px(context, mTextTitleColorSize);
		mTextColorSize = sp2px(context, mTextColorSize);
		mTextColorSmall = sp2px(context, mTextColorSmall);
//		mTrendLineSize = dp2px(context, mTrendLineSize);
		mInnerClicleSize = (int) dp2px(context, mInnerClicleSize);
		mOuterClicleSize = (int) dp2px(context, mOuterClicleSize);
		mOuerCicleRadius = (int) dp2px(context, mOuerCicleRadius);
		mInnerCicleRadius = (int) dp2px(context, mInnerCicleRadius);
		mYCenterSize = (int) dp2px(context, mYCenterSize);
		foucusTextSize = (int) dp2px(context, foucusTextSize);

		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);

		mInnerCiclePaint = new Paint();
		mInnerCiclePaint.setTextAlign(Align.CENTER);
		mInnerCiclePaint.setColor(mInnerClicleColor);
		mInnerCiclePaint.setTextSize(mInnerClicleSize);
		mInnerCiclePaint.setAntiAlias(true);

		mOuterCiclePaint = new Paint();
		mOuterCiclePaint.setTextAlign(Align.CENTER);
		mOuterCiclePaint.setTextSize(mOuterClicleSize);
		mOuterCiclePaint.setAntiAlias(true);

		mTitlePaint = new Paint();
		mTitlePaint.setTextAlign(Align.CENTER);
		mTitlePaint.setColor(Color.BLUE);
		mTitlePaint.setTextSize(sp2px(context, 20));

		mPath = new Path();
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();

		mYTitelRect = new Rect();
//		mPointColors = new int[] { 0XFFFFFBE4, 0xFF000000 };
		mYTitleWitdh = (int) dp2px(context, mYTitleWitdh);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.v("TAG", "WeightView:onDraw()----------");
		mDistance = (getWidth() - mYTitleWitdh) / mDrawCount;
		currentCenter = (getWidth() - mDistance);
		/**
		 * mPointes 点的集合 mXAxisValus x轴的内容集合
		 */
		int orgColor = mPaint.getColor();
		float scrollX = getScrollX();
		mTitlePaint.setColor(mTextColor);
		mTitlePaint.setTextSize(mTextColorSize);

		int xAxisHeight_up = getFontHeight(mTitlePaint, mTextColorSize);
		int xAxisHeight_blow = getFontHeight(mPaint, mTextColorSmall) - 20;
		int xAxisTitleHeight = xAxisHeight_up + xAxisHeight_blow;// x轴坐标内容的高度
		// 图例title
		int xAxisHeadHeight = 20;
		// Height （线框的高度）
		int trendHeight = getHeight() - xAxisTitleHeight - xAxisHeadHeight; // 图例趋势图区域高

		int fristPosion = (getScrollX() + mYTitleWitdh) / mDistance;
		int offsetCount = 5; // 偏移量 ,绘制多少个超出屏幕的点

		FontMetrics fontMetrics = mPaint.getFontMetrics();
		float fontHeight = fontMetrics.bottom - fontMetrics.top;// 用于X,Y轴坐标的内容字体高度

		// draw title
		// float orgSize = mPaint.getTextSize();
//		mTitlePaint.setColor(mPointColors[1]);
//		mTitlePaint.setTextSize(mTextTitleColorSize);

		float orgStroke = mTitlePaint.getStrokeWidth();
		Typeface orgTypeface = mTitlePaint.getTypeface();
		float orgPaintStroke = mPaint.getStrokeWidth();

		mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTitlePaint.setStrokeWidth(4);
		mTitlePaint.setTypeface(orgTypeface);
		mTitlePaint.setStrokeWidth(orgStroke);

		// 最左边边的线
		mTitlePaint.setColor(mBcakLineColor);
		canvas.drawLine((int) (scrollX + mYTitleWitdh), xAxisHeadHeight,
				(int) (scrollX + mYTitleWitdh), getHeight() - xAxisTitleHeight,
				mTitlePaint);
		// draw columns
		mTitlePaint.setTextSize(mLineSize);
		for (int i = 0; i < mDrawCount - 1; i++) {
			if (i == 5) {
				// 绘制y轴中心线
				// mTitlePaint.setTextSize(mTextColorSize);
				mTitlePaint.setColor(mYCenterColor);
				mTitlePaint.setStrokeWidth(mYCenterSize);
				canvas.drawLine(scrollX + mYTitleWitdh + mDistance * (mDrawCount - 1),
						xAxisHeadHeight, scrollX + mYTitleWitdh + mDistance
								* (mDrawCount - 1), getHeight() - xAxisTitleHeight - 20,
						mTitlePaint);
			} else {
				mTitlePaint.setColor(mBcakLineColor);
				mTitlePaint.setTextSize(mLineSize);
				canvas.drawLine(scrollX + mYTitleWitdh + mDistance * (i + 1),
						xAxisHeadHeight, scrollX + mYTitleWitdh + mDistance
								* (i + 1), getHeight() - xAxisTitleHeight,
						mTitlePaint);
			}
		}
		// draw rows
		mTitlePaint.setStrokeWidth(orgStroke);
		mTitlePaint.setColor(mTextColor);
		mTitlePaint.setTextSize(mTextColorSize);
		for (int i = 1; i <= mYxisCount; i++) {
			if (i == 1 || i == mYxisCount) {
				mTitlePaint.setColor(0xff7ecef9);
				canvas.drawLine(scrollX + mYTitleWitdh, xAxisHeadHeight
						+ trendHeight / (mYxisCount-1) * (i - 1), scrollX + getWidth(),
						xAxisHeadHeight + trendHeight / (mYxisCount-1) * (i - 1),
						mTitlePaint);
			} else {
				// draw rowes
				mTitlePaint.setColor(mBcakLineColor);
				canvas.drawLine(scrollX + mYTitleWitdh, xAxisHeadHeight
						+ trendHeight / (mYxisCount-1) * (i - 1), scrollX + getWidth(),
						xAxisHeadHeight + trendHeight / (mYxisCount-1) * (i - 1),
						mTitlePaint);
			}
		}
		// 绘制小三角
		drawTrigon(scrollX, canvas, xAxisTitleHeight, xAxisHeadHeight,
				trendHeight, mTitlePaint);
		//
		mOuterCiclePaint.setStrokeWidth(orgStroke);
		mInnerCiclePaint.setStrokeWidth(orgStroke);
		mPaint.setStrokeWidth(orgPaintStroke);
		mPaint.setColor(mTextColor);
		mPaint.setTextSize(mTextColorSmall);

		// 绘制数据点
		mTitlePaint.setStrokeWidth(orgStroke);
		mTitlePaint.setColor(0xffe5e5e5);
		if (mPointes != null) {
			for (int index = 0; index < 1; index++) {
				int startPostion = (fristPosion - offsetCount) >= 0 ? (fristPosion - offsetCount)
						: 0;
				int endPosiont = (mDrawCount + offsetCount + fristPosion) >= mPointes
						.size() ? mPointes.size()
						: (mDrawCount + offsetCount + fristPosion);
				// Log.v("WEI",
				// "startPostion : "+startPostion+" endPosiont: "+endPosiont);
				mPaint.setColor(mDataLineColor);
				mPaint.setStrokeWidth(2);
				// draw dataLine
				int currentY;
				int maxCurrent = xAxisHeadHeight;
				int minCurrent = getHeight() - xAxisTitleHeight;
				if (endPosiont > startPostion && endPosiont > 0) {

					for (int i = startPostion; i < endPosiont; i++) {

						currentY = (int) (getHeight() - (mPointes.get(i)
								* trendHeight / mMaxYValue + xAxisTitleHeight));
						// Log.v("WEI",
						// "mPointes.get(i)  "+mPointes.get(i)+"  currentY:"+currentY);
						if (mPointes.get(i) >= mMaxYValue) {
							if (i == startPostion) {
								mPath.moveTo(i * mDistance, maxCurrent);
							} else {
								mPath.lineTo(i * mDistance, maxCurrent);
							}
						} else if (mPointes.get(i) < 0.0) {
							if (i == startPostion) {
								mPath.moveTo(i * mDistance, minCurrent);
							} else {
								mPath.lineTo(i * mDistance, minCurrent);
							}
						} else {
							if (i == startPostion) {
								mPath.moveTo(i * mDistance, currentY);
							} else {
								mPath.lineTo(i * mDistance, currentY);
							}
						}
					}
				}
				canvas.drawPath(mPath, mPaint);
				canvas.save();
				mPath.reset();

				// draw cicle
//				mOuterCiclePaint.setStrokeWidth(mTrendLineSize);
//				mInnerCiclePaint.setStrokeWidth(mTrendLineSize);
				mOuterCiclePaint.setColor(mDataLineColor);
				if (endPosiont > startPostion && endPosiont > 0) {
					for (int i = startPostion; i < endPosiont; i++) {
						currentY = (int) (getHeight() - (mPointes.get(i)
								* trendHeight / mMaxYValue + xAxisTitleHeight));
						if (mPointes.get(i) >= mMaxYValue) {
							canvas.drawCircle(i * mDistance, maxCurrent,
									mOuerCicleRadius, mOuterCiclePaint); // 实心
						} else if (mPointes.get(i) <= 0) {
							canvas.drawCircle(i * mDistance, minCurrent,
									mOuerCicleRadius, mOuterCiclePaint); // 实心
						} else {
							canvas.drawCircle(i * mDistance, currentY,
									mOuerCicleRadius, mOuterCiclePaint);
							canvas.drawCircle(i * mDistance, currentY,
									mInnerCicleRadius, mInnerCiclePaint);
						}
					}
				}
				mPaint.setColor(orgColor);
			}
		}
		// and x-axis values
		mTitlePaint.setColor(0xff888888);
		if (mXAxisValus != null && mXAxisValus.size() > 0) {
			// List<Float> maxItem = getMaxItem();
			int startPostion = (fristPosion - offsetCount) >= 0 ? (fristPosion - offsetCount)
					: 0;
			int endPosiont = (mDrawCount + offsetCount + fristPosion) >= mPointes
					.size() ? mPointes.size()
					: (mDrawCount + offsetCount + fristPosion);
			if (endPosiont > startPostion && endPosiont > 0) {
				float textBaseY_x_blow = (getHeight() - xAxisHeight_blow / 2 - 10)
						* 2
						- ((getHeight() - xAxisHeight_blow / 2 - 10) * 2 - fontHeight)
						/ 2 - fontMetrics.bottom;
				for (int i = startPostion; i < endPosiont; i++) {
					// draw x-axis blow
					mYTitelRect.set((int) (mDistance * (i - 1)), (getHeight()
							- xAxisHeight_blow - 10),
							(int) (mDistance * (i + 1)), getHeight() - 10);
					mYTitelRect.set((int) (mDistance * (i - 1)), (getHeight()
							- xAxisHeight_up - xAxisHeight_blow - 10),
							(int) (mDistance * (i + 1)), getHeight()
									- xAxisHeight_blow - 10);
					// drawToCenterTextColor(i);
					// 如果是中心轴线，则换一种特定颜色
					if (i == centerPosition) {
						mTitlePaint.setColor(foucusTextColor);
						mTitlePaint.setTextSize(foucusTextSize);
					} else {
						mTitlePaint.setColor(0xff888888);
						mTitlePaint.setTextSize(mTextColorSize);
					}
					canvas.drawText(mXAxisValus.get(i), mYTitelRect.centerX(),
							textBaseY_x_blow, mTitlePaint);
				}
			}
		}
		// // draw y rxis rect
		mTitlePaint.setColor(Color.WHITE);
		mTitlePaint.setStyle(Style.FILL);
		mYTitelRect.set((int) scrollX, 0, (int) (scrollX + mYTitleWitdh) - 15,
				getHeight());
		canvas.drawRect(mYTitelRect, mTitlePaint);
		
		// draw y-axis values
		mTitlePaint.setColor(0xff888888);
		mTitlePaint.setTextSize(mTextColorSize);
		for (int i = 0; i <= mYxisCount; i++) {
			float textBaseY = (xAxisHeadHeight + trendHeight / (mYxisCount-1) * (i - 1))
					* 2
					- ((xAxisHeadHeight + trendHeight / (mYxisCount-1) * (i - 1)) * 2 - fontHeight)
					/ 2 - fontMetrics.bottom;// y轴坐标内容所在图例中的y值
			canvas.drawText(mYAxisUnit * (mYxisCount - i) + "", scrollX + mYTitleWitdh
					/ 2 - 10, textBaseY, mTitlePaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			oldX = (int) event.getX();
			if ((mIsBeingDragged)) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
			}
			mActivePointerId = event.getPointerId(0);
			return true;
		case MotionEvent.ACTION_MOVE:
			final int activePointerIndex = event
					.findPointerIndex(mActivePointerId);
			if (activePointerIndex == -1) {
				break;
			}

			final int x = (int) event.getX(activePointerIndex);
			int deltaX = oldX - x;
			if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				mIsBeingDragged = true;
				if (deltaX > 0) {
					deltaX -= mTouchSlop;
				} else {
					deltaX += mTouchSlop;
				}
			}

			// HorizontalScrollView
			if (mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
				oldX = x;
				mTowards = deltaX;
				scrollBy(deltaX, 0);
			}
			invalidate();
			return true;
			/*
			 * case MotionEvent.ACTION_UP: //HorizontalScrollView break;
			 */
		default:
			if (mIsBeingDragged) {
				mIsBeingDragged = false;
				int nextCenter = getToNextCenter(mTowards);
				if (mPointes != null && mPointes.size() > 0) {
					mTowards = 0;
					int halfWidth = currentCenter;
					int postionLocal = nextCenter * mDistance;
					scrollTo(postionLocal - halfWidth, 0);
					if (mAEndListener != null) {
						if (nextCenter == 0 && mPointes.size() == 0) {
							nextCenter = -1;
						}
						mAEndListener.actionEnd(nextCenter);
						centerPosition = nextCenter;
					}
				}
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	public void setOnEndListener(IActionEndListener aEndListener) {
		this.mAEndListener = aEndListener;
	}

	/**
	 * 
	 * @param pointValue
	 *            数据点集合
	 * @param colors
	 *            数据点的颜色
	 * @param xAixsValues
	 *            x轴的时间值
	 * @param center
	 *            中心点
	 */
	public void setPointes(List<Float> pointValue, List<String> xAixsValues,
			int center) {
		mPointes = pointValue;
		mXAxisValus = xAixsValues;
		centerPosition = center - 1;
		setSelectedInCenter(center);
		invalidate();
	}

	/**
	 * 设置居中显示的点的位置 。 注意 在Touch事件的流程方法中使用该方法，会引发闪屏现象，因此避免在Touch的相关方法中使用。
	 * 
	 * @param postion
	 *            从0开始
	 */

	private void setSelectedInCenter(int postion) {
		scrollTo((postion) * mDistance - getWidth(), 0);
	}

	private int getToNextCenter(int towards) {
		// int centerX = getScrollX() + mYTitleWitdh + (getWidth() -
		// mYTitleWitdh) / 2;//x轴中点坐标
		int centerX = getScrollX() + mYTitleWitdh + currentCenter;// x轴中点坐标
		float curNearCenter = ((float) centerX) / mDistance; //
		float remainder = curNearCenter % mDistance;
		if (curNearCenter <= 0) {
			return 0;
		}
		if (curNearCenter > mPointes.size() - 1) {
			return mPointes.size() - 1;
		}
		if (remainder == 0) {
			return (int) curNearCenter;
		}
		return (int) curNearCenter;
	}

	/**
	 * 绘制三角形
	 * 
	 * @param scrollX
	 * @param canvas
	 * @param textBaseY
	 * @param xAxisTitleHeight
	 * @param xAxisHeadHeight
	 * @param trendHeight
	 * @param paint
	 */
	private void drawTrigon(float scrollX, Canvas canvas, int xAxisTitleHeight,
			int xAxisHeadHeight, int trendHeight, Paint paint) {
		//
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setColor(0xff7ecef9);
		Path path = new Path();
		path.reset();
		path.moveTo(scrollX + mYTitleWitdh + mDistance * (6), getHeight()
				- xAxisTitleHeight - 20);// 开始坐标 也就是三角形的顶点
		path.lineTo(scrollX + mYTitleWitdh + mDistance * (6) - 20,
				xAxisHeadHeight + trendHeight / 5 * (6 - 1));
		path.lineTo(scrollX + mYTitleWitdh + mDistance * (6) + 20,
				xAxisHeadHeight + trendHeight / 5 * (6 - 1));
		path.close();
		canvas.drawPath(path, paint);
		// 绘制底部 Line
		mTitlePaint.setStrokeWidth(3);
		mTitlePaint.setColor(Color.WHITE);
		canvas.drawLine(scrollX + mYTitleWitdh + mDistance * (6) - 20,
				xAxisHeadHeight + trendHeight / 5 * (6 - 1), scrollX
						+ mYTitleWitdh + mDistance * (6) + 20, xAxisHeadHeight
						+ trendHeight / 5 * (6 - 1), mTitlePaint);
	}

	/**
	 * 事件分发机制 对事件进行分发,将事件分发给当前的onInterceptTouchEvent()， 然后分发给 当前的View
	 * 的onTouchEvent。由此处理
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(event);
	}

	private int getFontHeight(Paint paint, float fontSize) {
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	/**
	 * Touch事件结束监听
	 * 
	 * 
	 */
	public interface IActionEndListener {
		public void actionEnd(int position);
	}

	private float sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (spValue * fontScale + 0.5f);
	}

	private float dp2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (dpValue * scale + 0.5f);
	}

}
