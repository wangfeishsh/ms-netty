package com.bao.aio;

import java.io.IOException;

/**
 * Created by nannan on 2017/9/8.
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8888;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
    }
}