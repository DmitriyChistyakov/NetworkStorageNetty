package com.gb.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerDecoder extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
        ctx.writeAndFlush("Response from server for message: " + msg);      //отправка на клиента
        ctx.fireChannelRead(msg);                                           // вызови следующий декодер и передай сообщение
        ctx.channel().pipeline().remove(this);      //метод отработает один раз и закроется
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {   //при установке соединения клиент-сервер
        System.out.println("New channel is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {       //при прекращении соединения клиент-сервер
        System.out.println("Client disconnected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {      //отлов ошибок
        cause.printStackTrace();            //не очень хороший вариант - вывод ошибки в консоль. Лучше либо  реагировать, либо делигировать ошибку
    }
}
