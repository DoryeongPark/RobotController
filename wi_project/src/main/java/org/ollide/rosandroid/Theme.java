package org.ollide.rosandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by Felix on 2016-06-27.
 */
public class Theme implements Serializable {

    static final int BLACK = Color.rgb(48, 48, 48);
    static final int BLUE = Color.rgb(0, 0, 204);
    static final int GREEN = Color.rgb(102, 204, 102);
    static final int ORANGE = Color.rgb(255, 202, 29);
    static final int RED = Color.rgb(255, 51, 51);
    static final int PURPLE = Color.rgb(153, 51, 155);
    static final int WHITE = Color.rgb(255, 255, 255);

    /*
        Colors

       color0 : Boards / Joystick
       color1 : Background
       color2 : Texts

     */

    private int color0;
    private int color1;
    private int color2;

    //----- Color 0 -----

    //Menu Components
    private ImageView menu_button;
    private TextView battery_state;
    private TextView connection_state;

    //Board0 Components
    private LinearLayout board0_row0;
    private LinearLayout board0_row1;

    //Board1 Components
    private LinearLayout sonicArea;

    private Vector<View> sensor_bars;

    //Board2 Components

    //Monitor Components
    private LinearLayout monitor_row0;

    //Joystick
    private Joystick js;

    //Rotate icons
    private ImageView rotateLeft;
    private ImageView rotateRight;

    //----- Color 1 -----

    //Area components
    private LinearLayout menu_area;
    private LinearLayout ground_area;

    //----- Color 2 -----

    //All texts
    private Vector<TextView> texts;

    //Boards
    private LinearLayout board0;
    private View board0_line;

    private LinearLayout board1;

    private LinearLayout board2;

    private LinearLayout monitor_area;

    public Theme(Context context, Joystick js){

        texts = new Vector<TextView>();
        sensor_bars = new Vector<View>();

        initComponents(context);

        this.js = js;

    };

    public Theme(Context context, Joystick js, int color0, int color1, int color2){

        texts = new Vector<TextView>();

        initComponents(context);

        this.js = js;

        this.color0 = color0;
        this.color1 = color1;
        this.color2 = color2;

    }

    public Vector<Integer> getCurrentTheme(){

        Vector<Integer> result = new Vector<Integer>();

        result.add(color0);
        result.add(color1);
        result.add(color2);

        return result;
    }

    public void setCurrentTheme(int color0, int color1, int color2){

        this.color0 = color0;
        this.color1 = color1;
        this.color2 = color2;

    }

    public void setCurrentTheme(Vector<Integer> colors){

        color0 = colors.elementAt(0);
        color1 = colors.elementAt(1);
        color2 = colors.elementAt(2);

    }

