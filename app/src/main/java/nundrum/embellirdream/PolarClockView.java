package nundrum.embellirdream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import android.support.v4.app.ActivityCompat;

import java.util.Calendar;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

// notes to self:
// hour, minute
// daylight hours
// date, calendar items
// weather forecast

/**
 * TODO: document your custom view class.
 */
public class PolarClockView extends View implements LocationListener {
    private final String TAG = "PolarClockView";
    private Context mContext;
    private final int mCPMain = Color.rgb(0, 220, 20); // Color Primary Main
    private final int mCPHl = Color.rgb(0, 90, 0); // Color Highlight Main
    private final int mCPShd = Color.rgb(0, 70, 0); // Color Shadow Main
    private final int mCPFill = Color.rgb(0, 110, 9); // Color Shadow Main
    private final int mCSMain = Color.rgb(15, 90, 55); // Secondary
    private final int mCSHl = Color.rgb(150, 110, 250); // Secondary
    private final int mCSShd = Color.rgb(5, 25, 0); // Secondary
    private final int mCSFill = Color.rgb(10, 10, 10); // Secondary
    private final int mCTMain = Color.rgb(70, 60, 225); // Tertiary 
    private final int mCTHl = Color.rgb(50, 50, 125); // Tertiary 
    private Paint mPaint;


    private final float degPer60 = 360.0f / 60.0f;
    private final float degPer12 = 360.0f / 12.0f;
    private final float degPer24 = 360.0f / 24.0f;

    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

	private double mLat;
	private double mLon;

    public PolarClockView(Context context) {
        super(context);
        Log.v(TAG, "PolarClockView contstructor, context:" + context);
        mContext = context;
        init(null, 0);
    }

    public PolarClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v(TAG, "PolarClockView contstructor, context: " + context + ", attrs: " + attrs);
        mContext = context;
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
        mPaint = new Paint();

