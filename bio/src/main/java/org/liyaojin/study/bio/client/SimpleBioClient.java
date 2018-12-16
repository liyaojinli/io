package org.liyaojin.study.bio.client;

import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author LIYAOJIN
 * @Title: SimpleBioClient
 * @ProjectName io
 * @Description: TODO
 * @date 2018/11/24 17:30
 */
public class SimpleBioClient {
    private Socket socket;

    public SimpleBioClient(String host, int port) {
        BufferedReader reader = null;
        try {
            this.socket = new Socket(host,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("connect server success, and the server say:" + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if(null != socket){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String writeAndShowResponse(String toServer){
        String reply = "";
        if(null != socket){
            PrintWriter pw = null;
            BufferedReader reader = null;
            try {
                pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
                pw.println(toServer);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                reply = reader.readLine();
                System.out.println("the server reply:" + reply);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reply;
    }
}
