package nundrum.embellirdream;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

/**
 * TODO: document your custom view class.
 */
public class PolarClockView extends View {
    private final String TAG = "PolarClockView";
	private final int mCPMain = Color.rgb(0, 220, 20); // Color Primary Main
	private final int mCPHl = Color.rgb(0, 90, 0); // Color Highlight Main
	private final int mCPShd = Color.rgb(0, 70, 0); // Color Shadow Main
	private final int mCPFill = Color.rgb(0, 110, 9); // Color Shadow Main
	private final int mCSMain = Color.rgb(15, 90, 55); // Color Primary Main
	private final int mCSHl = Color.rgb(150, 110, 250); // Color Highlight Main
	private final int mCSShd = Color.rgb(5, 25, 0); // Color Shadow Main
	private final int mCSFill = Color.rgb(10, 10, 10); // Color Shadow Main
	private Paint mCPMainPaint;
	private Paint mCPHlPaint;
	private Paint mCPShdPaint;
	private Paint mCPFillPaint;

	private final float degPer60 = 360.0f / 60.0f;
	private final float degPer12 = 360.0f / 12.0f;
	private final float degPer24 = 360.0f / 24.0f;

    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    public PolarClockView(Context context) {
        super(context);
        init(null, 0);
    }

    public PolarClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PolarClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PolarClockView, defStyle, 0);

        //mExampleString = a.getString( R.styleable.PolarClockView_exampleString);
        //mExampleColor = a.getColor( R.styleable.PolarClockView_exampleColor, mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.PolarClockView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.PolarClockView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.PolarClockView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
		mCPMainPaint = new Paint(); mCPMainPaint.setColor(mCPMain); 
		mCPMainPaint.setStyle(Paint.Style.STROKE); mCPMainPaint.setStrokeWidth(10);
		mCPHlPaint = new Paint(); mCPHlPaint.setColor(mCPHl);
		mCPHlPaint.setStyle(Paint.Style.STROKE); mCPHlPaint.setStrokeWidth(10);
		mCPShdPaint = new Paint(); mCPShdPaint.setColor(mCPShd);
		mCPHlPaint.setStyle(Paint.Style.STROKE); mCPHlPaint.setStrokeWidth(10);
		mCPFillPaint = new Paint(); mCPFillPaint.setColor(mCPFill);
		mCPHlPaint.setStyle(Paint.Style.STROKE); mCPHlPaint.setStrokeWidth(10);

        //mTextPaint = new TextPaint();
        //mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        //mTextPaint.setTextSize(mExampleDimension);
        //mTextPaint.setColor(mExampleColor);
        //mTextWidth = mTextPaint.measureText(mExampleString);

        //Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        //mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw()==============");

		float size = Math.min(getWidth(), getHeight());
		float radius = 0.5f * size;
		float halfRadius = 0.5f * radius;

		// get time/date info
		Calendar now = Calendar.getInstance();
		//Calendar nowGMT = Calendar.getInstance(TZ, locale);
		int hour = now.get(Calendar.HOUR);
		int hour24 = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);

		Log.v(TAG, "hour24: " + hour24);
		Log.v(TAG, "hour: " + hour);
		Log.v(TAG, "minute: " + minute);

		// in degrees
		float minuteDeg =  minute * degPer60;
		float hourDeg = hour * degPer12;
		Log.v(TAG, "hourDeg: " + hourDeg);
		Log.v(TAG, "minuteDeg: " + minuteDeg);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

		float hp = size - (size * 0.9f);
		float mp = size - (size * 0.8f);

		//RectF r = new RectF(0.0f, 0.0f, size, size);

		canvas.translate(0.5f * getWidth(), 0.5f * getHeight() );
		canvas.rotate(-90);

        canvas.drawArc(0.0f + hp - radius, 0.0f + hp - radius, radius - hp, radius - hp, 0.0f, hourDeg, false, mCPHlPaint);
        canvas.drawArc(0.0f + mp - radius, 0.0f + mp - radius, radius - mp, radius - mp, 0.0f, minuteDeg,false, mCPMainPaint);
        //canvas.drawLine(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), mCPMainPaint);
        //canvas.drawLine(0.0f, (float) getHeight(), (float) getWidth(), 0.0f, mCPHlPaint);
        
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//				M
//                mTextPaint);
		// Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }

		this.postInvalidateDelayed(1000);
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
