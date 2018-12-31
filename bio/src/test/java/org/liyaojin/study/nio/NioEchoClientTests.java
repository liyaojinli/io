package org.liyaojin.study.nio;

import org.junit.Test;
import org.liyaojin.study.nio.server.NioEchoClient;

import java.io.IOException;

/**
 * @author LIYAOJIN
 * @Title: NioEchoClientTests
 * @ProjectName io
 * @Description: TODO
 * @date 2018/12/31 12:39
 */
public class NioEchoClientTests {
    @Test
    public void echo() throws IOException {
        NioEchoClient.create("127.0.0.1",9999).echo("this is a test message");
        System.in.read();
    }
}
