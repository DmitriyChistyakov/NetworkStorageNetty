package com.gb.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        new Client().start();
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();  //создание потоков каналов
        try {
            Bootstrap client = new Bootstrap();  //
            client.group(group)   //конфигурация клиента
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {  //инициализация каждого канала взаимодействия клиент-сервер
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(                          //обработка входящих-исходящих объектыов
                                    new StringDecoder(),
                                    new ByteArrayEncoder(),                 // в байт буфф
                                    new LengthFieldPrepender(4),            //добавляет размер сообщения в первые четыре байта сообщения
                                    new MessageEncoder(),                   // преобразует в массив байтов
                                    new ClientDecoder());
                        }
                    });

            ChannelFuture future = client.connect("localhost", 9000).sync();  //sync - ждем соединения, ChannelFuture - объект, с помощью которого мы взаимодействуем с нашим каналом

            System.out.println("Client started");

            while (true) {
                future.channel().writeAndFlush("Hello from client").sync(); //writeAndFlush записываем в буфер и отправь

                Thread.sleep(5000);
            }
        } finally {
            group.shutdownGracefully();                         //заканчивает работу потоков
        }
    }
}
