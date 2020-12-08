package com.test.io.concurrency.netty.client;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;



@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler <ByteBuf> {

    /**
     * 服务器的连接被建立后调用
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext  ctx) {
        ctx.writeAndFlush( Unpooled.copiedBuffer ("hello netty, how are you?",CharsetUtil.UTF_8));
    }
    /**
     * 接收到服务器返回的数据后调用
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0 (ChannelHandlerContext  channelHandlerContext, ByteBuf byteBuf)throws Exception {
//记录接收到的消息
        System.out. println(System.nanoTime()+" Client received："+byteBuf.toString(CharsetUtil.UTF_8));
    }
    /**
     * 捕获一个异常时调用
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        cause.printStackTrace();
        ctx.close ();
    }
}