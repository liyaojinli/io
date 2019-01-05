package org.liyaojin.study.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    /**
     * accept操作完成时形成的客户端channel,传入该对象用于往客户端输出信息
     */
    private final AsynchronousSocketChannel clientChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void completed(Integer readCount, ByteBuffer attachment) {
        if(-1 == readCount){
            return;
        }
        // 得到客户端输入
        String strFromClient = "";
        byte[] bytesFromClient = new byte[readCount];
        attachment.flip();
        attachment.get(bytesFromClient);
        try {
            strFromClient = new String(bytesFromClient,"utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
