package org.ollide.rosandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.Vector;

import geometry_msgs.Twist;

/**
 * Created by Felix on 2016-07-05.
 */
public class RosNode extends AbstractNodeMain implements NodeMain {

    private static final java.lang.String TAG = RosNode.class.getSimpleName();

    //----- Variables associated with Velocity publisher & subscriber -----

    private float direction0;
    private float direction1;

    private float velocityX;
    private float velocityY;

    private geometry_msgs.Twist veloPubData;

    //----- Variables associated with Sonic sensor -----


    //----- Variables associated with Compressed Image -----

    private Bitmap cameraSubData;

    //----- Variables associated with Connection with Android components -----

    Context context;
    private Joystick js;
    private Handler nodeHandler;

    //------Dialog for connection error -----

    ConnectionErrorDialog ced;

    public RosNode(Context context,Handler nodeHandler, Joystick js){

        this.context = context;
        this.js = js;
        this.nodeHandler = nodeHandler;

    }

    @Override
    public GraphName getDefaultNodeName() {

        return GraphName.of("FelixNode");

    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        final Publisher<geometry_msgs.Twist> veloPublisher = connectedNode.newPublisher(
                GraphName.of("cmd_vel"), geometry_msgs.Twist._TYPE);

        final Subscriber<sensor_msgs.CompressedImage> cameraSubscriber  = connectedNode.newSubscriber(
                GraphName.of("camera/rgb/image_raw/compressed"), sensor_msgs.CompressedImage._TYPE);

        Vector<Subscriber<sensor_msgs.Range>> sSensorSubscribers =
                new Vector<Subscriber<sensor_msgs.Range>>();

        for(int i = 0; i < 8; ++i) {
            Subscriber<sensor_msgs.Range> sensorSubscriber = connectedNode.newSubscriber(
                    GraphName.of("p1_sonar_" + (i + 1)), sensor_msgs.Range._TYPE);

            sSensorSubscribers.add(sensorSubscriber);
        }

        // ---------- Velocity publisher ----------

        final CancellableLoop veloPubLoop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {

                parseVeloPublishingData();

                veloPubData = connectedNode.
                        getTopicMessageFactory().newFromType(Twist._TYPE);

                veloPubData.getAngular().setZ(velocityX);
                veloPubData.getLinear().setX(velocityY);

                veloPublisher.publish(veloPubData);

                Thread.sleep(100);
            }

        };

        connectedNode.executeCancellableLoop(veloPubLoop);

        // ---------- 8 Sonic sensor subscribers ----------

        for(int i = 0; i < sSensorSubscribers.size(); ++i){

            final int in = i;

            sSensorSubscribers.elementAt(i).addMessageListener(new MessageListener<sensor_msgs.Range>(){

                public void onNewMessage(sensor_msgs.Range message){

                    adjustSonicSensorSubscribingData(in, message.getRange());

                }

            });
        }


        // ---------- Camera subscriber ----------

        cameraSubscriber.addMessageListener(new MessageListener<sensor_msgs.CompressedImage> (){

            public void onNewMessage(sensor_msgs.CompressedImage message){

                BitmapFromCompressedImage bfci = new BitmapFromCompressedImage();
                cameraSubData = bfci.call(message);

                adjustCameraSubscribingData();
            }

        });

    }

    private void parseVeloPublishingData(){

        direction0 = js.getRawDirection0() * js.getRawDirection1();

        velocityX = js.getRawAngle()/ 90.0f;
        velocityX = velocityX * direction0;

        velocityY = js.getRawAcc() / 100.0f;
        velocityY = velocityY * js.getRawDirection1();

    }

    private void adjustVeloSubscribingData(){



    }

    private void adjustSonicSensorSubscribingData(int i, float data){

        Message msg = new Message();
        msg.what = 1;
        msg.arg1 = i;
        msg.obj = data;

        nodeHandler.sendMessage(msg);

    }

    private void adjustCameraSubscribingData(){

        Message msg = new Message();
        msg.what = 2;
        msg.obj = cameraSubData;

        if(cameraSubData != null)
            nodeHandler.sendMessage(msg);

    }

    @Override
    public void onShutdown(Node node) {

    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

        Looper.prepare();

        String errUri = "Master URI: " + node.getMasterUri().toString();

        ced = new ConnectionErrorDialog(context);
        ced.show();
        ced.setErrorUri(errUri);

        Looper.loop();

    }
}
