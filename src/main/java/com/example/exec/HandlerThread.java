package com.example.exec;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by THINK on 2017/1/25.
 */
public class HandlerThread<R> extends Thread implements Handler<R> {
    private final BlockingQueue<PMessage> queue;
    private final Handler<R> handler;
    private R r;

    public HandlerThread(Handler<R> handler) {
        this.queue = new LinkedBlockingQueue<PMessage>();
        ;
        this.handler = handler;
    }




    @Override
    public void run() {
        boolean isInterrupted = isInterrupted();
        boolean complete = false;
        while (!complete) {
            try {
                PMessage poll = queue.take();
                PMessage.Key key = poll.key;
                String line = poll.line;
                switch (key) {
                    case STDERR:
                        handler.receiveError(line);
                        break;
                    case STDOUT:
                        handler.receive(line);
                        break;
                    case STDERR_END:
                        handler.onStderrEnd();
                        break;
                    case STDOUT_END:
                        handler.onStdoutEnd();
                        break;
                    case FINISH:
                        handler.onComplete(Integer.parseInt(line.trim()));
                        complete = true;
                }
            } catch (InterruptedException e) {
                isInterrupted = true;
            } finally {
                if (queue.isEmpty() && isInterrupted) {
                    complete = true;
                }
            }
        }
        publish(handler.get());
    }

    public void onComplete(int processExitValue) {
        try {
            queue.put(new PMessage(PMessage.Key.FINISH, String.valueOf(processExitValue)));
            join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void onStderrEnd() {
        try {
            queue.put(new PMessage(PMessage.Key.STDERR_END, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void onStdoutEnd() {
        try {
            queue.put(new PMessage(PMessage.Key.STDOUT_END, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void receiveError(String line) {
        try {
            queue.put(new PMessage(PMessage.Key.STDERR, line));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void receive(String line) {
        try {
            queue.put(new PMessage(PMessage.Key.STDOUT, line));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized R get() {
        return this.r;
    }
    private synchronized void publish(R r) {
        this.r = r;
    }
}
