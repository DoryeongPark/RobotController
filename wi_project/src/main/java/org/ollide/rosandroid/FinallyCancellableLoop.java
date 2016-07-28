package org.ollide.rosandroid;

import org.ros.concurrent.CancellableLoop;

/**
 * Created by Felix on 2016-07-14.
 */
public abstract class FinallyCancellableLoop extends CancellableLoop{

    protected abstract void loop() throws InterruptedException;
    protected abstract void executeFinally();

    @Override
    public void cancel(){

        executeFinally();
        super.cancel();

    }

}
