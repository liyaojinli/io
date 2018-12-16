package org.liyaojin.study.bio.server;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author LIYAOJIN
 * @Title: SimpleBioEchoServerTests
 * @ProjectName io
 * @Description: TODO
 * @date 2018/11/23 22:15
 */
public class SimpleBioEchoServerTests {
    private int port;
    private int queueSize;

    @Before
    public void setUp() {
        this.port = 8080;
        this.queueSize = 100;
    }

    @Test
    public void testStart() {
        SimpleBioEchoServer simpleBioEchoServer = new SimpleBioEchoServer(port, queueSize);
        Assert.assertEquals(port, simpleBioEchoServer.getPort());
        Assert.assertEquals(queueSize, simpleBioEchoServer.getQueueSize());

        simpleBioEchoServer = new SimpleBioEchoServer(port);
        Assert.assertEquals(port, simpleBioEchoServer.getPort());
        Assert.assertEquals(50, simpleBioEchoServer.getQueueSize());

        simpleBioEchoServer = new SimpleBioEchoServer();
        Assert.assertEquals(9999, simpleBioEchoServer.getPort());
        Assert.assertEquals(50, simpleBioEchoServer.getQueueSize());

        final SimpleBioEchoServer serverFinal = simpleBioEchoServer;
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverFinal.start();
            }
        });
        Thread telnetThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TelnetClient client = new TelnetClient();
                int index = 0;
                for (; ; ) {
                    System.out.println("try to connect server " + (++index) + " time");
                    if (index >= 3) {
                        throw new RuntimeException("can not connect to server...");
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        System.exit(1);
                    }
                    PrintWriter write;
                    try {
                        client.connect(InetAddress.getLocalHost(), serverFinal.getPort());
                        System.out.println("connected the server ");
                        break;
                    } catch (IOException e) {
                        continue;
                    }
                }
                PrintWriter write = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String toServerCmd = "hi i am client";
                write.println(toServerCmd);
                for(;;){
                    try {
                        String serverResponse = reader.readLine();
                        if(null == serverResponse){
                            break;
                        }
                        System.out.println(serverResponse);
                    } catch (IOException e) {
                        break;
                    }
                }
                System.out.println("the client exit...");
            }
        },"bioClientThread");
        serverThread.start();
        telnetThread.start();
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            System.out.println(serverThread.isInterrupted());
        }
    }
}
