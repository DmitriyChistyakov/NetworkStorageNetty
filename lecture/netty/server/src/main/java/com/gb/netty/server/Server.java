package com.gb.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        new Server().start();
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);         //разделение обработки входящих соединений и входящих сообщений
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();        //обработка сообщений от клиентов
        try {
            ServerBootstrap server = new ServerBootstrap();
            server                                                      //конфигурация сервера
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)              //подключение или установка соединения
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {      //инициализация канала
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(                                  // у каждого канала свои Decoder & Encoder
                                    new LengthFieldBasedFrameDecoder(1024*1024*1024, 0,4,0,4),  //ждет все байты от клиента
                                    new ByteArrayDecoder(),                         // преобразует из байт буффа
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new ServerDecoder(),
                                    new SecondServerDecoder());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);            //настройки соединения, незакрытие соединения сразу

            ChannelFuture sync = server.bind(9000).sync();
            sync.channel().closeFuture().sync();                        //ожидание, пока сервер не перестанет работать
        } finally {
            bossGroup.shutdownGracefully();         //закрытие потоков
            workerGroup.shutdownGracefully();
        }
    }
}
