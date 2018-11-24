package org.liyaojin.study.bio.server;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * @author LIYAOJIN
 * @Title: EchoHandler
 * @ProjectName io
 * @Description: TODO
 * @date 2018/11/24 16:23
 */
class EchoHandler implements Runnable {
    private final Socket socket;
    private final Logger logger = Logger.getLogger(EchoHandler.class);


    EchoHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if(socket == null){
            throw new NullPointerException("the socket is null in the EchoHandler...");
        }
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // autoFlush:true
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            writer.println("hi " + this.socket.getRemoteSocketAddress()+" welcome to EchoServer,print the \"bye\" when you want to leave ");
            String line;
            while ((line = reader.readLine()) != null) {
                // println() calls flush() when autoFlush is true
                writer.println(line);
                // print the "bye" to exit loop
                if("bye".equalsIgnoreCase(line)){
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred with EchoHandler...", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("An error occurred with reader.close() in EchoHandler...", e);
                }
            }
            if( writer != null){
                writer.close();
            }
            try {
                if(socket != null){
                    socket.close();
                }
            } catch (IOException e) {
                logger.error("An error occurred with socket.close() in EchoHandler...", e);
            }
        }
    }
}
