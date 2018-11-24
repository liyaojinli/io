package org.liyaojin.study.bio.server;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
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
                    System.out.println("try to connect server "+(++index)+" time");
                    if(index >= 3){
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
                        write = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
                        System.out.println("connected the server ");
                        write.println("bye");
                        // 这样不能终端accept的阻塞，要想别的办法
                        serverThread.interrupt();
                        break;
                    } catch (IOException e) {
                        continue;
                    }
                }
            }
        });
        serverThread.start();
        telnetThread.start();
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            System.out.println(serverThread.isInterrupted());
        }
    }
}
