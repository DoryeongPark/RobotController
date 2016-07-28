package org.ollide.rosandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.os.Handler;
import android.widget.TextView;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

/*

    MainActivity

    Author      :   Doryeong Park
    Date        :   2016. 6. 27

    Description :   Class associated with main layout containing view and control
                    It is classified by three parts - Menu / Board / Joystick

 */
public class MainActivity extends RosActivity {

    /*
        Menu Variables

        Location            Upper part of main layout

        Description         Menu part contains components informing
                            entire state of Robot and View navigation

    ------------------------------------------------------------------------------------------------

        lvItems             Strings for navigation

        nav                 ListView component for navigation

        dl                  Navigation frame

        mb                  Menu Button

        csTv                TextView showing connection state

    ------------------------------------------------------------------------------------------------

    */

    private String[] lvItems = {"Control view", "Sonar view", "Laser view",
                                "Camera view","User settings", "User Theme",
                                "Disconnect", "Exit"};
    private ListView nav;
    private DrawerLayout dl;
    private ImageView mb;
    private TextView csTv;

     /*
        Board Variables

        Location            Middle part of main layout

        Description         Board part shows visualized sensor data
                            Only one of boards(board0 ~ board3) can be showing

    ------------------------------------------------------------------------------------------------

        infoboardArea       Fundamental frame of Board part

        board0              Board for control view

        board1              Board for sonic view

        board2              Board for laser view

        board3              Board for camera view

    ------------------------------------------------------------------------------------------------
    */

    private LinearLayout infoboardArea;

    /*

        Variables associated with Control view (board0)

    ------------------------------------------------------------------------------------------------

    angleTv             TextView displaying control angle (0 ~ 90)

    angleDirTv          TextView displaying angle direction ("Left"/"Center"/"Right")

    speedTv             TextView displaying control speed (0% ~ 100%)

    speedDirTv          TextView displaying linear direction ("Forward"/"Stopped"/"Backward")

    ------------------------------------------------------------------------------------------------

    */

    private LinearLayout board0;

    private TextView angleTv;
    private TextView angleDirTv;

    private TextView speedTv;
    private TextView speedDirTv;

     /*

        Variables associated with Sonic view (board1)

    ------------------------------------------------------------------------------------------------

    sSensorArea         Frame Sensor view will be on

    sSensorView         View which contains image visualizing 8 sonic float data

    sData               Sonar DataSet from Node

    sSensorFlag         true - Updates sSensorView on sSensorArea / false - Do nothing


    ------------------------------------------------------------------------------------------------

    */

    private LinearLayout board1;

    private LinearLayout sSensorArea;
    private SSensorView sSensorView;
    private Vector<Float> sData;
    private boolean sSensorFlag = false;

    /*

        Variables associated with Laser view (board2)

    ------------------------------------------------------------------------------------------------

    lSensorArea         Frame Laser view will be on

    lSensorView         View which contains image visualizing Laser scanning data

    lSensorFlag         true - Updates lSensorView on lSensorArea / false - Do nothing


    ------------------------------------------------------------------------------------------------

    */

    private LinearLayout board2;

    private LinearLayout lSensorArea;
    private LSensorView lSensorView;
    private boolean lSensorFlag = false;


    /*

        Variables associated with Camera view (board3)

    ------------------------------------------------------------------------------------------------

        cameraArea              Frame Bitmap Image will be on

    ------------------------------------------------------------------------------------------------

    */

    private LinearLayout board3;

    private ImageView cameraArea;


    /*
            Monitor

        Description     Small board displaying data of wheel speed and direction from android node

    ------------------------------------------------------------------------------------------------

        monitorArea     Frame containing monitor variables

    ------------------------------------------------------------------------------------------------

     */

    private LinearLayout monitorArea;

    /*
            Joystick Variables

        Location            Bottom part of main layout

        Description         Part containing joystick & area joystick can move


    ------------------------------------------------------------------------------------------------

        infoboardArea       Fundamental frame of Board part

        board0              Board for control view

        board1              Board for sonic view

        board2              Board for laser view

        board3              Board for camera view

    ------------------------------------------------------------------------------------------------

    */

