package org.liyaojin.study.nio;

import org.junit.Before;
import org.junit.Test;
import org.liyaojin.study.nio.server.NioEchoServer;

/**
 * @author LIYAOJIN
 * @Title: NioEchoServerTests
 * @ProjectName io
 * @Description: TODO
 * @date 2018/12/24 22:10
 */
public class NioEchoServerTests {
    private int port;

    @Before
    public void setUp(){
        this.port = 9999;
    }

    @Test
    public void start(){
        new NioEchoServer(this.port).start();
    }
}
