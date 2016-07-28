package org.ollide.rosandroid;

import android.graphics.Bitmap;
import android.os.Message;
import android.os.Handler;

import org.ros.android.BitmapFromCompressedImage;
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
public class AndroidNode extends AbstractNodeMain implements NodeMain {

    //----- Variables associated with Connection with Android components -----

    private Joystick js;
    private Handler nodeHandler;
    private Handler uiHandler;

    public AndroidNode(Joystick js, Handler nodeHandler, Handler uiHandler){

        this.js = js;
        this.nodeHandler = nodeHandler;
        this.uiHandler = uiHandler;

    }

    @Override
    public GraphName getDefaultNodeName() {

        return GraphName.of("FelixNode");

    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        final Publisher<geometry_msgs.Twist> veloPublisher = connectedNode.newPublisher(
                GraphName.of("cmd_vel"), geometry_msgs.Twist._TYPE);

        Vector<Subscriber<sensor_msgs.Range>> sSensorSubscribers =
                new Vector<Subscriber<sensor_msgs.Range>>();

        for(int i = 0; i < 8; ++i) {
            Subscriber<sensor_msgs.Range> sonicSubscriber = connectedNode.newSubscriber(
                    GraphName.of("p1_sonar_" + (i + 1)), sensor_msgs.Range._TYPE);

            sSensorSubscribers.add(sonicSubscriber);
        }

        final Subscriber<sensor_msgs.LaserScan> laserSubscriber = connectedNode.newSubscriber(
                GraphName.of("scan"), sensor_msgs.LaserScan._TYPE);

        final Subscriber<sensor_msgs.CompressedImage> cameraSubscriber  = connectedNode.newSubscriber(
                GraphName.of("camera/rgb/image_raw/compressed"), sensor_msgs.CompressedImage._TYPE);

        final Subscriber<rosgraph_msgs.Clock> connectionCheckingSubscriber = connectedNode.newSubscriber(
                GraphName.of("clock"), rosgraph_msgs.Clock._TYPE);


        // ---------- Velocity publisher ----------

        final FinallyCancellableLoop veloPubLoop = new FinallyCancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {

                geometry_msgs.Twist veloPubData = connectedNode.
                        getTopicMessageFactory().newFromType(Twist._TYPE);

                veloPubData.getAngular().setZ(js.getDataAngular());
                veloPubData.getLinear().setX(js.getDataLinear());

                veloPubData.getLinear().setY(2.0f);

                veloPublisher.publish(veloPubData);

                Thread.sleep(100);

            }

            public void executeFinally(){

                geometry_msgs.Twist veloPubData = connectedNode.
                        getTopicMessageFactory().newFromType(Twist._TYPE);

                veloPubData.getLinear().setY(1.0f);

                veloPublisher.publish(veloPubData);

            }

        };


        connectedNode.executeCancellableLoop(veloPubLoop);

        // ---------- 8 Sonar sensor subscribers ----------

        for(int i = 0; i < sSensorSubscribers.size(); ++i){

            final int in = i;

            sSensorSubscribers.elementAt(i).addMessageListener(new MessageListener<sensor_msgs.Range>(){

                public void onNewMessage(sensor_msgs.Range message){

                    adjustSonarSensorSubscribingData(in, message.getRange());

                }

            });
        }

        // ---------- Laser sensor subscriber ----------

        laserSubscriber.addMessageListener(new MessageListener<sensor_msgs.LaserScan>() {

            public void onNewMessage(sensor_msgs.LaserScan message) {

                adjustLaserSensorSubscribingData(message);

            }

        });

        // ---------- Camera subscriber ----------

        cameraSubscriber.addMessageListener(new MessageListener<sensor_msgs.CompressedImage> (){

            public void onNewMessage(sensor_msgs.CompressedImage message){

                BitmapFromCompressedImage bfci = new BitmapFromCompressedImage();
                Bitmap data = bfci.call(message);

                adjustCameraSubscribingData(data);

            }

        });

        // ---------- Connection checking subscriber ----------

        connectionCheckingSubscriber.addMessageListener(new MessageListener<rosgraph_msgs.Clock> (){

            public void onNewMessage(rosgraph_msgs.Clock message){

                resetConnectionTimer();

            }

        });

        displayConnectingState();

        startConnectionTimer();

        closeErrorDialog();

    }

    private void displayConnectingState(){

        Message msg = new Message();
        msg.what = 0;
        msg.obj = "Connected ";

        uiHandler.sendMessage(msg);

    }

    private void adjustVeloSubscribingData(){



    }

    private void adjustSonarSensorSubscribingData(int i, float data){

        Message msg = new Message();
        msg.what = 1;
        msg.arg1 = i;
        msg.obj = data;

        nodeHandler.sendMessage(msg);

    }

    private void adjustLaserSensorSubscribingData(sensor_msgs.LaserScan data){

        Message msg = new Message();
        msg.what = 2;
        msg.obj = data;

        nodeHandler.sendMessage(msg);

    }

    private void adjustCameraSubscribingData(Bitmap data){

        Message msg = new Message();
        msg.what = 3;
        msg.obj = data;

        if(data != null)
            nodeHandler.sendMessage(msg);

    }

    private void startConnectionTimer(){

        Message msg = new Message();
        msg.what = 4;
        msg.arg1 = 0;

        nodeHandler.sendMessage(msg);

    }

    private void resetConnectionTimer(){

        Message msg = new Message();
        msg.what = 4;
        msg.arg1 = 1;

        nodeHandler.sendMessage(msg);

    }

    private void closeErrorDialog(){

        Message msg = new Message();
        msg.what = 3;

        uiHandler.sendMessage(msg);

    }

    @Override
    public void onShutdown(Node node) {

    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = 0;
        msg.obj = "Master URI: " +  node.getMasterUri().toString();

        uiHandler.sendMessage(msg);

    }

}
