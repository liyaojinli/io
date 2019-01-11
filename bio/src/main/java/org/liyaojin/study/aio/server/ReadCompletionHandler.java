package org.liyaojin.study.aio.server;

import java.io.IOException;
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
        // 判断已经读取的内容中是否已经有换行符了
        boolean isNewLine = false;
        // 判断是否要继续接受用户的输入
        boolean isNeedContinueRead = true;
        int rBegin = 0,rEnd = 0;
        for(int i = 0 ; i < attachment.remaining(); i++){
            if(attachment.get(i) == 13){
                rEnd = i;
                rBegin = rEnd + 1;
                isNewLine = true;
                break;
            }
        }
        // 从attachent中读取rBegin-rEnd中的内容
        if(isNewLine){
            byte[] clientSayBytes = new byte[rEnd+1];
        }

        // 除非用户输入bye，否则一直read
        if(isNeedContinueRead){
            clientChannel.read(attachment,attachment,this);
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
