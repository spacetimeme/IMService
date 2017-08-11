package com.service.im;

import com.service.im.protocol.Protocol;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
    public static void main(String[] args) throws Exception {
        for(int i=0; i<10; i++){
            new Thread(){
                @Override
                public void run() {
                    try {
                        test(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    public static void test(String[] args) throws Exception {
        SocketChannel socket = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6969));
        socket.configureBlocking(false);
        Selector selector = Selector.open();
        socket.register(selector, SelectionKey.OP_READ);
        new WriteThread(socket).start();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (socket.isOpen() && selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            if (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    buffer.clear();
                    int len = socket.read(buffer);
                    if (len < 0) {
                        System.out.println("close channel!");
                        key.cancel();
                        selector.close();
                        socket.close();
                        continue;
                    }
                    if (len < Protocol.HEADER_LENGTH) {
                        continue;
                    }
                    buffer.flip();
                    //{起始标记   -byte     -1}
                    //{协议版本   -byte     -1}
                    //{包总长度   -short      -2}
                    //{分片总数量  -short     -2}
                    //{分片编号   -short     -2}
                    //{包编号ID  -byte[32] -32}
                    //{消息类型   -byte     -1}
                    //{发送者编号  -long     -8}
                    //{接受者编号  -long     -8}
                    //{包内容     -byte[n]  -n}
                    //{结束标记   -byte      -1}
//                    int position = 0;
//                    System.out.print((char) buffer.get());
//                    buffer.position(position+=1);
//                    System.out.print("version=" + buffer.get());
//                    buffer.position(position+=1);
//                    int length = buffer.getShort();
//                    System.out.print(" length=" + length);
//                    buffer.position(position+=2);
//                    System.out.print(" mutCount=" + buffer.getShort());
//                    buffer.position(position+=2);
//                    System.out.print(" mutIndex=" + buffer.getShort());
//                    buffer.position(position+=2);
//                    byte[] bytes = new byte[32];
//                    buffer.get(bytes);
//                    System.out.print(" id=" + new String(bytes));
//                    buffer.position(position+=32);
//                    System.out.print(" type=" + buffer.get());
//                    buffer.position(position+=1);
//                    System.out.print(" fromUid=" + buffer.getLong());
//                    buffer.position(position+=8);
//                    System.out.print(" toUid=" + buffer.getLong());
//                    buffer.position(position+=8);
//                    byte[] body = new byte[length - Protocol.HEADER_LENGTH];
//                    buffer.get(body);
//                    System.out.print(" body=" + new String(body));
//                    System.out.println((char) buffer.get(length-1));
                }
                iterator.remove();
            }
        }
    }

    public static class WriteThread extends Thread {

        private SocketChannel socket;

        public WriteThread(SocketChannel socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= 10; i++) {
                    ByteBuffer buffer = get("I am client message index -> " + i);
                    while (buffer.hasRemaining()) {
                        socket.write(buffer);
                    }
                    Thread.sleep(300);
                }
//                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //{起始标记   -byte     -1}
    //{协议版本   -byte     -1}
    //{包总长度   -short      -2}
    //{分片总数量  -short     -2}
    //{分片编号   -short     -2}
    //{包编号ID  -byte[32] -32}
    //{消息类型   -byte     -1}
    //{发送者编号  -long     -8}
    //{接受者编号  -long     -8}
    //{包内容     -byte[n]  -n}
    //{结束标记   -byte      -1}
    public static ByteBuffer get(String s) {
        byte[] body = new byte[10];
        ByteBuffer buffer = ByteBuffer.allocate(Protocol.HEADER_LENGTH + body.length);
        buffer.put(Protocol.START_TAG);
        buffer.put(Protocol.VERSION);
        buffer.putShort((short) (Protocol.HEADER_LENGTH + body.length));
        buffer.put(Protocol.RETAIN);
        buffer.put(Protocol.VERIFY_TAG);
        buffer.put(body);
        buffer.put(Protocol.END_TAG);
        buffer.flip();
        return buffer;
    }

}