        //mTextPaint = new TextPaint();
        //mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        //mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        // Register to receive location events
		Log.v(TAG, "ACCESS_FINE_LOCATION? " + ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION));
		Log.v(TAG, "ACCESS_COARSE_LOCATION? " + ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION));
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if  (checkLocationPermission()) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 0, this);
			Log.v(TAG, "location:" + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        } else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
		}
    }

	private boolean checkLocationPermission () {
		return ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED)
		|| (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));
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

		float riseDeg = 0.0f;
		float setDeg = 0.0f;

		if (checkLocationPermission()) {
			com.luckycatlabs.sunrisesunset.dto.Location sslocation = new com.luckycatlabs.sunrisesunset.dto.Location(String.valueOf(mLat), String.valueOf(mLon));
			Log.v(TAG, "location:" + sslocation.getLatitude() + ", " + sslocation.getLongitude());
			SunriseSunsetCalculator calc = new SunriseSunsetCalculator( sslocation, now.getTimeZone()); 
			Calendar rise = calc.getOfficialSunriseCalendarForDate(now);
			Calendar set = calc.getOfficialSunsetCalendarForDate(now);
			Log.v(TAG, "now:" + now);
			Log.v(TAG, "sunrise:" + rise.get(Calendar.HOUR_OF_DAY) + ":" + rise.get(Calendar.MINUTE));
			Log.v(TAG, "sunset:" + set.get(Calendar.HOUR_OF_DAY) + ":" + set.get(Calendar.MINUTE));
			riseDeg = (rise.get(Calendar.HOUR_OF_DAY) * 15 )  + (rise.get(Calendar.MINUTE) * 0.25f);
			setDeg = (set.get(Calendar.HOUR_OF_DAY) * 15) + (set.get(Calendar.MINUTE) * 0.25f);
		}

		//Log.v(TAG, "hour24: " + hour24);
		//Log.v(TAG, "hour: " + hour);
		//Log.v(TAG, "minute: " + minute);

		// in degrees
		float minuteDeg =  minute * degPer60;
		float hourDeg = hour * degPer12;
		float hour24Deg = hour24 * degPer24 + minute / degPer60 / degPer24;
		//Log.v(TAG, "hourDeg: " + hourDeg);
		//Log.v(TAG, "hour24Deg: " + hour24Deg);
		//Log.v(TAG, "minuteDeg: " + minuteDeg);
		//Log.v(TAG, "riseDeg: " + riseDeg);
		//Log.v(TAG, "setDeg: " + setDeg);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

		float ringmax = (radius * 0.1f); //divide into 10 rings
		float hp = (ringmax * 4f);  //hour
		float mp = (ringmax * 3f);  //minute
		float ssp = (ringmax * 5f); //sunrise/sunset/24h
		float cp = (ringmax * 1f) - (ringmax * 0.5f);  //calendar
		float dp = (ringmax * 1f) + (ringmax * 0.5f);  //day
		Log.v(TAG, "cp: "+ cp);
		Log.v(TAG, "dp: "+ dp);

		//RectF r = new RectF(0.0f, 0.0f, size, size);

		// -------- paint -----------
		canvas.translate(0.5f * getWidth(), 0.5f * getHeight() );
		canvas.rotate(-90);

		mPaint.setStyle(Paint.Style.STROKE);
		//'daylight' ring
		if (checkLocationPermission()) {
			mPaint.setColor(mCTHl);
			mPaint.setStrokeWidth(ringmax);
			canvas.drawArc(0.0f + ssp - radius, 0.0f + ssp - radius, radius - ssp, radius - ssp, riseDeg, setDeg - riseDeg,false, mPaint);
			mPaint.setColor(mCTMain);
			mPaint.setStrokeWidth(ringmax - (ringmax * 0.1f));
			canvas.drawArc(0.0f + ssp - radius, 0.0f + ssp - radius, radius - ssp, radius - ssp, riseDeg, setDeg - riseDeg,false, mPaint);
		}
		//24h marker
		mPaint.setColor(mCSHl);
		mPaint.setStrokeWidth(ringmax - (ringmax * 0.5f));
        canvas.drawArc(0.0f + ssp - radius, 0.0f + ssp - radius, radius - ssp, radius - ssp, hour24Deg - 3f , 6f ,false, mPaint);
		//hour ring
		mPaint.setColor(mCPHl);
		mPaint.setStrokeWidth(ringmax / 2);
        canvas.drawArc(0.0f + hp - radius, 0.0f + hp - radius, radius - hp, radius - hp, 0.0f, hourDeg, false, mPaint);
		//minute ring
		mPaint.setColor(mCPMain);
		mPaint.setStrokeWidth(ringmax / 1.5f);
        canvas.drawArc(0.0f + mp - radius, 0.0f + mp - radius, radius - mp, radius - mp, 0.0f, minuteDeg,false, mPaint);
        //canvas.drawLine(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), mCPMainPaint);
        //canvas.drawLine(0.0f, (float) getHeight(), (float) getWidth(), 0.0f, mCPHlPaint);
       


		// info for drawing calendar ring
		int day = now.get(Calendar.DAY_OF_MONTH);
		int lastDay = now.getActualMaximum(Calendar.DAY_OF_MONTH);
		float degsPerDay = ( 360 / lastDay );
		float todayDeg =  degsPerDay * day;
		float startDeg =  degsPerDay * (day - 8);
		float endDeg =  degsPerDay * (day + 14);

		mPaint.setColor(mCPHl);
		mPaint.setStrokeWidth(ringmax * 1f);
        canvas.drawArc(0.0f + cp - radius, 0.0f + cp - radius, radius - cp, radius - cp, startDeg, endDeg - startDeg,false, mPaint);
		
		mPaint.setStyle(Paint.Style.FILL);

		Path cogPath = CalendarCogPath(ringmax, ringmax * 0.5f);
		canvas.rotate(90);
		canvas.save();
		canvas.rotate(startDeg);
		for (float f = startDeg ; f < endDeg ; f+= degsPerDay ) {
			canvas.rotate(degsPerDay);
			canvas.save();
			canvas.translate(0f, - radius + cp);
			canvas.translate(- ringmax, 0);
			canvas.drawPath(cogPath, mPaint);
			//canvas.drawCircle(0,radius - dp ,ringmax * 0.75f,mPaint);
			canvas.restore();
		}
		canvas.restore();

		Log.v(TAG, "day:" + day);
		Log.v(TAG, "lastDay:" + lastDay);
		Log.v(TAG, "todayDeg:" + todayDeg);
		Log.v(TAG, "startDeg:" + startDeg);
		mPaint.setStrokeWidth(1f);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(40);
		mPaint.setColor(mCSHl);
		String dayStr = String.valueOf(day);
        float mTextWidth = mPaint.measureText(dayStr);
		canvas.save();
		canvas.rotate(todayDeg);
		canvas.translate(0, (- radius + dp));
		canvas.rotate(180);
		//canvas.drawCircle(0, (radius - dp), ringmax, mPaint);
		canvas.drawText(String.valueOf(day), 0, 0, mPaint);
		canvas.restore();


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

	private Path CalendarCogPath (float width, float height) {
		float w = width * 2.0f;
		float h = height * 2.0f;
		float qw = w * 0.125f;
		Path mPath = new Path();
		mPath.moveTo(0.0f, 0.0f);
		mPath.lineTo(w, 0.0f);
		mPath.lineTo(w, h);
		mPath.lineTo((w - qw), h);
		mPath.quadTo((0.5f * w), 0.0f, qw, h);
		mPath.lineTo(0.0f, h);
		mPath.lineTo(0.0f, 0.0f);
		mPath.close();
		return mPath;
	}

	private void collectData() {
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

	/* methods for implementing LocationListener */
	public void onLocationChanged(Location location) {
		Log.v(TAG,"onLocationChanged(): " + location);
		mLat = location.getLatitude();
		mLon = location.getLongitude();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
	public void onProviderEnabled(String provider) {}
	public void onProviderDisabled(String provider) {}

}
