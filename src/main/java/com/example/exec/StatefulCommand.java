package com.example.exec;

/**
 * Created by THINK on 2017/2/4.
 */
public abstract class StatefulCommand<R> extends Excutable<R> {
    public Handler<R> createHandler() {
        HandlerThread<R> handlerThread = new HandlerThread<R>(statefulHandler());
        handlerThread.start();
        return handlerThread;
    }

    protected abstract Handler<R> statefulHandler();
}
