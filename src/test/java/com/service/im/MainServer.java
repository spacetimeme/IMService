package com.service.im;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MainServer {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(6969));
        int index = 1;
        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ssc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int length = sc.read(buffer);
                    if(length<0){
                        //返回-1等于连接被关闭
                        sc.close();
                        key.cancel();
                        continue;
                    }
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, length));
                    byte[] bytes = ("I am server message" + index).getBytes();
                    index++;
                    buffer = ByteBuffer.allocate(bytes.length);
                    buffer.put(bytes);
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        sc.write(buffer);
                    }
                }
            }
        }
    }
}
