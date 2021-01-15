package com.test.mq.io.netty.server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 通过 EchoServerHandler 实例给每一个新的 Channel 初始化
 * ChannelHandler.Sharable注解：标识这类的实例之间可以在 channel 里面共享
 *
 * @date 2018/9/27
 * @description
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 每个信息入站都会调用,信息入站后，可在其中处理业务逻辑
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println( System. nanoTime()+" server received: "+in.toString(CharsetUtil.UTF_8));
        //将所接受的消息返回给发送者
        ctx.write( in );
    }

    /**
     * 通知处理器最后的 channelread() 是当前批处理中的最后一条消息时调用
     *
     * @param ctx
     * @throws Exception
     */

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)throws Exception {
    //冲刷所有待审消息到远程节点，监听到关闭通道后，操作完成
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE );
    }

    /**
     * 读操作时捕获到异常时调用:打印异常及关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught (ChannelHandlerContext  ctx,Throwable cause)throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}