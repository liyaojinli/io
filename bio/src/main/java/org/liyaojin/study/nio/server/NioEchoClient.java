package org.liyaojin.study.nio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author LIYAOJIN
 * @Title: NioEchoClient
 * @ProjectName io
 * @Description: nioEchoClient
 * @date 2018/12/31 10:59
 */
public class NioEchoClient {
    private final String host;
    private final int port;
    private static Logger logger = Logger.getLogger(NioEchoClient.class);
    private SocketChannel channel;
    private Selector selector;
    private volatile boolean isStop = false;
    private final ConcurrentLinkedQueue<String> messageQueue;
    private static final String NEW_LINE = System.getProperty("line.separator");

    private NioEchoClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.messageQueue = new ConcurrentLinkedQueue<String>();
    }

    /**
     * clientNioSelect Thread
     */
    class ClientNioHandler implements Runnable {
        @Override
        public void run() {
            while (!isStop) {
                try {
                    selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey key = selectionKeyIterator.next();
                        selectionKeyIterator.remove();
                        SocketChannel channel = (SocketChannel) key.channel();
                        channel.configureBlocking(false);
                        if (key.isConnectable()) {
                            logger.info("connect the remoteServer success...");
                        } else if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int readBytes = channel.read(buffer);
                            String serverResponse = "";
                            if (readBytes > 0) {
                                buffer.flip();
                                byte[] bytes = new byte[readBytes - NEW_LINE.getBytes().length];
                                buffer.get(bytes, 0, readBytes - NEW_LINE.getBytes().length);
                                serverResponse = new String(bytes, "utf-8");
                                logger.info("the remote server reply:" + serverResponse);
                            }
                        } else if (key.isWritable()) {
                            // get message from queue
                            String message = messageQueue.poll();
                            if (null != message) {
                                byte[] messageBytes = message.getBytes("utf-8");
                                ByteBuffer buffer2send = ByteBuffer.allocate(messageBytes.length + NEW_LINE.getBytes().length);
                                buffer2send.put(messageBytes).put(NEW_LINE.getBytes());
                                buffer2send.flip();
                                channel.write(buffer2send);
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("The nioEchoClient error...", e);
                }
            }
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    logger.error("An error occured while closing the channel...", e);
                }
            }
        }
    }

    /**
     * create an instance
     *
     * @param host remoteHost
     * @param port remotePort
     * @return the new nioEchoClient instance
     */
    public static NioEchoClient create(String host, int port) {
        NioEchoClient client = new NioEchoClient(host, port);
        try {
            client.channel = SocketChannel.open(new InetSocketAddress(host, port));
            client.channel.configureBlocking(false);
            client.selector = Selector.open();
            client.channel.register(client.selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            Thread handlerThread = new Thread(client.new ClientNioHandler(), "NioEchoClient");
            handlerThread.start();
        } catch (IOException e) {
            logger.error("create nioEcho client error", e);
        }
        return client;
    }

    /**
     * send a message to remoteServer
     *
     * @param message the message
     */
    public void echo(String message) {
        if (null == channel) {
            logger.error("The channel is not initialized yet...");
            return;
        }
        this.messageQueue.offer(message);
    }

    /**
     * close the client
     */
    public void close() {
        this.isStop = true;
    }

}
