package com.example.exec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by THINK on 2017/2/4.
 */
public abstract class Excutable<R> {
    protected Charset stdoutCharset() {
        return charset();
    }

    protected Charset charset() {
        return Charset.forName("UTF8");
    }

    protected Charset stderrCharset() {
        return charset();
    }

    public abstract Handler<R> createHandler();

    protected List<String> getCommandLines(String... args) {
        return Arrays.asList(args);
    }

    protected void waitUntilProcessExit(Process process) throws Exception {
        process.waitFor();
    }

    protected Process createProcess(String... args) throws IOException {
        List<String> cmds = new ArrayList<String>();
        cmds.addAll(getCommandLines(args));
        ProcessBuilder pb = new ProcessBuilder(cmds);
        return pb.start();
    }

    public R excute(String... args) throws Exception {

        Process process = createProcess(args);
        InputStream errorStream = process.getErrorStream();
        Handler<R> handler = createHandler();
        ProcessReader stderrReader =
                new ProcessReader(false, errorStream, stderrCharset(), handler);
        stderrReader.start();
        InputStream inputStream = process.getInputStream();
        ProcessReader stdoutReader =
                new ProcessReader(inputStream, stdoutCharset(), handler);
        stdoutReader.start();
        try {
            waitUntilProcessExit(process);
        } finally {
            stdoutReader.join();
            stderrReader.join();
            killIfAlive(process);
            handler.onComplete(process.exitValue());
        }
        return handler.get();
    }

    public void killIfAlive(Process process) {
        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            process.destroy();
        }
    }
}
