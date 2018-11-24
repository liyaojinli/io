package org.liyaojin.study.bio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author LIYAOJIN
 * @Title: SimpleBioEchoServer
 * @ProjectName io
 * @Description: TODO
 * @date 2018/11/23 22:04
 */
public class SimpleBioEchoServer {
    private final int port;
    private final int queueSize;
    private final Logger logger = Logger.getLogger(SimpleBioEchoServer.class);
    private ServerSocket serverSocket;

    public SimpleBioEchoServer(int port, int queueSize) {
        this.port = port;
        this.queueSize = queueSize;
    }

    public SimpleBioEchoServer(int port) {
        this(port, 50);
    }

    public SimpleBioEchoServer() {
        this(9999, 50);
    }

    public int getPort() {
        return this.port;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void start() {
        try{
            serverSocket = new ServerSocket(this.port,this.queueSize);
            logger.info(String.format("The server started successfully on port:%d with a connection queue size of %d", port, queueSize));
            for(;;){
                Socket socket = serverSocket.accept();

            }
        } catch (IOException e) {
            logger.error("Server startup error...",e);
        }
    }
}
