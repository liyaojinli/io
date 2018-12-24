package org.liyaojin.study.bio;

import org.junit.Assert;
import org.junit.Test;
import org.liyaojin.study.bio.client.SimpleBioClient;

/**
 * @author LIYAOJIN
 * @Title: SimpleBioEchoClientTests
 * @ProjectName io
 * @Description: TODO
 * @date 2018/12/16 15:25
 */
public class SimpleBioEchoClientTests {
    @Test
    public void testWriteAndShowResponse(){
        SimpleBioClient client = new SimpleBioClient("127.0.0.1",9999);
        for(int i = 0 ; i < 10 ; i++){
            String toServer = "this is from client...@" + (i+1);
            Assert.assertEquals(client.writeAndShowResponse(toServer),toServer);
        }
        client.close();
    }
}
