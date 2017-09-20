package com.bao.fixLength;

import com.bao.echo.EchoClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by user on 16/10/18.
 */
public class SelfClientHandler extends ChannelInboundHandlerAdapter {
//    private final ByteBuf firstMessage;
    private CustomMsg customMsg;
    public SelfClientHandler(){
//        firstMessage = Unpooled.buffer(1024);
        String body = "1231235sdg我的这句话";
//        System.out.println(getLength(body));
//        firstMessage.writeBytes (("aaaa"+getLength(body)+body).getBytes());
        customMsg = new CustomMsg();
        customMsg.setCode("aaaa");
        customMsg.setLength(getLength(body));
        customMsg.setBody(body);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(customMsg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
//        ByteBuf byteBuf = (ByteBuf) msg;
//        byte[] req = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(req);
//        String body = new String(req, "UTF-8");
//        String body = (String) msg;
//        System.out.println("++++++" + body + "+++++" + counter++);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static String getLength(String body){
        int length = 0;
        try {
            length = body.getBytes("utf-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String lenStr = String.valueOf(length);
        while (lenStr.length() < 8) {
            lenStr = "0" + lenStr;
        }
        return lenStr;
    }
}
