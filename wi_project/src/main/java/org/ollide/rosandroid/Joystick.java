package org.ollide.rosandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by Felix on 2016-06-24.
 */
public class Joystick extends ImageView{

    private final float LEFT = 1.0f;
    private final float CENTER = 0.0f;
    private final float RIGHT = -1.0f;

    private final float FORWARD = 1.0f;
    private final float STOPPED = 0.0f;
    private final float BACKWARD = -1.0f;

    private int width;
    private int height;

    private int color = Color.rgb(254, 196, 54);

    private int centerX;
    private int centerY;

    private Rect area;

    private TextView acc;
    private TextView accDir;

    private TextView angle;
    private TextView angleDir;

    private int accRatio;

    //-- Monitor components

    private TextView mAngleDir;
    private TextView mAcc;

    //-- Data to be sent
    private float rawAngle = 0.0f;
    private float rawDir0 = 0.0f;

    private float rawAcc = 0.0f;
    private float rawDir1 = 0.0f;

    public Joystick(Context context){

        super(context);
        initiateSettings(context);

    }

    public Joystick(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        initiateSettings(context);

    }

    public Joystick(Context context, AttributeSet attrs) {

        super(context, attrs);
        initiateSettings(context);

    }

    public void setLimitation(Rect area){

        this.area = area;

        centerX = area.centerX();
        centerY = area.centerY();
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

        c.drawCircle(width/2, height/2, 100, p);

    }

    private void initiateSettings(Context context) {

        //Layout size setting
        this.setLayoutParams(new ViewGroup.LayoutParams(200, 200));

        width = 200;
        height = 200;

        this.setMinimumHeight(200);
        this.setMinimumWidth(200);

        this.setMaxHeight(200);
        this.setMaxWidth(200);

        //Connection setting with TextView
        View rootView = ((Activity)context).getWindow()
                .getDecorView().findViewById(android.R.id.content);

        acc = (TextView)rootView.findViewById(R.id.info_acceleration);
        accDir = (TextView)rootView.findViewById(R.id.info_direction0);

        angle = (TextView)rootView.findViewById(R.id.info_angle);
        angleDir = (TextView)rootView.findViewById(R.id.info_direction1);

        mAngleDir = (TextView)rootView.findViewById(R.id.info_direction2);
        mAcc = (TextView)rootView.findViewById(R.id.info_wheelspeed);

        //Touch event setting
        this.setOnTouchListener(new OnTouchListener(){

                    @Override
                    public boolean onTouch(View v, MotionEvent e){

                final int X = (int)e.getRawX();
                final int Y = (int)e.getRawY();

                switch(e.getAction() & MotionEvent.ACTION_MASK){

                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_MOVE:

                        float translateX = X - centerX;
                        float translateY = Y - centerY;

                        if(isInside(X, Y)) {

                            Joystick.this.setTranslationX(translateX);
                            Joystick.this.setTranslationY(translateY);

                            //Calculate angle
                            float angleCalculated = calculateAngle(X, Y);
                            angleCalculated = parseAngle(translateX, translateY, angleCalculated);

                            float dir1 = determineAccDir(translateY);
                            float dir0 = determineAngleDir(translateX);

                            acc.setText(String.valueOf(accRatio) + "%");
                            mAcc.setText(String.valueOf(accRatio * dir1)  +"%");
                            angle.setText(String.format("%.2f",angleCalculated));

                            //-- Set data to be sent
                            setData(angleCalculated, dir0, (float) accRatio, dir1);


                        }else{

                            Vector<Double> msgBox = new Vector<Double>();

                            returnMaximumPoint(X, Y, msgBox);

                            float maxTranslateX = (float)(msgBox.elementAt(0) - centerX);
                            float maxTranslateY = (float)(msgBox.elementAt(1) - centerY);

                            //Calculate angle
                            float angleCalculated = calculateAngle(X, Y);
                            angleCalculated = parseAngle(translateX, translateY, angleCalculated);

                            float dir1 = determineAccDir(maxTranslateY);
                            float dir0 = determineAngleDir(maxTranslateX);

                            acc.setText(100 + "%");
                            angle.setText(String.format("%.2f", angleCalculated));

                            Joystick.this.setTranslationX(maxTranslateX);
                            Joystick.this.setTranslationY(maxTranslateY);

                            //-- Set data to be sent
                            setData(angleCalculated, dir0, 100.0f, dir1);

                        }

                        break;

                    case MotionEvent.ACTION_UP:

                        Joystick.this.setTranslationX(0);
                        Joystick.this.setTranslationY(0);

                        accDir.setText("Stopped");
                        mAcc.setText("0%");
                        acc.setText("0%");

                        angleDir.setText("Center");
                        mAngleDir.setText("Center");
                        angle.setText("0");

                        setData(0.0f, CENTER, 0.0f, STOPPED);

                        break;

                    default:
                        break;
                }

                return true;

            }
        });

    }

