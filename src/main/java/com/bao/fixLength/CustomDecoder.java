package com.bao.fixLength;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nannan on 2017/9/18.
 */
@Slf4j
public class CustomDecoder extends LengthFieldBasedFrameDecoder {

    //判断传送客户端传送过来的数据是否按照协议传输，头部信息的大小应该是 交易码4+长度8 = 12
    private static final int CODE_SIZE = 4;
    private static final int LENGTH_SIZE = 8;

    /**
     * @param maxFrameLength      解码时，处理每个帧数据的最大长度
     * @param lengthFieldOffset   该帧数据中，存放该帧数据的长度的数据的起始位置
     * @param lengthFieldLength   记录该帧数据长度的字段本身的长度
     * @param lengthAdjustment    修改帧数据长度字段中定义的值，可以为负数
     * @param initialBytesToStrip 解析的时候需要跳过的字节数
     * @param failFast            为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
     */
    public CustomDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                         int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
                lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) {
            log.info("毛线都没收到");
            return null;
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
        System.out.println(code);
        String lengthStr = new String(LengthByte);
        System.out.println(lengthStr);
        int length = Integer.parseInt(lengthStr);

        if (in.readableBytes() < length) {
            throw new Exception("body字段你告诉我长度是" + length + ",但是真实情况是没有这么多，你又逗我？");
        }
        byte[] bodyByte = new byte[length];
        in.readBytes(bodyByte);
        String body = new String(bodyByte, "UTF-8");
        return CustomMsg.builder().code(code).length(lengthStr).body(body);
    }

}