    private void initComponents(Context context){

        //Connection setting with TextView
        View rootView = ((Activity)context).getWindow()
                .getDecorView().findViewById(android.R.id.content);

        menu_area = (LinearLayout)rootView.findViewById(R.id.menu_area);
        ground_area = (LinearLayout)rootView.findViewById(R.id.ground_area);

        menu_button = (ImageView)rootView.findViewById(R.id.menu_button);
        battery_state = (TextView)rootView.findViewById(R.id.battery_state);

        connection_state = (TextView)rootView.findViewById(R.id.connection_state);

        //Text components
        texts.add((TextView)rootView.findViewById(R.id.lbl_direction0));
        texts.add((TextView)rootView.findViewById(R.id.info_direction0));
        texts.add((TextView)rootView.findViewById(R.id.lbl_acceleration));
        texts.add((TextView)rootView.findViewById(R.id.info_acceleration));
        texts.add((TextView)rootView.findViewById(R.id.lbl_direction1));
        texts.add((TextView)rootView.findViewById(R.id.info_direction1));
        texts.add((TextView)rootView.findViewById(R.id.lbl_angle));
        texts.add((TextView)rootView.findViewById(R.id.info_angle));

        texts.add((TextView)rootView.findViewById(R.id.lbl_direction2));
        texts.add((TextView)rootView.findViewById(R.id.info_direction2));
        texts.add((TextView)rootView.findViewById(R.id.lbl_wheelspeed));
        texts.add((TextView)rootView.findViewById(R.id.info_wheelspeed));

        //Board0 components
        board0 = (LinearLayout)rootView.findViewById(R.id.infoboard0);
        board0_row0 = (LinearLayout)rootView.findViewById(R.id.infoboard0_row0);
        board0_row1 = (LinearLayout)rootView.findViewById(R.id.infoboard0_row1);

        //Board1 components
        board1 = (LinearLayout)rootView.findViewById(R.id.infoboard1);
        sonicArea = (LinearLayout)rootView.findViewById(R.id.sonic_area);

        //Board2 components
        board2 = (LinearLayout)rootView.findViewById(R.id.infoboard2);

        //Monitor components
        monitor_area = (LinearLayout)rootView.findViewById(R.id.monitor_area);
        monitor_row0 = (LinearLayout)rootView.findViewById(R.id.monitor_row0);

        //Lines
        board0_line = (View)rootView.findViewById(R.id.board0_line);

        //Rotation icons
        rotateLeft = (ImageView)rootView.findViewById(R.id.rotate_left_icon);
        rotateRight = (ImageView)rootView.findViewById(R.id.rotate_right_icon);
    }

    public void adjustCurrentTheme(){

        // Color0
        js.changeColor(color0);
        battery_state.setTextColor(color0);
        connection_state.setTextColor(color0);
        board0_row0.setBackgroundColor(color0);
        board0_row1.setBackgroundColor(color0);

        sonicArea.setBackgroundColor(color0);

        monitor_row0.setBackgroundColor(color0);

        changeIconsColor();

        // Color1
        menu_area.setBackgroundColor(color1);
        ground_area.setBackgroundColor(color1);

        // Color2
        for(int i = 0; i < texts.size(); ++i)
            texts.elementAt(i).setTextColor(color2);

        board0.setBackgroundColor(color2);
        board0_line.setBackgroundColor(color2);

        board1.setBackgroundColor(color2);
        board2.setBackgroundColor(color2);

        monitor_area.setBackgroundColor(color2);

    }

    private void changeIconsColor(){

        if(color0 == WHITE) {

            menu_button.setImageResource(R.drawable.menu_icon_white);

            rotateRight.setImageResource(R.drawable.white_rotate0);
            rotateLeft.setImageResource(R.drawable.white_rotate1);

        }
        else if(color0 == BLUE) {

            menu_button.setImageResource(R.drawable.menu_icon_blue);

            rotateRight.setImageResource(R.drawable.blue_rotate0);
            rotateLeft.setImageResource(R.drawable.blue_rotate1);

        }
        else if(color0 == GREEN) {

            menu_button.setImageResource(R.drawable.menu_icon_green);

            rotateRight.setImageResource(R.drawable.green_rotate0);
            rotateLeft.setImageResource(R.drawable.green_rotate1);

        }
        else if(color0 == ORANGE) {

            menu_button.setImageResource(R.drawable.menu_icon_orange);

            rotateRight.setImageResource(R.drawable.yellow_rotate0);
            rotateLeft.setImageResource(R.drawable.yellow_rotate1);

        }
        else if(color0 == RED) {

            menu_button.setImageResource(R.drawable.menu_icon_red);

            rotateRight.setImageResource(R.drawable.red_rotate0);
            rotateLeft.setImageResource(R.drawable.red_rotate1);

        }
        else if(color0 == PURPLE) {

            menu_button.setImageResource(R.drawable.menu_icon_purple);

            rotateRight.setImageResource(R.drawable.purple_rotate0);
            rotateLeft.setImageResource(R.drawable.purple_rotate1);

        }
        else {

            menu_button.setImageResource(R.drawable.menu_icon_black);

            rotateRight.setImageResource(R.drawable.black_rotate0);
            rotateLeft.setImageResource(R.drawable.black_rotate1);

        }
    }


}
