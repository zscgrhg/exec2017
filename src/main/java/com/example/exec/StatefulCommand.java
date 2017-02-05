package com.example.exec;

/**
 * Created by THINK on 2017/2/4.
 */
public abstract class StatefulCommand<R> extends Excutable<R> {
    @Override
    public Handler<R> createHandler(Process process) {
        HandlerThread<R> handlerThread = new HandlerThread<R>(process, statefulHandler(process));
        handlerThread.start();
        return handlerThread;
    }

    protected abstract Handler<R> statefulHandler(Process process);
}
