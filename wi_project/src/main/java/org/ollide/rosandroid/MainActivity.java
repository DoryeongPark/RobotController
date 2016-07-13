package org.ollide.rosandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

public class MainActivity extends RosActivity {

     /* ---------- Variables : menu and main components  ---------- */

    private String[] lvItems = {"Control view", "Sonic view",
                                "Camera view", "Style",
                                "Disconnect", "Exit"};
    private ListView lv;

    private DrawerLayout dl;

    private ImageView mb;

    private LinearLayout infoboardArea;

    private LinearLayout board0;
    private LinearLayout board1;
    private LinearLayout board2;

    private LinearLayout ground_area;
    private LinearLayout ground;

    private Joystick js;

    private ImageView rotateRight;
    private ImageView rotateLeft;


    /* ---------- Dialog ---------- */

    private StyleDialog sd;

    /* ---------- Variables associated with sensor thread ---------- */

    private LinearLayout sSensorArea;

    private SSensorView sSensorView;

    private Vector<Float> sData;

    private Vector<View> sSensors;
    private Vector<View> sensorBars;

    private boolean sSensorFlag = false;

    /*
    private Runnable sonicSensorThread;
    private boolean sonicSensorFlag = false;*/

    /* ---------- Variables associated with camera ---------- */

    private ImageView cameraArea;

      /* ---------- Variables associated with monitor ---------- */

    private LinearLayout monitorArea;

     /* ---------- Storage of image files' ids ---------- */

    private Vector<Integer> imageSet;

    /* ---------- Variables used to initiate user settings ---------- */

    private ColorAdjuster ca;

    /* ---------- MainActivity Flag set ---------- */

    private boolean resumeFirst = true;

    /* ---------- Node Connections ----------- */

    RosNode rosNode;

    Handler nodeHandler;

    public MainActivity() {

        super("WI_project", "WI_project");

    }

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

    @Override
    public void onResume(){

        super.onResume();

    }

    @Override
    public void onDestroy(){

        super.onDestroy();

        saveColorSettings();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        //Excuted Only one time

        if(resumeFirst == true) {

            initHandler();

            initInstances();

            initSettings();

            createDialog();

            System.out.println("onWindowFocusChanged called ... ");

        }

        resumeFirst = false;

    }

    private void initInstances(){

        //Inform ground area(Absolute) to joystick
        Rect g = new Rect();
        ground.getGlobalVisibleRect(g);
        js.setLimitation(g);

    }

    private void initComponents(){

        /* ---------- Init menu and main parent components  ---------- */

        mb = (ImageView)findViewById(R.id.menu_button);

        dl = (DrawerLayout)findViewById(R.id.main_drawer);
        lv = (ListView)findViewById(R.id.main_lv);

        infoboardArea = (LinearLayout)findViewById(R.id.infoboard_area);

        board0 = (LinearLayout)findViewById(R.id.infoboard0);
        board1 = (LinearLayout)findViewById(R.id.infoboard1);
        board2 = (LinearLayout)findViewById(R.id.infoboard2);

        ground_area = (LinearLayout)findViewById(R.id.ground_area);
        ground = (LinearLayout)findViewById(R.id.ground);

        rotateRight = (ImageView)findViewById(R.id.rotate_right_icon);
        rotateLeft = (ImageView)findViewById(R.id.rotate_left_icon);

        /* ---------- Init variables associated with sonic sensor---------- */

        sData = new Vector<Float>();

        for(int i = 0; i < 8; ++i)
            sData.add(0.0f);

        sSensorArea = (LinearLayout)findViewById(R.id.sensor_area);
        sSensorView = new SSensorView(this);

        /* ---------- Init variables associated with camera ---------- */

        cameraArea = (ImageView)findViewById(R.id.camera_area);

        /* ---------- Init variables associated with monitor ---------- */

        monitorArea = (LinearLayout)findViewById(R.id.monitor_area);

         /* ---------- Initial arrangement of joystick  ---------- */

        js = new Joystick(this);
        ground.addView(js);

    }


