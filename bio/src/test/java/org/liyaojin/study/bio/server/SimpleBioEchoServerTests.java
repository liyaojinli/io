package org.liyaojin.study.bio.server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

        simpleBioEchoServer.start();
    }
}
