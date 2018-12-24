package org.liyaojin.study.nio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author LIYAOJIN
 * @Title: NioEchoServer
 * @ProjectName io
 * @Description: Nio版本的ECHO服务端
 * @date 2018/12/24 21:08
 */
public class NioEchoServer {
    private final int port;
    private Logger logger = Logger.getLogger(NioEchoServer.class);
    private ServerSocketChannel channel;
    private Selector selector;
    private volatile boolean stop;

    public NioEchoServer(int port) {
        this.port = port;
        try {
            channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();
        } catch (IOException e) {
            logger.error("服务器初始化异常", e);
        }
    }

    private class EchoServerHandler implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                if (stop) {
                    break;
                }
                try {
                    selector.select(1000);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iteratorKeys = keys.iterator();
                    while (iteratorKeys.hasNext()) {
                        SelectionKey _key = iteratorKeys.next();
                        iteratorKeys.remove();
                        if (_key.isValid()) {
                            // 扫描到客户端可连接事件
                            if (_key.isAcceptable()) {
                                ServerSocketChannel channelServer = (ServerSocketChannel) _key
                                        .channel();
                                SocketChannel channelClient = channelServer.accept();
                                channelClient.configureBlocking(false);
                                // 客户端channel注册到选择器，并关注可读和可写事件
                                channelClient.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                            } else if (_key.isReadable()) {
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                SocketChannel channelClient = (SocketChannel) _key.channel();
                                int readBytes = channelClient.read(buffer);
                                if (readBytes > 0) {
                                    byte[] bytes = new byte[readBytes];
                                    String clientSay = new String(bytes,"utf-8");
                                    if("bye".equalsIgnoreCase(clientSay)){
                                        _key.channel();
                                        channelClient.close();
                                    }
                                    buffer.flip();
                                    buffer.get(bytes, 0, readBytes);
                                    _key.attach(bytes);
                                    // 需要进行写入时才注册写事件，否则写事件一直是就绪的会不停的触发写事件
                                    _key.interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                                }else{
                                    _key.cancel();
                                    channelClient.close();
                                }
                            } else if (_key.isWritable()) {
                                byte[] bytes = (byte[]) _key.attachment();
                                SocketChannel channelClient = (SocketChannel) _key.channel();
                                if(null == bytes){
                                    channelClient.write(ByteBuffer.wrap(new String("hi i am a echoServer, send \"bye\" to ended...\r\n").getBytes("utf-8")));
                                }else{
                                    ByteBuffer buffer = ByteBuffer.allocate(bytes.length+4);
                                    buffer.put(bytes);
                                    buffer.putChar('\r');
                                    buffer.putChar('\n');
                                    buffer.flip();
                                    channelClient.write(buffer);
                                }
                                // 写事件处理完毕之后，立即取消写事件的注册，防止写事件一直就绪
                                _key.interestOps(_key.interestOps()& ~SelectionKey.OP_WRITE);
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("服务器运行出现异常", e);
                }
            }
        }
    }

    public void start() {
        if (null == channel || selector == null) {
            logger.info(String.format("服务器无法启动，端口:%d", this.port));
            return;
        }
        try {
            channel.bind(new InetSocketAddress("127.0.0.1", this.port));
            channel.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            logger.error(String.format("服务器绑定端口%d失败", this.port), e);
            return;
        }
        Thread startThread = new Thread(new EchoServerHandler(), "NIO-EchoServer");
        startThread.start();
        logger.info(String.format("服务器启动成功，端口%d", this.port));
        try {
            startThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        stop = true;
    }
}
