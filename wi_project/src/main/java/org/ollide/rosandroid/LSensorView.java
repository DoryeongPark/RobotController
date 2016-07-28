package org.ollide.rosandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Notebook on 2016-07-13.
 */
public class LSensorView extends View{

    private boolean mode = false;
    private float old_dist = 1f;

    private Paint paintLine = new Paint();
    private Paint paintArc = new Paint();
    private Paint paintBackground = new Paint();

    private sensor_msgs.LaserScan scan_msg;
    private PointF near = null;
    private RectF rf = null;

    private float max_val = 0;
    private float width;
    private float height;

    public LSensorView(Context c){

        super(c);

        paintLine.setColor(Color.RED);
        paintLine.setStrokeWidth(2);

        paintArc.setColor(Color.BLACK);
        paintArc.setTextSize(40f);

        paintBackground.setColor(Color.WHITE);
    }
    public void new_msg(sensor_msgs.LaserScan nm){

        if(nm != null){

            this.scan_msg = nm;

            if(max_val == 0)
                max_val = scan_msg.getRangeMax();

            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas){

        if(this.scan_msg != null) {

            if(width ==0 && height == 0){

                width = canvas.getWidth();
                height = canvas.getHeight();

            }

            if(rf == null){

                rf = new RectF(0, 0, width, height * 2);

            }

            rf.set(0, 0, width, height * 2);
            canvas.drawArc(rf, -180, 180, true, paintArc);

            rf.set(3, 3, width - 3, height * 2 -3);
            canvas.drawArc(rf,-180,180,true, paintBackground);

            rf.set(width * 0.1f, height * 0.2f, width * 0.9f, height * 1.8f);
            canvas.drawArc(rf, -180, 180, true, paintArc);

            rf.set(width*0.1f+3, height*0.2f+3, width*0.9f-3, height*1.8f-3);
            canvas.drawArc(rf, -180, 180, true, paintBackground);

            rf.set(width * 0.2f, height * 0.4f, width * 0.8f, height * 1.6f);
            canvas.drawArc(rf, -180, 180, true, paintArc);

            rf.set(width * 0.2f + 3, height * 0.4f + 3, width * 0.8f - 3, height * 1.6f - 3);
            canvas.drawArc(rf, -180, 180, true, paintBackground);

            rf.set(width * 0.3f, height * 0.6f, width * 0.7f, height * 1.4f);
            canvas.drawArc(rf, -180, 180, true, paintArc);

            rf.set(width*0.3f + 3, height * 0.6f + 3, width * 0.7f - 3, height * 1.4f - 3);
            canvas.drawArc(rf, -180, 180, true, paintBackground);

            rf.set(width*0.4f, height * 0.8f, width * 0.6f, height * 1.2f);
            canvas.drawArc(rf, -180, 180, true, paintArc);

            rf.set(width * 0.4f + 3, height * 0.8f + 3, width * 0.6f - 3, height * 1.2f - 3);
            canvas.drawArc(rf, -180, 180, true, paintBackground);

            canvas.drawText(String.valueOf((int)max_val), width / 2,40, paintArc);
            canvas.drawText(String.valueOf((int)(max_val * 0.8f)), width / 2, height * 0.2f + 40, paintArc);
            canvas.drawText(String.valueOf((int)(max_val * 0.6f)), width / 2, height * 0.4f + 40, paintArc);
            canvas.drawText(String.valueOf((int)(max_val * 0.4f)), width / 2, height * 0.6f + 40, paintArc);
            canvas.drawText(String.valueOf((int)(max_val * 0.2f)), width / 2, height * 0.8f + 40, paintArc);

            float[] lineEndPoints = new float[scan_msg.getRanges().length * 4];
            int numEndPoints = 0;
            float angle = scan_msg.getAngleMin();

            for (float range : scan_msg.getRanges()) {
                // Only process ranges which are in the valid range.
                if (scan_msg.getRangeMin() <= range && range <= scan_msg.getRangeMax()) {

                    if(near == null){

                        near = new PointF(width / 2f, height);

                    }

                    PointF far = new PointF((width / 2f) - (float)Math.sin(angle) * (width / 2f) * (range / max_val),
                            height - (float)Math.cos(angle) * (width / 2) * (range / max_val));

                    lineEndPoints[numEndPoints++] = near.x;
                    lineEndPoints[numEndPoints++] = near.y;
                    lineEndPoints[numEndPoints++] = far.x;
                    lineEndPoints[numEndPoints++] = far.y;

                }

                angle += scan_msg.getAngleIncrement();

            }

            canvas.drawLines(lineEndPoints, 0, numEndPoints, paintLine);

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){

        switch(e.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_DOWN:    //첫번째 손가락 터치
                break;

            case MotionEvent.ACTION_MOVE:   // 드래그 중이면, 이미지의 X,Y값을 변환시키면서 위치 이동.

                if (mode == true) {    // 핀치줌 중이면, 이미지의 거리를 계산해서 확대를 한다.

                    float dist = spacing(e);

                    if (dist - old_dist > 20) {
                        // zoom in
                        max_val = max_val * 0.99f;

                    } else if(old_dist - dist > 20) {// zoom out

                        max_val = max_val * 1.01f;

                    }
                }

                break;

            case MotionEvent.ACTION_UP:    // 첫번째 손가락을 떼었을 경우

            case MotionEvent.ACTION_POINTER_UP:  // 두번째 손가락을 떼었을 경우

                mode = false;

                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                mode = true;
                old_dist = spacing(e);
                break;

            case MotionEvent.ACTION_CANCEL:


            default :

                break;

        }

        return true;

    }

    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);

    }

}
