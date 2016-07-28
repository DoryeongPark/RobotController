package org.ollide.rosandroid;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Felix on 2016-07-14.
 */
public class ConnectionTimer extends Thread {

    private final int MAX_PERMISSION_TIME = 8;
    private int timer;

    private boolean stateShutDown = false;
    private boolean statePause = false;

    private Handler uiHandler;

    public ConnectionTimer(Handler uiHandler){

        this.timer = MAX_PERMISSION_TIME;
        this.uiHandler = uiHandler;

    }

    public void run(){

        while(stateShutDown == false){

                --timer;
                System.out.println(this);

                try{

                    this.sleep(1000);

                } catch (Exception e) { e.printStackTrace(); }

                if (timer < MAX_PERMISSION_TIME * 2 / 3)
                    displayDisconnectionState();

                if (timer == 0)
                    displayErrorDialog();

                if (timer == -1 || statePause == true)
                    ++timer;

        }

        System.out.println("Timer - shutdown successful");

    }

    public void shutDown(){

        stateShutDown = true;

    }

    public void setPauseState(boolean state) {

        statePause = state;

    }

    public void getResponse(){

        timer = MAX_PERMISSION_TIME;

    }

    private void displayDisconnectionState(){

        Message msg = new Message();
        msg.what = 1;

        uiHandler.sendMessage(msg);

    }

    private void displayErrorDialog(){

        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = 1;

        uiHandler.sendMessage(msg);
    }

    public String toString(){

        return "Timer - " + timer;

    }

}