    private LinearLayout ground;

    private Joystick js;

    private ImageView rotateRight;
    private ImageView rotateLeft;


     /*

        Dialogs

    ------------------------------------------------------------------------------------------------

        usd             Dialog which enables users to set adjusting angular sensitivity
                        and speed weight.

        td              Dialog which enables users to pick and adjust theme in program on list

        ced             Dialog which informs user disconnection from ros master

    ------------------------------------------------------------------------------------------------

    */

    private UserSettingDialog usd;
    private ThemeDialog td;
    private ConnectionErrorDialog ced;


     /*

            Variables associated with User Settings

    ------------------------------------------------------------------------------------------------

        imageSet            Storage for theme image resources' id

        userTheme           Current Theme object

        userAngularWeight   User value to set control angular weight

        userSpeedWEight     User value to set control linear weight

    ------------------------------------------------------------------------------------------------

    */

    private Vector<Integer> imageSet;

    private Theme userTheme;

    private float userAngularWeight = 0.75f;
    private float userLinearWeight = 1.0f;

    /*

      executedFirst   Flag used to make logic executed only one time

      true - Executable / false - Not Executable

    */

    private boolean executedFirst = true;

    /*

            Variables associated with Node connection

    ------------------------------------------------------------------------------------------------

        androidNode         Node object which will be registered to Ros Master

        nodeHandler         Handler object handling sensor data message

        uiHandler           Handler object handling others except sensor data message

        cTimer              Timer thread to determine whether it maintains connection or not

        timerFlag           true - Timer's running  /  false- Timer's not running

    ------------------------------------------------------------------------------------------------

    */

    private AndroidNode androidNode;

    private Handler nodeHandler;
    private Handler uiHandler;

    private ConnectionTimer cTimer;

    /**
     *      Constructor
     */
    public MainActivity() {

        super("WI_project", "WI_project");

    }

    /**
     *  OnCreate
     * @param savedInstanceState
     * @description Defines all methods be executed in order when MainActivity instance is being created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        initListView();

        initMenuButton();

        initRotateButton();

        loadImageSet();

        System.out.println("onCreate called ... ");

    }

    /**
     *  onPause
     *  @description Defines all methods be executed when home button is pressed
     */
    @Override
    public void onPause(){

        super.onPause();

        if(cTimer != null)
            cTimer.setPauseState(true);

    }

    /**
     *  onResume
     *  @description    Defines all methods be executed when application is resumed
     */
    @Override
    public void onResume(){

        super.onResume();

        if(cTimer != null)
            cTimer.setPauseState(false);

    }

    @Override
    public void onDestroy(){

        super.onDestroy();

        cTimer.shutDown();

        saveUserSettings();

        saveThemeSettings();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        //Executed Only one time

        if(executedFirst == true) {

            initHandler();

            initInstances();

            initUserSettings();

            initThemeSettings();

            initDialogs();

            System.out.println("onWindowFocusChanged called ... ");

        }

        executedFirst = false;

    }

    private void initInstances(){

        //Inform ground area(Absolute) to joystick
        Rect g = new Rect();
        ground.getGlobalVisibleRect(g);
        js.setAreaMovable(g);

    }

