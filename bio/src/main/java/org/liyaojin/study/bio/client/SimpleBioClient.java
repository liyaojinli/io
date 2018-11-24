package org.liyaojin.study.bio.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author LIYAOJIN
 * @Title: SimpleBioClient
 * @ProjectName io
 * @Description: TODO
 * @date 2018/11/24 17:30
 */
public class SimpleBioClient {
    private final SocketAddress remote;

    public SimpleBioClient(String host, int port) {
        this.remote = new InetSocketAddress(host, port);
    }

    public void start(){
        
    }
}
