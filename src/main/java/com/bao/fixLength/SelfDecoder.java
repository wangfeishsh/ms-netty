package com.bao.fixLength;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by nannan on 2017/9/18.
 */
@Slf4j
public class SelfDecoder extends ByteToMessageDecoder {

    //判断传送客户端传送过来的数据是否按照协议传输，头部信息的大小应该是 交易码4+长度8 = 12
    private static final int CODE_SIZE = 4;
    private static final int LENGTH_SIZE = 8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in == null) {
            log.info("毛线都没收到");
            return;
        }
        System.out.println(in.readableBytes());

        if (in.readableBytes() < LENGTH_SIZE + CODE_SIZE) {
            throw new Exception("可读信息段比头部信息都小，你在逗我？");
        }

        byte[] codeByte = new byte[CODE_SIZE];
        byte[] LengthByte = new byte[LENGTH_SIZE];
        //注意在读的过程中，readIndex的指针也在移动
        in.readBytes(codeByte);
        in.readBytes(LengthByte);

        String code = new String(codeByte);
        String lengthStr = new String(LengthByte);
        int length = Integer.parseInt(lengthStr);

        if (in.readableBytes() < length) {
            throw new Exception("body字段你告诉我长度是" + length + ",但是真实情况是没有这么多，你又逗我？");
        }
        byte[] bodyByte = new byte[length];
        in.readBytes(bodyByte);
        String body = new String(bodyByte, "UTF-8");
        System.out.println(body);
        out.add(CustomMsg.builder().code(code).length(String.valueOf(lengthStr)).body(body));

    }
}
