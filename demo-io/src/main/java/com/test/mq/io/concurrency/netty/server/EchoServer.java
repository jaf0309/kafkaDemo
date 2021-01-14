package com.test.io.concurrency.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer{

    private final  int port;

    public EchoServer(int port){
        this.port=port ;
    }

    public static void main(String [] args) throws InterruptedException {
        new EchoServer (8080). start();
    }

    /**
     * 如果要在那个代码清单中使用 epoll 替代 NIO，只需要将 NioEventLoopGroup
     * 替换为 EpollEventLoopGroup ，并且将 NioServerSocketChannel.class 替换为
     * EpollServerSocketChannel.class 即可。
     * @throws InterruptedException
     */
    public void start() throws InterruptedException{
//使用NioEventLoopGroup接受和处理新连接
        NioEventLoopGroup group  = new NioEventLoopGroup ();
        try{
            ServerBootstrap server= new ServerBootstrap();
            server.group(group)
                    //指定NIO的传输channel
                    .channel(NioServerSocketChannel. class )
                    //设置 socket 地址使用所选的端口
                     .localAddress(port)
                    //添加 EchoServerHandler 到 Channel 的 ChannelPipeline
                    . childHandler ( new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void  initChannel(SocketChannel  socketChannel  )throws Exception{
                   socketChannel.pipeline ().addLast(new EchoServerHandler());
                    }
                 });
                //绑定的服务器 同步等待服务器绑定完成
            ChannelFuture future=server. bind ().sync();
            System. out. println ( EchoServer.class.getName()+" started and listen on " +future.channel().localAddress());
            //等待channel关闭
            future.channel().closeFuture ().sync();
        } catch(Exception e ){
            e.printStackTrace();
        }finally {
        //关闭NioEventLoopGroup 释放所有资源
            group. shutdownGracefully().sync ();
        }
    }
}