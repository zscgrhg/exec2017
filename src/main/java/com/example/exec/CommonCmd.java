package com.example.exec;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

/**
 * Created by THINK on 2017/1/25.
 */
public class CommonCmd extends Excutable<Integer> {



    public ExitValueHandler createHandler(Process process) {
        return new PwHandler();
    }

    protected Charset charset() {
        return Charset.forName("GBK");
    }



    public static void main(String[] args) throws Exception {

        CommonCmd commonCmd = new CommonCmd();
        for (int i = 0; i < 10; i++) {
            Integer ping = commonCmd.excute( "ping","www.qq.com");
            System.out.println(ping);
        }
    }
}
