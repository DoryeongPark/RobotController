package org.ollide.rosandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

/**
 * Created by Felix on 2016-07-12.
 */
public class SSensorView extends View {

    private final float MAXIMUM_VALUE = 8.0f;
    private final int SENSOR_COUNT = 8;

    private Rect area;
    private int width;
    private int height;

    private float widthInterval;

    private int color = Color.rgb(16, 16, 16);

    Vector<Float> sensorRatio;

    public SSensorView(Context context){

        super(context);

        initiateSettings();

    }

    public SSensorView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        initiateSettings();

    }

    public SSensorView(Context context, AttributeSet attrs) {

        super(context, attrs);

        initiateSettings();

    }

    public void changeColor(int color){

        this.color = color;

        invalidate();

    }

    protected void onDraw(Canvas c){

        super.onDraw(c);

        Paint p = new Paint();
        p.setColor(color);
        p.setAntiAlias(true);

        float startPoint = 0.0f;

        for(int i = 0; i < SENSOR_COUNT; ++i) {

            c.drawRect(startPoint,
                        sensorRatio.elementAt(i) * height,
                        startPoint + widthInterval,
                        height,
                        p);

            startPoint = startPoint + widthInterval;

        }

    }

    public void updateSensorUI(Vector<Float> vf){

        if(vf.size() == 8) {

            sensorRatio.clear();

            for (int i = 0; i < 8; ++i)
                sensorRatio.add(1.0f - vf.elementAt(i) / MAXIMUM_VALUE);

            invalidate();

        }

    }

    public void initiateSettings(){

        sensorRatio = new Vector<Float>();

        this.setLayoutParams(new ViewGroup.LayoutParams(0, 0));

        this.setMinimumHeight(0);
        this.setMinimumWidth(0);

    }

    public void setWidthAndHeight(Rect area){

        this.area = area;
        this.width = (int)area.width();
        this.height = (int)area.height();

        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = width;
        params.height = height;
        this.setLayoutParams(params);

        widthInterval = (float)width / (float)SENSOR_COUNT;

    }


}
