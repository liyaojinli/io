package org.liyaojin.study.aio.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * jdk1.7 aioEchoServer
 */
public class AioEchoServer {
    /**
     * 端口
     */
    private final int port;

    private final CountDownLatch latch;

    private AsynchronousServerSocketChannel serverChannel;

    /**
     * 构造函数
     *
     * @param port
     */
    public AioEchoServer(int port) {
        this.port = port;
        this.latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return this.latch;
    }

    /**
     * 返回服务端的channel,该channel用于accept客户端链接
     *
     * @return
     */
    public AsynchronousServerSocketChannel getServerChannel() {
        return this.serverChannel;
    }

    /**
     * 启动
     */
    public void start() {
        try {
            serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), this.port));
            serverChannel.accept(this, new AcceptCompletionHandler());
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