    private void initListView(){

        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_textview, lvItems));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView parent, View view,
                                    int position, long id){

                selectItem(position);

            }

            private void selectItem(int position){

                if(position == 0)

                    posControl();

                else if(position == 1)

                    posSonic();

                else if (position == 2)

                    posCamera();

                else if(position == 3)

                    posStyle();

                else if(position == 4)

                    posDisconnection();

                else if(position == 5)

                    posExit();


                dl.closeDrawer(lv);
            }

        });

    }

    public void posControl(){

        sSensorFlag = false;

        board0.setVisibility(View.VISIBLE);
        board1.setVisibility(View.GONE);
        board2.setVisibility(View.GONE);

        monitorArea.setVisibility(View.GONE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        7.0f));

    }

    public void posSonic(){

        sSensorFlag = true;

        board0.setVisibility(View.GONE);
        board1.setVisibility(View.VISIBLE);
        board2.setVisibility(View.GONE);

        monitorArea.setVisibility(View.VISIBLE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        6.0f));

    }

    public void posCamera(){

        sSensorFlag = false;

        board0.setVisibility(View.GONE);
        board1.setVisibility(View.GONE);
        board2.setVisibility(View.VISIBLE);

        monitorArea.setVisibility(View.VISIBLE);
        infoboardArea.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        6.0f));

    }

    public void posStyle(){

        sSensorFlag = false;

        dl.closeDrawer(lv);
        sd.show();

    }

    public void posDisconnection(){

        sSensorFlag = false;

        posControl();
        super.restart();

    }

    public void posExit(){

        this.finish();

    }


    private void initMenuButton(){

        mb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                dl.openDrawer(lv);

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

                       js.setData(90.0f, -1.0f, 0.0f, 1.0f);

                   }

               }else{

                   js.setData(0.0f, 0.0f, 0.0f, 0.0f);

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

                        js.setData(90.0f, 1.0f, 0.0f, 1.0f);

                    }

                }else{

                    js.setData(0.0f, 0.0f, 0.0f, 0.0f);

                }

                return true;

            }

        });

    }

    private void loadImageSet() {

        imageSet = new Vector<Integer>();

        for (int i = 0; i < 18; ++i) {

            String imgName = "color_design";
            imgName = imgName + String.valueOf(i);

            imageSet.add(getResources().getIdentifier(
                    imgName, "drawable", getPackageName()));

        }
    }

    private void createDialog(){

        sd = new StyleDialog(this, ca, imageSet);

    }


    private void initSettings(){

        ca = new ColorAdjuster(this, js);

        try {

            Vector<Integer> colors = new Vector<Integer>();

            FileInputStream is = openFileInput("UserSettings.txt");

            byte[] byteArray =  new byte[is.available()];

            while(is.read(byteArray) != -1){}

            is.close();

            String cString = "";

            for(int i = 0; i < byteArray.length; ++i)
                cString = cString + (char)byteArray[i];

            for(int i = 1; i < 4; ++i)
                colors.add(Integer.parseInt('-' + cString.split("-")[i]));

            ca.setCurrentColors(colors);
            ca.adjustColors();


        } catch(Exception e){ e.printStackTrace(); }


    }

    private void saveColorSettings(){

        try {

            deleteFile("UserSettings.txt");

            FileOutputStream os =  openFileOutput("UserSettings.txt", Context.MODE_PRIVATE);

            for(int i = 0; i < 3; ++i)
                os.write(String.valueOf(ca.getCurrentColors().elementAt(i)).getBytes());

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

                        cameraArea.setImageBitmap((Bitmap)msg.obj);
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

        rosNode = new RosNode(this, nodeHandler, js);

        nodeMainExecutor.execute(rosNode, nodeConfiguration);

        System.out.println("init called ... ");

    }


}
