package org.liyaojin.study.aio.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioEchoServer> {
    @Override
    public void completed(AsynchronousSocketChannel clientChannel, AioEchoServer attachment) {
        // 接受一个客户端请求后要继续accept其他客户端的请求，形成一个循环
        attachment.getServerChannel().accept(attachment,this);
        // 给客户端一个欢迎信息
        String toClientStr = "hi i am a echoServer, send \"bye\" to ended..."+System.getProperty("line.separator");
        try {
            ByteBuffer toClient = ByteBuffer.wrap(toClientStr.getBytes("utf-8"));
            clientChannel.write(toClient);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 等待客户都输入，并触发ReadCompletionHandler
        ByteBuffer bufferRead = ByteBuffer.allocate(1024);
        clientChannel.read(bufferRead,bufferRead,new ReadCompletionHandler(clientChannel));
    }

    @Override
    public void failed(Throwable exc, AioEchoServer attachment) {
        exc.printStackTrace();
        attachment.getLatch().countDown();
    }
}