    private void initComponents(){

        /* ---------- Init menu and main parent components  ---------- */

        mb = (ImageView)findViewById(R.id.menu_button);

        dl = (DrawerLayout)findViewById(R.id.main_drawer);
        nav = (ListView)findViewById(R.id.main_lv);

        csTv = (TextView)findViewById(R.id.connection_state);

        infoboardArea = (LinearLayout)findViewById(R.id.infoboard_area);

        board0 = (LinearLayout)findViewById(R.id.infoboard0);
        board1 = (LinearLayout)findViewById(R.id.infoboard1);
        board2 = (LinearLayout)findViewById(R.id.infoboard2);
        board3 = (LinearLayout)findViewById(R.id.infoboard3);

        ground = (LinearLayout)findViewById(R.id.ground);

        rotateRight = (ImageView)findViewById(R.id.rotate_right_icon);
        rotateLeft = (ImageView)findViewById(R.id.rotate_left_icon);

         /* ---------- Variables associated with control ---------- */

        angleTv = (TextView)findViewById(R.id.info_angle);
        angleDirTv = (TextView)findViewById(R.id.info_direction1);

        speedTv = (TextView)findViewById(R.id.info_acceleration);
        speedDirTv = (TextView)findViewById(R.id.info_direction0);

        /* ---------- Init variables associated with sonic sensor---------- */

        sData = new Vector<Float>();

        for(int i = 0; i < 8; ++i)
            sData.add(0.0f);

        sSensorArea = (LinearLayout)findViewById(R.id.sonic_area);
        sSensorView = new SSensorView(this);

        /* ---------- Init variables associated with laser sensor ---------- */

        lSensorArea =(LinearLayout)findViewById(R.id.laser_area);
        lSensorView = new LSensorView(this);

        /* ---------- Init variables associated with camera ---------- */

        cameraArea = (ImageView)findViewById(R.id.camera_area);

        /* ---------- Init variables associated with monitor ---------- */

        monitorArea = (LinearLayout)findViewById(R.id.monitor_area);

         /* ---------- Initial arrangement of joystick  ---------- */

        js = new Joystick(this);

        js.setOnJoystickListener(new Joystick.JoystickListener() {

            @Override
            public void onMove(float angle, float angleDir, float acc, float accDir){

                angleTv.setText(String.format("%.2f", angle));
                speedTv.setText(String.format("%.2f", acc)  + "%");

                if(angleDir == Joystick.LEFT)
                    angleDirTv.setText("Left");

                else if(angleDir == Joystick.RIGHT)
                    angleDirTv.setText("Right");

                else
                    angleDirTv.setText("Center");


                if(accDir == Joystick.FORWARD)
                    speedDirTv.setText("Forward");

                else if(accDir == Joystick.BACKWARD)
                    speedDirTv.setText("Backward");

                else
                    speedDirTv.setText("Stopped");

            }

        });

        ground.addView(js);

    }


    private void initListView(){

        nav.setAdapter(new ArrayAdapter<String>(this, R.layout.list_textview, lvItems));
        nav.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView parent, View view,
                                    int position, long id){

                selectItem(position);

            }