    private float calculateAngle(int pX, int pY){

        int dx, dy;
        int ax, ay;

        float t;

        dx = centerX - pX;
        ax = Math.abs(dx);

        dy = centerY - pY;
        ay = Math.abs(dy);

        t = (ax + ay == 0) ? 0 : (float)dy / (ax + ay);

        if (dx < 0)
            t = 2 - t;
        else if (dy < 0)
            t = 4 + t;

        t = t * 90.0f;

        return t;
    }

    private float parseAngle(float translateX, float translateY, float angle){

        if(translateY < 0 && translateX < 0)
            angle = 90 - angle;

        else if(translateY < 0 && translateX > 0)
            angle = angle - 90;

        else if(translateY > 0 && translateX > 0)
            angle= 270 - angle;

        else if(translateY > 0 && translateX < 0)
            angle = angle - 270;

        else
            angle = 0.0f;

        return angle;
    }

    private float determineAccDir(float translateY){

        //Update information of Y
        if(translateY < 0) {
            accDir.setText("Forward");
            return FORWARD;
        }

        else if(translateY > 0) {
            accDir.setText("Backward");
            return BACKWARD;
        }

        else {
            accDir.setText("Stopped");
            return STOPPED;
        }

    }

    private float determineAngleDir(float translateX){

        //Update information of X
        if(translateX < 0) {

            mAngleDir.setText("Left");
            angleDir.setText("Left");
            return LEFT;

        } else if(translateX > 0) {

            mAngleDir.setText("Right");
            angleDir.setText("Right");
            return RIGHT;

        } else {

            mAngleDir.setText("Center");
            angleDir.setText("Center");
            return CENTER;

        }
    }

    private boolean isInside(int x, int y){

        double dx = x - centerX;
        double dy = y - centerY;

        double distance = Math.sqrt(Math.pow(dx, 2.0d) + Math.pow(dy, 2.0d));
        double radius = (area.height()/2 - height/2);

        accRatio = (int)(distance / radius * 100);

        if(distance < area.height()/2 - height/2)
            return true;

        else
            return false;

    }

    private void returnMaximumPoint(int x, int y, Vector<Double> msgBox){

        double dx = x - centerX;
        double dy = y - centerY;

        double radian = Math.atan2(dy, dx);
        double radius = (area.height()/2 - height/2);

        msgBox.add(centerX + Math.cos(radian) * radius);
        msgBox.add(centerY + Math.sin(radian) * radius);

    }

    public void setData(float vX, float dir0, float vY, float dir1){

        this.rawAngle = vX;
        this.rawDir0 = dir0;

        this.rawAcc = vY;
        this.rawDir1 = dir1;

    }

    public float getRawAngle(){

        return rawAngle;

    }

    public float getRawDirection0(){

        return rawDir0;

    }

    public float getRawAcc(){

        return rawAcc;

    }

    public float getRawDirection1(){

        return rawDir1;

    }

    public String toString(){

        return rawAngle + " " +
                rawDir0 + " " +
                rawAcc + " " +
                rawDir1 + " ";

    }
}