            private void selectItem(int position){

                flagSetting(position);

                if(position == 0)
                    posControl();

                else if(position == 1)
                    posSonic();

                else if (position == 2)
                    posLaser();

                else if(position == 3)
                    posCamera();

                else if(position == 4)
                    posUserSettings();

                else if(position == 5)
                    posStyle();

                else if(position == 6)
                    posDisconnection();

                else if(position == 7)
                    posExit();

                dl.closeDrawer(nav);

            }

        });

    }

    private void flagSetting(int position){

        if(position == 1) {

            sSensorFlag = true;
            lSensorFlag = false;

        }
        else if(position == 2) {

            sSensorFlag = false;
            lSensorFlag = true;

        }
        else if(position == 4 || position == 5) {

            //Do nothing...

        }
        else{

            sSensorFlag = false;
            lSensorFlag = false;

        }

    }

    private void posControl(){

        board0.setVisibility(View.VISIBLE);
        board1.setVisibility(View.GONE);
        board2.setVisibility(View.GONE);
        board3.setVisibility(View.GONE);

        monitorArea.setVisibility(View.GONE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        7.0f));

    }

    private void posSonic(){

        board0.setVisibility(View.GONE);
        board1.setVisibility(View.VISIBLE);
        board2.setVisibility(View.GONE);
        board3.setVisibility(View.GONE);

        monitorArea.setVisibility(View.VISIBLE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        6.0f));

    }

    private void posLaser(){

        board0.setVisibility(View.GONE);
        board1.setVisibility(View.GONE);
        board2.setVisibility(View.VISIBLE);
        board3.setVisibility(View.GONE);

        monitorArea.setVisibility(View.VISIBLE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        6.0f));

    }

    private void posCamera(){

        board0.setVisibility(View.GONE);
        board1.setVisibility(View.GONE);
        board2.setVisibility(View.GONE);
        board3.setVisibility(View.VISIBLE);

        monitorArea.setVisibility(View.VISIBLE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        6.0f));

    }

    private void posUserSettings(){

        dl.closeDrawer(nav);
        usd.show();
        usd.setWeight(userAngularWeight, userLinearWeight);

        usd.setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog){

                Vector<Float> data = usd.getWeight();

                userAngularWeight = data.elementAt(0);
                userLinearWeight = data.elementAt(1);

                js.setWeight(userAngularWeight, userLinearWeight);

                System.out.println(userAngularWeight + " " + userLinearWeight);

            }
        });

    }

    private void posStyle(){

        dl.closeDrawer(nav);
        td.show();

    }

    private void posDisconnection(){

        if(td.isShowing())
            td.dismiss();

        csTv.setText("Connecting...");

        dl.closeDrawer(nav);

        posControl();

        cTimer.shutDown();

        super.killingNodes();

    }

    private void posExit(){

        this.finish();

    }

    private void initMenuButton(){

        mb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                dl.openDrawer(nav);

            }

        });

    }

    private void initRotateButton(){

       rotateRight.setOnTouchListener(new View.OnTouchListener() {

           Rect range = new Rect();

           public boolean onTouch(View v, MotionEvent e) {

               final int X = (int)e.getRawX();
               final int Y = (int)e.getRawY();

               rotateRight.getGlobalVisibleRect(range);

               if ((e.getAction() & MotionEvent.ACTION_MASK)
                       == MotionEvent.ACTION_MOVE) {

                   if(range.contains(X, Y)) {

                       js.setData(90.0f, Joystick.RIGHT, 0.0f, Joystick.FORWARD);

                   }

               }else{

                   js.setData(0.0f, Joystick.CENTER, 0.0f, Joystick.STOPPED);

               }

               return true;

           }

       });

        rotateLeft.setOnTouchListener(new View.OnTouchListener() {

            Rect range = new Rect();

            public boolean onTouch(View v, MotionEvent e) {

                final int X = (int)e.getRawX();
                final int Y = (int)e.getRawY();

                rotateLeft.getGlobalVisibleRect(range);

                if ((e.getAction() & MotionEvent.ACTION_MASK)
                        == MotionEvent.ACTION_MOVE) {

                    if(range.contains(X, Y)) {

                        js.setData(90.0f, Joystick.LEFT, 0.0f, Joystick.FORWARD);

                    }

                }else{

                    js.setData(0.0f, Joystick.CENTER, 0.0f, Joystick.STOPPED);

                }

                return true;

            }

        });

    }

    private void loadImageSet() {

        imageSet = new Vector<Integer>();

        for (int i = 0; i < 9; ++i) {

            String imgName = "color_design";
            imgName = imgName + String.valueOf(i);

            imageSet.add(getResources().getIdentifier(
                    imgName, "drawable", getPackageName()));

        }
    }

    private void initDialogs(){

        usd = new UserSettingDialog(this);
        td = new ThemeDialog(this, userTheme, imageSet);

    }

    private void initUserSettings(){

        try {

            FileInputStream is = openFileInput("UserWeightSettings.txt");

            byte[] byteArray =  new byte[is.available()];

            while(is.read(byteArray) != -1){}

            is.close();

            String cString = "";

            for(int i = 0; i < byteArray.length; ++i)
                cString = cString + (char)byteArray[i];

            System.out.println(cString);

            userAngularWeight = Float.parseFloat(cString.split("a")[0]);
            userLinearWeight = Float.parseFloat(cString.split("a")[1]);

            System.out.println("User setting applied by [" + userAngularWeight + ", " + userLinearWeight + "]");

            js.setWeight(userAngularWeight, userLinearWeight);

        } catch(Exception e){ e.printStackTrace(); }

    }

    private void saveUserSettings(){

        try {

            deleteFile("UserWeightSettings.txt");

            FileOutputStream os =  openFileOutput("UserWeightSettings.txt", Context.MODE_PRIVATE);

            System.out.println("User setting saved as [" + userAngularWeight + ", " + userLinearWeight + "]");

            os.write(String.valueOf(userAngularWeight).getBytes());
            os.write('a');
            os.write(String.valueOf(userLinearWeight).getBytes());

            os.close();

        } catch(Exception e){ e.printStackTrace(); }

    }

    private void initThemeSettings(){

        userTheme = new Theme(this, js);

        try {

            Vector<Integer> colors = new Vector<Integer>();

            FileInputStream is = openFileInput("UserThemeSettings.txt");

            byte[] byteArray =  new byte[is.available()];

            while(is.read(byteArray) != -1){}

            is.close();

            String cString = "";

            for(int i = 0; i < byteArray.length; ++i)
                cString = cString + (char)byteArray[i];

            System.out.println(cString);

            for(int i = 0; i < 3; ++i)
                colors.add(Integer.parseInt(cString.split("a")[i]));

            userTheme.setCurrentTheme(colors);
            userTheme.adjustCurrentTheme();


        } catch(Exception e){ e.printStackTrace(); }


    }

    private void saveThemeSettings(){

        try {

            deleteFile("UserThemeSettings.txt");

            FileOutputStream os =  openFileOutput("UserThemeSettings.txt", Context.MODE_PRIVATE);

            for(int i = 0; i < 3; ++i) {

                os.write(String.valueOf(userTheme.getCurrentTheme().elementAt(i)).getBytes());
                os.write('a');

            }

            os.close();

        } catch(Exception e){ e.printStackTrace(); }

    }

    private void initHandler(){

        nodeHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){

                switch(msg.what) {

                    case 0:

                        break;

                    case 1:

                        if(sSensorFlag == true) {

                            sSensorArea.removeAllViewsInLayout();
                            sData.setElementAt((float) msg.obj, msg.arg1);
                            sSensorView.updateSensorUI(sData);

                            Rect r = new Rect();
                            sSensorArea.getGlobalVisibleRect(r);
                            sSensorView.setWidthAndHeight(r);

                            sSensorArea.addView(sSensorView);

                        }

                        break;

                    case 2:

                        if(lSensorFlag == true) {

                            lSensorArea.removeAllViewsInLayout();

                            lSensorView.new_msg((sensor_msgs.LaserScan)msg.obj);
                            lSensorArea.addView(lSensorView);

                        }

                        break;

                    case 3:

                        cameraArea.setImageBitmap((Bitmap)msg.obj);

                        break;

                    /*

                        ConnectionTimer creation & update

                        from AndroidNode - startConnectionTimer() & resetConnectionTimer()

                     */
                    case 4:

                        if(msg.arg1 == 0) {

                            if(!isPublicMaster()) {

                                cTimer = new ConnectionTimer(uiHandler);
                                cTimer.start();

                            }

                        } else if(msg.arg1 == 1) {

                            csTv.setText("Connected ");
                            cTimer.getResponse();

                        }

                        break;

                }

            }
        };

        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what) {

                    case 0:

                        csTv.setText("Connected ");
                        break;

                    case 1:

                        csTv.setText("Connecting...");
                        break;

                    case 2:

                        if(msg.arg1 == 0) {

                            ced = new ConnectionErrorDialog(MainActivity.this,
                                    uiHandler,
                                    ConnectionErrorDialog.UNABLE_TO_REGISTER);

                            ced.setOnDismissListener(new DialogInterface.OnDismissListener(){
                                @Override
                                public void onDismiss(DialogInterface dialog) {

                                    posDisconnection();

                                }

                            });

                            ced.show();

                            ced.setErrorUri((String)msg.obj);

                        }

                        if(msg.arg1 == 1) {

                            ced = new ConnectionErrorDialog(MainActivity.this,
                                    uiHandler,
                                    ConnectionErrorDialog.DISCONNECTED_AFTER_REGISTERED);

                            ced.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {

                                    posDisconnection();

                                }

                            });

                            ced.show();

                        }

                        break;

                    case 3:

                        if(ced != null) {

                            if (ced.isShowing()) {

                                ced.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {}

                                });

                                ced.dismiss();

                            }

                        }

                        break;

                }
            }
        };

    }



    /*  Node creation */

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        while(nodeHandler == null || uiHandler == null){}

        androidNode = new AndroidNode(js, nodeHandler, uiHandler);

        nodeMainExecutor.execute(androidNode, nodeConfiguration);

        System.out.println("init called ... ");

    }


}
